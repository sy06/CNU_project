package com.cnu_bus_alarm.cnu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000; //1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; //0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocation;

    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;

    LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


    //받아올 정보
    String word[];
    int count = 0;
    double latitudeA = 36.366, longitudeA = 127.3439;
    double latitudeB=36.368, latitudeC=36.368, latitudeD=36.3665;
    double longitudeB=127.3445, longitudeC=127.3439, longitudeD=127.3445;
    String nosun;

    //소켓
    DatagramSocket ds = null;

    // 노선별로 토큰 구분 -> 노선별로 위도, 경도 값 받음
    public void word_token(String inputLine) {
        if (inputLine != null) {
            StringTokenizer parser = new StringTokenizer(inputLine, "|");
            while (parser.hasMoreTokens()) {
                word[count] = parser.nextToken();
                count++;
            }
        }
        nosun = word[2];
        if (nosun == "A") {
            latitudeA = Double.parseDouble(word[0]);
            longitudeA = Double.parseDouble(word[1]);
            Log.e("A 정보","latitude : " + latitudeA + ", longigude : " + longitudeA + "nosun" + nosun);
        } else if (nosun == "B") {
            latitudeB = Double.parseDouble(word[0]);
            longitudeB = Double.parseDouble(word[1]);
            Log.e("B 정보","latitude : " + latitudeB + ", longigude : " + longitudeB + "nosun" + nosun);
        } else if (nosun == "C") {
            latitudeC = Double.parseDouble(word[0]);
            longitudeC = Double.parseDouble(word[1]);
            Log.e("C 정보","latitude : " + latitudeC + ", longigude : " + longitudeC + "nosun" + nosun);
        } else if (nosun == "D") {
            latitudeD = Double.parseDouble(word[0]);
            longitudeD = Double.parseDouble(word[1]);
            Log.e("D 정보","latitude : " + latitudeD + ", longigude : " + longitudeD + "nosun" + nosun);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_buslocation);

        Log.d(TAG, "onCreate");
        mActivity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapp);
        mapFragment.getMapAsync(this);
        StrictMode.enableDefaults(); //소켓

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket ds = new DatagramSocket(7000);
                    Log.e("준비완료", "받을준비완료");

                    while(true){
                        byte[]b = new byte[1024];
                        DatagramPacket dp = new DatagramPacket(b,b.length);
                        ds.receive(dp);
                        String str = new String(dp.getData(),0,dp.getLength());
                        Log.e("메세지: ",str);
                    }
                }catch(Exception e){
                    Log.d("error","Socket error");
                }
            }
        }).start();

    }

    @Override
    public void onResume(){
        super.onResume();
        if(mGoogleApiClient.isConnected()){
            Log.d(TAG, "onResume : call startLocationUpdates");
            if(!mRequestingLocationUpdates) startLocationUpdates();
        }

        if(askPermissionOnceAgain){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }

    }

    private void startLocationUpdates(){
        if(!checkLocationServicesStatus()){
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }
        else{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
            mRequestingLocationUpdates = true;
            mGoogleMap.setMyLocationEnabled(true);
        }
    }
    private void stopLocationUpdates(){
        Log.d(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        Log.d(TAG, "onMapReady : ");
        mGoogleMap = googleMap;
        setDefaultLocation();

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick : ");
            }
        });
        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(mMoveMapByUser == true && mRequestingLocationUpdates){
                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }
                mMoveMapByUser = true;
            }
        });
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location){

        LatLng A_BUS = new LatLng(latitudeA, longitudeA);
        MarkerOptions MarkerA = new MarkerOptions();
        MarkerA.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launchera));
        mGoogleMap.addMarker(MarkerA.position(A_BUS).title("A노선 버스"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(A_BUS));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        LatLng B_BUS = new LatLng(latitudeB, longitudeB);
        MarkerOptions MarkerB = new MarkerOptions();
        MarkerB.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcherb));
        mGoogleMap.addMarker(MarkerB.position(B_BUS).title("B노선 버스"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(B_BUS));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        LatLng C_BUS = new LatLng(latitudeC, longitudeC);
        MarkerOptions MarkerC = new MarkerOptions();
        MarkerC.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcherc));
        mGoogleMap.addMarker(MarkerC.position(C_BUS).title("C노선 버스"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(C_BUS));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        LatLng D_BUS = new LatLng(latitudeD, longitudeD);
        MarkerOptions MarkerD = new MarkerOptions();
        MarkerD.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcherd));
        mGoogleMap.addMarker(MarkerD.position(D_BUS).title("D노선 버스"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(D_BUS));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "onLocationChanged : ");
        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude() + " 경도:" + String.valueOf(location.getLongitude()));

        setCurrentLocation(location, markerTitle, markerSnippet);
        mCurrentLocation = location;
    }

    //@Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    // @Override
    public void onProviderEnabled(String s) {

    }

    //@Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onStart(){
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){
            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop(){
        if(mRequestingLocationUpdates){
            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }
        if(mGoogleApiClient.isConnected()){
            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        if(ds!= null){
            ds.close();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint){
        if(mRequestingLocationUpdates == false){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION );
                }
                else{
                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
            else{
                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    public void onConnectionSuspend(int cause){
        Log.d(TAG, "onConnectionSuspend");
        if(cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " + "connection lost. Cause : network lost.");
        else if(cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended(): Google Play services" + "connection lost. Cuase : service disconnected");
    }

    public String getCurrentAddress(LatLng latLng){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try{
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        }
        catch(IOException ioException){
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        }
        catch(IllegalArgumentException illegalArgumentException){
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null || addresses.size() == 0){
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        else{
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet){
        mMoveMapByUser = false;
        if(currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);



        currentMarker = mGoogleMap.addMarker(markerOptions);

        if(mMoveMapByAPI){
            Log.d(TAG, "setCurrentLocation : mGoogleMap moveCamera " + location.getLatitude() + " " + location.getLongitude());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation(){
        mMoveMapByUser = false;

        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요.";

        if(currentMarker != null)
            currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 17);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions(){
        boolean fineLocationRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야 합니다.");
        else if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED && !fineLocationRationale){
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음)"+ "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        }
        else if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
            if(mGoogleApiClient.isConnected() == false){
                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    //  @Override
    public void onRequestPermissionResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(permsRequestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0){
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if(permissionAccepted){
                if(mGoogleApiClient.isConnected() == false){
                    Log.d(TAG, "onRequestPermissionResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }
            }
            else {
                checkPermissions();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                askPermissionOnceAgain = true;
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" +mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case GPS_ENABLE_REQUEST_CODE:
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");
                        if(mGoogleApiClient.isConnected() == false){
                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }
                break;
        }
    }
}
