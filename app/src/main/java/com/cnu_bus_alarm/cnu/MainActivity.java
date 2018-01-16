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

    public void LocationClicked(View v){
        Toast.makeText(this,"잠시만 기다려주세요.",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LocationSubActivity.class);
        startActivity(intent);
    }
}
