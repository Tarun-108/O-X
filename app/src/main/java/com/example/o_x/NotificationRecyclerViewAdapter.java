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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private ArrayList<NotificationHandler> notifications;
    private Context context;

    private String uId = auth.getCurrentUser().getUid();
    private String senderUser;

    public NotificationRecyclerViewAdapter(Context context, ArrayList<NotificationHandler> notifications) {
        this.notifications = notifications;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationHandler notificationHandler = notifications.get(position);
        senderUser = notificationHandler.getSenderUser();
        //User user = users.get(position);
        database.getReference().child("Users").child(senderUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.name.setText(user.getName());
                if(!user.getProfileImage().equals("#bugFix_noProfileImage"))
                Picasso.with(context).load(user.getProfileImage()).placeholder(R.drawable.no_profile).into(holder.image);
                //else  profilePic.setImageResource(R.drawable.no_profile);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView name;
        CardView card;
        Button acceptBtn,rejectBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.users_pic);
            name = itemView.findViewById(R.id.users_name);
            card = itemView.findViewById(R.id.card);
            acceptBtn = itemView.findViewById(R.id.acceptButton);
            rejectBtn = itemView.findViewById(R.id.rejectButton);
        }
    }
}