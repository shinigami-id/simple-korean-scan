package com.wiryaimd.korean_scan;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.wiryaimd.korean_scan.model.TlxBlockModel;
import com.wiryaimd.korean_scan.model.TlxLineModel;
import com.wiryaimd.korean_scan.model.TlxModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Scanner {

    private static final String TAG = "Scanner";

    public interface Listener{
        void recognizeComplete(Iterator<TlxBlockModel> iterator);
        void recognizeFailed(String msg);

    }

    private TextRecognizer textRecognizer;

    public Scanner() {

        textRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

    }

    public void recognize(Bitmap bitmap, Listener listener){

        textRecognizer.process(bitmap, 0).addOnCompleteListener(new OnCompleteListener<Text>() {
            @Override
            public void onComplete(@NonNull Task<Text> task) {
                List<TlxLineModel> lineList = new ArrayList<>();

                Text result = task.getResult();

                for(Text.TextBlock block : result.getTextBlocks()){
                    for (Text.Line line : block.getLines()){

                        int wordSize = 0;
                        elementLoop: for (Text.Element element : line.getElements()){
                            if (element.getBoundingBox() == null) {
                                continue;
                            }

                            Rect wordRect = element.getBoundingBox();
                            wordSize = wordRect.bottom - wordRect.top;
                            break elementLoop;
                        }
                        lineList.add(new TlxLineModel(line.getText(), line.getBoundingBox(), line.getConfidence(), wordSize));
                    }
                }

                List<TlxBlockModel> blockList = cleanBlock(lineList);
                TlxModel tlxModel = new TlxModel(blockList);

                listener.recognizeComplete(tlxModel.getBlockList().iterator());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.recognizeFailed(e.getMessage());
            }
        });
    }

    private List<TlxBlockModel> cleanBlock(List<TlxLineModel> lineList){
        List<TlxBlockModel> blockList = new ArrayList<>();

        for (int i = 0; i < lineList.size();) {
            List<TlxLineModel> result = merge(lineList, lineList.get(i));
            blockList.add(new TlxBlockModel(result));
            lineList.removeAll(result);
        }

        return blockList;
    }

    private List<TlxLineModel> merge(List<TlxLineModel> sentenceList, TlxLineModel root){

        List<TlxLineModel> blockList = new ArrayList<>();
        TlxLineModel head = root;
        TlxLineModel tail = root;

        blockList.add(head);

        for (int i = 0; i < sentenceList.size(); i++) {
            int spaceHeightB = (head.bottom() - head.top());

            if (blockList.contains(sentenceList.get(i))){
                continue;
            }

            int res = sentenceList.get(i).top() - head.bottom();
            int mid = head.centerX();
            if (res > -spaceHeightB &&
                    res <= spaceHeightB &&
                    sentenceList.get(i).right() > mid &&
                    sentenceList.get(i).left() < mid) {
                blockList.add(sentenceList.get(i));
                head = sentenceList.get(i);
                i = 0;
            }
        }

        for (int i = 0; i < sentenceList.size(); i++) {
            int spaceHeightT = tail.bottom() - tail.top();

            if (blockList.contains(sentenceList.get(i))){
                continue;
            }

            int res = tail.top() - sentenceList.get(i).bottom();
            int mid = tail.centerX();
            if (res > -spaceHeightT &&
                    res <= spaceHeightT &&
                    sentenceList.get(i).right() > mid &&
                    sentenceList.get(i).left() < mid) {
                blockList.add(0, sentenceList.get(i));
                tail = sentenceList.get(i);
                i = 0;
            }
        }

        return blockList;
    }
}
