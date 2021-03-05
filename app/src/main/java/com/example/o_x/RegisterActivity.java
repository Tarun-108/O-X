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

import com.example.o_x.databinding.ActivityRegisterBinding;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uID;
    private GoogleSignInClient mGoogleSignInClient;


    //checking for whether the user is already registered or not
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = Auth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }

    // main fxn
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //creating binding object
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //creating a request for email to be sent to google
        request();

        //Switch to login activity
        binding.tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        //button for google registration
        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });

        //button for email password registration
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

                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())
                        || binding.editTextEmailAddress.getText().toString().trim().contains("@gmail.com") != true){
                    binding.editTextEmailAddress.setError("Valid Email Required");
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1802);
    }
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = Auth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));  // Error might be in this line but i don't know my mistake.
                            finish();
                            saveData();
                        } else {
                            Log.d("fail_to_Auth", "e "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveData() {
        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
        if (user != null) {
            String name = user.getDisplayName();
            String Email =user.getEmail();
            uID = Auth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("users").document(uID);
            Map<String,Object> User = new HashMap<>();
            User.put("Name", name);
            User.put("Email",Email);
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

    private void request(){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

    }

    public void hidesoftkeyboard(View view) {

        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0 );
    }
}