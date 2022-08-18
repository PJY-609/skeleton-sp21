package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>{
    private final int RFACTOR = 2;
    private final int MIN_CAPACITY = 8;

    protected int size = 0;
    private int capacity = MIN_CAPACITY;
    private T[] items;

    private int nextFirst;
    private int nextLast;

    public ArrayDeque(){
        items = (T[]) new Object[capacity];
        nextFirst = 0;
        nextLast = 1;
    }

    @Override
    public void addFirst(T item){
        if(size + 1 >= capacity){
            resize(capacity * RFACTOR);
        }

        size++;
        items[nextFirst] = item;
        nextFirst = (capacity + (nextFirst - 1)) % capacity;
    }

    @Override
    public void addLast(T item){
        if(size + 1 >= capacity){
            resize(capacity * RFACTOR);
        }

        size++;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % capacity;
    }


    private void resize(int capacity){
        if(capacity <= MIN_CAPACITY || capacity <= size){
            return;
        }

        T[] resized = (T[]) new Object[capacity];

        for(int i = 0; i < size; i++){
            resized[i] = get(i);
        }

        this.capacity = capacity;
        nextFirst = this.capacity - 1;
        nextLast = size;

        items = resized;
    }


    @Override
    public int size(){
        return size;
    }

//    @Override
//    public boolean isEmpty(){
//        return size == 0;
//    }

    @Override
    public T get(int index){
        if(index < 0 || size <= index){
            return null;
        }

        return items[(nextFirst + 1 + index) % capacity];
    }

    @Override
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }

        if(2 * (size - 1) < capacity / RFACTOR){
            resize(capacity / RFACTOR);
        }

        T value = get(0);
        nextFirst = (nextFirst + 1) % capacity;
        size--;
        return value;
    }

    @Override
    public T removeLast(){
        if(isEmpty()){
            return null;
        }

        if((size - 1) < capacity / RFACTOR){
            resize(capacity / RFACTOR);
        }

        T value = get(size - 1);
        nextLast = (capacity + nextLast - 1) % capacity;
        size--;
        return value;
    }

    @Override
    public void printDeque(){
        for(int i = 0; i < size - 1; i++){
            System.out.print(get(i) + " ");
        }
        System.out.print(get(size - 1) + "\n");
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof ArrayDeque)){
            return false;
        }

        ArrayDeque<?> deque = (ArrayDeque<?>) o;
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
