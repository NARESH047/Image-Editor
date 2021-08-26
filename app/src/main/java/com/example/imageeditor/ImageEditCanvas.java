package com.example.imageeditor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

public class ImageEditCanvas extends View {

    android.content.Context Context;

    boolean touchRespond = false;

    float xTouched, yTouched;
    Paint textPaint, objPaint, linePaint;
    Bitmap selectedImage;
    int dWidth, dHeight;

    Bitmap selectedImageOrginal, selectedImageAfterResize;
    String textToAdd;
    Path path;

    public ImageEditCanvas(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
        Context = context;
        textPaint = new Paint();
        objPaint = new Paint();
        linePaint = new Paint();
        path = new Path();
        selectedImageOrginal = MainActivity.getSelctedImageOrg();

        objPaint.setAntiAlias(true);
        objPaint.setFilterBitmap(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(96f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);

        linePaint.setStrokeWidth(16);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#FFD770"));
        linePaint.setStyle(Paint.Style.STROKE);

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        selectedImageAfterResize = Bitmap.createScaledBitmap(selectedImageOrginal, (dWidth)-16, 3*(dHeight)/5, true);
        selectedImage = selectedImageAfterResize;
        textToAdd = " ";

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#FF6F61"));
        canvas.drawBitmap(selectedImage,8,8, objPaint);
        canvas.drawText(textToAdd, dWidth / 2, dHeight/14, textPaint);
        canvas.drawPath(path, linePaint);
        invalidate();
    }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (touchRespond) {
                xTouched = event.getX();
                yTouched = event.getY();
                if (yTouched >= 8 && yTouched < 8+(3*(dHeight/5))) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        path.moveTo(xTouched, yTouched);

                    } else if(event.getAction() == MotionEvent.ACTION_MOVE){
                        path.lineTo(xTouched, yTouched);
                    } else if(event.getAction() == MotionEvent.ACTION_UP){

                    }
                }
            }
            invalidate();
            return true;
        }

    public class ImageAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            selectedImage = getGreyScaleImage(selectedImage);
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap greyScaledImage) {
            invalidate();
        }
    }

        public void greyScaleImageSelected(){
            ImageAsyncTask task = new ImageAsyncTask();
            task.execute();
        }

        public static Bitmap getGreyScaleImage(Bitmap originalImage){
        Bitmap greyScaleImage = Bitmap.createBitmap(originalImage.getWidth(), originalImage.getHeight(), originalImage.getConfig());
        int A, R, G, B, average, originalPixel;
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        for(int x=0;x<width; x++){
            for (int y=0; y<height; y++){
                originalPixel = originalImage.getPixel(x,y);
                A = Color.alpha(originalPixel);
                R = Color.red(originalPixel);
                G = Color.green(originalPixel);
                B = Color.blue(originalPixel);

                average = ((R+G+B)/3);
                R=average;
                G=average;
                B=average;

                greyScaleImage.setPixel(x,y, Color.argb(A, R, G, B));

            }
        }

        return greyScaleImage;
        }

        public void makeTouchTrue(){
        touchRespond = true;
        invalidate();
        }

        public void makeTextAdded(){
            textToAdd = MainActivity.getTextToAdd();
            invalidate();
        }

        public void goToOriginalImage(){
        textToAdd = "";
        path = null;
        path = new Path();
        selectedImage = selectedImageAfterResize;
        invalidate();
    }

}
