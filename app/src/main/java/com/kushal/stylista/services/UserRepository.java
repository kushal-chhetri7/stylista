package com.kushal.stylista.services;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {

    private FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }



}
