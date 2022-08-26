import java.util.NoSuchElementException;

public class TYIterator extends OHIterator{
    public TYIterator(OHRequest queue) {
        super(queue);
    }

    @Override
    public OHRequest next(){
        OHRequest nextOHRequest = super.next();

        if(nextOHRequest.description.contains("thank u")){
            super.next();
        }

        return nextOHRequest;
    }
}
