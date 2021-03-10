package com.example.o_x;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeRecycleViewAdapter extends RecyclerView.Adapter<HomeRecycleViewAdapter.ViewHolder> {

    public static final String tag = "Recycle View Adapter";

    private String[] namesUsers;
    //private Context mContext;

    public HomeRecycleViewAdapter(String[] namesUsers) {
        this.namesUsers = namesUsers;
        //this.mContext = mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.items, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        String nameX = namesUsers[position];
        holder.nameOf.setText(nameX);
    }

    @Override
    public int getItemCount() {
        return namesUsers.length;
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
