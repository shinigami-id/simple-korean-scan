package com.wiryaimd.korean_scan;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.wiryaimd.korean_scan.model.TlxBlockModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnSelect, btnCopy;
    private TextView tvResult;

    private Scanner scanner;

    private ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            try {

                InputStream inputStream = getContentResolver().openInputStream(result);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                scanner.recognize(bitmap, new Scanner.Listener() {
                    @Override
                    public void recognizeComplete(Iterator<TlxBlockModel> iterator) {
                        StringBuilder sb = new StringBuilder();
                        int size = 0;
                        while (iterator.hasNext()){
                            TlxBlockModel tlxBlockModel = iterator.next();
                            sb.append(tlxBlockModel.getText()).append("(").append(tlxBlockModel.getBoundingBox().top).append(", ").append(tlxBlockModel.getBoundingBox().left).append(")").append("\n\n");
                            size += 1;
                        }

                        sb.append("\n\n\nTotal detected dialog: ").append(size);

                        tvResult.setText(sb.toString());
                    }

                    @Override
                    public void recognizeFailed(String msg) {
                        Toast.makeText(MainActivity.this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.main_tv_result);
        btnSelect = findViewById(R.id.main_btn_scan);
        btnCopy = findViewById(R.id.main_btn_copy);
        scanner = new Scanner();

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("res_kr", tvResult.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "result copied", Toast.LENGTH_SHORT).show();
            }
        });
        
    }
}