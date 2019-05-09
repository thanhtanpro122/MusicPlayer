package com.example.nhom9.musicplayer.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.nhom9.musicplayer.Adapter.PlayNhacAdapter;
import com.example.nhom9.musicplayer.DatabaseAccess.BaiHatService;
import com.example.nhom9.musicplayer.DatabaseAccess.QuetBaiHatService;
import com.example.nhom9.musicplayer.Model.BaiHat;
import com.example.nhom9.musicplayer.R;

public class ListBaiHatActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bai_hat);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            loadData();
        }
    }

    private void loadData() {
        SharedPreferences ref = getSharedPreferences("com.example.nhom9.musicplayer.Activity", MODE_PRIVATE);
        if (ref.getBoolean("first-run", true)) {
            try {
                QuetBaiHatService quetBaiHatService = new QuetBaiHatService(getApplicationContext());
                quetBaiHatService.scanAndSave();
                ref.edit().putBoolean("first-run", false).apply();
                loadData();
            } catch (Exception ignored) {
            }
        } else {
            RecyclerView rclbaiHat = findViewById(R.id.recycler_play_baihat);
            rclbaiHat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rclbaiHat.setHasFixedSize(true);

            try {
                BaiHatService baiHatService = new BaiHatService(getApplicationContext());

                PlayNhacAdapter adapter = new PlayNhacAdapter(getApplicationContext(), baiHatService.layDanhSachBaiHat());

                adapter.setOnItemClickListener(new PlayNhacAdapter.ItemClickListener() {
                    @Override
                    public void onClick(View view, BaiHat baiHat, int pos) {
                        Intent intent = new Intent(ListBaiHatActivity.this, Activity_play_nhac.class);
                        intent.putExtra("song", baiHat);

                        startActivity(intent);
                    }
                });

                rclbaiHat.setAdapter(adapter);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            }
        }
    }
}
