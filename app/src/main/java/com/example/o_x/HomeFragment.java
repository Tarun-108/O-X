package com.example.o_x;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeFragment extends Fragment {


    public HomeFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FragmentActivity c = getActivity();
        RecyclerView usersList =(RecyclerView) view.findViewById(R.id.usersList);
        usersList.setLayoutManager(new LinearLayoutManager(c));
        String[] names =  {"Tarun","Hasnain","Shivansh","Rahul","Bhavya","Tanishq","Uday","Sumit"};
        usersList.setAdapter(new HomeRecycleViewAdapter(names));
        return view;
    }
}