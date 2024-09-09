import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class ExternalChainingHashMap<K, V> {

    public static final int INITIAL_CAPACITY = 13;

    public static final double MAX_LOAD_FACTOR = 0.67;

    private ExternalChainingMapEntry<K, V>[] table;
    private int size;

    public ExternalChainingHashMap() {
        this(INITIAL_CAPACITY);
    }

    public ExternalChainingHashMap(int initialCapacity) {
        table = (ExternalChainingMapEntry<K, V>[]) new ExternalChainingMapEntry[initialCapacity];
    }

    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is null.");
        }
        int index = Math.abs(key.hashCode() % table.length);
        V result = null;
        if (((size + 1.0) / table.length) > MAX_LOAD_FACTOR) {
            resizeBackingTable(2 * table.length + 1);
        } else {
            if (table[index] == null) {
                // add front
                table[index] = new ExternalChainingMapEntry<K, V>(key, value);
                size++;
            } else {
                // iterate through whole list
                while (table[index].getNext() != null) {
                    if (table[index].getKey() == key) {
                        // if yes duplicate, replace entry value w new value
                        result = table[index].getValue();
                        table[index].setValue(value);
                    } else {
                        // if no duplicate, add front
                        ExternalChainingMapEntry<K, V> node = new ExternalChainingMapEntry<>(key, value);
                        node.setNext(table[index]);
                        table[index] = node;
                        size++;
                    }
                }
            }
        }
        return result;
    }

    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        V value = null;
        int index = Math.abs(key.hashCode() % table.length);
        ExternalChainingMapEntry<K, V> node = table[index];
        if (node == null) {
            throw new NoSuchElementException("Key is not in map.");
        }
        if (table[index].getKey().equals(key)) {
            value = table[index].getValue();
            table[index] = table[index].getNext();
        } else {
            while (node.getNext() != null) {
                if (node.getNext().getKey().equals(key)) {
                    value = node.getNext().getValue();
                    node.setNext(node.getNext().getNext());
                }
            }
            if (value == null) {
                throw new NoSuchElementException("Key is not in map.");
            }
        }
        size--;
        return value;
    }

    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        int index = Math.abs(key.hashCode() % table.length);
        V result = null;
        ExternalChainingMapEntry<K, V> node = table[index];
        while (node != null) {
            if (node.getKey().equals(key)) {
                result = node.getValue();
            }
            node = node.getNext();
        }
        if (result == null) {
            throw new NoSuchElementException("Key is not in map.");
        }
        return result;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        int index = Math.abs(key.hashCode() % table.length);
        ExternalChainingMapEntry<K, V> node = table[index];
        while (node != null) {
            if (node.getKey().equals(key)) {
                return true;
            }
            node = node.getNext();
        }
        return false;
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        int count = 0;
        int i = 0;
        while (count < size && i < table.length) {
            ExternalChainingMapEntry<K, V> temp = table[i];
            while (temp != null) {
                set.add(temp.getKey());
                temp = temp.getNext();
                count++;
            }
            i++;
        }
        return set;
    }

    public List<V> values() {
        List<V> list = new LinkedList<>();
        int count = 0;
        int i = 0;
        while (count < size && i < table.length) {
            ExternalChainingMapEntry<K, V> temp = table[i];
            while (temp != null) {
                list.add(temp.getValue());
                temp = temp.getNext();
                count++;
            }
            i++;
        }
        return list;
    }

    public void resizeBackingTable(int length) {
        if (length < size) {
            throw new IllegalArgumentException("Length is less than number of items in hash map.");
        }
        ExternalChainingMapEntry<K, V>[] newTable = (ExternalChainingMapEntry<K, V>[])
                new ExternalChainingMapEntry[length];
        int count = 0;
        int i = 0;
        while (count < size && i < table.length) {
            ExternalChainingMapEntry<K, V> temp = table[i];
            while (temp != null) {
                int newIndex = Math.abs(temp.getKey().hashCode() % length);
                newTable[newIndex] = temp;
                count++;
                temp = temp.getNext();
            }
            i++;
        }
        table = newTable;
    }

    public void clear() {
        table = (ExternalChainingMapEntry<K, V>[]) new ExternalChainingMapEntry[INITIAL_CAPACITY];
        size = 0;
    }

    public ExternalChainingMapEntry<K, V>[] getTable() {
        return table;
    }

    public int size() {
        return size;
    }
}
