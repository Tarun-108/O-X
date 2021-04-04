package com.example.o_x;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    private FirebaseAuth Auth;
    FirebaseDatabase database;
    ArrayList<NotificationHandler> notifications;
    NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;
    ProgressBar progressBar;
    TextView no_notification;

    public NotificationFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        database = FirebaseDatabase.getInstance();
        Auth = FirebaseAuth.getInstance();
        String uId = Auth.getCurrentUser().getUid();
        notifications = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);
        no_notification = view.findViewById(R.id.no_notification);
        FragmentActivity context = getActivity();
        progressBar.setVisibility(View.VISIBLE);
        
        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(context, notifications);

        RecyclerView notificationsList =(RecyclerView) view.findViewById(R.id.notificationList);
        notificationsList.setLayoutManager(new LinearLayoutManager(context));
        notificationsList.setAdapter(notificationRecyclerViewAdapter);


        database.getReference().child("Game Request").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                notifications.clear();
                System.out.println(snapshot.exists());
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        NotificationHandler notification = snapshot1.getValue(NotificationHandler.class);
                        String status = notification.getRequestType();
                        if (status.equals("received")) {
                            notifications.add(notification);
                        }
                    }
                }
                notificationRecyclerViewAdapter.notifyDataSetChanged();
                if(notifications.size() == 0){
                    no_notification.setVisibility(View.VISIBLE);
                }else no_notification.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}