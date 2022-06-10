package com.dien.informatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.barteksc.pdfviewer.PDFView;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

public class DocumentsActivity extends AppCompatActivity {

    private PDFView pdfView;
    private VideoView videoView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        String url = getIntent().getStringExtra("PDF_URL_KEY");
        String title = getIntent().getStringExtra("CHAPTER_NAME_KEY");
        boolean isVideo = getIntent().getBooleanExtra("IS_VIDEO_KEY", false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        pdfView = findViewById(R.id.pdf_viewer);
        videoView = findViewById(R.id.videoView);
        if (isVideo) {
            videoView.setVisibility(View.VISIBLE);
            loadVideo(url);
            return;
        }
        pdfView.setVisibility(View.VISIBLE);
        showPDF(url);
    }

    public void showPDF(String url) {
        FileLoader.with(DocumentsActivity.this)
                .load(url)
                .fromDirectory("PDFFiles", FileLoader.DIR_CACHE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        pdfView.fromFile(response.getBody())
                                .password(null)
                                .defaultPage(0)
                                .enableDoubletap(true)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth())
                                .enableAnnotationRendering(true)
                                .invalidPageColor(Color.WHITE)
                                .load();
                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(DocumentsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void loadVideo(String url) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(url));
        videoView.start();
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