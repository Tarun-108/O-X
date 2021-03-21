package com.example.o_x;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRecycleViewAdapter extends RecyclerView.Adapter<HomeRecycleViewAdapter.ViewHolder> {

    public static final String tag = "Recycle View Adapter";

    private ArrayList<User> users;
    private Context context;

    public HomeRecycleViewAdapter(Context context, ArrayList<User> users) {
        this.users = users;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.items, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameOf.setText(user.getName());
        if(!user.getProfileImage().equals("#bugFix_noProfileImage"))
            Picasso.with(context).load(user.getProfileImage()).placeholder(R.drawable.no_profile).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView nameOf;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.users_pic);
            nameOf = itemView.findViewById(R.id.users_name);
            card = itemView.findViewById(R.id.card);


        }
    }

}
