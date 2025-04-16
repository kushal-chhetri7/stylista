package com.kushal.stylista.services;

import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.stylista.LoginActivity;
import com.kushal.stylista.MainActivity;
import com.kushal.stylista.model.UserModel;
import com.kushal.stylista.utils.FirebaseConstants;

public class UserRepository {

    private final CollectionReference usersCollection;
    private static UserModel userModel;

    public UserRepository() {
        usersCollection = FirebaseFirestore.getInstance().collection(FirebaseConstants.COLLECTION_USERS);
    }

    public void loginUserWithEmailAndPassword(String email, String password, ApiDataResponseCallback<UserModel> callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getCurrentUser(callback);
                    } else {
                        callback.onFailure(new Exception("user cannot login"));
                    }
                }).addOnFailureListener(callback::onFailure);
    }

    public void getCurrentUser(ApiDataResponseCallback<UserModel> callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        if (userModel != null) {
            callback.onSuccess(userModel);
            return;
        }

        usersCollection.document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userModel = UserModel.fromDocumentSnapshot(documentSnapshot);
                callback.onSuccess(userModel);
            } else {
                callback.onFailure(new Exception("User not found"));
            }
        }).addOnFailureListener(callback::onFailure);

    }


}
