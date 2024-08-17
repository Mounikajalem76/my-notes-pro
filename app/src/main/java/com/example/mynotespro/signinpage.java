package com.example.mynotespro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signinpage extends AppCompatActivity {
    EditText editText_email, editText_password;

    Button button_signin;

    TextView textView_signup;

    TextView textView_forget;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signinpage);
        editText_email = (EditText) findViewById(R.id.signin_email);
        editText_password = (EditText) findViewById(R.id.signin_password);

        button_signin = (Button) findViewById(R.id.signup_button);

        textView_signup = (TextView) findViewById(R.id.tvsignup);
        textView_forget = (TextView) findViewById(R.id.forgetPassword);

        firebaseAuth = FirebaseAuth.getInstance();


        textView_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(signinpage.this, SignUppage.class);
                startActivity(intent);
            }
        });
        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }

            private boolean validatePassword() {
                String val = editText_email.getText().toString();
                if (val.isEmpty()) {
                    editText_email.setError("Username is Empty");
                    return false;
                } else {
                    editText_email.setError(null);
                    return true;
                }

            }

            private boolean validateUsername() {
                String val = editText_password.getText().toString();
                if (val.isEmpty()) {
                    editText_password.setError("Password is Empty");
                    return false;
                } else {
                    editText_password.setError(null);
                    return true;
                }
            }
            private void checkUser() {
                String email = editText_email.getText().toString();
                String password = editText_password.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
//                        Intent intent = new Intent(signinpage.this, Sidenavigationbar.class);
//                        startActivity(intent);
                        Toast.makeText(signinpage.this, "SignIn Sucessflly", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(signinpage.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        textView_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent=new Intent(signinpage.this, forgetpassword.class);
                startActivity(intent);
            }
        });

    }
}