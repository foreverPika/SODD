package contexttree;

import dependencydiscover.predicate.Operator;
import dependencydiscover.predicate.SingleAttributePredicate;
import sodd.AttributePair;
import sodd.AttributeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OcdSet {
    public HashMap<AttributePair, ContextSet> ocd2context;
    final private int columnNum;
    double maxThreshold = 0.9;
    double minThreshold = 0.3;
    public List<Double> nothingNonOcdTimes;

    public OcdSet(AttributeSet schema) {
        this.ocd2context = new HashMap<>();
        this.columnNum = schema.getSize();
        for (int i = 0; i < columnNum; i++) {
            for (int j = i + 1; j < columnNum; j++) {
                AttributePair attributePair = new AttributePair(new SingleAttributePredicate(i, Operator.LESSEQUAL), j);
                ocd2context.put(attributePair, new ContextSet(columnNum - 2, columnNum - 2));
                attributePair = new AttributePair(new SingleAttributePredicate(i, Operator.GREATEREQUAL), j);
                ocd2context.put(attributePair, new ContextSet(columnNum - 2, columnNum - 2));
            }
        }
        this.nothingNonOcdTimes = new ArrayList<>();
        for (int i = 0; i <= columnNum-2; i++){
            this.nothingNonOcdTimes.add(minThreshold + (maxThreshold - minThreshold) * ((double) i / (columnNum-2)));
        }
    }
    public double getThreshold(int level){
        return nothingNonOcdTimes.get(level);
    }
    public boolean add(AttributeSet context, AttributePair ocd) {
        if(ocd2context.containsKey(ocd)){
            return ocd2context.get(ocd).add(context);
        }
        return false;
    }

    public void clear() {
        for (ContextSet contextSet : ocd2context.values()) {
            contextSet.clear();
        }
    }
    public boolean remove(AttributePair ocd){
        if(ocd2context.containsKey(ocd)){
            ocd2context.remove(ocd);
            return true;
        }
        return false;
    }
    public void clean(AttributePair ocd){
        ocd2context.get(ocd).clear();
    }
    public ContextSet getContextSet(AttributePair ocd){
        if(ocd2context.containsKey(ocd)){
            return ocd2context.get(ocd);
        }
        return null;
    }
}
