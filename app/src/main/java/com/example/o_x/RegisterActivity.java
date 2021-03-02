package com.example.o_x;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.o_x.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    private FirebaseAuth Auth;
    private FirebaseDatabase database;
    private FirebaseFirestore db;
    String uID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Auth = FirebaseAuth.getInstance();
        database =  FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = Auth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        binding.tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.editTextName.getText().toString();
                String email = binding.editTextEmailAddress.getText().toString();


                hidesoftkeyboard(v);
                binding.progressBar.setVisibility(View.VISIBLE);


                if(TextUtils.isEmpty(binding.editTextName.getText()) ){
                    binding.editTextName.setError("Enter Your Name");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())){
                    binding.editTextEmailAddress.setError("Email Required");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(TextUtils.isEmpty(binding.editTextPassword.getText())){
                    binding.editTextPassword.setError("Enter password");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if(binding.editTextPassword.getText().toString().length() < 6){
                    binding.editTextPassword.setError("Password can not be smaller than 6 characters.");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                //Registering user
                Auth.createUserWithEmailAndPassword(binding.editTextEmailAddress.getText().toString().trim()
                        ,binding.editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this,"Registration Complete", Toast.LENGTH_SHORT).show();
                            uID = Auth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection("users").document(uID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email",email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Pass","User's Data stored");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Fail", "Unable to create collection "+task.getException().getMessage());
                                }
                            }
                            );
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));  // Error might be in this line but i don't know my mistake.
                            finish();
                        }else{
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this,"Error! "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
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