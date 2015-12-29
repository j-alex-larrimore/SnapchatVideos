package com.android.larrimorea.snapchat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
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
    private View mView;
    private ListView listView;
    private ListView picView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> picAdapter;
    private List<String> arrayStrings = new ArrayList<String>();
    private List<String> fromStrings = new ArrayList<String>();
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_fragment, container, false);
        pause = false;

        getFriends();

        displayPics();
        getSentPics();

        return mView;
    }

    private void displayPics(){
        picView = (ListView)mView.findViewById(R.id.listViewInbox);
        picAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, fromStrings);
        picView.setAdapter(picAdapter);
        //setClickListener();
    }

    private void getSentPics(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SentPicture");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    fillSentPics(objects);
                    displayPics();
                } else {
                    Log.e("MainFragment", "getSentPics" + e.getMessage());
                    Toast.makeText(getActivity(), "No Pictures Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillSentPics(List<ParseObject> list){
        for(ParseObject object: list){
            fromStrings.add(object.get("From").toString());
        }
    }



    private void getFriends(){
        ParseRelation relation = ParseUser.getCurrentUser().getRelation("Friends");
        ParseQuery<ParseUser> query = relation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    fillFriends(objects);
                    displayFriends();
                } else {
                    Log.e("MainFragment", "getFriends" + e.getMessage());
                    Toast.makeText(getActivity(), "No Friends Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillFriends(List<ParseUser> list){
        for(ParseObject object: list){
            arrayStrings.add(object.get("username").toString());
        }
        if(arrayStrings.size() == 0){
            arrayStrings.add("No Friends Yet. Add some Friends");
        }
    }

    private void displayFriends(){
        listView = (ListView)mView.findViewById(R.id.listViewSend);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayStrings);
        listView.setAdapter(arrayAdapter);
        setClickListener();
    }

    private void setClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    Intent intent = new Intent(getActivity(), ChoosePicActivity.class);
                    String str = (String) arrayAdapter.getItem(position);
                    intent.putExtra("to", str);
                    startActivity(intent);
                }
            }
        });

        picView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    acceptPic(picView.getItemAtPosition(position).toString());
                }
            }
        });
    }

    private void acceptPic(String from){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SentPicture");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("From", from);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    viewPic(object.getParseFile("Picture"));
                    try {
                        object.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.e("MainFragment", "AcceptPic" + e.getMessage());
                }
            }
        });
    }

    private void viewPic(ParseFile picture){
       if(picture != null){
           makePicPopup(picture);
       }else{
           Log.e("MainFragment", "View Pic Error");
       }
    }

    private void makePicPopup(ParseFile picture){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        final ImageView image = new ImageView(getActivity());
        alert.setView(image);

        picture.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image.setImageBitmap(bmp);
                } else {
                    Log.e("MainFragment", "MakePicPopup" + e.getMessage());
                }
            }
        });

        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                getActivity().finish();
            }
        });

        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}
