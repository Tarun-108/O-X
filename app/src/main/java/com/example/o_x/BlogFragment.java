package com.example.o_x;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BlogFragment extends Fragment {

    // 454dp
    View view;

    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser currentUser = Auth.getCurrentUser();

    String uId = currentUser.getUid();
    String random;

    ArrayList<Blog> blog;
    BlogRecycleViewAdapter BlogRecycleViewAdapter;

    TextView to_write;
    LinearLayout write;
    Button post,cancel;
    EditText title,body;

    public BlogFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blog, container, false);

        blog = new ArrayList<>();

        FragmentActivity context = getActivity();


        BlogRecycleViewAdapter = new BlogRecycleViewAdapter(context,blog);

        RecyclerView blogList =(RecyclerView) view.findViewById(R.id.blog_container);
        blogList.setLayoutManager(new LinearLayoutManager(context));
        blogList.setAdapter(BlogRecycleViewAdapter);


        database.getReference().child("Blogs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                blog.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Blog blogStored = snapshot1.getValue(Blog.class);
                    blog.add(blogStored);
                }
                BlogRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        to_write = (TextView) view.findViewById(R.id.to_write);
        write = (LinearLayout) view.findViewById(R.id.write_blog);
        cancel = (Button) view.findViewById(R.id.cancel_blog_button);
        post = (Button) view.findViewById(R.id.post_blog_button);
        title = (EditText) view.findViewById(R.id.editTextTitle);
        body = (EditText) view.findViewById(R.id.editTextBody);

        to_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser.isEmailVerified()){
                    to_write.setVisibility(View.GONE);
                    write.setVisibility(View.VISIBLE);
                    ((MainActivity)getActivity()).hidesoftkeyboard(v);

                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.isEmpty(title.getText())){
                                title.setError("Title required");
                                return;
                            }
                            if(TextUtils.isEmpty(body.getText())){
                                body.setError("Body required");
                                return;
                            }
                            database.getReference().child("Users").child(uId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange( DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    String username = user.getName();
                                    random = String.valueOf((int)(Math.random()*10000000));
                                    Blog blog = new Blog(title.getText().toString(),body.getText().toString(),username);
                                    database.getReference().child("Blogs")
                                            .child(uId+random).setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    to_write.setVisibility(View.VISIBLE);
                                                    write.setVisibility(View.GONE);
                                                    ((MainActivity)getActivity()).hidesoftkeyboard(v);
                                                    title.getText().clear();
                                                    body.getText().clear();
                                                }
                                            });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    });

                }else{
                    MainActivity.makeToast(v,context,"PlEASE VERIFY YOUR EMAIL TO USE THIS FEATURE");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_write.setVisibility(View.VISIBLE);
                write.setVisibility(View.GONE);
                ((MainActivity)getActivity()).hidesoftkeyboard(v);
            }
        });

        return view;
    }

}