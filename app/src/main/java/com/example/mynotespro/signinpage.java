package com.example.mynotespro;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signinpage extends AppCompatActivity {
    EditText editText_email, editText_password;
    Button button_signin;
    TextView textView_signup, textView_forget;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinpage);

        editText_email = findViewById(R.id.signin_email);
        editText_password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressbar);
        builder = new AlertDialog.Builder(this);

        button_signin = findViewById(R.id.signup_button);
        textView_signup = findViewById(R.id.tvsignup);
        textView_forget = findViewById(R.id.forgetPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        textView_signup.setOnClickListener(view -> {
            Intent intent = new Intent(signinpage.this, SignUppage.class);
            startActivity(intent);
        });

        button_signin.setOnClickListener(view -> {
            if (!validateUsername() | !validatePassword()) {
                // Do nothing if validation fails
            } else {
                checkUser();
            }
        });

        textView_forget.setOnClickListener(view -> {
            Intent intent = new Intent(signinpage.this, forgetpassword.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if the user is already signed in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, redirect to the homepage
            Intent intent = new Intent(signinpage.this, Homepage.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateUsername() {
        String val = editText_email.getText().toString();
        if (val.isEmpty()) {
            editText_email.setError("Username is Empty");
            return false;
        } else {
            editText_email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
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

        changeInProgress(true);

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            changeInProgress(false);
            Intent intent = new Intent(signinpage.this, Homepage.class);
            startActivity(intent);
            finish(); // Prevent the user from returning to the sign-in screen
            Toast.makeText(signinpage.this, "SignIn Successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            changeInProgress(false);
            Toast.makeText(signinpage.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        builder.setTitle("Alert")
                .setMessage("Do you want to close this app?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialogInterface, i) -> finish())
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }
}