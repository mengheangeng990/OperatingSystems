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

class Transaction {

    static void moveMoney(BankAccount from, BankAccount to, int amount) {
        try {
            System.out.println(Thread.currentThread().getName()
                    + " locking " + from.id);
            from.mutex.acquire();

            Thread.sleep(150);

            System.out.println(Thread.currentThread().getName()
                    + " locking " + to.id);
            to.mutex.acquire();

            from.balance -= amount;
            to.balance += amount;

            System.out.println("Transfer done");

            to.mutex.release();
            from.mutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class DeadlockExample {

    public static void main(String[] args) throws InterruptedException {

        BankAccount acc1 = new BankAccount("ACC-1", 1000);
        BankAccount acc2 = new BankAccount("ACC-2", 1000);

        Thread t1 = new Thread(() ->
                Transaction.moveMoney(acc1, acc2, 100),
                "Worker-1"
        );

        Thread t2 = new Thread(() ->
                Transaction.moveMoney(acc2, acc1, 200),
                "Worker-2"
        );

        t1.start();
        Thread.sleep(50);
        t2.start();
    }
}
