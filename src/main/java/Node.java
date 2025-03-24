import java.util.Date;

public class Node {
    private int id;
    private Node next;
    private String data;
    private String Hash;

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public Node(String data) {
        this.data = data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public String getData() {
        return  data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return id+data+next+new Date();
    }
}



