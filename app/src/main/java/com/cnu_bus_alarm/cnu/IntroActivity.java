package com.cnu_bus_alarm.cnu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by 진수연 on 2018-01-06.
 */

public class IntroActivity extends Activity {
    Handler handler = new Handler();
    Runnable r = new Runnable(){
        @Override
        public void run(){
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            startActivity(intent); //다음화면으로 넘어감
            finish();
        }
    };

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout); //xml , java 소스 연결
    }

    @Override
    protected void onResume(){
        super.onResume(); //다시 화면에 들어오면 예약 걸어줌
        handler.postDelayed(r, 1500); //1.5초 뒤에 runnable 객체 r 실행
    }

    @Override
    protected  void onPause(){
        super.onPause(); //화면을 벗어나면, 예약한 작업 취소
        handler.removeCallbacks(r); //예약 취소
    }
}