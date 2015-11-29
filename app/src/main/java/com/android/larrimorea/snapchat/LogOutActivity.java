package com.android.larrimorea.snapchat;

import android.support.v4.app.Fragment;

/**
 * Created by Alex on 11/25/2015.
 */
public class LogOutActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LogOutFragment();
    }
}
