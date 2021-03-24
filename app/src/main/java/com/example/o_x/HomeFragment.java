package com.example.o_x;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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


public class HomeFragment extends Fragment {

    private FirebaseAuth Auth;
    FirebaseDatabase database;
    ArrayList<User> users;
    HomeRecycleViewAdapter HomeRecycleViewAdapter;
    ProgressBar progressBar;
    public HomeFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        database = FirebaseDatabase.getInstance();
        Auth = FirebaseAuth.getInstance();
        String uId = Auth.getCurrentUser().getUid();
        users = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);
        FragmentActivity c = getActivity();
        progressBar.setVisibility(View.VISIBLE);

        HomeRecycleViewAdapter = new HomeRecycleViewAdapter(c,users);

        RecyclerView usersList =(RecyclerView) view.findViewById(R.id.usersList);
        usersList.setLayoutManager(new LinearLayoutManager(c));
        usersList.setAdapter(HomeRecycleViewAdapter);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(user.getUid().equals(uId)){
                        continue;
                    }
                    users.add(user);
                }
                HomeRecycleViewAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}