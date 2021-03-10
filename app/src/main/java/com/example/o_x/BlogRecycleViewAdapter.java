package com.example.o_x;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class BlogRecycleViewAdapter extends RecyclerView.Adapter<BlogRecycleViewAdapter.ViewHolder> {

    private String[] titles,bodies,writers;

    public BlogRecycleViewAdapter(String[] titles, String[] bodies, String[] writers) {
        this.titles = titles;
        this.bodies = bodies;
        this.writers = writers;
    }


    @Override
    public BlogRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.blog_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BlogRecycleViewAdapter.ViewHolder holder, int position) {
        String title,body,writer;
        title = titles[position];
        body = bodies[position];
        writer = writers[position];
        holder.ftitle.setText(title);
        holder.fbody.setText(body);
        holder.fwriter.setText(writer);
    }

    @Override
    public int getItemCount() {
        return titles.length;
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
