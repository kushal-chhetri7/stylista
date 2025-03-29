package com.kushal.stylista.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kushal.stylista.R;
import com.kushal.stylista.model.ClothModel;

import java.util.List;

public class SwipeCardStackAdapter extends RecyclerView.Adapter<SwipeCardStackAdapter.ViewHolder> {

    private List<ClothModel> clothModelList;

    public SwipeCardStackAdapter(List<ClothModel> clothModelList) {
        this.clothModelList = clothModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cloth_swipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//       holder.image.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.sample_cloth));
        Glide.with(holder.itemView.getContext())
                .load(clothModelList.get(position).getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(32))
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return clothModelList != null ? clothModelList.size() : 0;
    }

    public void setClothModelList(List<ClothModel> clothModelList) {
        this.clothModelList = clothModelList;
    }

    public List<ClothModel> getClothModelList() {
        return clothModelList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.imageCloth);
        }
    }
}
