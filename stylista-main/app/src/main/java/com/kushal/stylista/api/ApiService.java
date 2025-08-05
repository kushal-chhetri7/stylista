package com.kushal.stylista.api;

import com.kushal.stylista.api.request.ConfirmPaymentRequest;
import com.kushal.stylista.api.request.PaymentIntentRequest;
import com.kushal.stylista.api.response.PaymentIntentResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/create-payment-intent")
    Call<PaymentIntentResponse> createPaymentIntent(
            @Body PaymentIntentRequest request,
            @Header("Authorization") String bearerToken
    );

    @POST("/confirm-payment")
    Call<Map<String, Object>> confirmPayment(
            @Body ConfirmPaymentRequest request,
            @Header("Authorization") String bearerToken
    );


}
