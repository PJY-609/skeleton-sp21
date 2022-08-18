package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Comparator;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void testMaxInteger(){
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        Integer max = Integer.MIN_VALUE;
        Integer min = Integer.MAX_VALUE;
        int N = 1000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                deque.addLast(randVal);
                max = Math.max(max, randVal);
                min = Math.min(min, randVal);
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                deque.addFirst(randVal);
                max = Math.max(max, randVal);
                min = Math.min(min, randVal);
            }
        }

        Integer maxValue = deque.max();
        Integer minValue = deque.max(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        assertEquals(max, maxValue);
        assertEquals(min, minValue);

    }

    @Test
    public void testMaxDouble(){
        MaxArrayDeque<Double> deque = new MaxArrayDeque<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return (o1 - o2 > 1e-6) ? 1 : ((o1 - o2) > 0 ? 0 : -1);
            }
        });

        Double max = Double.MIN_VALUE;
        Double min = Double.MAX_VALUE;
        int N = 1000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                double randVal = StdRandom.uniform(0., 100.);
                deque.addLast(randVal);
                max = Math.max(max, randVal);
                min = Math.min(min, randVal);
            } else if (operationNumber == 1) {
                double randVal = StdRandom.uniform(0., 100.);
                deque.addFirst(randVal);
                max = Math.max(max, randVal);
                min = Math.min(min, randVal);
            }
        }

        Double maxValue = deque.max();
        Double minValue = deque.max(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return (o1 - o2 > 1e-6) ? -1 : ((o1 - o2) > 0 ? 0 : 1);
            }
        });

        assertEquals(max, maxValue);
        assertEquals(min, minValue);

    }
}
