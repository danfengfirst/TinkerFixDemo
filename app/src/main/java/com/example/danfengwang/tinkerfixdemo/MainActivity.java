package com.example.danfengwang.tinkerfixdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.danfengwang.tinkerfixdemo.R;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etUserName;
    private String path=Environment.getExternalStorageDirectory()+File.separator;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String patchPath  = Environment.getExternalStorageDirectory() + File.separator+"ApkPatchs";
                String patchPath  = path + "FixPath"+File.separator+"patch_signed_7zip.apk";
                File file = new File(patchPath);
                if (file.exists()) {
                    Log.e(TAG,"补丁文件存在");
                    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);
                    Log.e(TAG,"安装完成");
                    Toast.makeText(getApplicationContext(),"安装完成",Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG,"补丁文件不存在");
                    Toast.makeText(getApplicationContext(),"文件不存在",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}