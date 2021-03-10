package com.example.o_x;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.o_x.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String uID;
    private FirebaseFirestore db;
    //private FirebaseDatabase database;

    //fxn to check whether the user is currently logged In or not
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        if(currentUser != null && !currentUser.isEmailVerified()){
            binding.textViewTitle.setVisibility(View.VISIBLE);
        }
    }

    //the main fxn
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Instance creation
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //database = FirebaseDatabase.getInstance();

        //calling fxn that create request to be sent to google
        request();

        //to switch to register activity
        binding.tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });

        //to use google login
        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                signIn();

            }
        });

        //forgot password ?
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

        //Login button (allowing user to login)
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidesoftkeyboard(v);
                binding.progressBar.setVisibility(View.VISIBLE);

                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())){
                    binding.editTextEmailAddress.setError("Email Required");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                if(TextUtils.isEmpty(binding.editTextTextPassword.getText().toString())){
                    binding.editTextTextPassword.setError("Enter Password");
                    binding.progressBar.setVisibility(View.INVISIBLE);
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
                            FirebaseUser User = mAuth.getCurrentUser();
                            if(User.isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }else{
                                binding.textViewTitle.setVisibility(View.VISIBLE);
                                return;
                            }
                        }else {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }

    //calling to show intent for user with list of his/her mail
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1802);
    }

    //logging In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1802){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("pass", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException error) {
                Log.d("fail", error.getMessage());
            }
        }
    }

    //Authentication
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));  // Error might be in this line but i don't know my mistake.
                            finish();
                            saveData();
                        } else {
                            Log.d("fail_to_Auth", "e "+task.getException().getMessage());
                        }
                    }
                });
    }

    //Saving user's data on fire-store
    private void saveData() {
        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
        if (user != null) {
            String name = user.getDisplayName();
            String Email =user.getEmail();
            //Uri picsrc = user.getPhotoUrl();
            uID = mAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("users").document(uID);
            Map<String,Object> User = new HashMap<>();
            User.put("Name", name);
            User.put("Email",Email);
            //User.put("Photo",picsrc);
            documentReference.set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Pass","User's Data stored");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("failToStoreData","Fail "+e.getMessage());
                }
            });
        }



    }

    //Creating request
    private void request(){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

    }

    //fxn to hide softKeyboard
    public void hidesoftkeyboard(View view) {

        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0 );
    }

}