package com.kushal.stylista.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.stylista.exceptions.UnAuthenticatedException;
import com.kushal.stylista.model.OrderModel;
import com.kushal.stylista.utils.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final FirebaseFirestore db;

    private final CollectionReference orderRef;

    public OrderRepository() {
        db = FirebaseFirestore.getInstance();
        orderRef = db.collection("orders");
    }

    public void fetchOrders(ApiListResponseCallback<OrderModel> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new UnAuthenticatedException());
            return;
        }

        orderRef.whereEqualTo(FirebaseConstants.FIELD_USER_ID, user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderModel> orderModelList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        OrderModel orderModel = OrderModel.fromDocumentSnapshot(document);
                        orderModelList.add(orderModel);
                    }
                    callback.onSuccess(orderModelList);
                }).addOnFailureListener(callback::onFailure);

    }
}
