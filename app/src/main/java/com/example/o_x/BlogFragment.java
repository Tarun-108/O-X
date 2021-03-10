package com.example.o_x;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class BlogFragment extends Fragment {

    View view;
    public BlogFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blog, container, false);
        FragmentActivity context =getActivity();
        RecyclerView blogs = (RecyclerView) view.findViewById(R.id.blog_container);
        blogs.setLayoutManager(new LinearLayoutManager(context));
        String[] titles ={"1","2","3","4","5","6"};
        String[] bodies ={"1","2","3","4","5","6"};
        String[] writers ={"1","2","3","4","5","6"};
        blogs.setAdapter(new BlogRecycleViewAdapter(titles,bodies,writers));
        return view;
    }
}