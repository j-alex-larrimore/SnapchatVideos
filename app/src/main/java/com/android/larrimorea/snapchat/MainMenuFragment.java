package com.android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Alex on 11/25/2015.
 */
public class MainMenuFragment extends Fragment{
    private ListView listView;
    private String[] menuStrings;
    private ArrayAdapter<String> mAdapter;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainmenu_fragment, container, false);
        listView = (ListView)view.findViewById(R.id.listView);

        menuStrings = new String[]{
                "Take a Picture",
                "Send a Picture",
                "Inbox"
        };

        setMenu();

        return view;
    }

    private void setMenu(){
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(pause==false) {
                    pause = true;
                    if (id == 0) {
                        Intent intent = new Intent(getActivity(), TakePictureActivity.class);
                        startActivity(intent);
                    } else if (id == 1) {
                        Intent intent = new Intent(getActivity(), SendPictureActivity.class);
                        startActivity(intent);
                    } else if (id == 2) {
                        Intent intent = new Intent(getActivity(), InboxActivity.class);
                        startActivity(intent);
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
}
