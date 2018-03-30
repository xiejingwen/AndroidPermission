package com.jevon.androidpermission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //其他权限也是这个流程操作
    public void openCamera(View view) {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, new PermissionsListener() {
            @Override
            public void onGranted() {
                Toast.makeText(mActivity, "相机权限获取成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDenied(List<String> deniedPermissions, boolean isNeverAsk) {
                //无法弹出权限弹框 需自己弹框说明
                if (isNeverAsk) {
                    new AlertDialog.Builder(mActivity)
                            .setMessage("相机权限已被禁止，请手动授予应用权限")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }
                    }).create().show();
                }
            }
        });
    }
}
