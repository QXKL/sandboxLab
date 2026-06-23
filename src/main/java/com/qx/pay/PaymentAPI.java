package com.qx.pay;

public interface PaymentAPI {
    void pay(String orderNo, double amount);
}
