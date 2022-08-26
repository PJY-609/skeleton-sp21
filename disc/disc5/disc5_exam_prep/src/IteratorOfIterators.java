import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorOfIterators implements Iterator<Integer> {
    private List<Iterator<Integer>> list;
    private int currIterIndex;

    public IteratorOfIterators(List<Iterator<Integer>> a) {
        list = a;
        currIterIndex = 0;
    }

    @Override
    public boolean hasNext(){
        if(list.isEmpty()){
            return false;
        }

        Iterator<Integer> currIter = list.get(currIterIndex);

        int startIndex = currIterIndex;
        while(currIter == null || !currIter.hasNext()){
            currIterIndex = (currIterIndex + 1) % list.size();

            if(startIndex == currIterIndex){
                return false;
            }

            currIter = list.get(currIterIndex);
        }

        return true;
    }

    @Override
    public Integer next(){
        if(!hasNext()){
            throw new NoSuchElementException();
        }

        Iterator<Integer> iter = list.get(currIterIndex);

        currIterIndex = (currIterIndex + 1) % list.size();
        return iter.next();
    }

    public static void main(String[] args){
        List<Iterator<Integer>> list = new ArrayList<>();

        List<Integer> l1 = new ArrayList<>();
        l1.add(1);
        l1.add(2);
        l1.add(3);
        l1.add(4);

        List<Integer> l2 = new ArrayList<>();
        l2.add(5);
        l2.add(6);
        l2.add(7);

        List<Integer> l3 = new ArrayList<>();
        l3.add(8);
        l3.add(9);

        List<Integer> l4 = new ArrayList<>();
        l4.add(10);

        List<Integer> l5 = new ArrayList<>();


        list.add(l1.iterator());
        list.add(l2.iterator());
        list.add(l3.iterator());
        list.add(l4.iterator());
        list.add(l5.iterator());

        IteratorOfIterators iterOfIters = new IteratorOfIterators(list);

        while(iterOfIters.hasNext()){
            int val = iterOfIters.next();

            System.out.println(val);
        }
    }
}
