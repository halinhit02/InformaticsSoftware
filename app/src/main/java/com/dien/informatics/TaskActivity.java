package com.dien.informatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class TaskActivity extends AppCompatActivity {

    private List<Task> taskList;
    private TextView txtNumQues, txtQuestion;
    private RadioGroup rgAnswer;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnPrevious, btnNext;

    private String document, lesson;
    private int answer, questionIndex;
    private ArrayList<Integer> correctList, answerQuesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        document = getIntent().getStringExtra("PDF_URL_KEY");
        lesson = getIntent().getStringExtra("CHAPTER_NAME_KEY");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(lesson);
        }
        init();
        registerListener();
        getTask(lesson);
    }

    private void init() {
        txtNumQues = findViewById(R.id.txt_num_question);
        txtQuestion = findViewById(R.id.txtQuestion);
        rgAnswer = findViewById(R.id.rg_answer);
        rbA = findViewById(R.id.rb_a);
        rbB = findViewById(R.id.rb_b);
        rbC = findViewById(R.id.rb_c);
        rbD = findViewById(R.id.rb_d);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        correctList = new ArrayList<>();
        answerQuesList = new ArrayList<>();
        questionIndex = 0;
    }

    private void registerListener() {
        rgAnswer.setOnCheckedChangeListener(((radioGroup, i) -> {
            if (i == R.id.rb_a)
                answer = 0;
            else if (i == R.id.rb_b)
                answer = 1;
            else if (i == R.id.rb_c)
                answer = 2;
            else
                answer = 3;
        }));
        btnPrevious.setOnClickListener(v -> {
            if (answer == taskList.get(questionIndex).getCorrect() && !correctList.contains(questionIndex))
                correctList.add(questionIndex);
                answerQuesList.set(questionIndex, answer);
            if (questionIndex > 0) {
                questionIndex -= 1;
                showQuestion(questionIndex);
            }
        });
        btnNext.setOnClickListener(v -> {
            if (answer == taskList.get(questionIndex).getCorrect() && !correctList.contains(questionIndex))
                correctList.add(questionIndex);
            answerQuesList.set(questionIndex, answer);
            if (questionIndex == taskList.size() - 1) {
                goToResult();
                return;
            }
            questionIndex += 1;
            showQuestion(questionIndex);
        });
    }

    private void showQuestion(int index) {
        if (taskList.size() == 0)
            return;
        rgAnswer.clearCheck();
        answer = -1;
        txtNumQues.setText("Câu hỏi " + (index + 1) + " của " + taskList.size());
        Task currentTask = taskList.get(index);
        txtQuestion.setText(currentTask.getQuestion());
        List<String> answerList = currentTask.getAnswers();
        rbA.setText(answerList.get(0));
        rbB.setText(answerList.get(1));
        rbC.setText(answerList.get(2));
        rbD.setText(answerList.get(3));
        if (answerQuesList.get(index) != -1)
            setAnswer(answerQuesList.get(index));
    }

    private void setAnswer(int answerIndex) {
        if (answerIndex == 0)
            rbA.setChecked(true);
        else if (answerIndex == 1)
            rbB.setChecked(true);
        else if (answerIndex == 2)
            rbC.setChecked(true);
        else rbD.setChecked(true);
    }

    private void getTask(String title) {
        taskList = new ArrayList<>();
        ProgressDialog mLoading = new ProgressDialog(this);
        mLoading.setMessage("Đang tải xuống dữ liệu...");
        mLoading.setCancelable(false);
        mLoading.show();
        FirebaseFirestore.getInstance().collection("Contest").document(title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (int i = 0; i < 15; i++) {
                            Task taskData = task.getResult().get(i + "", Task.class);
                            if (taskData != null)
                                taskList.add(taskData);
                        }
                        if (taskList.size() == 0) {
                            Toast.makeText(this, "Không có dữ liệu.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        for (int i = 0; i < taskList.size(); i++) {
                            answerQuesList.add(-1);
                        }
                        showQuestion(questionIndex);
                    } else {
                        Toast.makeText(this, "Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    }
                    mLoading.cancel();
                });
    }

    private void goToResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putIntegerArrayListExtra("CORRECT_QUESTION_LIST_KEY", correctList);
        intent.putIntegerArrayListExtra("ANSWER_QUESTION_LIST_KEY", answerQuesList);
        intent.putExtra("PDF_URL_KEY", document);
        intent.putExtra("CHAPTER_NAME_KEY", lesson);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}