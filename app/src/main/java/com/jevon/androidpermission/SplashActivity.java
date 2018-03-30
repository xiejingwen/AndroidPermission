package com.jevon.androidpermission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * 启动页 申请权限示例
 *
 * @author jevon
 */
public class SplashActivity extends BaseActivity {
    /**
     * 为了避免在onResume中多次请求权限
     */
    private boolean isRequesting;
    /**
     * 需要申请的权限
     */
    private String[] permissions = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //是否正在申请权限
        if (!isRequesting) {
            isRequesting = true;
            requestPermissions(permissions, mListener);
        }
    }

    private PermissionsListener mListener = new PermissionsListener() {
        @Override
        public void onGranted() {
            //权限获取成功
            isRequesting = false;
            startActivity(new Intent(mActivity, MainActivity.class));
        }

        @Override
        public void onDenied(List<String> deniedPermissions, boolean isNeverAsk) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            //请求权限没有全被勾选"不再提示" 则继续提醒申请权限
            if (!isNeverAsk) {
                builder.setMessage("为了能正常使用应用，请授予以下权限：" + getPermissionsAlias(deniedPermissions))
                        .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissions, mListener);
                            }
                        });
            } else {//全被勾选不再提示
                builder.setMessage("应用缺少必要权限，请手动授予以下权限：" + getPermissionsAlias(deniedPermissions))
                        .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                                isRequesting = false;
                            }
                        });
            }
            builder.create().show();
        }
    };

    /**
     * 设置权限中文别名 用于提示用户
     * @param deniedPermissions
     * @return
     */
    private String getPermissionsAlias(List<String> deniedPermissions) {
        if (deniedPermissions.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < deniedPermissions.size(); i++) {
            String permission = deniedPermissions.get(i);
            if (permission.equals(Manifest.permission.READ_PHONE_STATE)) {
                sb.append("\n获取手机信息");
            } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                sb.append("\n读写手机存储");
            } else if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                sb.append("\n获取位置信息");
            }
        }
        return sb.toString();
    }
}
