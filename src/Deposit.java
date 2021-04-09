public class Deposit {
    public static void main(String[] args) {
        Container container = new Container();

        DepositResource d1 = new DepositResource(container, 1, "dollar");
        ConsumeResource c1 = new ConsumeResource(container, 1, "dollar");

        DepositResource d2 = new DepositResource(container, 2, "euro");
        ConsumeResource c2 = new ConsumeResource(container, 2, "euro");

        DepositResource d3 = new DepositResource(container, 3, "pound");
        ConsumeResource c3 = new ConsumeResource(container, 3, "pound");

        d1.start();
        c1.start();

        d2.start();
        c2.start();

        d3.start();
        c3.start();
    }
}

class DepositResource extends Thread {
    private Container dHold;
    private int numberOfResources;
    private String currency;

    public DepositResource(Container c, int n, String currency) {
        super("Deposit resource");
        dHold = c;
        numberOfResources = n;
        this.currency = currency;
    }

    public void run() {
        for (int i = 1; i <= numberOfResources; i++) {
            try {
                Thread.sleep( (int) ( Math.random() * 2000 ) );
            } catch (InterruptedException e) {
                System.err.println(e.toString());
            }

            dHold.setDepositResource(i, currency);
        }
    }
}

class ConsumeResource extends Thread {
    private Container cHold;
    private int numberOfResources;
    private String currency;

    public ConsumeResource(Container c, int n, String currency) {
        super("Consume resource");
        cHold = c;
        numberOfResources = n;
        this.currency = currency;
    }

    public void run() {
        int val;
        do {
            // sleep for a random interval
            try {
                Thread.sleep( (int) ( Math.random() * 2000 ) );
            } catch(InterruptedException e) {
                System.err.println(e.toString());
            }
            val = cHold.getDepositResource();
        } while (val != numberOfResources);

    }
}

class Container {
    private int shareInt = 0;
    private String currency = "";
    private boolean writeable = true;

    public synchronized void setDepositResource(int val, String currency) {
        while (!writeable) {
            try {
                System.out.println("Waiting for my friend");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() +
                " deposit money: " + val + " in " + currency);
        shareInt = val;
        this.currency = currency;
        writeable = false;
        notify();
    }

    public synchronized int getDepositResource() {
        while (writeable) {
            try {
                System.out.println("Waiting for money");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        writeable = true;
        notify();
        System.out.println(Thread.currentThread().getName() +
                " receive money: " + shareInt + " in " + currency);
        return shareInt;
    }
}