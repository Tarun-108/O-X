package com.example.o_x;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.o_x.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    private FirebaseAuth Auth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Auth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance();

        FirebaseUser currentUser = Auth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hidesoftkeyboard(v);
                binding.progressBar.setVisibility(View.VISIBLE);


                if(TextUtils.isEmpty(binding.editTextName.getText()) ){
                    binding.editTextName.setError("Username Required");
                    return;
                }

                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())){
                    binding.editTextEmailAddress.setError("Email Required");
                    return;
                }

                if(TextUtils.isEmpty(binding.editTextPassword.getText())){
                    binding.editTextPassword.setError("Enter password");
                    return;
                }

                if(binding.editTextPassword.getText().toString().length() < 6){
                    binding.editTextPassword.setError("Password can not be smaller than 6 characters.");
                    return;
                }

                //Registering user

                Auth.createUserWithEmailAndPassword(binding.editTextEmailAddress.getText().toString().trim()
                        ,binding.editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Registration Complete", Toast.LENGTH_SHORT);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this,"Error! "+ task.getException().getMessage(),Toast.LENGTH_SHORT);
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