package com.kushal.stylista.services;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kushal.stylista.ApiSaveResponseCallback;
import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.utils.FirebaseConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClothRepository {

    private FirebaseFirestore db;

    public ClothRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getClothes(ApiListResponseCallback<ClothModel> callback) {
        db.collection(FirebaseConstants.COLLECTION_CLOTHES)
                .get()  // Fetches all the documents from the "clothes" collection
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        List<ClothModel> clothesList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : documents) {
                            // Map the document to a ClothModel
                            ClothModel cloth = document.toObject(ClothModel.class);
                            cloth.setId(document.getId());
                            clothesList.add(cloth);
                        }

                        callback.onSuccess(clothesList);

                    } else {
                        callback.onFailure(task.getException());
                        Log.e("ClothesActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    public void updateFavoriteCloth(String clothId, final ApiSaveResponseCallback callback) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        DocumentReference favoriteRef = db.collection(FirebaseConstants.COLLECTION_FAVORITES).document(user.getUid());
        favoriteRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                favoriteRef.update(FirebaseConstants.FIELD_FAVORITE, FieldValue.arrayUnion(clothId))
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            } else {
                Map<String, Object> favoriteData = new HashMap<>();
                favoriteData.put(FirebaseConstants.FIELD_FAVORITE, FieldValue.arrayUnion(clothId));

                favoriteRef.set(favoriteData)
                        .addOnSuccessListener(success -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            }
        });
    }

    public void getFavoriteClothesId(ApiListResponseCallback<String> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        DocumentReference favoriteRef = db.collection(FirebaseConstants.COLLECTION_FAVORITES).document(user.getUid());
        favoriteRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> favoriteIds = (List<String>) documentSnapshot.get(FirebaseConstants.FIELD_FAVORITE);
                callback.onSuccess(favoriteIds);
            } else {
                callback.onSuccess(new ArrayList<>());
            }
        });
    }

    public void getFavoriteClothes(ApiListResponseCallback<ClothModel> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        DocumentReference favoriteRef = db.collection(FirebaseConstants.COLLECTION_FAVORITES).document(user.getUid());
        favoriteRef.get().continueWithTask(task -> {
            if (!task.isSuccessful() || !task.getResult().exists()) {
                return Tasks.forResult(new ArrayList<DocumentSnapshot>()); // Return empty list if no favorites
            }
            List<String> favoriteIds = (List<String>) task.getResult().get(FirebaseConstants.FIELD_FAVORITE);


            // Fetch clothes from the main "clothes" collection using IDs
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            for (String clothId : favoriteIds) {
                tasks.add(db.collection(FirebaseConstants.COLLECTION_CLOTHES).document(clothId).get());
            }

            return Tasks.whenAllSuccess(tasks); // Return a single task for all fetches


        }).addOnSuccessListener(results -> {
            List<ClothModel> favoriteClothes = new ArrayList<>();
            for (Object result : results) {
                if (result instanceof DocumentSnapshot) {
                    ClothModel cloth = ((DocumentSnapshot) result).toObject(ClothModel.class);
                    if (cloth != null) {
                        cloth.setId(((DocumentSnapshot) result).getId());
                        favoriteClothes.add(cloth);
                    }
                }
            }
            callback.onSuccess(favoriteClothes);
        });
    }

    public void deleteFavoriteCloth(String clothId, final ApiSaveResponseCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onFailure(new Exception("User not logged in"));
            return;
        }
        DocumentReference favoriteRef = db.collection(FirebaseConstants.COLLECTION_FAVORITES)
                .document(user.getUid());
        favoriteRef.update(FirebaseConstants.FIELD_FAVORITE, FieldValue.arrayRemove(clothId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

}
