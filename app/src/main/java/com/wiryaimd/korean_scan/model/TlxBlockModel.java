package com.wiryaimd.korean_scan.model;

import android.graphics.Rect;

import java.util.List;

public class TlxBlockModel {

    private String text;
    private Rect boundingBox;
    private List<TlxLineModel> lineList;

    public TlxBlockModel(List<TlxLineModel> lineList) {
        this.lineList = lineList;

        blockFromLines();
    }

    public int avgSize(){
        int avg = 0;
        for (int i = 0; i < lineList.size(); i++){
            avg += lineList.get(i).getWordSize();
        }
        return avg / lineList.size();
    }

    public void blockFromLines(){
        StringBuilder sb = new StringBuilder();
        int left = Integer.MAX_VALUE, top = Integer.MAX_VALUE, bottom = 0, right = 0;
        for (TlxLineModel line : lineList){
            if (line.left() < left){
                left = line.left();
            }
            if (line.top() < top){
                top = line.top();
            }
            if (line.bottom() > bottom){
                bottom = line.bottom();
            }
            if (line.right() > right){
                right = line.right();
            }

            System.out.println("sentence check: " + line.getText());
            System.out.println("confidence: " + line.getConfidence());
            System.out.println("===============");
            sb.append(line.getText()).append(" ");
        }

        this.text = sb.toString();
        this.boundingBox = new Rect(left, top, right, bottom);
    }

    public String getText() {
        return text;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public List<TlxLineModel> getLineList() {
        return lineList;
    }

    public int centerX(){
        return boundingBox.centerX();
    }

    public int centerY(){
        return boundingBox.centerY();
    }

    public int left(){
        return boundingBox.left;
    }

    public int right(){
        return boundingBox.right;
    }

    public int top(){
        return boundingBox.top;
    }

    public int bot(){
        return boundingBox.bottom;
    }

    public int height(){
        return boundingBox.height();
    }

    public int width(){
        return boundingBox.width();
    }
}
