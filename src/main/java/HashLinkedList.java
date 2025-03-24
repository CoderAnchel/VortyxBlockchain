import Exceptions.NodeException;

public class HashLinkedList {
    private Node head;

    public HashLinkedList() {

    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public void showList() {
        Node current = this.head;
        int counter = 1;
        while (current != null) {
            System.out.printf("Node %d: %s\n", counter, current.getData());
            current = current.getNext();
            counter++;
        }
    }

    public Node get(String value) throws NodeException {
        Node current = this.head;
        int counter = 1;
        while (current != null) {
            System.out.println("Iteration "+counter);
            if (current.getData().equals(value)) {
                return current;
            }
            counter++;
            current = current.getNext();
        }

        throw new NodeException("Node not finded");
    };

    public void add(Node node) {
        if (this.head == null) {
            this.setHead(node);
        } else {
            Node current = this.head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(node);
        }
    }
}
