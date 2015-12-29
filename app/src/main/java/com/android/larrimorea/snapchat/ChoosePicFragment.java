package com.android.larrimorea.snapchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 12/29/2015.
 */
public class ChoosePicFragment extends Fragment {
    private boolean pause = false;
    private String sendTo;
    private static final int READ_REQUEST_CODE = 42;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_pic, container, false);

        pause = false;
        sendTo = getActivity().getIntent().getStringExtra("to");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == READ_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK && pause == false){
                Uri uri = null;
                Bitmap bitmap = null;
                if(data != null){
                    uri = data.getData();
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                        .getContentResolver(), uri);
                    }catch(Exception e){
                        Log.e("ChoosePic", "onActivityResult" + e);
                    }
                    saveImage(bitmap);
                }
            }
        }
    }

    private void saveImage(Bitmap bitmap){
        byte[] scaledData;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        scaledData = stream.toByteArray();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_.jpg";
        ParseFile photoFile = new ParseFile(imageFileName, scaledData);
        ParseObject sentPic = new ParseObject("SentPicture");
        sentPic.put("Picture", photoFile);
        sentPic.put("From", ParseUser.getCurrentUser().getUsername());
        sentPic.put("To", sendTo);
        pause = true;
        sentPic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    makeFinishPopup();

                }else{
                    Log.e("ChoosePic", "SaveImage" + e.getMessage());
                }
            }
        });
    }

    private void makeFinishPopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Picture sent to " + sendTo + "!");
        alert.setPositiveButton("Cool!", new DialogInterface.OnClickListener() {
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
