package com.example.notetakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupLoginActivity extends AppCompatActivity {

    DatabaseReference userDatabase;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_login);
        getSupportActionBar().hide();
        userDatabase=FirebaseDatabase.getInstance().getReference("users");
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        checkAlreadyLoggedIn();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().toString().isEmpty()){
                    usernameEditText.setError("Please Enter Email");
                    usernameEditText.requestFocus();
                    return;
                }
                if(passwordEditText.getText().toString().isEmpty()){
                    passwordEditText.setError("Please Enter Password");
                    passwordEditText.requestFocus();
                    return;
                }
                loadingProgressBar.setVisibility(View.VISIBLE);
                ProcessLogin(usernameEditText.getText().toString(),passwordEditText.getText().toString());
            }
        });
    }

    private void checkAlreadyLoggedIn() {
        if(StaticUtils.getUserEmail(SignupLoginActivity.this)!=null){
            startActivity(new Intent(SignupLoginActivity.this,MainActivity.class));
            finish();
        }
    }

    private void ProcessLogin(final String email, final String password) {
        userDatabase.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String username1=dataSnapshot.child("email").getValue(String.class);
                    String password1=dataSnapshot.child("password").getValue(String.class);
                    String created=dataSnapshot.child("created_at").getValue(String.class);
                    if(password1.equalsIgnoreCase(password)){
                        loadingProgressBar.setVisibility(View.GONE);
                        StaticUtils.StoreLoggedEmail(SignupLoginActivity.this,email);
                        startActivity(new Intent(SignupLoginActivity.this,MainActivity.class));
                        finish();

                    }
                    else{
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupLoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    CreateNewUser(email,password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CreateNewUser(String email,String password) {

        String id=userDatabase.push().getKey();

        UserModel userModel=new UserModel(id,email,password);

        userDatabase.child(email).setValue(userModel);
        StaticUtils.StoreLoggedEmail(SignupLoginActivity.this,email);
        startActivity(new Intent(SignupLoginActivity.this,MainActivity.class));
        finish();
    }


}