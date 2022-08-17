package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest(){
        ArrayDequeSolution<Integer> L = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> L1 = new StudentArrayDeque<>();

        StringBuilder stringBuilder = new StringBuilder();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L1.addLast(randVal);
                stringBuilder.append("addLast(").append(randVal).append(")\n");
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                L1.addFirst(randVal);
                stringBuilder.append("addFirst(").append(randVal).append(")\n");
            } else if (operationNumber == 2) {
                // size
                int size = L.size();
                int size1 = L1.size();
                stringBuilder.append("size()\n");
                assertEquals(stringBuilder.toString(), size, size1);
            }  else if (operationNumber == 3) {
                // size
                boolean empty = L.isEmpty();
                boolean empty1 = L1.isEmpty();
                stringBuilder.append("isEmpty()\n");
                assertEquals(stringBuilder.toString(), empty, empty1);
            } else if (operationNumber == 4 && L.size() > 0 && L1.size() > 0){
                int index = StdRandom.uniform(0, Math.min(L.size(), L1.size()));
                Integer elem = L.get(index);
                Integer elem1 = L1.get(index);
                stringBuilder.append("get(").append(index).append(")\n");
                assertEquals(stringBuilder.toString(), elem, elem1);
            } else if (operationNumber == 5 && L.size() > 0 && L1.size() > 0){
                Integer last = L.removeLast();
                Integer last1 = L1.removeLast();
                stringBuilder.append("removeLast()\n");
                assertEquals(stringBuilder.toString(), last, last1);
            } else if (operationNumber == 6 && L.size() > 0 && L1.size() > 0){
                Integer first = L.removeFirst();
                Integer first1 = L1.removeFirst();
                stringBuilder.append("removeFirst()\n");
                assertEquals(stringBuilder.toString(), first, first1);
            }
        }
    }
}
