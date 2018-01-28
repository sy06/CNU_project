package com.cnu_bus_alarm.cnu;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    List<String> list1 = new ArrayList<String>();
    List<String> list2 = new ArrayList<String>();
    boolean check1[]={false,false,false,false};
    boolean check2[]={false,false,false};
    AlarmHATT am;
    public static AlarmManager alarmManager = null;
    public static PendingIntent sender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sf = getSharedPreferences("storedList", MODE_PRIVATE);
        int size1 = sf.getInt("FirstStatus_size", 0);
        int size2 = sf.getInt("SecondStatus_size", 0);

        for(int i=0;i<size1;i++)
        {
            list1.add(sf.getString("FirstStatus_" + i, null));
        }
        for(int i=0;i<check1.length;i++){
            check1[i]=sf.getBoolean("FirstCheck_"+i, check1[i]);
        }
        for(int i=0;i<size2;i++)
        {
            list2.add(sf.getString("SecondStatus_" + i, null));
        }
        for(int i=0;i<check2.length;i++){
            check2[i]=sf.getBoolean("SecondCheck_"+i, check2[i]);
        }

        am = new AlarmHATT(getApplicationContext());
    }

    public class AlarmHATT {
        private Context context;
        public AlarmHATT(Context context) {
            this.context = context;
        }

        final long theday = 1000 * 60 * 60 * 24;

        public void SetAlarm(Context context,int id,int hour,int minute){
            Log.i(id+"등록", "SetAlarm()");
           alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = null;
           if(id==1||id==2||id==3||id==4){
                intent = new Intent(context, BroadcastD.class);
            }else{
                intent = new Intent(context, BroadcastL.class);
            }
            sender =PendingIntent.getBroadcast(context,id,intent,0);

            Calendar calendar= Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE),hour,minute,0);
            long aTime = System.currentTimeMillis();
            long bTime = calendar.getTimeInMillis();
            //만일 내가 설정한 시간이 현재 시간보다 작다면 알람이 바로 울려버리기 때문에 이미 시간이 지난 알람은 다음날 울려야 한다.
            while(aTime>bTime){
                bTime += theday;
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, bTime, theday,sender);
        }

        public void AllcancelAlarmFirst(Context context){
            Log.i("첫차알람모두해제", "AllcancelAlarm()");
            Intent intent = new Intent(context, BroadcastD.class);
            int count = check1.length;
            if(count>=0) {
                for (int i = 1; i <= count; i++) {
                    if(sender!=null) {
                        sender = PendingIntent.getBroadcast(context, i, intent, 0);
                        alarmManager.cancel(sender);
                        sender.cancel();
                    }
                }
            }
        }
        public void AllcancelAlarmSecond(Context context){
            Log.i("막차알람모두해제", "AllcancelAlarm()");
            Intent intent = new Intent(context, BroadcastL.class);
            int count = check2.length;
            if(count>=0) {
                for (int i = 1; i <= count; i++) {
                    if(sender!=null) {
                        sender = PendingIntent.getBroadcast(context, i+check1.length, intent, 0);
                        alarmManager.cancel(sender);
                        sender.cancel();
                    }
                }
            }
        }

        public void CancelAlarm(Context context, int id){
            Intent intent = null;
            if(alarmManager != null) {
                Log.i(id+"해제", "CancelAlarm()");
                alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                if(id==1||id==2||id==3||id==4){
                    intent = new Intent(context, BroadcastD.class);
                }else{
                    intent = new Intent(context, BroadcastL.class);
                }
                sender = PendingIntent.getBroadcast(context, id, intent, 0);
                alarmManager.cancel(sender);
                sender.cancel();
                alarmManager=null;
                sender=null;
            }
        }
    }

    public void onClick(View v){
        Intent intent = null;
        switch(v.getId()){
            case R.id.locationbtn :
                intent = new Intent(MainActivity.this, LocationActivity.class);
                break;

            case R.id.routebtn :
                intent = new Intent(MainActivity.this, RouteActivity.class);
                break;

            case R.id.timebtn :
                intent = new Intent(MainActivity.this, TimeActivity.class);
                break;

            case R.id.adminbtn :
                intent = new Intent(MainActivity.this, DevActivity.class);
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
    }


    public void onAlarmClickFirst(View v){
        Toast.makeText(this,"첫차 알람 설정",Toast.LENGTH_SHORT).show();
        final String[] items = new String[]{"8 : 00 (C노선)","8 : 40 (A,B노선)","18 : 50 (D노선 상행)","19 : 05 (D노선 하행)"};
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        alertdialog.setCancelable(false);
        alertdialog.setMultiChoiceItems(
                items,check1
                ,new DialogInterface.OnMultiChoiceClickListener(){
                    boolean count=false;
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked){
                    if(isChecked){
                        if(!list1.contains(items[which])) {
                            count=true;
                            list1.add(items[which]);
                            check1[which]=true;
                            switch (items[which]) {
                                case "8 : 00 (C노선)":
                                    am.SetAlarm(MainActivity.this,1,8,0);
                                    break;
                                case "8 : 40 (A,B노선)":
                                    am.SetAlarm(MainActivity.this,2,8,40);
                                    break;
                                case "18 : 50 (D노선 상행)":
                                    am.SetAlarm(MainActivity.this,3,18,50);
                                    break;
                                case "19 : 05 (D노선 하행)":
                                   am.SetAlarm(MainActivity.this,4,19,5);
                                    break;
                            }
                        }
                    }else{
                            list1.remove(items[which]);
                            check1[which]=false;
                            int id=0;
                        switch (items[which]) {
                            case "8 : 00 (C노선)": id=1;   break;
                            case "8 : 40 (A,B노선)": id=2;   break;
                            case "18 : 50 (D노선 상행)": id=3;   break;
                            case "19 : 05 (D노선 하행)": id=4;   break;
                        }
                        am.CancelAlarm(MainActivity.this,id);
                    }
                    }
                });
        alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = "";
                for(int i=0;i<list1.size();i++) {
                    if(i==list1.size()-1){
                        selectedItem += list1.get(i) + "을 선택하셨습니다.";
                    }else{
                        selectedItem += list1.get(i) + ", ";
                    }
                }
                if(list1.size()==0){
                    selectedItem+="선택한 알람이 없습니다.";
                }
                Toast.makeText(MainActivity.this,selectedItem,Toast.LENGTH_SHORT).show();
            }
        });

        alertdialog.setNegativeButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "'초기화'버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
                list1.clear();
                for(int i=0;i<check1.length;i++){
                    check1[i]=false;
                }
                new AlarmHATT(getApplicationContext()).AllcancelAlarmFirst(MainActivity.this);
            }
        });

        AlertDialog alert = alertdialog.create();
        alert.setTitle("첫차 알람 설정");
        alert.show();
    }

    public void onAlarmClickSecond(View v){
        Toast.makeText(this,"막차 알람 설정",Toast.LENGTH_SHORT).show();
        final String[] items = new String[]{"17 : 40 (A,B,C노선)","20 : 50 (D노선 상행)","21 : 05 (D노선 하행)"};
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        alertdialog.setCancelable(false);
        alertdialog.setMultiChoiceItems(
                items,check2
                ,new DialogInterface.OnMultiChoiceClickListener(){
                    boolean count=false;
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked){
                        if(isChecked){
                            if(!list2.contains(items[which])) {
                                count=true;
                                list2.add(items[which]);
                                check2[which]=true;
                                switch (items[which]) {
                                    case "17 : 40 (A,B,C노선)":
                                        am.SetAlarm(MainActivity.this,5,17,40);
                                        break;
                                    case "20 : 50 (D노선 상행)":
                                        am.SetAlarm(MainActivity.this,6,20,50);
                                        break;
                                    case "21 : 05 (D노선 하행)":
                                        am.SetAlarm(MainActivity.this,7,21,05);
                                        break;
                                }
                            }
                        }else{
                            list2.remove(items[which]);
                            check2[which]=false;
                            int id=0;
                            switch (items[which]) {
                                case "17 : 40 (A,B,C노선)": id=5;   break;
                                case "20 : 50 (D노선 상행)": id=6;   break;
                                case "21 : 05 (D노선 하행)": id=7;   break;
                            }
                            am.CancelAlarm(MainActivity.this,id);
                        }
                    }
                });
        alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = "";
                for(int i=0;i<list2.size();i++) {
                    if(i==list2.size()-1){
                        selectedItem += list2.get(i) + "을 선택하셨습니다.";
                    }else{
                        selectedItem += list2.get(i) + ", ";
                    }
                }
                if(list2.size()==0){
                    selectedItem+="선택한 알람이 없습니다.";
                }
                Toast.makeText(MainActivity.this,selectedItem,Toast.LENGTH_SHORT).show();
            }
        });

        alertdialog.setNegativeButton("초기화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "'초기화'버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
                list2.clear();
                for(int i=0;i<check2.length;i++){
                    check2[i]=false;
                }
                new AlarmHATT(getApplicationContext()).AllcancelAlarmSecond(MainActivity.this);
            }
        });

        AlertDialog alert = alertdialog.create();
        alert.setTitle("막차 알람 설정");
        alert.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Activity 가 종료되기 전에 저장한다
        SharedPreferences sf= getSharedPreferences("storedList", MODE_PRIVATE);
        SharedPreferences.Editor editor= sf.edit();
        editor.putInt("FirstStatus_size",list1.size()); /*sKey is an array*/
        for(int i=0;i<list1.size();i++)
        {
            editor.remove("FirstStatus_" + i);
            editor.putString("FirstStatus_" + i, list1.get(i));
        }
        for(int i=0;i<check1.length;i++){
            editor.remove("FirstCheck_"+i);
            editor.putBoolean("FirstCheck_"+i, check1[i]);
        }

        editor.putInt("SecondStatus_size",list2.size()); /*sKey is an array*/
        for(int i=0;i<list2.size();i++)
        {
            editor.remove("SecondStatus_" + i);
            editor.putString("SecondStatus_" + i, list2.get(i));
        }
        for(int i=0;i<check2.length;i++){
            editor.remove("SecondCheck_"+i);
            editor.putBoolean("SecondCheck_"+i, check2[i]);
        }
        editor.commit();
    }

    private long lastTimeBack;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastTimeBack < 1500){
            finish();
            return;
        }
        Toast.makeText(this,"뒤로 버튼을 한 번 더 눌러 종료합니다.",Toast.LENGTH_SHORT).show();
        lastTimeBack = System.currentTimeMillis();
    }
}
