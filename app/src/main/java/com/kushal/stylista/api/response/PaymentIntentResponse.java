package com.kushal.stylista.api.response;

import com.google.gson.annotations.SerializedName;

public class PaymentIntentResponse {
    @SerializedName("clientSecret")
    public String clientSecret;
    public String paymentIntentId;
}