package com.kushal.stylista.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kushal.stylista.R;
import com.kushal.stylista.listeners.OnFavoriteClickListener;
import com.kushal.stylista.model.ClothModel;

import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    private static final String TAG = "FavoriteListAdapter";

    private List<ClothModel> favoriteModelList;
    private OnFavoriteClickListener onFavoriteCLickListener;

    public FavoriteListAdapter(List<ClothModel> favoriteModelList, OnFavoriteClickListener onFavoriteCLickListener) {

        this.favoriteModelList = favoriteModelList;
        this.onFavoriteCLickListener = onFavoriteCLickListener;
    }

    public void setFavoriteModelList(List<ClothModel> favoriteModelList) {
        this.favoriteModelList = favoriteModelList;
        notifyDataSetChanged();
    }

    public void removeCloth(ClothModel clothModel) {
        favoriteModelList.remove(clothModel);
        notifyDataSetChanged();
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
        ClothModel favoriteModel = favoriteModelList.get(position);
        holder.bind(favoriteModel, onFavoriteCLickListener);
    }

    @Override
    public int getItemCount() {
        return favoriteModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final ImageView clothImage;
        private final TextView priceTextView;
        private final AppCompatImageButton favoriteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            clothImage = itemView.findViewById(R.id.imageCloth);
            priceTextView = itemView.findViewById(R.id.tvPrice);
            favoriteButton = itemView.findViewById(R.id.btnFavorite);
        }

        public void bind(ClothModel favoriteModel, OnFavoriteClickListener onFavoriteCLickListener) {
            nameTextView.setText(favoriteModel.getName());
            // Set other views as needed
            priceTextView.setText(itemView.getResources().getString(R.string.price, favoriteModel.getPrice()));
            Log.e(TAG, "bind: cloth image" + favoriteModel.getImageUrl());
            Glide.with(itemView.getContext())
                    .load(favoriteModel.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(32))
                    .placeholder(R.drawable.image_placeholder)
                    .fallback(R.drawable.no_image)
                    .into(clothImage);
            itemView.setOnClickListener(v -> onFavoriteCLickListener.onClothClicked(favoriteModel));
            favoriteButton.setOnClickListener(v -> onFavoriteCLickListener.onFavoriteClicked(favoriteModel));
        }
    }
}
