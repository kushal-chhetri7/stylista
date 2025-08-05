package com.kushal.stylista.services;

import java.util.List;

public interface ApiDataResponseCallback<T> {
    void onSuccess(T item);
    void onFailure(Exception exception);
}
