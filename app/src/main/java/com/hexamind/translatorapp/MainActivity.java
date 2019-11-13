package com.hexamind.translatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout translatorLayout;
    AppCompatEditText textToTranslate, translatedText;
    AppCompatButton translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translatorLayout = findViewById(R.id.translator);
        textToTranslate = findViewById(R.id.textToTranslate);
        translatedText = findViewById(R.id.translatedText);
        translate = findViewById(R.id.translate);

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.FR)
                .build();
        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideTranslator(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideTranslator(false);
                System.out.println("Translator model error: \n" + e.getMessage());
                Toast.makeText(MainActivity.this, "Some issue occurred. Please try again later...", Toast.LENGTH_SHORT).show();
            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateText(translator, textToTranslate.getText().toString());
            }
        });
    }

    private void hideTranslator(boolean isVisible) {
        if (isVisible)
            translatorLayout.setVisibility(View.VISIBLE);
        else
            translatorLayout.setVisibility(View.GONE);
    }

    private void translateText(FirebaseTranslator translator, String textToTranslate) {
        translator.translate(textToTranslate)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String receivedText) {
                        translatedText.setText(receivedText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error: " + e.getMessage());
                Toast.makeText(MainActivity.this, "There was some problem translating the text. Please try again later....", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
