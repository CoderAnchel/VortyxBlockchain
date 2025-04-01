package Core.Entities;

import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public void setTransactions(List<String> transactions) {
        this.transactions = (ArrayList<String>) transactions;
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


    public byte[] toRLP() {
        List<RlpType> blockElements = new ArrayList<>();
        blockElements.add(RlpString.create(this.timestamp != null ? this.timestamp.toString() : ""));
        blockElements.add(RlpString.create(this.previousHash != null ? this.previousHash : ""));
        blockElements.add(RlpString.create(this.hash != null ? this.hash : ""));
        blockElements.add(RlpString.create(String.valueOf(this.nonce)));
        blockElements.add(RlpString.create(this.merkleRoot != null ? this.merkleRoot : ""));

        List<RlpType> transactions = new ArrayList<>();
        if (this.transactions != null) {
            for (String tx: this.transactions) {
                transactions.add(RlpString.create(tx != null ? tx.getBytes() : "".getBytes()));
            }
        }

        blockElements.add(new RlpList(transactions));
        blockElements.add(RlpString.create(String.valueOf(this.position)));
        blockElements.add(RlpString.create(miner != null ? miner.getBytes() : "".getBytes()));
        blockElements.add(RlpString.create(String.valueOf(fee)));

        return RlpEncoder.encode(new RlpList(blockElements));
    }

    @Override
    public String toString() {
        return "Block{" +
                "timestamp=" + timestamp +
                ", merkleRoot='" + merkleRoot + '\'' +
                ", miner='" + miner + '\'' +
                ", position=" + position +
                ", fee=" + fee +
                ", nonce=" + nonce +
                ", previousHash='" + previousHash + '\'' +
                ", hash='" + hash + '\'' +
                ", transactions=" + transactions.stream().map(Object::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
