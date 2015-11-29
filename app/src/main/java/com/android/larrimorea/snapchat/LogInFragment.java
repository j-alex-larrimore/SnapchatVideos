package com.android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Alex on 11/25/2015.
 */
public class LogInFragment extends Fragment{
    private Button mLogInButton;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private String mUsername;
    private String mPassword;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);

        pause = false;
        mLogInButton = (Button)view.findViewById(R.id.loginButton);
        mUsernameField = (EditText)view.findViewById(R.id.username);
        mPasswordField = (EditText)view.findViewById(R.id.password);

        mUsernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsername = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsername != null && mPassword != null && pause == false) {
                    pause = true;
                    ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e == null){
                                Intent returnIntent = new Intent();
                                getActivity().setResult(getActivity().RESULT_OK, returnIntent);
                                getActivity().finish();
                            }else{
                                pause = false;
                                Log.e("Login", "Log in error" + e);
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}
