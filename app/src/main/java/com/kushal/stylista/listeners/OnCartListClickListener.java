package com.kushal.stylista.listeners;

import com.kushal.stylista.model.CartModel;

public interface OnCartListClickListener {
    void onDeletePressed(int position, CartModel cartModel);
}
