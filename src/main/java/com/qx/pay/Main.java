package com.qx.pay;

import com.qx.pay.impl.AlipayPayment;
import com.qx.pay.impl.CreditCardPayment;

public class Main {
    public static void main(String[] args) {
        PaymentService paymentService1 = new PaymentService(new CreditCardPayment());
        paymentService1.pay("123456", 100.00);

        PaymentService paymentService2 = new PaymentService(new AlipayPayment("123456789"));
        paymentService2.pay("123456", 100.00);


    }
}
