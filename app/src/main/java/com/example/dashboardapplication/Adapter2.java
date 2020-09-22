package com.example.dashboardapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter2 extends RecyclerView.Adapter<Adapter2.ViewHolder> {

    private Context context;
    private List<Content2> list;

    public Adapter2(Context context, List<Content2> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Adapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter2.ViewHolder holder, int position) {
        Content2 content2 = list.get(position);
        holder.textView.setText(String.valueOf(content2.getTime()));
        holder.textView2.setText(String.valueOf(content2.getPhone()));
        holder.textView3.setText(String.valueOf(content2.getStatus()));
        holder.textView4.setText(String.valueOf(content2.getDuration()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView, textView2, textView3, textView4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.time);
            textView2 = itemView.findViewById(R.id.phone);
            textView3 = itemView.findViewById(R.id.status);
            textView4 = itemView.findViewById(R.id.duration);
        }
    }
}
