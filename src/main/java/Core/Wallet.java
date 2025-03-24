package Core;

import java.util.ArrayList;

public class Wallet {
    private String publicKey;
    private double balance;
    private ArrayList<Transaction> transactions;
    private int Nonce;
    private String state;

    public String publicKey() {
        return publicKey;
    }

    public Wallet setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public void setTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public ArrayList<Transaction> transactions() {
        return transactions;
    }

    public Wallet setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        return this;
    }

    public double balance() {
        return balance;
    }

    public Wallet setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public int Nonce() {
        return Nonce;
    }

    public Wallet setNonce(int nonce) {
        Nonce = nonce;
        return this;
    }

    public String state() {
        return state;
    }

    public Wallet setState(String state) {
        this.state = state;
        return this;
    }

    public void showInfo() {
        System.out.println("------------------------");
        System.out.println("Wallet "+this.publicKey()+": ");
        System.out.println("    Balance: "+this.balance());
        System.out.println("    Nonce: "+this.Nonce());
        System.out.println("    State: "+this.state());
        System.out.println("    Transactions: ");
        for (Transaction transaction : this.transactions()) {
            System.out.println("       HashID: " + transaction.HashID());
            System.out.println("       Sender Public Key: " + transaction.senderPublicKey());
            System.out.println("       Receiver Public Key: " + transaction.reciverPublicKey());
            System.out.println("       Block Hash: " + transaction.blockHash());
            System.out.println("       State: " + transaction.state());
            System.out.println("       Value: " + transaction.value());
            System.out.println("       Nonce: " + transaction.nonce());
            System.out.println("       Timestamp: " + transaction.timestamp());
            System.out.println("       Data: " + transaction.data());
            System.out.println("       Fee: " + transaction.fee());
            System.out.println("       Number of Confirmations: " + transaction.numberOfComfirmations());
        }
    }
}
