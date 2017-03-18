package com.order;

public class Throttling {
	
    public static void sleep(int sec){
    	try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	
}
