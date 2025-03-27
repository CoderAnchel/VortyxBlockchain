package Core.Entities;

import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import utils.KeyPairUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public Transaction(String hashID, String senderPublicKey, String reciverPublicKey, String blockHash, String state, double value, int nonce, Date timestamp, String data, Double fee, int numberOfComfirmations) {
        HashID = hashID;
        this.senderPublicKey = senderPublicKey;
        this.reciverPublicKey = reciverPublicKey;
        this.blockHash = blockHash;
        this.state = state;
        this.value = value;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.data = data;
        this.fee = fee;
        this.numberOfComfirmations = numberOfComfirmations;
    }

    public Transaction() {

    }


    public String senderPublicKeyHex() {
        return KeyPairUtils.base64ToHex(this.senderPublicKey);
    }

    public String reciverPublicKeyHex() {
        return KeyPairUtils.base64ToHex(this.reciverPublicKey);
    }

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

    public void showInfo() {
        System.out.println("------------------------");
        System.out.println("Transaction "+this.HashID()+": ");
        System.out.println("    value: "+this.value());
        System.out.println("    Nonce: "+this.nonce());
        System.out.println("    State: "+this.state());
        System.out.println("    From: "+this.senderPublicKey());
        System.out.println("    To: "+this.reciverPublicKey());
        System.out.println("    Fee: "+this.fee());
        System.out.println("    Timestampt: "+this.timestamp    );
    }

    public byte[] toRLP() {
        List<RlpType> walletElements = new ArrayList<>();
        walletElements.add(RlpString.create(this.HashID));
        walletElements.add(RlpString.create(this.senderPublicKey));
        walletElements.add(RlpString.create(this.reciverPublicKey));
        walletElements.add(RlpString.create(this.blockHash));
        walletElements.add(RlpString.create(this.state));
        walletElements.add(RlpString.create(Double.toString(this.value)));
        walletElements.add(RlpString.create(Integer.toString(this.nonce)));
        walletElements.add(RlpString.create(this.timestamp.toString()));
        walletElements.add(RlpString.create(this.data));
        walletElements.add(RlpString.create(Double.toString(this.fee)));
        walletElements.add(RlpString.create(Integer.toString(this.numberOfComfirmations)));
        return RlpEncoder.encode(new RlpList(walletElements));
    }
}
