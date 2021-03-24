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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class BlogFragment extends Fragment {

    // 454dp
    View view;

    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = Auth.getCurrentUser();

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
        FragmentActivity context = getActivity();
        RecyclerView blogs = (RecyclerView) view.findViewById(R.id.blog_container);
        blogs.setLayoutManager(new LinearLayoutManager(context));
        String[] titles ={"1","2","3","4","5","6"};
        String[] bodies ={"1","2","3","4","5","6"};
        String[] writers ={"1","2","3","4","5","6"};
        blogs.setAdapter(new BlogRecycleViewAdapter(titles,bodies,writers));

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
                            System.out.println("hi hi hi hi hi");
                            if(TextUtils.isEmpty(title.getText())){
                                title.setError("Title required");
                                return;
                            }
                            if(TextUtils.isEmpty(body.getText())){
                                body.setError("Body required");
                                return;
                            }
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