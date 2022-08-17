package deque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

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
}