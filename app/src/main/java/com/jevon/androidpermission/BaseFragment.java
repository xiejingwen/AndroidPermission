package com.jevon.androidpermission;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


/**
 * @author jevon
 */
public abstract class BaseFragment extends Fragment {

    public BaseActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof BaseActivity){
            mActivity = (BaseActivity) getActivity();
        }
    }

    /**
     * 请求权限封装
     *
     * @param permissions
     * @param listener
     */
    public void requestPermissions(String[] permissions, PermissionsListener listener) {
        if (mActivity==null){
            new RuntimeException("please inheritance BaseActivity");
            return;
        }
        mActivity.requestPermissions(permissions,listener);
    }
}
