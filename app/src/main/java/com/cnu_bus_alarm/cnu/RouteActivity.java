package com.cnu_bus_alarm.cnu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by 진수연 on 2018-01-16.
 */

public class RouteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busroute);
    }

    public void onClick(View v){
        Intent intent = null;
        switch(v.getId()){
            case R.id.button1 :
                Toast.makeText(this,"A노선",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, ArouteActivity.class);
                break;

            case R.id.button2 :
                Toast.makeText(this,"B노선",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, BrouteActivity.class);
                break;

            case R.id.button3 :
                Toast.makeText(this,"C노선",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, CrouteActivity.class);
                break;

            case R.id.button4 :
                Toast.makeText(this,"D노선",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, DrouteActivity.class);
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
    }

}
