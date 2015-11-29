package com.android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseUser;

/**
 * Created by Alex on 11/25/2015.
 */
public class MainMenuFragment extends Fragment{
    private static final int LOGGED_IN = 0;
    private static final int LOGGED_OUT = 1;

    private ListView listView;
    private String[] inMenuStrings;
    private String[] outMenuStrings;
    private ArrayAdapter<String> mAdapter;
    private boolean pause = false;
    private boolean loggedIn = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainmenu_fragment, container, false);
        listView = (ListView)view.findViewById(R.id.listView);

        inMenuStrings = new String[]{
                "Option 1",
                "Option 2",
                "Option 3",
                "Log Out"
        };

        outMenuStrings = new String[]{
                "Register",
                "Log In"
        };

        setMenu();

        return view;
    }

    private void setMenu(){
        if(loggedIn){
            loggedIn();
        }else{
            loggedOut();
        }
    }

    private void loggedIn(){
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, inMenuStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(pause==false) {
                    pause = true;
                    if(id == 3){
                        Intent intent = new Intent(getActivity(), LogOutActivity.class);
                        startActivityForResult(intent, LOGGED_OUT);
                    }
                }
            }
        });
    }

    private void loggedOut(){
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, outMenuStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    if (id == 0) {
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        startActivityForResult(intent, LOGGED_IN);
                    } else if (id == 1) {
                        Intent intent = new Intent(getActivity(), LogInActivity.class);
                        startActivityForResult(intent, LOGGED_IN);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == LOGGED_IN){
                loggedIn = true;
            }else if(requestCode == LOGGED_OUT){
                loggedIn = false;
                ParseUser.getCurrentUser().logOut();
            }
            setMenu();
        }else{
            Log.i("MainMenuFragment", "Error Registering/Loggin in or out");
        }
    }
}
