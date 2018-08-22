package com.threestudio.multi_downloader.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
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
                startActivity(new Intent(HomeActivity.this, NativeActivity.class));
                break;

        }

    }
}
