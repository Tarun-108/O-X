package com.example.o_x;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// <a href='https://pngtree.com/free-backgrounds'>free background photos from pngtree.com</a>

public class MainActivity extends AppCompatActivity {



    private  FirebaseAuth auth;
    private FirebaseDatabase database;
    private static String uID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth =FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uID = auth.getCurrentUser().getUid();

        BottomNavigationView bottomnav = findViewById(R.id.nav_view);
        bottomnav.setOnNavigationItemSelectedListener(navlistener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();


    }
    final boolean keepRunning = true;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 1000);
                database.getReference().child("Game Request").child(uID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                NotificationHandler request_type = snapshot1.getValue(NotificationHandler.class);
                                String request = request_type.getRequestType();
                                if (request.equals("your request accepted") || request.equals("you accepted request")) {
                                    Game data = new Game(request_type.getSenderUser(),request_type.getReceiverUser(),"x","o");
                                    database.getReference().child("Game").child(request_type.getSenderUser()+" "+request_type.getReceiverUser())
                                            .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                                                intent.putExtra("GameId",request_type.getSenderUser()+" "+request_type.getReceiverUser());
                                                intent.putExtra("turn",request_type.getSenderUser());
                                                if(request.equals("your request accepted")) intent.putExtra("player1",request_type.getReceiverUser());
                                                if (request.equals("you accepted request")) intent.putExtra("player1",request_type.getSenderUser());
                                                startActivity(intent);
                                                finish();
                                            }
                                            Log.d("Game","Room created");
                                        }
                                    });
                                    //System.out.println(request_type.getSenderUser()+" "+request_type.getReceiverUser());

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, 1000);
        super.onResume();
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_blog:
                            selectedFragment = new BlogFragment();
                            break;
                        case R.id.navigation_notifications:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    public void signOut(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        Toast.makeText(MainActivity.this, "You are logged out", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
    }
    public void hidesoftkeyboard(View view) {

        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0 );
    }
    public static void makeToast(View view,Context c,String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

}