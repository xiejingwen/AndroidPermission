package com.jevon.androidpermission;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jevon
 */
public abstract class BaseActivity extends AppCompatActivity {
    public BaseActivity mActivity;
    private PermissionsListener mListener;
    private final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }

    /**
     * 请求权限封装
     *
     * @param permissions
     * @param listener
     */
    public void requestPermissions(String[] permissions, PermissionsListener listener) {
        mListener = listener;
        List<String> requestPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permission);
            }
        }
        if (!requestPermissions.isEmpty() && Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[requestPermissions.size()]), PERMISSION_REQUEST_CODE);
        } else {
            if (mListener != null) {
                try {
                    mListener.onGranted();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                //存放被拒绝的权限集合
                List<String> deniedPermissions = new ArrayList<>();
                //当所有拒绝的权限都勾选不再询问，这个值为true,这个时候可以引导用户手动去授权。
                boolean isNeverAsk = true;
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    String permission = permissions[i];
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        deniedPermissions.add(permissions[i]);
                        // 点击拒绝但没有勾选不再询问
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            isNeverAsk = false;
                        }
                    }
                }
                //被拒绝权限为空 则表示所有权限都已获取
                if (mListener != null) {
                    if (deniedPermissions.isEmpty()) {
                        //try catch 避免接口内有异常导致应用崩溃 可不加
                        try {
                            mListener.onGranted();
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mListener.onDenied(deniedPermissions, isNeverAsk);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
