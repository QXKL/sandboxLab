package com.qx.pay.impl;

import com.qx.pay.PaymentAPI;

public class AlipayPayment implements PaymentAPI {

    private String key;

    public AlipayPayment(String key) {
        this.key = key;
    }

    @Override
    public void pay(String orderNo, double amount) {
        System.out.println("支付宝支付, 携带key " + key + "支付" + amount + "元");
    }
}
