package hashmap;

import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    class KeyIterator implements Iterator<K>{
        private int index;
        private Iterator<Node> it;

        public KeyIterator(){
            for (int i = 0; i < buckets.length; i++){
                it = buckets[i].iterator();
                if(it.hasNext()){
                    index = i;
                    return;
                }
            }
        }
        public boolean hasNext(){
            return it.hasNext();
        }

        public K next(){
            K data = null;
            if (it.hasNext()){
                Node node = it.next();
                data = node.key;
            }

            if (it.hasNext()){
                return data;
            }

            for (int i = index + 1; i < buckets.length; i++){
                it = buckets[i].iterator();
                if (it.hasNext()){
                    index = i;
                    break;
                }
            }

            return data;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    private int size = 0;
    private int initialSize = 16;
    private double maxLoad = 0.75;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = Math.max(initialSize, 16);
        buckets = createTable(this.initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = Math.max(initialSize, 16);
        buckets = createTable(this.initialSize);
        this.maxLoad = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];

        for (int i = 0; i < table.length; i++){
            table[i] = createBucket();
        }

        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear(){
        size = 0;
        buckets = createTable(this.initialSize);
    }

    @Override
    public boolean containsKey(K key){
        if (size == 0){
            return false;
        }

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                if (node.key.equals(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(K key){
        if (size == 0){
            return null;
        }

        int hashCode = key.hashCode();
        hashCode = hashCode < 0 ? - hashCode : hashCode;

        Collection<Node> bucket = buckets[hashCode % buckets.length];
        for(Node node: bucket){
            if(node.key.equals(key)){

                return node.value;
            }
        }

        return null;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void put(K key, V value){
        int hashCode = key.hashCode();
        hashCode = hashCode < 0 ? - hashCode : hashCode;

        Collection<Node> bucket = buckets[hashCode % buckets.length];
        for(Node node: bucket){
            if(node.key.equals(key)){
                node.value = value;
                return;
            }
        }

        size += 1;
        Node newNode = createNode(key, value);
        bucket.add(newNode);


        double loadFactor = size / (buckets.length + 0.001);

        if (loadFactor < maxLoad){
            return;
        }

        doubleExpand();
    }

    private void doubleExpand(){
        Collection<Node>[] newTable = createTable(buckets.length * 2);

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int hashCode = node.key.hashCode();
                hashCode = hashCode < 0 ? - hashCode : hashCode;
                newTable[hashCode % newTable.length].add(node);
            }
        }

        buckets = newTable;
    }

    @Override
    public KeyIterator iterator(){
        return new KeyIterator();
    }

    @Override
    public Set<K> keySet(){
        KeyIterator it = iterator();
        Set<K> ks = new HashSet<>();
        while(it.hasNext()){
            ks.add(it.next());
        }
        return ks;
    }

    @Override
    public V remove(K key){
        if (size == 0){
            return null;
        }

        int hashCode = key.hashCode();
        hashCode = hashCode < 0 ? - hashCode : hashCode;

        Collection<Node> bucket = buckets[hashCode % buckets.length];

        Node targetNode = null;
        for(Node node: bucket){
            if(node.key.equals(key)){
                targetNode = node;
                break;
            }
        }

        if(targetNode == null){
            return null;
        }

        bucket.remove(targetNode);
        return targetNode.value;
    }

    @Override
    public V remove(K key, V value){
        throw new UnsupportedOperationException();
    }
}
