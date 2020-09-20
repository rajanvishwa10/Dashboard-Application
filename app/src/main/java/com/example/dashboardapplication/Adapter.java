package com.example.dashboardapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context context;
    private List<Content> list;
    private RecyclerViewOnClickListener listener;

    public Adapter(Context context, List<Content> list, RecyclerViewOnClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Content content = list.get(position);
        holder.textView.setText(String.valueOf(content.getName()));
        holder.textView2.setText(String.valueOf(content.getDate()));
        holder.textView3.setText(String.valueOf(content.getTime()));
        holder.textView4.setText(String.valueOf(content.getPhone()));
        holder.textView5.setText(String.valueOf(content.getStatus()));
        holder.textView6.setText(String.valueOf(content.getFirstcall()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface RecyclerViewOnClickListener {
        void onClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView, textView2, textView3, textView4, textView5, textView6;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.agentName);
            textView2 = itemView.findViewById(R.id.totalDuration);
            textView3 = itemView.findViewById(R.id.totalCall);
            textView4 = itemView.findViewById(R.id.uniqueCall);
            textView5 = itemView.findViewById(R.id.lastCall);
            textView6 = itemView.findViewById(R.id.firstcall);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
    public void filterlist(List<Content> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }
}
