package com.dien.informatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class CertificateActivity extends AppCompatActivity {

    private WebView wvCert;
    private String certificateUrl;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        AppCompatImageView ivDownload = findViewById(R.id.iv_download);
        wvCert = findViewById(R.id.iv_cert);
        wvCert.setWebViewClient(new WebViewClient());
        WebSettings settings = wvCert.getSettings();
        settings.setBuiltInZoomControls(true);
        String document = getIntent().getStringExtra("PDF_URL_KEY");
        String lesson = getIntent().getStringExtra("CHAPTER_NAME_KEY");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(lesson);
        }
        getCertificate();
        ivDownload.setOnClickListener(v -> openLink(certificateUrl));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        return sharedPreferences.getString("UserId", "");
    }

    private void getCertificate() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Đang tải xuống dữ liệu...");
        loadingDialog.show();
        String userId = getUserId();
        FirebaseFirestore.getInstance()
                .collection("AppUsers")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    loadingDialog.cancel();
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Kiểm tra kết nối mạng và thử lại.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    certificateUrl = task.getResult().get("certificate", String.class);
                    if (certificateUrl == null || certificateUrl.isEmpty()) {
                        Toast.makeText(this, "Chứng chỉ đang được cập nhật. Vui lòng đợi!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    wvCert.loadDataWithBaseURL(null, "<html><head></head><body><table style=\"width:100%; height:100%;\"><tr><td style=\"vertical-align:middle;\"><img src=\"" + certificateUrl + "\"></td></tr></table></body></html>", "html/css", "utf-8", null);
                });

    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}