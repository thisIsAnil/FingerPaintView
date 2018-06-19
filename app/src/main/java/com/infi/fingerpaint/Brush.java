package com.infi.fingerpaint;

/**
 * Created by INFIi on 1/21/2017.
 */


import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;


public class  Brush {

    public static MaskFilter setNeonBrush(float raduis){
       return  new BlurMaskFilter(raduis, BlurMaskFilter.Blur.OUTER);
    }
    public static MaskFilter setSolidBrush(float radius){
        return  new BlurMaskFilter(radius, BlurMaskFilter.Blur.SOLID);
    }
    public static MaskFilter setInnerBrush(float radius){
        return  new BlurMaskFilter(radius, BlurMaskFilter.Blur.INNER);
    }
    public static MaskFilter setBlurBrush(float radius){
        return  new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
    }
    public static MaskFilter setEmbossBrush(){
        return new EmbossMaskFilter(new float[]{0f,1f,0.5f},0.8f,3f,3f);
    }
    public static MaskFilter setDebossBrush(){
        return new EmbossMaskFilter(new float[]{0f,-1f,0.5f},0.8f,15f,1f);
    }
}