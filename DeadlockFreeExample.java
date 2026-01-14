import java.util.concurrent.Semaphore;

class BankAccount {
    String id;
    int balance;
    Semaphore mutex = new Semaphore(1);

    BankAccount(String id, int balance) {
        this.id = id;
        this.balance = balance;
    }
}

class SafeTransaction {

    static void moveMoney(BankAccount a, BankAccount b, int amount) {
        BankAccount first = a.id.compareTo(b.id) < 0 ? a : b;
        BankAccount second = a.id.compareTo(b.id) < 0 ? b : a;

        try {
            System.out.println(Thread.currentThread().getName()
                    + " locking " + first.id);
            first.mutex.acquire();

            Thread.sleep(100);

            System.out.println(Thread.currentThread().getName()
                    + " locking " + second.id);
            second.mutex.acquire();

            a.balance -= amount;
            b.balance += amount;

            System.out.println("Transfer completed safely");

            second.mutex.release();
            first.mutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class DeadlockFreeExample {

    public static void main(String[] args) {

        BankAccount acc1 = new BankAccount("ACC-1", 1000);
        BankAccount acc2 = new BankAccount("ACC-2", 1000);

        Thread t1 = new Thread(() ->
                SafeTransaction.moveMoney(acc1, acc2, 100),
                "Worker-1"
        );

        Thread t2 = new Thread(() ->
                SafeTransaction.moveMoney(acc2, acc1, 200),
                "Worker-2"
        );

        t1.start();
        t2.start();
    }
}
