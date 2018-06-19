package com.infi.fingerpaint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import static android.view.ViewAnimationUtils.createCircularReveal;

/**
 * Created by INFIi on 1/21/2017.
 */

/**
 * interfaces onActionListener, onColorChangedListener*/
public class DrawableOnTouchView extends FrameLayout {
    public interface OnActionListener{
        void OnCancel();
        void OnDone(Bitmap bitmap);
        void show();
        void killSelf();
    }

    public interface  OnColorChangedListener{
        void onColorChanged(int color);
        void onStrokeWidthChanged(float strokeWidth);
        void onBrushChanged(int Brushid);
    }
    private OnActionListener actionListener;

    public void setActionListener(OnActionListener actionListener) {
        this.actionListener = actionListener;
    }
    private OnColorChangedListener colorChangedListener;


    public void setColorChangedListener(final OnColorChangedListener colorChangedListener) {
        this.colorChangedListener = colorChangedListener;
        fingerPaintView.setColorPickerChanged(new FingerPaintView.OnColorPickerChanged() {
            @Override
            public void onColorChanged(int color) {
                DrawableOnTouchView.this.colorChangedListener.onColorChanged(color);
            }

            @Override
            public void onStrokeWidthChanged(float strokeWidth) {
                DrawableOnTouchView.this.colorChangedListener.onStrokeWidthChanged(strokeWidth);
            }

            @Override
            public void onBrushChanged(int Brushid) {
                DrawableOnTouchView.this.colorChangedListener.onBrushChanged(Brushid);
            }
        });
    }

    private ImageButton undo, painterIcon,done,cancel;
    private ShaderTextView normal_brush,neon_brush,inner_brush,blur_brush,emboss_brush,deboss_brush;
    private ColorPicker colorPicker;
    private FingerPaintView fingerPaintView;
    private LinearLayout select_brush_frame,strokeWidth_frame,draw_action_layout;
    FrameLayout undo_frame;
    private SeekBar strokeSeekbar;
    private Context context;
    private TextView strokeWidthstatus;
    private FrameLayout main_frame;
    private ImageView onDoneIv;
    private static final int MAX_WIDTH=50;
    boolean controlsHidden=false;
    private FrameLayout canvasFrame;
    public DrawableOnTouchView(Context context) {
        super(context);
        init(context);
    }

    public DrawableOnTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawableOnTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    private void init(Context context){
        this.context=context;
        try {
            View layout=View.inflate(context, R.layout.drawable_view_layout,null);
            addView(layout);
            bindViews(layout);
            setClickables();
        }catch (Exception e){
            Log.e("onInit():",e.getMessage());
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    public  void attachCanvas(){
        LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        canvasFrame.addView(fingerPaintView,params);
    }
    public void attachCanvas(int width,int height){
        LayoutParams params=new LayoutParams(width,height, Gravity.CENTER);
        canvasFrame.addView(fingerPaintView,params);

    }
    public void hideActions(){
        draw_action_layout.setVisibility(GONE);
    }
    public void flipOnTouch(boolean isVisible){
        int v=isVisible?GONE:VISIBLE;
        colorPicker.setVisibility(v);
        select_brush_frame.setVisibility(GONE);
        strokeWidth_frame.setVisibility(GONE);
        undo_frame.setVisibility(v);

    }
    public void clearFromLastWatcher(){
        fingerPaintView.clearFromLastWatcher();
        fingerPaintView.clearWatcerList();
    }
    public boolean hasDrawn(){
        return fingerPaintView.hasDrawn();
    }
    public void clearWatcherList(){
        fingerPaintView.clearWatcerList();

    }
    private void enableShader(ShaderTextView shaderTextView,int filterId){
        shaderTextView.setFilterId(filterId);
        shaderTextView.setRadius(16);
        shaderTextView.enableMask();
    }
    private void bindViews(View layout){
        undo=(ImageButton)layout.findViewById(R.id.undo_btn);
        undo.setImageResource(R.drawable.ic_undo);
        painterIcon =(ImageButton)layout.findViewById(R.id.show_stroke_bar);
        painterIcon.setImageResource(R.drawable.ic_gestures);
        painterIcon.setClickable(false);
        painterIcon.setVisibility(GONE);

        normal_brush=(ShaderTextView)layout.findViewById(R.id.normal_brush);
        enableShader(normal_brush,BrushType.BRUSH_SOLID);

        neon_brush=(ShaderTextView)layout.findViewById(R.id.neon_brush);
        enableShader(neon_brush,BrushType.BRUSH_NEON);

        inner_brush=(ShaderTextView)layout.findViewById(R.id.inner_brush);
        enableShader(inner_brush,BrushType.BRUSH_INNER);

        blur_brush=(ShaderTextView)layout.findViewById(R.id.blur_brush);
        enableShader(blur_brush,BrushType.BRUSH_BLUR);

        emboss_brush=(ShaderTextView)layout.findViewById(R.id.emboss_brush);
        enableShader(emboss_brush,BrushType.BRUSH_EMBOSS);

        deboss_brush=(ShaderTextView)layout.findViewById(R.id.deboss_brush);
        enableShader(deboss_brush,BrushType.BRUSH_DEBOSS);

        colorPicker=(ColorPicker)layout.findViewById(R.id.color_picker);
        canvasFrame=(FrameLayout)layout.findViewById(R.id.canvas_frame);
        fingerPaintView=new FingerPaintView(context);

        float  location = context.getSharedPreferences("paint", Activity.MODE_PRIVATE).getFloat("last_color_location", 0.5f);
        fingerPaintView.setBrushColor(colorPicker.colorForLocation(location));
        fingerPaintView.setBrushStrokeWidth(12.f);

        select_brush_frame=(LinearLayout)layout.findViewById(R.id.brush_option_frame);
        strokeWidth_frame=(LinearLayout)layout.findViewById(R.id.stroke_width_layout);

        strokeSeekbar=(SeekBar)layout.findViewById(R.id.stroke_width_seekbar);
        strokeSeekbar.setMax(50);
        strokeSeekbar.setProgress(14);

        strokeWidthstatus=(TextView)layout.findViewById(R.id.stroke_width_status);

        cancel=(ImageButton)layout.findViewById(R.id.draw_canceled);
        done=(ImageButton)layout.findViewById(R.id.draw_done);

        main_frame=(FrameLayout)layout.findViewById(R.id.draw_main_frame);
        draw_action_layout=(LinearLayout)layout.findViewById(R.id.draw_action_layout);

        onDoneIv=(ImageView)layout.findViewById(R.id.onDone_iv);

        undo_frame=(FrameLayout)layout.findViewById(R.id.undo_frame);
        hideChecks();
    }
    public void setColorPicker(int color, float strokeWidth, int id){
        fingerPaintView.setBrushColor(color);
        fingerPaintView.setBrushStrokeWidth(strokeWidth);
        fingerPaintView.setBrushType(id);
    }
    public void setColorAttribute(ColorAttribute colorAttribute){
        colorPicker.setColorAttribute(colorAttribute);
    }
    public void hideChecks(){
        cancel.setVisibility(GONE);
        done.setVisibility(GONE);
    }
    public void showChecks(){
        cancel.setVisibility(VISIBLE);
        done.setVisibility(VISIBLE);
    }
    private void setClickables(){
        fingerPaintView.setUndoEmptyListener(new FingerPaintView.OnUndoEmptyListener() {
            @Override
            public void undoListEmpty() {
                undo.setAlpha(0.4f);
            }

            @Override
            public void redoListEmpty() {

            }

            @Override
            public void refillUndo() {
                undo.setAlpha(1.0f);
            }

            @Override
            public void OnUndoStarted() {
               // Toast.makeText(context,"Undo Started",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnUndoCompleted() {


            }

            @Override
            public void onTouchDown() {
                flipOnTouch(true);
            }

            @Override
            public void onTouchUp() {
                if(!controlsHidden)
                flipOnTouch(false);
            }
        });
        undo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.onUndo();
            }
        });
        painterIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeWidth_frame.setVisibility(strokeWidth_frame.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);

            }
        });
        strokeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    strokeWidthstatus.setText("Stroke Width:"+progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fingerPaintView.setBrushStrokeWidth(seekBar.getProgress());
            }
        });
        colorPicker.setColorPickerListener(new ColorPicker.ColorPickerListener() {
            @Override
            public void onBeganColorPicking() {
              //  FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
               // params.setMargins(AndroidUtilities.dp(0),AndroidUtilities.dp(64),AndroidUtilities.dp(20),AndroidUtilities.dp(30));
               // colorPicker.setLayoutParams(params);

            }

            @Override
            public void onColorValueChanged(int color) {

            }

            @Override
            public void onFinishedColorPicking(int color) {
                //FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(AndroidUtilities.dp(100), ViewGroup.LayoutParams.MATCH_PARENT);
               // params.setMargins(AndroidUtilities.dp(0),AndroidUtilities.dp(64),AndroidUtilities.dp(20),AndroidUtilities.dp(30));
               // colorPicker.setLayoutParams(params);
                fingerPaintView.setBrushColor(color);
                //fingerPaintView.setBrushStrokeWidth((float)(colorPicker.getColorAttribute().getBrushWeight()*MAX_WIDTH));
            }

            @Override
            public void onSettingsPressed() {
                showBrushOptions();
            }
        });
        normal_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.setBrushType(BrushType.BRUSH_SOLID);
                showBrushOptions();
            }
        });
        neon_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.setBrushType(BrushType.BRUSH_NEON);
                showBrushOptions();
            }
        });

        inner_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.setBrushType(BrushType.BRUSH_INNER);
                showBrushOptions();
            }
        });
        blur_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.setBrushType(BrushType.BRUSH_BLUR);
                showBrushOptions();
            }
        });
        emboss_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.setBrushType(BrushType.BRUSH_EMBOSS);
                showBrushOptions();
            }
        });
        deboss_brush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerPaintView.setBrushType(BrushType.BRUSH_DEBOSS);
                showBrushOptions();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionListener!=null)actionListener.OnCancel();
            }
        });
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneIv.setImageBitmap(fingerPaintView.getmBitmap());
                fingerPaintView.setVisibility(GONE);
                if(actionListener!=null)actionListener.OnDone(fingerPaintView.getmBitmap());
                    hideOnDone();
            }
        });
    }
    @TargetApi(21)
    private void OnDoneAnimation(){
        int centerX = (int) (done.getX() + (done.getWidth() / 2));
        int centerY = (int) (done.getY() + (done.getHeight() / 2));
        done.setVisibility(GONE);
        cancel.setVisibility(GONE);
        Animator reveal = createCircularReveal(draw_action_layout, centerX, centerY, 0, getWidth() / 2f);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                draw_action_layout.setVisibility(GONE);
            }
        });
        reveal.start();

    }
    public Bitmap getBitmap(){
        return fingerPaintView.getmBitmap();
    }
    public void hideOnDone(){
        //OnDoneAnimation();
        controlsHidden=true;
        onDoneIv.setImageBitmap(fingerPaintView.getmBitmap());
        onDoneIv.setVisibility(VISIBLE);
        colorPicker.setVisibility(GONE);
        select_brush_frame.setVisibility(GONE);
        undo.setVisibility(GONE);
        strokeWidth_frame.setVisibility(GONE);
        main_frame.setBackgroundColor(Color.TRANSPARENT);
        draw_action_layout.setVisibility(GONE);
        fingerPaintView.setVisibility(GONE);
        painterIcon.setVisibility(GONE);
    }
    public void makeNonClickable(boolean show){
        painterIcon.setClickable(true);
        undo_frame.setClickable(true);
        onDoneIv.setClickable(false);
        colorPicker.setClickable(show);
        select_brush_frame.setClickable(false);
        undo.setClickable(true);
        strokeWidth_frame.setClickable(false);
        draw_action_layout.setClickable(false);
        fingerPaintView.setClickable(show);
    }

    public void show(){
       // OnDoneAnimation();
        controlsHidden=false;
        onDoneIv.setImageBitmap(null);
        onDoneIv.setVisibility(GONE);
        fingerPaintView.setVisibility(VISIBLE);
        colorPicker.setVisibility(VISIBLE);
        select_brush_frame.setVisibility(GONE);
        undo.setVisibility(VISIBLE);
        strokeWidth_frame.setVisibility(GONE);
        painterIcon.setVisibility(VISIBLE);
        main_frame.setBackgroundColor(Color.TRANSPARENT);
    }
    private void showBrushOptions(){
        boolean show=select_brush_frame.getVisibility()!=VISIBLE;
        if(show) {
            select_brush_frame.setVisibility(VISIBLE);
            select_brush_frame.setAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_right));
        }else {
            select_brush_frame.setVisibility(INVISIBLE);
            select_brush_frame.setAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_right));
        }
        strokeWidth_frame.setVisibility(show?VISIBLE:GONE);
    }
}
