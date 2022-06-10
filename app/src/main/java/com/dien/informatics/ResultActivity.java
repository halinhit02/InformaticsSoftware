package com.dien.informatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResult, tvCorrect, tvIncorrect;

    private String document, lesson;
    private ArrayList<Integer> answerQuesList, correctList;
    private double correctPerInCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        document = getIntent().getStringExtra("PDF_URL_KEY");
        lesson = getIntent().getStringExtra("CHAPTER_NAME_KEY");
        correctList = getIntent().getIntegerArrayListExtra("CORRECT_QUESTION_LIST_KEY");
        answerQuesList = getIntent().getIntegerArrayListExtra("ANSWER_QUESTION_LIST_KEY");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(lesson);
        }
        init();
        correctPerInCorrect = correctList.size() * 1.0 / answerQuesList.size() * 100;
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        tvResult.setText("Bạn đã vượt qua bài trắc nghiệm " + decimalFormat.format(correctPerInCorrect) + "%");
        tvCorrect.setText("Đúng (" + correctList.size() + "/" + answerQuesList.size() + ")");
        tvIncorrect.setText("Sai (" + (answerQuesList.size() - correctList.size()) + "/" + answerQuesList.size() + ")");
    }

    private void init() {
        tvResult = findViewById(R.id.tv_result);
        tvCorrect = findViewById(R.id.tv_correct);
        tvIncorrect = findViewById(R.id.tv_incorrect);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.taskCallback.onCallback(correctPerInCorrect);
    }
}