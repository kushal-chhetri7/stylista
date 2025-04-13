package com.kushal.stylista.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.stylista.ApiSaveResponseCallback;
import com.kushal.stylista.exceptions.UnAuthenticatedException;
import com.kushal.stylista.model.CartModel;
import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.utils.FirebaseConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CartRepository {

    private final FirebaseFirestore db;
    private final CollectionReference cartRef;
    private ClothRepository clothRepository;

    public CartRepository() {
        db = FirebaseFirestore.getInstance();
        cartRef = db.collection(FirebaseConstants.COLLECTION_CARTS);
        clothRepository = new ClothRepository();
    }

    public void getCart(ApiListResponseCallback<CartModel> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new UnAuthenticatedException());
            return;
        }

        db.collection(FirebaseConstants.COLLECTION_CARTS).
                whereEqualTo(FirebaseConstants.FIELD_USER_ID, user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> clothes = new HashSet<>();
                    List<CartModel> cartModelList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Map the document to a ClothModel
                        CartModel cartModel = document.toObject(CartModel.class);
                        cartModel.setId(document.getId());
                        clothes.add(cartModel.getClothId());
                        cartModelList.add(cartModel);
                    }
                    clothRepository.fetchClothesByIds(new ArrayList<>(clothes), new ApiListResponseCallback<ClothModel>() {
                        @Override
                        public void onSuccess(List<ClothModel> clothes) {
                            Map<String, ClothModel> clothesMap = new HashMap<>();
                            for (ClothModel clothe : clothes) {
                                clothesMap.put(clothe.getId(), clothe);
                            }
                            for (CartModel cartModel : cartModelList) {
                                cartModel.setClothModel(clothesMap.get(cartModel.getClothId()));
                            }
                            callback.onSuccess(cartModelList);
                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }
                    });
                }).addOnFailureListener(callback::onFailure);
    }

    // Check if item exists in cart and return its document ID and count
    private void fetchCartItem(String userId, String clothId, String color, String size, ApiDataResponseCallback<CartModel> callback) {
        cartRef.whereEqualTo("userId", userId)
                .whereEqualTo("clothId", clothId)
                .whereEqualTo("color", color)
                .whereEqualTo("size", size)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    CartModel cartModel = null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        cartModel = document.toObject(CartModel.class);
                        cartModel.setId(document.getId());
                    }
                    callback.onSuccess(cartModel);

                })
                .addOnFailureListener(callback::onFailure);
    }

    public void addToCart(ClothModel clothModel, int count, String size, String color, ApiSaveResponseCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new UnAuthenticatedException());
            return;
        }

        fetchCartItem(user.getUid(), clothModel.getId(), color, size, new ApiDataResponseCallback<CartModel>() {
            @Override
            public void onSuccess(CartModel item) {

                if (item == null) {
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("userId", user.getUid());
                    cartItem.put("clothId", clothModel.getId());
                    cartItem.put("color", color);
                    cartItem.put("size", size);
                    cartItem.put("count", count);
                    cartRef.add(cartItem)
                            .addOnSuccessListener(doc -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                } else {
                    updateCartItem(item.getId(), item.getCount() + count, callback);
                }

            }

            @Override
            public void onFailure(Exception exception) {
                callback.onFailure(exception);
            }
        });
    }

    // Update existing cart item count
    public void updateCartItem(String docId, int newCount, ApiSaveResponseCallback callback) {
        cartRef.document(docId).update("count", newCount)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteCartItem(String docId, ApiSaveResponseCallback callback) {
        cartRef.document(docId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

}
