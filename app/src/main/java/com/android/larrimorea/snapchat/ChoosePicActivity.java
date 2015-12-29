package com.android.larrimorea.snapchat;

import android.support.v4.app.Fragment;

/**
 * Created by Alex on 12/29/2015.
 */
public class ChoosePicActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new ChoosePicFragment();
    }
}
