package com.example.o_x;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1080;
    private static final int GOOGLE_REQUEST_CODE = 1802;
    private static final int ACCESS_GALLERY_CODE = 108;
    Uri profileImg;
    ActivityRegisterBinding binding;

    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
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

        //Set image to profile and requesting to access gallery
        binding.setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for SDK version
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    // permission check
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                        requestPermissions(permissions, PERMISSION_CODE);
                    }else{
                        getImageFromGallery();
                    }
                }else{
                    getImageFromGallery();
                }
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

                if(TextUtils.isEmpty(binding.editTextEmailAddress.getText().toString().trim())){
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

                            FirebaseUser vUser = Auth.getCurrentUser();
                            vUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("onFailure",""+e.getMessage());
                                }
                            });


                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this,"Registration Complete", Toast.LENGTH_SHORT).show();
                            uID = Auth.getCurrentUser().getUid();

                            // to store image on storage
                            StorageReference reference = storage.getReference().child("Profiles").child(Auth.getUid());
                            if(profileImg != null){
                                reference.putFile(profileImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isSuccessful()){
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageProfileUri = uri.toString();
                                                    User userRegistering = new User(uID,name,email,imageProfileUri);
                                                    database.getReference().child("Users").child(uID).setValue(userRegistering)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Pass","User's Data stored");
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }else{
                                User userRegistering = new User(uID,name,email,"#bugFix_noProfileImage");
                                database.getReference().child("Users").child(uID).setValue(userRegistering).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Pass","User's Data stored");
                                    }
                                });
                            }



                            if(!vUser.isEmailVerified()){
                                Toast.makeText(RegisterActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                //binding.textViewTitle.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();
                                    }
                                },2000);
                            }
                        }else{
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this,"Error! "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

    // to get a photo from gallery
    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,ACCESS_GALLERY_CODE);
    }

    @Override
    // checking the result of requested permission
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getImageFromGallery();
            }else{
                Toast.makeText(this, "     Permission denied \n Gallery can't be accessed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // to signIn into an account through google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_REQUEST_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("pass", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException error) {
                Log.d("fail", error.getMessage());
            }
        }
        if(resultCode == RESULT_OK && requestCode == ACCESS_GALLERY_CODE && data != null){
            if(data.getData()!=null){
                binding.profilePicRegister.setImageURI(data.getData());
                profileImg = data.getData();
            }
        }
    }

    // for google authentication
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
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

    //to save data fetched from google account of registered user
    private void saveData() {
        GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
        if (user != null) {
            String name = user.getDisplayName();
            String email =user.getEmail();
            String imageProfileUri = user.getPhotoUrl().toString();
            uID = Auth.getCurrentUser().getUid();
            User userRegistering = new User(uID,name,email,imageProfileUri);
            database.getReference().child("Users").child(uID).setValue(userRegistering).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Pass","User's Data stored");
                }
            });
        }
    }

    // requesting google for signIn
    private void request(){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

    }

    // method to hide soft keyboard
    public void hidesoftkeyboard(View view) {

        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0 );
    }
}
/*
DocumentReference documentReference = db.collection("users").document(uID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email",email);
                            user.put("Photo","#bugFix");
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
 */