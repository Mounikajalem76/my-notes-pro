package com.example.mynotespro;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        lottieAnimationView= (LottieAnimationView) findViewById(R.id.animation);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("animation");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // String fromDbUrl=snapshot.getValue(String.class);
                //   Toast.makeText(MainActivity.this, snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, snapshot.child("url").getValue(String.class), Toast.LENGTH_SHORT).show();


//                String loading="https://lottie.host/1722a160-d6a6-4aa0-8652-58bb159f00e8/rmKhkkVgxg.json";
                String loading=snapshot.child("url").getValue(String.class);

                lottieAnimationView.setAnimationFromUrl(loading);
                lottieAnimationView.playAnimation();
                handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(MainActivity.this, signinpage.class);
                        startActivity(intent);
                        finish();

                    }
                },3000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Network Failed"+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}