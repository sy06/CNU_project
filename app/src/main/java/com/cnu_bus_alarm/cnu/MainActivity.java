package com.cnu_bus_alarm.cnu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        Intent intent = null;
        switch(v.getId()){
            case R.id.locationbtn :
                Toast.makeText(this,"실시간 버스 위치 확인",Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, LocationSubActivity.class);
                break;

            case R.id.routebtn :
                Toast.makeText(this,"노선 정보",Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, RouteActivity.class);
                break;

            case R.id.timebtn :
                Toast.makeText(this,"운행 시간 확인",Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, TimeActivity.class);
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
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
