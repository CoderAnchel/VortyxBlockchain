public class HashTable {
    private final HashLinkedList[] linkedLists = new HashLinkedList[100];

    public HashTable() {
        for (int i = 0; i < linkedLists.length; i++) {
            System.out.println("creating");
            linkedLists[i] = new HashLinkedList();
        }
    }

    public int hashFunction(String key) {
        char[] keyChars = key.toCharArray();
        int number = 0;
        for (char keyChar : keyChars) {
            number += keyChar;
        }
        return number % this.linkedLists.length;
    }

    public Node find(String key) {
        return linkedLists[hashFunction(key)].get(key);
    }

    public void insert(Node node) {
        int index = hashFunction(node.getData());
        this.linkedLists[index].add(node);
    }
}
