package com.vysiontech.sewagemonitor;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ChooseCityActivity extends AppCompatActivity {

    private Button mLucknow,mJodhpur;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);


        mToolbar=findViewById(R.id.choose_city_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sewage Monitor");
        mLucknow=findViewById(R.id.Choose_Lucknow);
        mJodhpur=findViewById(R.id.Choose_Jodhpur);

        mLucknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin1=new Intent(ChooseCityActivity.this,LoginActivity.class);
                toLogin1.putExtra("email","lucknow_lmc@gmail.com");
                startActivity(toLogin1);
            }
        });

        mJodhpur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin2=new Intent(ChooseCityActivity.this,LoginActivity.class);
                toLogin2.putExtra("email","jodhpur_jmc@gmail.com");
                startActivity(toLogin2);
            }
        });
    }
}
