public class Deposit {
    private int amount;
    private String currency;

    public Deposit(int amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    int getAmount() { return this.amount; }
    String getCurrency() { return this.currency; }

    public static void main(String[] args) {
        Container container = new Container();

        Deposit[] deposits = new Deposit[3];
        deposits[0] = new Deposit(1, "dollar");
        deposits[1] = new Deposit(2, "euro");
        deposits[2] = new Deposit(3, "pound");


        DepositResource d = new DepositResource(container, deposits);
        ConsumeResource c = new ConsumeResource(container, deposits);

        d.start();
        c.start();
    }
}

class DepositResource extends Thread {
    private Container dHold;
    private Deposit[] deposits;

    public DepositResource(Container c, Deposit[] deposits) {
        super("Deposit resource");
        dHold = c;
        this.deposits = deposits;
    }

    public void run() {
        for (int i = 0; i < deposits.length; i++) {
            for (int j = 1; j <= deposits[i].getAmount(); j++) {
                try {
                    Thread.sleep( (int) ( Math.random() * 3000 ) );
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                }

                dHold.setDepositResource(j, deposits[i].getCurrency());
            }

        }
    }
}

class ConsumeResource extends Thread {
    private Container cHold;
    private Deposit[] deposits;

    public ConsumeResource(Container c, Deposit[] deposits) {
        super("Consume resource");
        cHold = c;
        this.deposits = deposits;
    }

    public void run() {
        int val;
        int i = 0;
        do {
            // sleep for a random interval
            for (int j = 1; j <= deposits[i].getAmount(); j++) {
                try {
                    Thread.sleep( (int) ( Math.random() * 3000 ) );
                } catch(InterruptedException e) {
                    System.err.println(e.toString());
                }
                cHold.getDepositResource();
            }
            i++;
        } while (i != deposits.length);

    }
}

class Container {
    private int shareInt = 0;
    private String currency = "";
    private boolean writeable = true;

    public synchronized void setDepositResource(int val, String currency) {
        while (!writeable) {
            try {
                System.out.println("----------------Waiting for my friend to consume the money----------------");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() +
                " deposit money: " + val + " in " + currency);
        this.shareInt = val;
        this.currency = currency;
        writeable = false;
        notify();
    }

    public synchronized int getDepositResource() {
        while (writeable) {
            try {
                System.out.println("----------------Waiting for me to deposit money----------------");
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