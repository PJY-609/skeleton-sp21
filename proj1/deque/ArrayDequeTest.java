package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Iterator;
import java.util.Optional;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void addFirstTest(){
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for(int i = 0; i < 8; i++){
            assertEquals(arrayDeque.size(), i);
            arrayDeque.addFirst(i + 1);
        };

        for(int i = 0; i < 8; i++){
//            assertEquals(arrayDeque.size(), i);
            Integer a = arrayDeque.removeFirst();
            assertNotNull(a);
        };
    }

    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> L = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            } else if (operationNumber == 2) {
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                System.out.println("addFirst(" + randVal + ")");
            } else if (operationNumber == 3 && L.size() > 0){
                Integer last = L.removeLast();
                System.out.println("removeLast: " + last);
            } else if (operationNumber == 4 && L.size() > 0){
                Integer last = L.removeFirst();
                System.out.println("removeFirst: " + last);
            }
        }
    }

    @Test
    public void testEquals(){
        ArrayDeque<Integer> arrayDeque1 = new ArrayDeque<>();
        ArrayDeque<Integer> arrayDeque2 = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                arrayDeque1.addLast(randVal);
                arrayDeque2.addLast(randVal);
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                arrayDeque1.addFirst(randVal);
                arrayDeque2.addFirst(randVal);
            } else if (operationNumber == 3 && arrayDeque1.size() > 0 && arrayDeque2.size() > 0){
                arrayDeque1.removeLast();
                arrayDeque2.removeLast();
            } else if (operationNumber == 4 && arrayDeque1.size() > 0 && arrayDeque1.size() > 0){
                arrayDeque1.removeFirst();
                arrayDeque2.removeFirst();
            }
        }

        assertEquals(arrayDeque1, arrayDeque2);

        arrayDeque2.addFirst(0);
        assertNotEquals(arrayDeque1, arrayDeque2);
    }

    @Test
    public void testIterator(){
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                arrayDeque.addLast(randVal);
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                arrayDeque.addFirst(randVal);
            } else if (operationNumber == 3 && arrayDeque.size() > 0){
                arrayDeque.removeLast();
            } else if (operationNumber == 4 && arrayDeque.size() > 0){
                arrayDeque.removeFirst();
            }
        }

        Iterator<Integer> iter = arrayDeque.iterator();

        int i = 0;
        while(iter.hasNext()){
            assertEquals(iter.next(), arrayDeque.get(i++));
        }
    }
}
