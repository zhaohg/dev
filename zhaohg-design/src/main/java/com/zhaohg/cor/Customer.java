package com.zhaohg.cor;


import com.zhaohg.cor.handler.PriceHandler;
import com.zhaohg.cor.handler.PriceHandlerFactory;

import java.util.Random;

/**
 * 客户，请求折扣
 */
public class Customer {
    
    private PriceHandler priceHandler;
    
    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setPriceHandler(PriceHandlerFactory.createPriceHandler());
        
        Random rand = new Random();
        
        for (int i = 1; i <= 100; i++) {
            System.out.print(i + ":");
            customer.requestDiscount(rand.nextFloat());
        }
        
        
    }
    
    public void setPriceHandler(PriceHandler priceHandler) {
        this.priceHandler = priceHandler;
    }
    
    public void requestDiscount(float discount) {
        priceHandler.processDiscount(discount);
    }
    
    
}
