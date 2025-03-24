import Exceptions.NodeException;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LinkedList {
    private Node head;
    private Boolean loading = false;
    private int nonce = 0;

    public LinkedList() {
        loadNodesFromFile();
    }

    public void add(Node node) {
        if (!loading) {
            nonce = 0;
            String sha256hex;
            boolean found = false;
            while (!found) {
                nonce++;
                sha256hex = Hashing.sha256()
                        .hashString(nonce + node.toString(), StandardCharsets.UTF_8)
                        .toString();
                System.out.println("Mining...");
                System.out.println(sha256hex);
                if (sha256hex.startsWith("0000")) {
                    System.out.println("Finded! ✅");
                    node.setHash(sha256hex);
                    found = true;
                }
            }
        }

        if (this.head == null) {
            this.head = node;
        } else {
            Node current = this.head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(node);
        }

        if (!loading) {
            saveNodeToFile(node);
        }
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
        if (value == null) {
            throw new NodeException("Search value cannot be null");
        }

        Node current = this.head;
        int iteration = 1;

        while (current != null) {
            System.out.println("Iteration: " + iteration);
            System.out.println("Current node data: '" + current.getData() + "'");
            System.out.println("Searching for: '" + value + "'");

            if (current.getData() != null && current.getData().equals(value)) {
                System.out.println("Match found!");
                return current;
            }

            iteration++;
            current = current.getNext();
        }

        throw new NodeException("Node not found");
    }

    private void saveNodeToFile(Node node) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("nodes.json", true)) {
            gson.toJson(node, writer);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNodesFromFile() {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader("nodes.json"))) {
            loading = true;
            String line;
            while ((line = reader.readLine()) != null) {
                Node node = gson.fromJson(line, Node.class);
                add(node);
            }
            loading = false;
        } catch (IOException e) {
            loading = false; // Reset flag in case of exception
            if (!e.getMessage().contains("nodes.json (No such file or directory)")) {
                e.printStackTrace();
            }
        }
    }
}