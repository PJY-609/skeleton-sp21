package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>,V> implements Map61B<K, V>{

    class BSTNode{
        private K key;
        private V value;
        private BSTNode left = null;
        private BSTNode right = null;

        public BSTNode(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    private BSTNode root = null;
    private BSTNode hot = null;
    private int size = 0;

    public BSTMap(){}

    private void insert(K key, V value){
        boolean found = find(key);
        if(!found && hot == null){
            root = new BSTNode(key, value);
        }
        else if(found && hot.right.key.equals(key)){
            hot.right.value = value;
        }
        else if(found && hot.left.key.equals(key)){
            hot.left.value = value;
        }
        else if(!found && hot.key.compareTo(key) < 0){
            hot.right = new BSTNode(key, value);
        }
        else{
            hot.left = new BSTNode(key, value);
        }
    }

    private BSTNode delete(K key){
        boolean found = find(key);

        if(!found){
            return null;
        }

        BSTNode node = hot.left.key.equals(key) ? hot.left : hot.right;

        if(node.left == null && hot.left.key.equals(key)){
            hot.left = node.right;
            return node;
        }
        else if(node.left == null && hot.right.key.equals(key)){
            hot.right = node.right;
            return node;
        }
        else if(node.right == null && hot.left.key.equals(key)){
            hot.left = node.left;
            return node;
        }
        else if(node.right == null && hot.right.key.equals(key)){
            hot.right = node.left;
            return node;
        }

        BSTNode succ = findSucc(node);
        if(succ == null){
            return null;
        }

        K tmpKey = succ.key;
        succ.key = node.key;
        node.key = tmpKey;

        V tmpValue = succ.value;
        succ.value = node.value;
        node.value = tmpValue;

        if(succ.right != null){
            hot.left = succ.right;
            return succ;
        }

        hot.left = null;
        return succ;
    }

    private BSTNode findSucc(BSTNode node){
        if (node.right == null){
            return null;
        }

        hot = node;
        node = node.right;
        while(node.left != null){
            hot = node;
            node = node.left;
        }

        return node;
    }

    private boolean find(K key){
        if (size == 0){
            return false;
        }

        hot = null;
        BSTNode node = root;
        while(node != null && !node.key.equals(key)){
            hot = node;

            if(node.key.compareTo(key) < 0){
                node = node.right;
            }
            else{
                node = node.left;
            }
        }

        return node != null;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return find(key);
    }

    @Override
    public V get(K key) {
        boolean found = find(key);
        if(!found){
            return null;
        }

        return hot.left.key.equals(key) ? hot.left.value : hot.right.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        insert(key, value);
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public V remove(K key) {


        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
