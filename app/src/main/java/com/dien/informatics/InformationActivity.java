package com.dien.informatics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.Util;
import com.google.gson.Gson;

public class InformationActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String gender = "Nam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông tin cá nhân");
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải lên dữ liệu...");
        progressDialog.setCancelable(false);
        findViewById(R.id.btn_next).setOnClickListener(v -> {
            progressDialog.show();
            User user = getData();
            searchUser(user);
        });
        ((RadioGroup)findViewById(R.id.rg_gender)).setOnCheckedChangeListener((radioGroup, i) -> {
            gender = ((RadioButton)findViewById(i)).getText().toString();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userId = getString("UserId");
        if (userId != null && !userId.equals("")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private User getData() {
        User user = new User();
        user.setName(((EditText)findViewById(R.id.edt_name)).getText().toString());
        user.setGender(gender);
        user.setBirthday(((EditText)findViewById(R.id.edt_date)).getText().toString());
        user.setPhone(((EditText)findViewById(R.id.edt_phone)).getText().toString());
        user.setEmail(((EditText)findViewById(R.id.edt_email)).getText().toString());
        user.setAddress(((EditText)findViewById(R.id.edt_address)).getText().toString());
        return user;
    }

    private void saveString(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private String getString(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void searchUser(User appUser) {
        FirebaseFirestore.getInstance().collection("AppUsers")
                .whereEqualTo("phone", appUser.getPhone())
                .whereEqualTo("email", appUser.getEmail())
                .whereEqualTo("birthday", appUser.getBirthday())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Toast.makeText(this, "Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    else if (!task.getResult().isEmpty()) {
                        String userId = task.getResult().getDocuments().get(0).getId();
                        saveIdAndNext(userId);
                    } else
                        saveToFireBase(appUser);
                });
    }

    private void saveToFireBase(User appUser) {
        @SuppressLint("RestrictedApi") String userId = Util.autoId();
        FirebaseFirestore.getInstance().collection("AppUsers").document(userId).set(appUser)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       saveIdAndNext(userId);
                   } else {
                       Toast.makeText(this, "Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                   }
                   progressDialog.dismiss();
                });
    }

    private void saveIdAndNext(String userId) {
        saveString("UserId", userId);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}