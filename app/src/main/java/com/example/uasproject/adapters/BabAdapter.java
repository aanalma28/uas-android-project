package com.example.uasproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasproject.models.Bab;
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;

import java.util.List;

public class BabAdapter extends RecyclerView.Adapter<BabAdapter.BabViewHolder> {
    private final RecycleViewInterface recycleViewInterface;
    private List<Bab> babList;

    public BabAdapter(List<Bab> babList, RecycleViewInterface recycleViewInterface) {
        this.babList = babList;
        this.recycleViewInterface = recycleViewInterface;
    }

    @NonNull
    @Override
    public BabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bab, parent, false);
        return new BabViewHolder(view, recycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull BabViewHolder holder, int position) {
        Bab bab = babList.get(position);
        holder.babnumber.setText("Bab " + (position + 1));
        holder.title.setText(bab.getName());
        holder.description.setText(bab.getDetail());
    }

    @Override
    public int getItemCount() {
        return babList.size();
    }

    public static class BabViewHolder extends RecyclerView.ViewHolder {
        TextView babnumber,title, description;

        public BabViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            babnumber = itemView.findViewById(R.id.bab);
            title = itemView.findViewById(R.id.title_bab);
            description = itemView.findViewById(R.id.desc_bab);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recycleViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recycleViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
