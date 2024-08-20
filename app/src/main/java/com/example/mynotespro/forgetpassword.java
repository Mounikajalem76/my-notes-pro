package com.example.mynotespro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class forgetpassword extends AppCompatActivity {
    EditText editText_emailaddress;

    Button button_back,button_send;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        editText_emailaddress=(EditText) findViewById(R.id.forget_emailadress);


        button_send=(Button) findViewById(R.id.send_button);
        button_back=(Button) findViewById(R.id.back_button);

        firebaseAuth=FirebaseAuth.getInstance();


        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editText_emailaddress.getText().toString();
                if (email.isEmpty()){
                    editText_emailaddress.setError("Please enter Email Address");
                }else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(forgetpassword.this, "Reset link is sending Successfully for your register email", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(forgetpassword.this, "sending failed "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(forgetpassword.this,signinpage.class);
                startActivity(intent);
            }
        });


    }
}