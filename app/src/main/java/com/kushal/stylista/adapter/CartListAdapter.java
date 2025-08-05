package com.kushal.stylista.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.kushal.stylista.R;
import com.kushal.stylista.listeners.OnCartListClickListener;
import com.kushal.stylista.model.CartModel;

import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    private List<CartModel> cartModelList;
    private OnCartListClickListener listener;

    public CartListAdapter(List<CartModel> cartModelList, OnCartListClickListener listener) {
        this.cartModelList = cartModelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(cartModelList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public List<CartModel> getCartModelList() {
        return cartModelList;
    }

    public void setCartModelList(List<CartModel> cartModelList) {
        this.cartModelList = cartModelList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartModelList.size()) {
            cartModelList.remove(position);
            notifyItemRemoved(position);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView clothName;
        private TextView tvPrice;
        private TextView tvQuantity;
        private TextView tvTotalPrice;
        private ImageView imageCloth;
        private AppCompatImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clothName = itemView.findViewById(R.id.tvClothName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvItemTotalPrice);
            imageCloth = itemView.findViewById(R.id.imageCloth);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void onBind(CartModel cartModel, OnCartListClickListener listener) {
            clothName.setText(cartModel.getClothModel().getName());
            tvPrice.setText(itemView.getContext().getResources().getString(R.string.price, cartModel.getClothModel().getPrice()));
            tvQuantity.setText(itemView.getContext().getResources().getString(R.string.quantity, cartModel.getCount()));
            tvTotalPrice.setText(itemView.getContext().getResources().getString(R.string.item_total_price, cartModel.getSubtotal()));
            Glide.with(itemView.getContext())
                    .load(cartModel.getClothModel().getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(32))
                    .placeholder(R.drawable.image_placeholder)
                    .fallback(R.drawable.no_image)
                    .into(imageCloth);

            btnDelete.setOnClickListener(v -> listener.onDeletePressed(getBindingAdapterPosition(), cartModel));

        }

    }
}
