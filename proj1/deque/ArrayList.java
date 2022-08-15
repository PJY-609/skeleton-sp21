package deque;

public class ArrayList<T>{
    private final int RFACTOR = 2;
    private final int MIN_CAPACITY = 8;

    private int size;
    private int capacity = MIN_CAPACITY;
    private T[] items;

    private int nextFirst;
    private int nextLast;

    public ArrayList(){
        items = (T[]) new Object[capacity];
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T item){
        if(size + 1 >= capacity){
            resize(capacity * RFACTOR);
        }

        size++;
        items[nextFirst] = item;
        nextFirst = (capacity + (nextFirst - 1)) % capacity;
    }

    public void addLast(T item){
        if(size + 1 >= capacity){
            resize(capacity * RFACTOR);
        }

        size++;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % capacity;
    }

    private void resize(int capacity){
        if(capacity <= MIN_CAPACITY){
            return;
        }

        this.capacity = capacity;
        T[] resized = (T[]) new Object[this.capacity];

        for(int i = 0; i < size; i++){
            resized[i] = get(i);
        }

        nextFirst = capacity - 1;
        nextLast = size;

        items = resized;
    }


    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public T get(int index){
        if(index < 0 || size <= index){
            return null;
        }

        return items[(nextFirst + 1 + index) % capacity];
    }

    public T removeFirst(){
        if(isEmpty()){
            return null;
        }

        if((size - 1) < capacity / RFACTOR){
            resize(capacity / RFACTOR);
        }

        size--;
        T value = get(0);
        nextFirst = (nextFirst + 1) % capacity;
        return value;
    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }

        if((size - 1) < capacity / RFACTOR){
            resize(capacity / RFACTOR);
        }

        size--;
        T value = get(nextLast - 1);
        nextLast = (capacity + nextLast - 1) % capacity;
        return value;
    }

    
}
