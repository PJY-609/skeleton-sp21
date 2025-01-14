package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>{
    private int size;
    private Node<T> sentinelFront = new Node<T>();
    private Node<T> sentinelBack = new Node<T>();

    private static class Node<T>{
        T value;
        Node<T> pred;
        Node<T> succ;

        public Node(){
            value = null;
            pred = null;
            succ = null;
        }

    }

    public LinkedListDeque(){
        size = 0;
        sentinelFront.succ = sentinelBack;
        sentinelBack.pred = sentinelFront;
    }

    @Override
    public void addFirst(T item){
        size++;

        Node<T> node = new Node<>();
        node.value = item;

        // fix succ
        node.succ = sentinelFront.succ;
        sentinelFront.succ = node;

        // fix pred
        node.pred = sentinelFront;
        node.succ.pred = node;
    }

    @Override
    public void addLast(T item){
        size++;

        Node<T> node = new Node<>();
        node.value = item;

        // fix pred
        node.pred = sentinelBack.pred;
        sentinelBack.pred = node;

        // fix succ
        node.succ = sentinelBack;
        node.pred.succ = node;
    }

//    @Override
//    public boolean isEmpty(){
//        return size == 0;
//    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        Node<T> node = sentinelFront.succ;
        while(node != sentinelBack && node.succ != sentinelBack){
            System.out.print(node.value + " ");
            node = node.succ;
        }

        System.out.print(node.value + "\n");
    }

    @Override
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }

        size--;

        Node<T> node = sentinelFront.succ;
        sentinelFront.succ = node.succ;
        node.succ.pred = sentinelFront;

        T value = node.value;
        node = null;

        return value;
    }

    @Override
    public T removeLast(){
        if(isEmpty()){
            return null;
        }

        size--;

        Node<T> node = sentinelBack.pred;
        sentinelBack.pred = node.pred;
        node.pred.succ = sentinelBack;

        T value = node.value;
        node = null;
        return value;
    }

    @Override
    public T get(int index){
        if (index < 0 || size <= index){
            return null;
        }

        if(index < (size - index)){
            return getFromFront(index);
        }

        return getFromBack(index);
    }

    private T getFromFront(int index){
        if (index < 0 || size <= index){
            return null;
        }

        Node<T> node = sentinelFront.succ;

        for(int i = 0; i < index; i++){
            node = node.succ;
        }

        return node.value;
    }

    private T getFromBack(int index){
        if (index < 0 || size <= index){
            return null;
        }

        Node<T> node = sentinelBack.pred;

        for(int i = 0; i < (size - 1 - index); i++){
            node = node.pred;
        }

        return node.value;
    }

    public T getRecursive(int index){
        if (index < 0 || size <= index){
            return null;
        }

        if(index < (size - index)){
            return getFromFrontRecursive(sentinelFront.succ, index);
        }

        return getFromBackRecursive(sentinelBack.pred, size - 1 - index);
    }

    private T getFromFrontRecursive(Node<T> node, int index){
        if(index < 0 || size <= index || node == null){
            return null;
        } else if(index == 0){
            return node.value;
        } else{
            return getFromFrontRecursive(node.succ, index - 1);
        }
    }

    private T getFromBackRecursive(Node<T> node, int index){
        if(index < 0 || size <= index || node == null){
            return null;
        } else if(index == 0){
            return node.value;
        } else{
            return getFromBackRecursive(node.pred, index - 1);
        }
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof LinkedListDeque)){
            return false;
        }

        LinkedListDeque<?> deque = (LinkedListDeque<?>) o;
        if(size() != deque.size()){
            return false;
        }

        for(int i = 0; i < size(); i++){
            if(!get(i).equals(deque.get(i))){
                return false;
            }
        }

        return true;
    }

    public Iterator<T> iterator(){
        return new Iterator<T>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return 0 <= index && index < size;
            }

            @Override
            public T next() {
                if(hasNext()){
                    T value = get(index);
                    index++;
                    return value;
                }

                return null;
            }
        };
    }
}
