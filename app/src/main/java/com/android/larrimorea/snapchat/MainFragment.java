package com.android.larrimorea.snapchat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Alex on 12/21/2015.
 */
public class MainFragment extends Fragment {
    private String friendReqName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_add_friend:
                makePopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makePopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Type your Friend's name");

        final EditText input = new EditText(getActivity());
        alert.setView(input);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                friendReqName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (friendReqName != null) {
                    searchForFriend();
                    dialog.cancel();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    public void searchForFriend(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", friendReqName);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    sendFriendRequest(user);
                } else {
                    Toast.makeText(getActivity(), "Friend not found!", Toast.LENGTH_SHORT).show();
                    Log.e("MainFragment", "SearchForFriend" + e);
                }
            }
        });
    }

    public void sendFriendRequest(ParseUser user){
        ParseObject fr = new ParseObject("FriendRequests");
        fr.put("From", ParseUser.getCurrentUser().getUsername());
        fr.put("To", user.getUsername());
        fr.saveInBackground();

        Log.i("MainFragment", "Sending Friend Request to " + user.getUsername());
    }
}
