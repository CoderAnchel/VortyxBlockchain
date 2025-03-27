package Core.Entities;

import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import utils.KeyPairUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private String publicKey;
    private String publicKeyBase64;
    private double balance;
    private ArrayList<Transaction> transactions;
    private int Nonce;
    private String state;

    public Wallet() {

    }

    public Wallet(String publicKey, String publicKeyBase64, double balance, ArrayList<Transaction> transactions, int nonce, String state) {
        this.publicKey = publicKey;
        this.publicKeyBase64 = publicKeyBase64;
        this.balance = balance;
        this.transactions = transactions;
        Nonce = nonce;
        this.state = state;
    }

    public String publicKeyBase64() {
        return publicKeyBase64;
    }

    public Wallet setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
        return this;
    }

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

    public String publicKeyHex() {
        return KeyPairUtils.base64ToHex(this.publicKeyBase64);
    }

    public void showInfo() {
        System.out.println("------------------------");
        System.out.println("Wallet "+this.publicKeyHex()+": ");
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

    public byte[] toRLP() {
        List<RlpType> walletElements = new ArrayList<>();
        walletElements.add(RlpString.create(this.publicKey));
        walletElements.add(RlpString.create(this.publicKeyBase64));
        walletElements.add(RlpString.create(Double.toString(this.balance)));

        List<RlpType> walletTransactions = new ArrayList<>();
        for (Transaction tx : this.transactions) {
            walletTransactions.add(RlpString.create(tx.toRLP()));
        }

        walletElements.add(new RlpList(walletTransactions));
        walletElements.add(RlpString.create(Integer.toString(this.Nonce)));
        walletElements.add(RlpString.create(this.state));

        return RlpEncoder.encode(new RlpList(walletElements));
    }


}
