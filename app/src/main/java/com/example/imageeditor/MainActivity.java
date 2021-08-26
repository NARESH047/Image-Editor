package com.example.imageeditor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Uri imageUri;
    Button openImage, greyScale, addText, doodle, save, share, clear, camera;
    public static Bitmap selectedImageOrg;
    static EditText editText;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    boolean imageOpened;
    int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runTimePermission();
        setContentView(R.layout.activity_main);
        openImage = findViewById(R.id.openImageButton);
        greyScale=findViewById(R.id.greyScaleButton);
        addText = findViewById(R.id.addTextButton);
        doodle = findViewById(R.id.doddleButton);
        save = findViewById(R.id.saveButton);
        share = findViewById(R.id.shareButton);
        editText = findViewById(R.id.editText);
        clear = findViewById(R.id.clearAllButton);
        camera = findViewById(R.id.cameraOpenButton);
        imageOpened = false;

        openImage.setOnClickListener(this);
        greyScale.setOnClickListener(this);
        doodle.setOnClickListener(this);
        addText.setOnClickListener(this);
        save.setOnClickListener(this);
        share.setOnClickListener(this);
        clear.setOnClickListener(this);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.openImageButton) {
            openGallery();
            imageOpened = true;
        } else if(v.getId() == R.id.cameraOpenButton){

        }
        if (imageOpened==false) {
            Toast.makeText(getApplicationContext(), "Open an image", Toast.LENGTH_SHORT).show();
        } else {
                if (v.getId() == R.id.greyScaleButton) {
                    ImageEditFragment.makeGreyScaledImage();

                } else if (v.getId() == R.id.addTextButton) {
                    if(editText.getText().toString().length()==0 || editText.getText().toString()==null){
                        Toast.makeText(getApplicationContext(), "Type text to add", Toast.LENGTH_SHORT).show();
                    } else{
                        ImageEditFragment.makeTextAddedTrue();
                    }

                } else if (v.getId() == R.id.doddleButton) {
                    ImageEditFragment.makeTouchRespondTrue();

                } else if (v.getId() == R.id.clearAllButton) {
                    ImageEditFragment.makeImageClearEdit();

                } else if (v.getId() == R.id.saveButton) {
                    saveEditedImage();

                } else if (v.getId() == R.id.shareButton) {
                    Bitmap editedImage = ImageEditFragment.makeEditedImageReturned();
                    String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), editedImage, "Shared Image. " + System.currentTimeMillis() , "IMAGE EDITOR EDITED IMAGE. " + System.currentTimeMillis());
                    Uri editedImageUri = Uri.parse(bitmapPath);
                    ShareAsyncTask task = new ShareAsyncTask();
                    task.execute();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getContentResolver().delete(editedImageUri, null, null);
                        }
                    }, 30000);
                }

            }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            imageUri = data.getData();
            try {
                selectedImageOrg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            selectedImageOrg = (Bitmap) data.getExtras().get("data");
        }

        Fragment imageFragment = new ImageEditFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.image_container,
                imageFragment).commit();
    }

    private void saveEditedImage() {
        Bitmap editedImage = ImageEditFragment.makeEditedImageReturned();
        MediaStore.Images.Media.insertImage(getContentResolver(), editedImage, "editedImage. " + System.currentTimeMillis() , "IMAGE EDITOR EDITED IMAGE. " + System.currentTimeMillis());
        Toast.makeText(getApplicationContext(), "Image saved in gallery", Toast.LENGTH_SHORT).show();
    }

    private void runTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE );
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "Provide file and camera permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }



    public static Bitmap getSelctedImageOrg(){
        return selectedImageOrg;
    }

    public static String getTextToAdd(){
        String currentText = editText.getText().toString();
        return currentText;
    }

    public class ShareAsyncTask extends AsyncTask<String, Void, Uri> {

        @Override
        protected Uri doInBackground(String... strings) {
            Bitmap editedImage = ImageEditFragment.makeEditedImageReturned();
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), editedImage, "editedImage. " + System.currentTimeMillis() , "IMAGE EDITOR EDITED IMAGE. " + System.currentTimeMillis());
            Uri editedImageUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, editedImageUri);
            startActivity(Intent.createChooser(intent, "Share"));
            return editedImageUri;
        }

        @Override
        protected void onPostExecute(Uri editedImageUri) {

        }
    }
}