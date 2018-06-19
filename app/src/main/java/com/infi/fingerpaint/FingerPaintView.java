package com.infi.fingerpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;


/**
 * Created by INFIi on 1/21/2017.
 */
public class FingerPaintView extends AppCompatImageView{

    public interface OnUndoEmptyListener{
        void undoListEmpty();
        void redoListEmpty();
        void refillUndo();
        void OnUndoStarted();
        void OnUndoCompleted();
        void onTouchDown();
        void onTouchUp();
    }
    public interface OnColorPickerChanged{
        void onColorChanged(int color);
        void onStrokeWidthChanged(float strokeWidth);
        void onBrushChanged(int Brushid);
    }
    private OnColorPickerChanged colorPickerChanged=null;

    public void setColorPickerChanged(OnColorPickerChanged colorPickerChanged) {
        this.colorPickerChanged = colorPickerChanged;
    }

    private OnUndoEmptyListener undoEmptyListener=null;

    public void setUndoEmptyListener(OnUndoEmptyListener undoEmptyListener) {
        this.undoEmptyListener = undoEmptyListener;
    }


    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private   ArrayList<PaintData> pathList=new ArrayList<>();
    private   ArrayList<PaintData> undoList=new ArrayList<>();
    private int width;
    private int height;
    private float radius=28;
    private Paint undoPaint;
    private boolean redraw;
    private int lastcolor;
    private float lastStrokeWidth;
    private MaskFilter lastMaskFilter;
    private int watcher=0;
    public FingerPaintView(Context c) {
        super(c);
        init(c);
}
    public FingerPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FingerPaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public boolean hasDrawn(){
        return pathList.size()>0;
    }
    public void clearWatcerList(){
        watcher=0;
    }
    public void clearFromLastWatcher(){
        for(int i=0;i<watcher;i++){
            pathList.remove(pathList.size()-1);
        }
        watcher=0;
        invalidate();
    }
    private void init(Context c){
        mPaint = new Paint();

        lastcolor=0xffff0000;
        lastStrokeWidth=c.getSharedPreferences("paint",Context.MODE_PRIVATE).getFloat("stroke",12.0f);
        lastMaskFilter=idToMaskFilter(c.getSharedPreferences("paint",Context.MODE_PRIVATE).getInt("id",BrushType.BRUSH_SOLID),radius=28);


        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(lastcolor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(lastStrokeWidth);

        mPaint.setMaskFilter(lastMaskFilter);
        undoPaint=mPaint;

        redraw=false;
        DisplayMetrics displayMetrics=c.getResources().getDisplayMetrics();
        width=displayMetrics.widthPixels;
        height=displayMetrics.heightPixels;
        mBitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        pathList.add(new PaintData(lastcolor,lastStrokeWidth,lastMaskFilter,mPath));
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    }
    public Bitmap getmBitmap(){
        return mBitmap;
    }

    public void setBrushColor(int color){
        if(colorPickerChanged!=null)colorPickerChanged.onColorChanged(color);
        mPaint.setColor(color);
        lastcolor=color;

    }
    public void setBrushStrokeWidth(float width){
        if(colorPickerChanged!=null)colorPickerChanged.onStrokeWidthChanged(width);
        if(width>0.f)lastStrokeWidth=width;
        if(lastStrokeWidth==0.f)lastStrokeWidth=12.f;
        mPaint.setStrokeWidth(lastStrokeWidth);
        undoPaint=mPaint;
        getContext().getSharedPreferences("paint",Context.MODE_PRIVATE).edit().putFloat("stroke",lastStrokeWidth).commit();
    }
    public void setBrushType(int id){
        if(colorPickerChanged!=null)colorPickerChanged.onBrushChanged(id);
        lastMaskFilter=idToMaskFilter(id,radius);
            mPaint.setMaskFilter(lastMaskFilter);
            undoPaint=mPaint;
        getContext().getSharedPreferences("paint",Context.MODE_PRIVATE).edit().putInt("id",id).commit();
    }
    private MaskFilter idToMaskFilter(int id,float radius){
        switch (id){

            case BrushType.BRUSH_NEON:
                return Brush.setNeonBrush(radius);
            case BrushType.BRUSH_BLUR:
                return Brush.setBlurBrush(radius);
            case BrushType.BRUSH_INNER:
                return Brush.setInnerBrush(radius);
            case BrushType.BRUSH_EMBOSS:
                return Brush.setEmbossBrush();
            case BrushType.BRUSH_DEBOSS:
                return Brush.setDebossBrush();
            default:
                return Brush.setSolidBrush(radius);

        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
        redraw=true;
        mBitmap=Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(mBitmap);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawColor(Color.TRANSPARENT);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            if (!redraw) canvas.drawPath(mPath, mPaint);
            else {redraw();}
        }catch (Exception e){
            Log.e("onDraw",e.getMessage());
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        if(undoEmptyListener!=null)undoEmptyListener.onTouchDown();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        if(undoEmptyListener!=null&&pathList.size()==0)undoEmptyListener.refillUndo();
        if(undoEmptyListener!=null)undoEmptyListener.onTouchUp();
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw

        PaintData pd=new PaintData(lastcolor,lastStrokeWidth,lastMaskFilter,mPath);
        pathList.add(pd);
        watcher++;
        mPath.reset();

    }

    public void onUndo(){
        try {
            if (undoEmptyListener != null) undoEmptyListener.OnUndoStarted();
            redraw=true;
            if (pathList.size() > 0) {
                undoList.add(pathList.remove(pathList.size() - 1));
                onSizeChanged(width,height,width,height);
                if (pathList.isEmpty() && undoEmptyListener != null)
                    undoEmptyListener.undoListEmpty();
                //redraw();
                invalidate();
            } else {
                if (undoEmptyListener != null) undoEmptyListener.undoListEmpty();
            }
        }catch (Exception e){
            Log.e("onUndo",e.getMessage());
        }
        if (undoEmptyListener != null) undoEmptyListener.OnUndoCompleted();

    }
    private void redraw(){
        for(PaintData pd:pathList){
            undoPaint=setPaintAttrs();
            undoPaint.setColor(pd.color);
            undoPaint.setStrokeWidth(pd.strokeWidth);
            if(pd.maskFilter!=null)undoPaint.setMaskFilter(pd.maskFilter);
            mCanvas.drawPath(pd.path, undoPaint);
        }
        redraw=false;

    }
    public void onRedo(){
        if(undoList.size()>0) {
            PaintData redo=undoList.get(undoList.size()-1);
            pathList.add(undoList.remove(undoList.size()-1));
            Paint p=mPaint;
            mCanvas.drawPath(redo.path,p);
            if(undoList.isEmpty()&&undoEmptyListener!=null)undoEmptyListener.undoListEmpty();
            invalidate();

        }else {
            if(undoEmptyListener!=null)undoEmptyListener.redoListEmpty();

        }
    }
    private Paint setPaintAttrs(){
        Paint mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        return mPaint;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    private class PaintData{
        public int color;
        public float strokeWidth;
        public MaskFilter maskFilter=Brush.setSolidBrush(20);
        public Path path=new Path();
        public PaintData(int clr,float strokeWidth,MaskFilter maskFilter,Path path){
            this.color=clr;
            this.maskFilter=maskFilter;
            this.strokeWidth=strokeWidth;
            this.path.set(path);

        }
    }
}


