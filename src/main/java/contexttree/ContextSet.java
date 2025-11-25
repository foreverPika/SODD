package contexttree;
import sodd.AttributeSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ContextSet {

    private List<HashSet<AttributeSet>> ocdLevels;

    private int depth = 0;
    private int maxDepth;
//    public boolean isChange;

    public ContextSet(int numAttributes, int maxDepth) {
        this.maxDepth = maxDepth;
        this.ocdLevels = new ArrayList<HashSet<AttributeSet>>(numAttributes+1);
        for (int i = 0; i <= numAttributes; i++){
            this.ocdLevels.add(new HashSet<AttributeSet>());
        }
//        isChange = false;
    }

    public List<HashSet<AttributeSet>> getOcdLevels() {
        return this.ocdLevels;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public boolean add(AttributeSet context) {
        int length = context.getSize();

//        if ((this.maxDepth > 0) && (length > this.maxDepth))
//            return false;
//
//        this.depth = Math.max(this.depth, length);
//        isChange = true;
        return this.ocdLevels.get(length).add(context);
    }

    public boolean contains(AttributeSet context) {
        int length = context.getSize();

        if ((this.maxDepth > 0) && (length > this.maxDepth))
            return false;

        return this.ocdLevels.get(length).contains(context);
    }

//    public void trim(int newDepth) {
//        while (this.ocdLevels.size() > (newDepth + 1)) // +1 because uccLevels contains level 0
//            this.ocdLevels.remove(this.ocdLevels.size() - 1);
//
//        this.depth = newDepth;
//        this.maxDepth = newDepth;
//    }

    public void clear() {
        int numLevels = this.ocdLevels.size();
        this.ocdLevels = new ArrayList<HashSet<AttributeSet>>(numLevels);
        for (int i = 0; i < numLevels; i++)
            this.ocdLevels.add(new HashSet<AttributeSet>());
        this.depth = 0;
//        isChange = false;
    }

    public int size() {
        int size = 0;
        for (HashSet<AttributeSet> contexts : this.ocdLevels)
            size += contexts.size();
        return size;
    }
}