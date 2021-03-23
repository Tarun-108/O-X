package com.example.o_x;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {


    View view;
    CircleImageView profilePic;
    TextView name,email;
    String picS;

    private FirebaseAuth auth;
    private FirebaseFirestore storage;
    private FirebaseDatabase database;
    private static String uID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        uID = auth.getCurrentUser().getUid();

        name = (TextView) view.findViewById(R.id.tvName);
        email = (TextView) view.findViewById(R.id.tvEmail);
        profilePic = (CircleImageView) view.findViewById(R.id.profilePic);
        FragmentActivity context = getActivity();

        database.getReference().child("Users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());

                //if(!user.getProfileImage().equals("#bugFix_noProfileImage"))
                    Picasso.with(context).load(user.getProfileImage()).placeholder(R.drawable.no_profile).into(profilePic);
                //else  profilePic.setImageResource(R.drawable.no_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

}