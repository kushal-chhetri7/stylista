package com.kushal.stylista.services;

import java.util.List;

public interface ApiListResponseCallback<T> {
    void onSuccess(List<T> clothes);
    void onFailure(Exception exception);
}
