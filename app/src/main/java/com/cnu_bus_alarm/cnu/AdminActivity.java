package com.cnu_bus_alarm.cnu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by 진수연 on 2018-01-18.
 */

public class AdminActivity extends AppCompatActivity implements LocationListener {
    public  static final int RequestPermissionCode  = 1 ;
    Button buttonEnable, buttonGet ;
    TextView textViewLongitude, textViewLatitude ;
    Context context;
    Intent intent1 ;
    Location location;
    LocationManager locationManager ;
    boolean GpsStatus = false ;
    Criteria criteria ;
    String Holder;

    //소켓시작
    public static Socket socket;
    PrintWriter socket_out;
    public static double choicelatitide = 0;
    public static double choicelongitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        EnableRuntimePermission();

        buttonEnable = (Button)findViewById(R.id.gps_button1);
        buttonGet = (Button)findViewById(R.id.gps_button2);
        textViewLongitude = (TextView)findViewById(R.id.textView);
        textViewLatitude = (TextView)findViewById(R.id.textView2);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        Holder = locationManager.getBestProvider(criteria, false);
        context = getApplicationContext();
        CheckGpsStatus();
        buttonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
            }
        });

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckGpsStatus();
                if (GpsStatus == true) {
                               if (Holder != null) {
                                            if (ActivityCompat.checkSelfPermission(
                                                    AdminActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                    &&
                                                    ActivityCompat.checkSelfPermission(AdminActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                                            != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            Log.e("서버전송:", "위치받아오기");
                                            location = locationManager.getLastKnownLocation(Holder);//최근 위치 조회, 결과는 바로 얻을 수 있음
                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, AdminActivity.this);
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, AdminActivity.this);
                                            //결과는 locationListener을 통해 수신
                                            Log.e("서버전송중", "위치받아오기 성공");

                                            //socket시작
                                            StrictMode.enableDefaults(); //소켓
                                            try {
                                                socket = new Socket("168.188.129.143", 5555);
                                                socket_out = new PrintWriter(socket.getOutputStream(), true);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            String selectedData = "";
                                            if (choicelatitide > -91.0 && choicelatitide < 91.0) {
                                                selectedData += Double.toString(choicelatitide) + "|";
                                            }
                                            if (choicelongitude > -181.0 && choicelongitude < 181.0) {
                                                selectedData += Double.toString(choicelongitude) + "|";
                                            }

                                            if (selectedData != "") {
                                                Log.w("정보보내기", " " + selectedData);
                                                selectedData += "A";
                                                socket_out.println(selectedData);
                                            } else {
                                                socket_out.println("보낼 정보가 없습니다.");
                                            }
                                        }
                                    } else {
                                        Toast.makeText(AdminActivity.this, "Please Enable GPS First", Toast.LENGTH_LONG).show();
                                    }
            }
        });
    }

        // @Override
        public void onLocationChanged(Location location) {
        choicelatitide = location.getLatitude();
        choicelongitude = location.getLongitude();
        textViewLongitude.setText("Longitude:" + choicelongitude);
        textViewLatitude.setText("Latitude:" + choicelatitide);
        }

        // @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        // @Override
        public void onProviderEnabled(String s) {
        }

        //@Override
        public void onProviderDisabled(String s) {
        }

    public void CheckGpsStatus(){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(AdminActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {
            Toast.makeText(AdminActivity.this,"ACCESS_FINE_LOCATION permission allows us to Access GPS in app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(AdminActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AdminActivity.this,"Permission Granted, Now your application can access GPS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdminActivity.this,"Permission Canceled, Now your application cannot access GPS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        try{
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}