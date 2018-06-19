package com.infi.fingerpaint;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawableOnTouchView drawableOnTouchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout mainFrame=(FrameLayout)findViewById(R.id.main_frame);
        try {
            drawableOnTouchView = new DrawableOnTouchView(this);
            drawableOnTouchView.setActionListener(new DrawableOnTouchView.OnActionListener() {
                @Override
                public void OnCancel() {
                            drawableOnTouchView.setClickable(false);

                }

                @Override
                public void OnDone(Bitmap bitmap) {
                            drawableOnTouchView.makeNonClickable(false);
                }

                @Override
                public void show() {
                        drawableOnTouchView.makeNonClickable(true);
                }

                @Override
                public void killSelf() {
                    //if(listener!=null)listener.killSelf(drawableOnTouchView.getBitmap());
                }
            });

            drawableOnTouchView.setColorChangedListener(new DrawableOnTouchView.OnColorChangedListener() {
                @Override
                public void onColorChanged(int color) {

                }

                @Override
                public void onStrokeWidthChanged(float strokeWidth) {

                }

                @Override
                public void onBrushChanged(int Brushid) {

                }
            });
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity= Gravity.CENTER;

            mainFrame.addView(drawableOnTouchView,params);

            drawableOnTouchView.attachCanvas(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

        }catch (Exception e){
            Log.e("MainActivity",e.getMessage());
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
}
