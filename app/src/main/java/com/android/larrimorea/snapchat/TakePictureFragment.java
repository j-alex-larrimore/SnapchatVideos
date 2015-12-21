package com.android.larrimorea.snapchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePictureFragment extends Fragment{
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1;

    private String imageFileName;
    private String currentPhotoPath;
    private boolean pause;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.take_picture, container, false);

        pause = false;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = createImageFile();
            currentPhotoPath = photoFile.getAbsolutePath();

            if(photoFile != null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

        return view;
    }

    private File createImageFile(){
        File image = null;
        try{
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timestamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        }catch(IOException e){
            Log.e("TakePictureFragment", "createImageFile" + e);
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK){
                pause = true;
                Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
                galleryAddPic();
                parseAddPic();
                getActivity().finish();
            }else if(resultCode == getActivity().RESULT_CANCELED){
                Toast.makeText(getActivity(), "Image capture cancelled!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "Image capture failed!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void galleryAddPic(){
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        Log.i("Pic", contentUri.toString());
    }

    private void parseAddPic(){
        Bitmap bitmap = null;
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getActivity()
            .getContentResolver(), contentUri);
        }catch(Exception e){
            Log.e("ParseAddError", "activity result " + e);
        }
        byte[] scaledData;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        scaledData = stream.toByteArray();
        final ParseFile photoFile = new ParseFile(imageFileName, scaledData);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    ParseUser.getCurrentUser().add("photos", photoFile);
                    ParseUser.getCurrentUser().saveInBackground();
                }else{
                    Log.e("TakePictureFragment", "ParseSaveError" + e);
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
