package com.omi.thread;



public class MyClass5 {
	
    public MyClass5() {
        System.out.println("Hen gap mat ");
    }
    private synchronized void rutTien () {
        System.out.println("A Hello");
            System.out.println("Doi B ");
            try {
                wait(); // phương thức wail sẽ đưa Thread rơi vào trạng thái sleeping
            } catch (InterruptedException ie) {
                System.out.println(ie.toString());
            }
        System.out.println("clasp B");
    }
    private synchronized void nopTien() {
        System.out.println("B hello");
        System.out.println("");
        notify();
    }
    public static void main(String[] args) {
         
        final MyClass5 customer = new MyClass5();
        Thread t1 = new Thread(){
            public void run() {
                customer.rutTien();
            }
        };
        t1.start();
        
        Thread t2 = new Thread(){
             
            public void run() {
                customer.nopTien();
            }
        };
        t2.start();
    }
}