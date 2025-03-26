package Core.Entities;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    private Date timestamp;
    private String previousHash;
    private String hash;
    private int nonce;
    private String merkleRoot;
    private ArrayList<String> transactions;
    private int position;
    private String miner;
    private double fee;

    public Block() {
        this.transactions = new ArrayList<>();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public double fee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String miner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public int position() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<String> transactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<String> transactions) {
        this.transactions = transactions;
    }

    public String merkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public int nonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return timestamp + merkleRoot + miner + position + fee + nonce + previousHash + transactions;
    }
}
