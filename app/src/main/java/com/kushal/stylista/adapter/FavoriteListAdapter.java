package com.kushal.stylista.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushal.stylista.R;
import com.kushal.stylista.model.FavoriteModel;

import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    private List<FavoriteModel> favoriteModelList;

    public FavoriteListAdapter(List<FavoriteModel> favoriteModelList){
        this.favoriteModelList = favoriteModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteModel favoriteModel = favoriteModelList.get(position);
        holder.bind(favoriteModel);
    }

    @Override
    public int getItemCount() {
        return favoriteModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final ImageView clothImage;
        private final TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            clothImage = itemView.findViewById(R.id.imageCloth);
            priceTextView = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(FavoriteModel favoriteModel) {
            nameTextView.setText(favoriteModel.getTitle());
            // Set other views as needed
            priceTextView.setText("$"+favoriteModel.getPrice());
            clothImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.sample_cloth));
        }
    }
}
