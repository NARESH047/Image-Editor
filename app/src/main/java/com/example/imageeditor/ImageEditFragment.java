package com.example.imageeditor;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class ImageEditFragment extends Fragment {
    static ImageEditCanvas ImageEditCanvas;
    Uri uri;
    Uri imageUri;
    static View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.image_edit, container, false);
        ImageEditCanvas = (ImageEditCanvas) view.findViewById(R.id.image_edit);
        view.setDrawingCacheEnabled(true);
        Bitmap editedImage = view.getDrawingCache();
        return view;
    }



    public static void makeGreyScaledImage(){
        ImageEditCanvas.greyScaleImageSelected();
    }
    public static void makeTouchRespondTrue(){
        ImageEditCanvas.makeTouchTrue();
    }
    public static void makeTextAddedTrue(){
        ImageEditCanvas.makeTextAdded();
    }
    public static void makeImageClearEdit(){
        ImageEditCanvas.goToOriginalImage();
    }
    public static Bitmap makeEditedImageReturned(){
        Bitmap editedImage = view.getDrawingCache();
        return editedImage;
    }

}


