package com.example.o_x;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.o_x.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    private FirebaseAuth mAuth;
    //private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        //database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        binding.tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });
        binding.textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())){
                    binding.editTextEmailAddress.setError("Email Required");
                    return;
                }
                mAuth.sendPasswordResetEmail(binding.editTextEmailAddress.getText().toString().trim()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Reset mail sent", Toast.LENGTH_SHORT).show();
                        }else{
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Error! please retry again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidesoftkeyboard(v);
                binding.progressBar.setVisibility(View.VISIBLE);

                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())){
                    binding.editTextEmailAddress.setError("Email Required");
                    return;
                }
                if(TextUtils.isEmpty(binding.editTextTextPassword.getText().toString())){
                    binding.editTextTextPassword.setError("Enter Password");
                    return;
                }

                //Login User
                mAuth.signInWithEmailAndPassword(binding.editTextEmailAddress.getText().toString().trim(),
                        binding.editTextTextPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this,"Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    public void hidesoftkeyboard(View view) {

        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0 );
    }
}