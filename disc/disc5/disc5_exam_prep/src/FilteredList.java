import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FilteredList<T> implements Iterable<T> {
    private List<T> list;
    private Predicate<T> filter;

    public FilteredList (List<T> L, Predicate<T> filter) {
        this.list = L;
        this.filter = filter;
    }

    private class FilteredIterator implements Iterator<T>{
        private int index = 0;

        @Override
        public boolean hasNext(){
            while(index < list.size() && !filter.test(list.get(index))){
                index++;
            }

            return index < list.size();
        }

        @Override
        public T next(){
            if(!hasNext()){
                throw new NoSuchElementException();
            }

            return list.get(index);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new FilteredIterator();
    }
}
