package com.example.o_x;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.o_x.databinding.ActivityGameBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class GameActivity extends AppCompatActivity {


    ActivityGameBinding binding ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String player2 = auth.getCurrentUser().getUid();
//    String GameId = getIntent().getStringExtra("GameId");
  //  String player1 = getIntent().getStringExtra("player1");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
       // System.out.println(GameId);

    }

   /* public void cancel_game(View view) {
        database.getReference().child("Game Request").child(player1).child(player2)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    database.getReference().child("Game Request").child(player2).child(player1)
                            .removeValue();

                    database.getReference().child("Game").child(GameId).removeValue();
                }
            }
        });
    }*/
}