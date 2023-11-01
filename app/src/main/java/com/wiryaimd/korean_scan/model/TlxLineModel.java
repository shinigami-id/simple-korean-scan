package com.wiryaimd.korean_scan.model;

import android.graphics.Rect;

public class TlxLineModel {

    private String text;
    private Rect boundingBox;
    private float confidence;
    private int wordSize;

    public TlxLineModel(String text, Rect boundingBox, float confidence, int wordSize) {
        this.text = text;
        this.boundingBox = boundingBox;
        this.confidence = confidence;
        this.wordSize = wordSize;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getText() {
        return text;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public int getWordSize() {
        return wordSize;
    }

    public int top(){
        return boundingBox.top;
    }

    public int bottom(){
        return boundingBox.bottom;
    }

    public int left(){
        return boundingBox.left;
    }

    public int right(){
        return boundingBox.right;
    }

    public int centerX(){
//        return left() + ((right() - left()) / 2);
        return boundingBox.centerX();
    }

    public int centerY(){
//        return top() + ((bottom() - top()) / 2);
        return boundingBox.centerY();
    }

    public int height(){
//        return bottom() - top();
        return boundingBox.height();
    }

}
