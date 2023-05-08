package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

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

    class KeyIterator implements Iterator<K>{
        Stack<BSTNode> stack = new Stack<>();

        public KeyIterator(BSTNode root) {
            BSTNode node = root;

            while (node != null){
                stack.push(node);
                node = node.left;
            }
        }

        public boolean hasNext() {
            return !stack.empty();
        }

        public K next() {
            BSTNode node = stack.pop();
            K key = node.key;
            node = node.right;

            while(node != null){
                stack.push(node);
                node = node.left;
            }

            return key;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private BSTNode root = null;

    private int size = 0;

    public BSTMap(){}

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (size == 0){
            return false;
        }

        BSTNode node = root;

        while (node != null && !node.key.equals(key)){
            if (node.key.compareTo(key) < 0){
                node = node.right;
            }
            else {
                node = node.left;
            }
        }

        return (node != null);
    }

    @Override
    public V get(K key) {
        if (size == 0){
            return null;
        }

        BSTNode node = root;

        while (node != null && !node.key.equals(key)) {
            if (node.key.compareTo(key) < 0) {
                node = node.right;
            }
            else {
                node = node.left;
            }
        }

        if (node == null){
            return null;
        }

        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        size += 1;

        if (root == null){
            root = new BSTNode(key, value);
            return;
        }

        BSTNode node = root;
        BSTNode next = root;

        while (next != null && !node.key.equals(key)){
            node = next;
            if (node.key.compareTo(key) < 0){
                next = node.right;
            }
            else{
                next = node.left;
            }
        }

        if (node.key.compareTo(key) < 0){
            node.right = new BSTNode(key, value);
        }
        else if(node.key.compareTo(key) > 0){
            node.left = new BSTNode(key, value);
        }
        else{
            node.value = value;
        }


    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (K k : this) {
            set.add(k);
        }
        return set;
    }

    @Override
    public V remove(K key) {
        if (size == 0){
            return null;
        }

        BSTNode prev = null;
        BSTNode node = root;

        while (node != null && !node.key.equals(key)) {
            if (node.key.compareTo(key) < 0) {
                prev = node;
                node = node.right;
            }
            else {
                prev = node;
                node = node.left;
            }
        }

        // not found
        if (node == null){
            return null;
        }

        size -= 1;

        // remove node with both children
        if (node.left != null && node.right != null){
            prev = node;
            BSTNode succ = node.right;

            while (succ.left != null){
                prev = succ;
                succ = succ.left;
            }

            node.key = succ.key;
            node.value = succ.value;
            node = succ;
        }

        // remove leaf
        if (node.left == null && node.right == null){
            if (prev != null && prev.left != null && prev.left.key.equals(key)){
                prev.left = null;
            }
            else if (prev != null && prev.right != null && prev.right.key.equals(key)){
                prev.right = null;
            }
            return node.value;
        }

        // remove node with one child
        if (prev == null){
            root = (node.left != null) ? node.left : node.right;
        }
        else if (prev.left != null && prev.left.key.equals(key)){
            prev.left = (node.left != null) ? node.left : node.right;
        }
        else if (prev.right != null && prev.right.key.equals(key)){
            prev.right = (node.left != null) ? node.left : node.right;
        }
        return node.value;
    }



    @Override
    public V remove(K key, V value) {
        if (size == 0){
            return null;
        }

        BSTNode prev = null;
        BSTNode node = root;

        while (node != null && !node.key.equals(key)) {
            if (node.key.compareTo(key) < 0) {
                prev = node;
                node = node.right;
            }
            else {
                prev = node;
                node = node.left;
            }
        }

        // not found
        if (node == null || node.value != value){
            return null;
        }

        size -= 1;

        // remove node with both children
        if (node.left != null && node.right != null){
            prev = node;
            BSTNode succ = node.right;

            while (succ.left != null){
                prev = succ;
                succ = succ.left;
            }

            node.key = succ.key;
            node.value = succ.value;
            node = succ;
        }

        // remove leaf
        if (node.left == null && node.right == null){
            if (prev != null && prev.left != null && prev.left.key.equals(key)){
                prev.left = null;
            }
            else if (prev != null && prev.right != null && prev.right.key.equals(key)){
                prev.right = null;
            }
            return node.value;
        }

        // remove node with one child
        if (prev == null){
            root = (node.left != null) ? node.left : node.right;
        }
        else if (prev.left != null && prev.left.key.equals(key)){
            prev.left = (node.left != null) ? node.left : node.right;
        }
        else if (prev.right != null && prev.right.key.equals(key)){
            prev.right = (node.left != null) ? node.left : node.right;
        }
        return node.value;
    }

    @Override
    public Iterator<K> iterator() {
        return new KeyIterator(root);
    }
}
