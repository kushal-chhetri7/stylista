package com.kushal.stylista.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class UserModel {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String gender;

    UserModel(){

    }

    public UserModel(String id, String firstName, String lastName, String email, String role, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public static UserModel fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
       UserModel userModel = documentSnapshot.toObject(UserModel.class);
       userModel.setId(documentSnapshot.getId());
       return userModel;
    }
}
