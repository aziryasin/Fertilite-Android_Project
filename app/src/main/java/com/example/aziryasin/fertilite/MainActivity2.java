package com.example.aziryasin.fertilite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import android.widget.ImageView;
import android.os.AsyncTask;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.*;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;


public class MainActivity2 extends AppCompatActivity {
    private VisualRecognition vrClient;
    private CameraHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vrClient=new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.api_key)
        );

        helper=new CameraHelper(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CameraHelper.REQUEST_IMAGE_CAPTURE){

        }
        final Bitmap photo = helper.getBitmap(resultCode);
        final File photoFile = helper.getFile(resultCode);


        ImageView preview = findViewById(R.id.preview);
        preview.setImageBitmap(photo);



        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VisualClassification response =
                        vrClient.classify(
                                new ClassifyImagesOptions.Builder()
                                        .images(photoFile)
                                        .build()
                        ).execute();

                // More code here
                ImageClassification classification=response.getImages().get(0);
                VisualClassifier classifier= classification.getClassifiers().get(0);
                final StringBuffer output=new StringBuffer();
                for(VisualClassifier.VisualClass object : classifier.getClasses()){
                    if(object.getScore()>0.7f){
                        output.append('<')
                                .append(object.getName())
                                .append('>');
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView detectedObjects=(TextView)findViewById(R.id.detected_objects);
                        detectedObjects.setText(output);
                    }
                });

            }
        });




    }

    public void takePicture(View view){
        helper.dispatchTakePictureIntent();
    }
}
