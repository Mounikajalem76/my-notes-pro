package com.example.mynotespro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUppage extends AppCompatActivity {
    EditText editText_name,editText_email,editText_mobile,editText_password;

    Button button_signup;

    TextView textView_signin;

    FirebaseAuth firebaseAuth;

    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_uppage);
        editText_name=(EditText) findViewById(R.id.signup_name);
        editText_email=(EditText) findViewById(R.id.signup_email);
        editText_mobile=(EditText) findViewById(R.id.signup_mobile);
        editText_password=(EditText) findViewById(R.id.signup_password);
        progressBar= (ProgressBar) findViewById(R.id.progressbar);

        button_signup=(Button) findViewById(R.id.signup_button);

        textView_signin=(TextView) findViewById(R.id.tvsignin);

        firebaseAuth=FirebaseAuth.getInstance();
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=editText_name.getText().toString();
                String email=editText_email.getText().toString();
                String mobile=editText_mobile.getText().toString();
                String password=editText_password.getText().toString();


                if (name.isEmpty()){
                    editText_name.setError("Please enter name");
                }else if (email.isEmpty()){
                    editText_email.setError("Please enter email id");

                } else if (mobile.isEmpty()) {
                    editText_mobile.setError("Please enter mobile number");

                } else if (password.isEmpty()) {
                    editText_password.setError("please enter password");

                }else {
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                        @Override
                        public void onSuccess(AuthResult authResult) {
                            changeInProgress(true);
                            HelperClass helperClass = new HelperClass (name, email,mobile,password);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference("users").child("user data");
                            databaseReference.child(name).setValue(helperClass);
                            Toast.makeText(SignUppage.this, "SignUp Sucessfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUppage.this, signinpage.class);
                            startActivity(intent);

                        }
                        void changeInProgress(boolean inProgress){
                            if (inProgress){
                                progressBar.setVisibility(View.VISIBLE);
                            }else {
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUppage.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
            }

        }
    });
        textView_signin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(SignUppage.this, signinpage.class);
            startActivity(intent);
        }
    });


    }
}