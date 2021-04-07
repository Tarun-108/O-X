package com.example.o_x;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    static FirebaseAuth auth = FirebaseAuth.getInstance();
    final static String player2 = auth.getCurrentUser().getUid();
    static String player1;
    static String GameId;

    FloatingActionButton toHome;

    ImageView sign1,sign2;
    TextView player1name,player2name;
    TextView player1scoretextview,player2scoretextview;

    final static String[] player1Sign = new String[1];
    final static String[] player2Sign = new String[1];

    private static TextView cell[][] = new TextView[3][3];
    private static String turn ;
    private int player1points,player2points;
    private static int rcount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        GameId = getIntent().getStringExtra("GameId");
        //System.out.println(GameId);


        turn = getIntent().getStringExtra("turn");
        toHome = findViewById(R.id.toHome);

        player1 = getIntent().getStringExtra("player1");


        game(GameId,player1,player2);


        database.getReference().child("Game").child(GameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(GameActivity.this, "Another player left the game", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player1scoretextview = findViewById(R.id.player1scoretv);
        player2scoretextview = findViewById(R.id.player2scoretv);



        // display names of players
        player1name = findViewById(R.id.player1);
        player2name = findViewById(R.id.player2);
        //player 1 name display
        database.getReference().child("Users").child(player1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                player1name.setText(user1.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //player 2 name display
        database.getReference().child("Users").child(player2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user2 = snapshot.getValue(User.class);
                player2name.setText(user2.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        // to leave to main activity
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Game Request").child(player1).child(player2)
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            database.getReference().child("Game Request").child(player2).child(player1)
                                    .removeValue();

                            firebaseFirestore.collection("Game Board").document(GameId).delete();
                            database.getReference().child("Game").child(GameId).removeValue();
                            Toast.makeText(getApplicationContext(), "You left Game",Toast.LENGTH_SHORT);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }
                });

            }
        });
    }


    private void game(String gameId, String player1, String player2) {

        // to allot signs
        database.getReference().child("Game").child(gameId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Game game = snapshot.getValue(Game.class);
                turn = game.getPlayer1Id();
                if(game.getPlayer1Id().equals(player1)){
                    player1Sign[0] = game.player1sign;
                    player2Sign[0] = game.player2sign;
                }
                else {
                    player1Sign[0] = game.player2sign;
                    player2Sign[0] = game.player1sign;
                }
                showSign(player1Sign,player2Sign);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                String cellId = "cell_"+ i + j ;
                int resId = getResources().getIdentifier(cellId,"id",getPackageName());
                cell[i][j] = findViewById(resId);
                cell[i][j].setOnClickListener(this);
            }
        }


        DocumentReference documentReference = firebaseFirestore.collection("Game Board").document(GameId);
        Map<String,Object> game = new HashMap<>();
        game.put("turn",turn);
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                game.put(cell[i][j].getId()+"","");
            }
        }
        game.put("roundcount",rcount+"");
        game.put(player1+" points",player1points+"");
        game.put(player2+" points",player2points+"");
        documentReference.set(game);

    }

    private void showSign(String[] player1Sign, String[] player2Sign) {
        sign1 = findViewById(R.id.sign1);
        sign2 = findViewById(R.id.sign2);
        //System.out.println(player1Sign[0]);
        //System.out.println(player2Sign[0]);
        if(player1Sign[0].equals("x")) sign1.setImageResource(R.drawable.x);
        else if(player1Sign[0].equals("o")) sign1.setImageResource(R.drawable.o);
        if(player2Sign[0].equals("x")) sign2.setImageResource(R.drawable.x);
        else if(player2Sign[0].equals("o")) sign2.setImageResource(R.drawable.o);
    }




    final boolean keepRunning = true;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 100);
                DocumentReference documentReference = firebaseFirestore.collection("Game Board").document(GameId);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        turn = documentSnapshot.getString("turn");

                        //if(player1scoretextview.getText().toString().equals(documentSnapshot.getString(player1+" points")) &&
                          //      player2scoretextview.getText().toString().equals(documentSnapshot.getString(player2+" points")))
                            player1scoretextview.setText(documentSnapshot.getString(player1+" points"));
                            player2scoretextview.setText(documentSnapshot.getString(player2+" points"));

                        for(int i=0;i<3;i++){
                            for(int j=0;j<3;j++){
                                String cellId = "cell_"+ i + j ;
                                int resId = getResources().getIdentifier(cellId,"id",getPackageName());
                                cell[i][j] = findViewById(resId);
                                String storageId = cell[i][j].getId()+"";
                                cell[i][j].setText(documentSnapshot.getString(storageId));
                            }
                        }
                    }
                });
            }
        }, 100);
        super.onResume();
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }





    @Override
    public void onClick(View v) {
        DocumentReference documentReference = firebaseFirestore.collection("Game Board").document(GameId);

        if(!((TextView) v).getText().toString().equals(""))
        {
            return;
        }

        if(player2.equals(turn)){
            ((TextView) v).setText(player2Sign[0]);
            turn = player1;
            documentReference.update("turn",turn);
            String storageId = ((TextView) v).getId()+"";
            documentReference.update(storageId,player2Sign[0]);

            // Round Counting
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    rcount = Integer.parseInt(documentSnapshot.getString("roundcount"));
                    rcount++;
                    if(rcount == 9){
                        draw();
                        rcount = 0;
                        reset();
                    }
                    documentReference.update("roundcount",rcount+"");
                }
            });
        }else{
            Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show();
        }


        if(checkWin()){
            if(turn.equals(player1)){
                win();
            }else{
                Toast.makeText(getApplicationContext(),"you lose",Toast.LENGTH_SHORT);
            }
            reset();
        }else if(rcount == 9){
            draw();
            reset();
        }
    }

    private void draw() {
        Toast.makeText(this, "match draw", Toast.LENGTH_SHORT).show();
    }

    private void reset() {
        DocumentReference documentReference = firebaseFirestore.collection("Game Board").document(GameId);
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                documentReference.update(cell[i][j].getId()+"","");
            }
        }
        rcount = 0;
        documentReference.update("roundcount","-1");
    }

    private void win() {
        DocumentReference documentReference = firebaseFirestore.collection("Game Board").document(GameId);
        database.getReference().child("Users").child(player2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Toast.makeText(GameActivity.this, user.getName()+" WON !!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //assigning points
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                player2points = Integer.parseInt(documentSnapshot.getString(player2+" points"));
                player2points++;
                documentReference.update(player2+" points",player2points+"");
            }
        });
    }

    private boolean checkWin() {
        String[][] field = new String[3][3];

        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                field[i][j] = cell[i][j].getText().toString();
            }
        }


        for (int i=0;i<3;i++){
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")){
                return true;
            }
        }

        for (int i=0;i<3;i++){
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")){
                return true;
            }
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")){
            return true;
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")){
            return true;
        }



        return false;
    }

}