package com.kushal.stylista.api.request;

public class PaymentIntentRequest {
    public double amount;
    public String currency;

    public PaymentIntentRequest(double amount) {
        this.amount = amount;
        this.currency = "cad";
    }
}
