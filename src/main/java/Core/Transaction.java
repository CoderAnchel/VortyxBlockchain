package Core;

import java.util.Date;

public class Transaction {
    private String HashID;
    private String senderPublicKey;
    private String reciverPublicKey;
    private String blockHash;
    private String state;
    private double value;
    private int nonce;
    private Date timestamp;
    private String data;
    private Double fee;
    private int numberOfComfirmations;

    public String HashID() {
        return HashID;
    }

    public void setHashID(String hashID) {
        HashID = hashID;
    }

    public String senderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String reciverPublicKey() {
        return reciverPublicKey;
    }

    public void setReciverPublicKey(String reciverPublicKey) {
        this.reciverPublicKey = reciverPublicKey;
    }

    public String blockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String state() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int nonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public double value() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date timestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String data() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Double fee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public int numberOfComfirmations() {
        return numberOfComfirmations;
    }

    public void setNumberOfComfirmations(int numberOfComfirmations) {
        this.numberOfComfirmations = numberOfComfirmations;
    }
}
