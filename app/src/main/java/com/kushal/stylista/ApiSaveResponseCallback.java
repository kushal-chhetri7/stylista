package com.kushal.stylista;

public interface ApiSaveResponseCallback {
    void onSuccess();
    void onFailure(Exception exception);
}
