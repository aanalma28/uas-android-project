package com.example.uasproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasproject.models.Materi;
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;

import java.util.List;

public class MateriAdapter extends RecyclerView.Adapter<MateriAdapter.MateriViewHolder> {
    private final RecycleViewInterface recycleViewInterface;
    private List<Materi> materiList;

    public MateriAdapter(List<Materi> materiList, RecycleViewInterface recycleViewInterface) {
        this.materiList = materiList;
        this.recycleViewInterface = recycleViewInterface;
    }

    @NonNull
    @Override
    public MateriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bab, parent, false);
        return new MateriViewHolder(view, recycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriViewHolder holder, int position) {
        Materi materi = materiList.get(position);
        holder.materinumber.setText("Materi " + (position + 1));
        holder.title.setText(materi.getTitle());
        holder.description.setText(materi.getContent());
    }

    @Override
    public int getItemCount() {
        return materiList.size();
    }

    public static class MateriViewHolder extends RecyclerView.ViewHolder {
        TextView materinumber,title, description;

        public MateriViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            materinumber = itemView.findViewById(R.id.bab);
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
