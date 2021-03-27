package com.example.o_x;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BlogRecycleViewAdapter extends RecyclerView.Adapter<BlogRecycleViewAdapter.ViewHolder> {

    private ArrayList<Blog> blogStored = new ArrayList<>();
    private Context context;

    public BlogRecycleViewAdapter(Context context, ArrayList<Blog> blogStored) {
        this.context = context;
        this.blogStored = blogStored;
    }


    @Override
    public BlogRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.blog_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BlogRecycleViewAdapter.ViewHolder holder, int position) {
        Blog blogstored = blogStored.get(position);
        holder.ftitle.setText(blogstored.getTitle());
        holder.fbody.setText(blogstored.getBody());
        holder.fwriter.setText("- "+blogstored.getWriter());
    }

    @Override
    public int getItemCount() {
        return blogStored.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ftitle,fbody,fwriter;
        public ViewHolder(View itemView) {
            super(itemView);
            ftitle =itemView.findViewById(R.id.blog_title);
            fbody =itemView.findViewById(R.id.blog_body);
            fwriter =itemView.findViewById(R.id.written_by);
        }
    }
}
