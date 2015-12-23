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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 12/21/2015.
 */
public class MainFragment extends Fragment {
    private String friendReqName;
    private ArrayList<String> frArrayStrings = new ArrayList<String>();
    private ArrayAdapter mAdapter;
    private ParseUser mUser;

    private View mView;
    private ListView friendRequestsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_fragment, container, false);

        updateFriends();
        displayFriendRequests();

        getFriendRequests();

        return mView;
    }

    private void updateFriends(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("From", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("Accepted", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject ob : objects) {
                        updateFriendList(ob);
                        deleteRequest(ob);
                    }
                } else {
                    Toast.makeText(getActivity(), "No Friend Requests Accepted", Toast.LENGTH_SHORT).show();
                    Log.e("MainFragment", "updateFriends" + e.getMessage());
                }
            }
        });
    }

    private void updateFriendList(ParseObject request){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("usernmae", request.getString("To"));
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null){
                    ParseRelation<ParseUser> relation = ParseUser.getCurrentUser()
                            .getRelation("Friends");
                    relation.add(object);
                    ParseUser.getCurrentUser().saveInBackground();
                }else{
                    Log.e("MainFragment", "updateFriendList" + e.getMessage());
                }
            }
        });
    }

    private void deleteRequest(ParseObject request){
        try {
            request.delete();
        }catch(ParseException e){
            Log.e("MainFragment", "deleteRequest" + e);
        }
    }






    private void displayFriendRequests(){
        friendRequestsView = (ListView)mView.findViewById(R.id.listViewFriendReqs);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, frArrayStrings);
        friendRequestsView.setAdapter(mAdapter);
        setClickListener();
    }

    private void getFriendRequests(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereNotEqualTo("Accepted", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    fillFriendRequests(objects);
                    displayFriendRequests();
                } else {
                    Toast.makeText(getActivity(), "No Friend Requests Found", Toast.LENGTH_SHORT).show();
                    Log.e("MainFragment", "getFriendReqError " + e.getMessage());
                }
            }
        });
    }

    private void fillFriendRequests(List<ParseObject> list){
        for(ParseObject object: list){
            frArrayStrings.add(object.get("From").toString());
        }
        if(frArrayStrings.size() == 0){
            frArrayStrings.add("No pending friend requests");
        }
    }

    private void setClickListener(){
        friendRequestsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeAcceptPopup(friendRequestsView.getItemAtPosition(position).toString());
            }
        });
    }

    private void makeAcceptPopup(final String from){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Accept Friend Request?");

        alert.setPositiveButton("YES!!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptRequest(from);
                dialog.cancel();
            }
        });

        alert.setNegativeButton("NOOOOOO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void acceptRequest(final String from){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("From", from);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put("Accepted", true);
                    object.saveInBackground();
                    ParseRelation<ParseUser> relation = ParseUser.getCurrentUser()
                            .getRelation("Friends");
                    addFriend(from, relation);
                } else {
                    Log.e("MainFragment", "AcceptRequest" + e.getMessage());
                }
            }
        });
    }

    private void addFriend(String from, final ParseRelation relation){
        mUser = null;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", from);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null){
                    mUser = object;
                    relation.add(mUser);
                    ParseUser.getCurrentUser().saveInBackground();
                }else{
                    Log.e("MainFragment", "addFriend" + e.getMessage());
                }
            }
        });
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
