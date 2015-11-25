package com.android.larrimorea.snapchat;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.parse.Parse;


public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MainMenuFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "nGQQIIDSHEW6TlIAeIny8pdAE5jGmR4rIqQzUX4G", "szIqo1mJYGtsnNvdZr1kVO6kzLIReRSas1NdcJ3Z");
    }
}
