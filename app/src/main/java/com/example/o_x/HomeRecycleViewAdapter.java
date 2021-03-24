package com.example.o_x;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRecycleViewAdapter extends RecyclerView.Adapter<HomeRecycleViewAdapter.ViewHolder> {

    public static final String tag = "Recycle View Adapter";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ArrayList<User> users;
    private Context context;

    private String senderUser = auth.getCurrentUser().getUid();
    private String receiverUser;

    public HomeRecycleViewAdapter(Context context, ArrayList<User> users) {
        this.users = users;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_items, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        User user = users.get(position);
        receiverUser = user.getUid();
        //System.out.println(receiverUser);
        holder.nameOf.setText(user.getName());
        if(!user.getProfileImage().equals("#bugFix_noProfileImage"))
            Picasso.with(context).load(user.getProfileImage()).placeholder(R.drawable.no_profile).into(holder.image);

        final String[] Button_Text = {"SEND REQUEST"};

        // to handle Buttons
        database.getReference().child("Game Request").child(senderUser).child(receiverUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    NotificationHandler buttonHandling = snapshot.getValue(NotificationHandler.class);
                    String status = buttonHandling.getRequestType();
                    if (status.equals("sent")) {
                        holder.request.setText("CANCEL REQUEST");
                        Button_Text[0] = "CANCEL REQUEST";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiverUser = user.getUid();
                MainActivity main = new MainActivity();
                if(Button_Text[0].equals("CANCEL REQUEST")) {
                    //MainActivity.makeToast(v, context, "Request cancelled");

                    database.getReference().child("Game Request").child(senderUser).child(receiverUser)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful()){
                                database.getReference().child("Game Request").child(receiverUser).child(senderUser)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            holder.request.setText("SEND REQUEST");
                                            Button_Text[0]="SEND REQUEST";
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    //MainActivity.makeToast(v, context, "Request sent");
                    database.getReference().child("Game Request").child(senderUser).child(receiverUser)
                            .setValue(new NotificationHandler(senderUser,receiverUser,"sent")).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful()){
                                database.getReference().child("Game Request").child(receiverUser).child(senderUser)
                                        .setValue(new NotificationHandler(senderUser,receiverUser,"received")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            holder.request.setText("CANCEL REQUEST");
                                            Button_Text[0] = "CANCEL REQUEST";
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView nameOf;
        CardView card;
        Button request;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.users_pic);
            nameOf = itemView.findViewById(R.id.users_name);
            card = itemView.findViewById(R.id.card);
            request = itemView.findViewById(R.id.request);
        }
    }

}
