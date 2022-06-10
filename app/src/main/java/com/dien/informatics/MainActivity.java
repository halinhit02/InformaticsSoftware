package com.dien.informatics;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Presenter {

    private @IdRes
    final List<Integer> iconList = Arrays.asList(R.drawable.pdf_icon, R.drawable.certificate_icon, R.drawable.link_icon, R.drawable.final_test_icon,
            R.drawable.pdf_icon, R.drawable.certificate_icon);
    private List<Chapter> chapterData;
    private ChapterRecyclerAdapter adapter;
    private RecyclerView rcv_chapter;
    private ChapterSaved chapterSaved;
    private Chapter currentChapter;
    private int position;
    public static TaskCallback taskCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        chapterSaved = new ChapterSaved("", 0, "", 0);
        chapterData = new ArrayList<>();
        rcv_chapter = findViewById(R.id.rcv_chapter);
        rcv_chapter.setLayoutManager(new LinearLayoutManager(this));
        rcv_chapter.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChapterRecyclerAdapter(this, chapterData, iconList, chapterSaved, this);
        rcv_chapter.setAdapter(adapter);
        getChapterSaved();
        getChapters();
        taskCallback = taskScore -> {
            if (taskScore >= 50)
                setChapterSaved(currentChapter, position);
            else if (position == chapterSaved.getLessonIndex())
                Toast.makeText(this, "Bạn phải hoàn thành 50% trở lên để mở bài học tiếp theo.", Toast.LENGTH_SHORT).show();
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.logout) {
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit().clear().apply();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(Chapter chapter, int position, View view) {
        currentChapter = chapter;
        this.position = position;
        if (chapter.getName().equals(chapterSaved.getKey()) && position == chapterSaved.getLessonIndex())
            if (!chapter.getLessons().get(position).contains("Trắc nghiệm chương"))
                setChapterSaved(chapter, position);
        nextScreen(chapter, position);
    }

    private void getChapterSaved() {
        String userId = getUserId();
        FirebaseFirestore.getInstance().collection("MyLesson")
                .document(userId)
                .addSnapshotListener((value, error) -> {
                    if (error == null && value != null) {
                        chapterSaved = value.toObject(ChapterSaved.class);
                        if (chapterSaved == null)
                            chapterSaved = new ChapterSaved("", 0, "", 0);
                        adapter.setChapterSaved(chapterSaved);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "Kiểm tra kết nối mạng và thử lại.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        return sharedPreferences.getString("UserId", "");
    }

    private void getChapters() {
        ProgressDialog mLoading = new ProgressDialog(this);
        mLoading.setMessage("Đang tải xuống dữ liệu...");
        mLoading.setCancelable(false);
        mLoading.show();
        FirebaseFirestore.getInstance().collection("Data")
                .orderBy("index", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    mLoading.cancel();
                    if (task.isSuccessful()) {
                        List<DocumentChange> allDocuments = task.getResult().getDocumentChanges();
                        chapterSaved.setKey(allDocuments.get(chapterSaved.getIndex()).getDocument().getId());
                        for (DocumentChange documentChange : allDocuments) {
                            String key = documentChange.getDocument().getId();
                            getDocuments(documentChange.getDocument(), key);
                        }
                    } else {
                        Toast.makeText(this, "Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDocuments(QueryDocumentSnapshot snapshot, String key) {
        Chapter chapter = snapshot.toObject(Chapter.class);
        chapter.setName(key);
        if (chapterSaved.getKey().equals(key) && chapterSaved.getLesson().isEmpty()) {
            chapterSaved.setLesson(chapter.getLessons().get(0));
        }
        chapterData.add(chapter);
        adapter.notifyItemInserted(chapterData.size() - 1);
        adapter.notifyItemRangeChanged(0, chapterData.size() - 1);
    }

    private void setChapterSaved(Chapter chapter, int position) {
        String userId = getUserId();
        ChapterSaved newChapterSaved = new ChapterSaved();
        if (chapterSaved.getIndex() < chapterData.size() - 1
                && chapterSaved.getLessonIndex() == chapterData.get(chapterSaved.getIndex()).getLessons().size() - 1) {
            newChapterSaved.setIndex(chapterSaved.getIndex() + 1);
            newChapterSaved.setLessonIndex(0);
        } else {
            newChapterSaved.setIndex(chapterSaved.getIndex());
            newChapterSaved.setLessonIndex(chapterSaved.getLessonIndex());
            if (chapterSaved.getLessonIndex() < chapterData.get(chapterSaved.getIndex()).getLessons().size() - 1) {
                newChapterSaved.setLessonIndex(chapterSaved.getLessonIndex() + 1);
            }
        }
        newChapterSaved.setKey(chapterData.get(newChapterSaved.getIndex()).getName());
        newChapterSaved.setLesson(chapterData.get(newChapterSaved.getIndex()).getLessons().get(newChapterSaved.getLessonIndex()));
        FirebaseFirestore.getInstance().collection("MyLesson")
                .document(userId)
                .set(newChapterSaved)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    } else {
                        chapterSaved = newChapterSaved;
                    }
                });
    }

    private void nextScreen(Chapter chapter, int position) {
        String lesson = chapter.getLessons().get(position);
        String document = chapter.getDocuments().get(position);
        Intent taskIntent = new Intent(this, DocumentsActivity.class);
        if (document.equals("Contest"))
            taskIntent = new Intent(this, TaskActivity.class);
        if (chapter.getLessons().get(position).equals("Chứng chỉ"))
            taskIntent = new Intent(this, CertificateActivity.class);
        taskIntent.putExtra("PDF_URL_KEY", document);
        taskIntent.putExtra("CHAPTER_NAME_KEY", lesson);
        if (lesson.contains(":"))
            taskIntent.putExtra("IS_VIDEO_KEY", false);
        else if (lesson.contains("-") || lesson.contains("GIẢI ĐỀ THI HSG"))
            taskIntent.putExtra("IS_VIDEO_KEY", true);
        startActivity(taskIntent);
    }
}