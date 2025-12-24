import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankWithoutLock {
    private int balance = 0;
    
    public void deposit() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        balance += 100;
    }
    
    public void withdraw() {
        balance -= 100;
    }
    
    public int getValue() {
        return balance;
    }
    
    public void run() {
        // NO LOCK - Multiple threads can execute this simultaneously
        deposit();
        System.out.println("After deposit " + Thread.currentThread().getName() + ": " + getValue());
        withdraw();
        System.out.println("After withdraw " + Thread.currentThread().getName() + ": " + getValue());
    }
}

// AFTER: With Lock - Thread Safe
class BankWithLock {
    private int balance = 0;
    private Lock lock = new ReentrantLock();
    
    public void deposit() {
        try {
            Thread.sleep(10);  // Simulates delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        balance += 100;
    }
    
    public void withdraw() {
        balance -= 100;
    }
    
    public int getValue() {
        return balance;
    }
    
    public void run() {
        lock.lock();  // Acquire lock - only one thread can execute at a time
        try {
            deposit();
            System.out.println("After deposit " + Thread.currentThread().getName() + ": " + getValue());
            withdraw();
            System.out.println("After withdraw " + Thread.currentThread().getName() + ": " + getValue());
        } finally {
            lock.unlock();  // Always release lock
        }
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("DEMO 1: WITHOUT LOCK (Race Condition)");
        System.out.println("========================================");
        runWithoutLock();
        
        System.out.println("\n========================================");
        System.out.println("DEMO 2: WITH LOCK (Thread Safe)");
        System.out.println("========================================");
        runWithLock();
    }
    
    private static void runWithoutLock() {
        BankWithoutLock bank = new BankWithoutLock();
        
        Thread t1 = new Thread(bank::run, "Thread-1");
        Thread t2 = new Thread(bank::run, "Thread-2");
        Thread t3 = new Thread(bank::run, "Thread-3");
        
        t1.start();
        t2.start();
        t3.start();
        
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Final balance (WITHOUT LOCK): " + bank.getValue());
        System.out.println("Expected: 0, Actual may vary due to race condition!");
    }
    
    private static void runWithLock() {
        BankWithLock bank = new BankWithLock();
        
        Thread t1 = new Thread(bank::run, "Thread-1");
        Thread t2 = new Thread(bank::run, "Thread-2");
        Thread t3 = new Thread(bank::run, "Thread-3");
        
        t1.start();
        t2.start();
        t3.start();
        
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Final balance (WITH LOCK): " + bank.getValue());
        System.out.println("Expected: 0, Actual: Always 0 (Thread Safe!)");
    }
}
