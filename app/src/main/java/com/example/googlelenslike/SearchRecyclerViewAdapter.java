package com.example.googlelenslike;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchRecyclerViewModal> searchRecyclerViewModalArrayList;

    public SearchRecyclerViewAdapter(Context context, ArrayList<SearchRecyclerViewModal> searchRecyclerViewModalArrayList) {
        this.context = context;
        this.searchRecyclerViewModalArrayList = searchRecyclerViewModalArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerViewAdapter.ViewHolder holder, int position) {
        SearchRecyclerViewModal searchRecyclerViewModal = searchRecyclerViewModalArrayList.get(position);
        holder.txtTitle.setText(searchRecyclerViewModal.getTitle());
        holder.txtSnippet.setText(searchRecyclerViewModal.getLink());
        holder.txtDescription.setText(searchRecyclerViewModal.getSnippet());
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(searchRecyclerViewModal.getLink()));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return searchRecyclerViewModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtSnippet, txtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSnippet = itemView.findViewById(R.id.txtSnippet);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
