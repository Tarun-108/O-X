package com.example.o_x;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private static String uID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseFirestore.getInstance();

        uID = auth.getCurrentUser().getUid();

        name = (TextView) view.findViewById(R.id.tvName);
        email = (TextView) view.findViewById(R.id.tvEmail);
        profilePic = (CircleImageView) view.findViewById(R.id.profilePic);
        FragmentActivity context = getActivity();

        DocumentReference docRef = storage.collection("users").document(uID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //User user = documentSnapshot.toObject(User.class);
                name.setText(documentSnapshot.getString("Name"));
                email.setText(documentSnapshot.getString("Email"));
                picS = documentSnapshot.getString("Photo");
                profilePic.setVisibility(View.VISIBLE);
                if(!picS.equals("#bugFix")){
                    Picasso.with(context).load(picS).placeholder(R.drawable.no_profile).into(profilePic);
                }
                //Uri picUri = Uri.parse(picS);
                //profilePic.setImageURI(picUri);
                //profilePic.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

}