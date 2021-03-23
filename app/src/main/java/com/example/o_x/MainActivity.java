package com.example.o_x;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.o_x.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

// <a href='https://pngtree.com/free-backgrounds'>free background photos from pngtree.com</a>

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomnav = findViewById(R.id.nav_view);
        bottomnav.setOnNavigationItemSelectedListener(navlistener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();



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