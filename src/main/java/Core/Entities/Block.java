package Core.Entities;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    private Date timestamp;
    private String data;
    private String previousHash;
    private String hash;
    private int nonce;
    private String merkleRoot;
    private ArrayList<String> transactions;
    private int position;
    private String miner;
    private double fee;


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
}
