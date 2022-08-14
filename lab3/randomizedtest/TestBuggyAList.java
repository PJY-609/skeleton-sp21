package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> aList1 = new AListNoResizing<>();
        BuggyAList<Integer> aList2 = new BuggyAList<>();

        aList1.addLast(4);
        aList1.addLast(5);
        aList1.addLast(6);

        aList2.addLast(4);
        aList2.addLast(5);
        aList2.addLast(6);

        for(int i = 0; i < 3; i++){
            assertEquals(aList1.removeLast(), aList2.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L1 = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L1.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size1 = L1.size();
                assertEquals(size, size1);
                System.out.println("size: " + size + " size1: " + size1);
            } else if (operationNumber == 2 && L.size() > 0){
                int last = L.getLast();
                int last1 = L1.getLast();
                System.out.println("getLast: " + last + " getLast1: " + last1);
            } else if (operationNumber == 3 && L.size() > 0){
                int last = L.removeLast();
                int last1 = L1.removeLast();
                assertEquals(last, last1);
                System.out.println("removeLast: " + last + " removeLast1: " + last1);
            }
        }
    }
}
