package com.cnu_bus_alarm.cnu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 진수연 on 2018-01-19.
 */

public class DevActivity extends AppCompatActivity {
    //static String[] num = {"1234","2345","3456","5678"};
    static String code = "0000";
    EditText editText;//텍스트
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        editText = (EditText) findViewById(R.id.adminanswer);
        button = (Button) findViewById(R.id.buttonOk);

        button.setOnClickListener(new View.OnClickListener() {
            Intent intent = null;//이걸로 화면전환

            @Override
            public void onClick(View view) {//버튼을 눌렀을때
                String inputText = editText.getText().toString();//받아옴
                if (inputText.equals(code)) {
                    Toast.makeText(DevActivity.this, "관리자 모드를 실행합니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(DevActivity.this, AdminActivity.class);

                } else {
                    Toast.makeText(DevActivity.this, "♥福새해 복 많이 받으세요福♥", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    public void onClick(View view) {
        Intent i = null;
        if (view.getId() == R.id.buttondev) {
            Toast.makeText(DevActivity.this, "CNU 순환버스 알리미 개발팀", Toast.LENGTH_SHORT).show();
            i = new Intent(DevActivity.this, FinishActivity.class);
        }
        if (i != null) {
            startActivity(i);
        }
    }
}
