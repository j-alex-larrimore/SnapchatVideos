package com.android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Alex on 11/25/2015.
 */
public class LogOutFragment extends Fragment{
    private Button mLogOutButton;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logout, container, false);

        mLogOutButton = (Button)view.findViewById(R.id.logoutButton);
        mLogOutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(pause==false){
                    pause = true;
                    Intent returnIntent = new Intent();
                    getActivity().setResult(getActivity().RESULT_OK, returnIntent);
                    getActivity().finish();
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
