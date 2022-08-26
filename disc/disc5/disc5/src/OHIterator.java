import java.util.Iterator;
import java.util.NoSuchElementException;

public class OHIterator implements Iterator<OHRequest> {
    OHRequest curr;

    public OHIterator(OHRequest queue) {
        curr = queue;
    }

    public boolean isGood(String description) {
        return description != null && description.length() > 5;
    }

    @Override
    public boolean hasNext(){
        return curr != null;
    }

    @Override
    public OHRequest next(){
        if(!hasNext()){
            throw new NoSuchElementException();
        }

        OHRequest ohRequest = curr;

        curr = curr.next;
        while(curr != null && !isGood(curr.description)){
            curr = curr.next;
        }

        return ohRequest;

    }
}
