package com.threestudio.multi_downloader.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.threestudio.multi_downloader.R;

public class HomeActivity extends AppCompatActivity  implements View.OnClickListener{
    private Button javaSingleButton;
    private Button javaMultithButton;
    private Button ndkSingleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        javaSingleButton = (Button)findViewById(R.id.javaSingle);
        javaMultithButton = (Button)findViewById(R.id.javaMultith);
        ndkSingleButton= (Button)findViewById(R.id.ndkSingle);
        javaSingleButton.setOnClickListener(this);
        javaMultithButton.setOnClickListener(this);
        ndkSingleButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.javaSingle:
                startActivity(new Intent(HomeActivity.this, SingleActivity.class));
                break;
            case R.id.javaMultith:
                startActivity(new Intent(HomeActivity.this, MultiActivity.class));
                break;
            case R.id.ndkSingle:
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                break;

        }

    }
}
