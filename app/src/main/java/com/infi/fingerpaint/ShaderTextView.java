package com.infi.fingerpaint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by INFIi on 1/22/2017.
 */

public class ShaderTextView extends AppCompatTextView {
    public ShaderTextView(Context context){
        super(context);

    }
    public ShaderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShaderTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    //to be called after setting id and radius
    public void enableMask(){
        this.getPaint().setMaskFilter(idToMaskFilter(filterId,radius));
    }

    private int filterId,radius;
    private void init(Context context, AttributeSet attrs){
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderTextView);
        filterId=typedArray.getInt(R.styleable.ShaderTextView_mask_id,BrushType.BRUSH_SOLID);
        radius=typedArray.getInt(R.styleable.ShaderTextView_mask_radius,14);

        this.getPaint().setColor(Color.parseColor("#ff00cc"));
        this.getPaint().setMaskFilter(idToMaskFilter(filterId,radius));

        typedArray.recycle();
    }
    private MaskFilter idToMaskFilter(int id, float radius){
        Log.d("Brush Id: ",id+"");
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
}
