package pli;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import contexttree.AllOcdTree;
import dependencydiscover.dataframe.DataFrame;
import dependencydiscover.predicate.Operator;
import dependencydiscover.predicate.SingleAttributePredicate;
import sodd.AttributeSet;
import sodd.DataAndDataAndIndex;
import util.Timer;

public class PLI {

    private List<Integer> indexes;

    private List<Integer> begins;
    private static DataFrame data;
    final private static int singletonValueId = 0;

    public static long mergeTime = 0;
    public static long validateTime = 0;
    public static long cloneTime = 0;
    public static final int CACHE_SIZE = 10000;
    private int[] probingTableCache;

    private static final ConcurrentHashMap<AttributeSet, PLI> sampleCache = new ConcurrentHashMap<AttributeSet, PLI>() {
    };

    public static final PLICache cache = new PLICache() {};

    public PLI() {
    }

    public PLI(DataFrame data) {
        indexes = new ArrayList<>();
        for (int i = 0; i < data.getTupleCount(); i++) {
            indexes.add(i);
        }
        begins = new ArrayList<>();
        if (data.getTupleCount() != 0) {
            begins.add(0);
        }
        begins.add(data.getTupleCount());
    }

    public PLI(PLI origin) {
        this.indexes = new ArrayList<>(origin.indexes);
        this.begins = new ArrayList<>(origin.begins);
        if (origin.probingTableCache != null) {
            this.probingTableCache = new int[origin.probingTableCache.length];
            System.arraycopy(origin.probingTableCache, 0, this.probingTableCache, 0, origin.probingTableCache.length);
        } else {
            this.probingTableCache = null;
        }
    }


    //one attribute merge
    public PLI product(int attribute) {
        util.Timer timer = new util.Timer();
        List<Integer> newIndexes = new ArrayList<>();
        List<Integer> newBegins = new ArrayList<>();
        int fillPointer = 0;

        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);
            HashMap<Integer, List<Integer>> subGroups = new HashMap<>();

            for (int i = groupBegin; i < groupEnd; i++) {
                int index = indexes.get(i);
                int value = data.get(index, attribute);

                if (!subGroups.containsKey(value)) {
                    subGroups.put(value, new ArrayList<>());
                }
                subGroups.get(value).add(index);
            }

            for (List<Integer> newGroup : subGroups.values()) {
                if (newGroup.size() > 1) {
                    newBegins.add(fillPointer);
                    for (int i : newGroup) {
                        newIndexes.add(i);
                        fillPointer++;
                    }
                }
            }
        }
        this.indexes = newIndexes;

        this.begins = newBegins;
        begins.add(indexes.size());

        mergeTime += timer.getTimeUsed();
        return this;
    }

    public PLI product(PLI sp) {
        util.Timer timer = new util.Timer();
        List<Integer> newIndexes = new ArrayList<>();
        List<Integer> newBegins = new ArrayList<>();
        int fillPointer = 0;
        int[] probingTable = sp.getProbingTable();
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);
            HashMap<Integer, List<Integer>> subGroups = new HashMap<>();
            for (int i = groupBegin; i < groupEnd; i++) {
                int index = indexes.get(i);
                int value = probingTable[index];
                if (value == singletonValueId) continue;
                if (!subGroups.containsKey(value)) {
                    subGroups.put(value, new ArrayList<>());
                }
                subGroups.get(value).add(index);
            }
            for (List<Integer> newGroup : subGroups.values()) {
                if (newGroup.size() > 1) {
                    newBegins.add(fillPointer);
                    for (int i : newGroup) {
                        newIndexes.add(i);
                        fillPointer++;
                    }
                }
            }
        }

        this.indexes = newIndexes;

        this.begins = newBegins;
        begins.add(indexes.size());

        mergeTime += timer.getTimeUsed();
        return this;
    }


    public Map<Integer,Set<Integer>> splitForSample(int right, int vioNum) {
        util.Timer timer = new util.Timer();
        HashMap<Integer, Integer> index2length = new HashMap<>();
        HashMap<Integer, Integer> index2vioindex = new HashMap<>();
        ArrayList<Integer> vioindex = new ArrayList<>();
        int minlength = 0;
        int minindex = 0;
        Set<Integer> viorows = new HashSet<>();
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);

            int groupValue = data.get(indexes.get(groupBegin), right);
            for (int i = groupBegin + 1; i < groupEnd; i++) {
                int index = indexes.get(i);
                int value = data.get(index, right);
                if (value != groupValue) {
                    //temp is the num of same value
                    int temp = trueContextLength(index, indexes.get(groupBegin), right);
                    if (vioindex.size() < vioNum) {
                        vioindex.add(index);
                        index2vioindex.put(index, indexes.get(groupBegin));
                        index2length.put(index, temp);
                        if (temp < minlength) {
                            minindex = index;
                            minlength = temp;
                        }
                    } else if (temp > minlength) {
                        vioindex.remove(new Integer(minindex));
                        index2vioindex.remove(minindex);
                        index2length.remove(minindex);
                        vioindex.add(index);
                        index2vioindex.put(index, indexes.get(groupBegin));
                        index2length.put(index, temp);
                        minlength = temp;
                        minindex = index;
                        for (int k : vioindex) {
                            if (index2length.get(k) < minlength) {
                                minindex = k;
                                minlength = index2length.get(k);
                            }
                        }
                    }
                }
            }
        }
        Map<Integer,Set<Integer>> vioMap = new HashMap<>();
        for (int index : vioindex) {
            int indexKey = index2vioindex.get(index);
            if(!vioMap.containsKey(indexKey)){
                Set<Integer> indexValue = new HashSet<>();
                indexValue.add(index);
                vioMap.put(indexKey,indexValue);
            }else{
                vioMap.get(indexKey).add(index);
            }
        }

//        for (int index : vioindex) {
//            viorows.add(index);
//            viorows.add(index2vioindex.get(index));
//        }
        validateTime += timer.getTimeUsed();
        return vioMap;
    }
    public Map<Integer,Set<Integer>> splitForSampleRandom(int right, int vioNum) {
        util.Timer timer = new util.Timer();
        HashMap<Integer, Integer> index2length = new HashMap<>();
        HashMap<Integer, Integer> index2vioindex = new HashMap<>();
        ArrayList<Integer> vioindex = new ArrayList<>();
        int minlength = 0;
        int minindex = 0;
        Set<Integer> viorows = new HashSet<>();
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);

            int groupValue = data.get(indexes.get(groupBegin), right);
            for (int i = groupBegin + 1; i < groupEnd; i++) {
                int index = indexes.get(i);
                int value = data.get(index, right);
                if (value != groupValue) {
                    int temp = trueContextLength(index, indexes.get(groupBegin), right);
                    if (vioindex.size() < vioNum) {
                        vioindex.add(index);
                        index2vioindex.put(index, indexes.get(groupBegin));
                        index2length.put(index, temp);
                        if (temp < minlength) {
                            minindex = index;
                            minlength = temp;
                        }
                    }
                    if(vioindex.size() == vioNum){
//                        Map<Integer,Set<Integer>> vioMap = new HashMap<>();
//                        Set<Integer> s = new HashSet<>();
//                        s.add(2);
//                        vioMap.put(1,s);
//                        return vioMap;
                        break;
                    }
                }

            }
            if(vioindex.size() == vioNum){
                break;
            }
        }
        Map<Integer,Set<Integer>> vioMap = new HashMap<>();
        for (int index : vioindex) {
            int indexKey = index2vioindex.get(index);
            if(!vioMap.containsKey(indexKey)){
                Set<Integer> indexValue = new HashSet<>();
                indexValue.add(index);
                vioMap.put(indexKey,indexValue);
            }else{
                vioMap.get(indexKey).add(index);
            }
        }

//        for (int index : vioindex) {
//            viorows.add(index);
//            viorows.add(index2vioindex.get(index));
//        }
        validateTime += timer.getTimeUsed();
        return vioMap;
    }




    public Map<Integer,Set<Integer>> swapForSampleNew(SingleAttributePredicate left, int right, int vioNum) {
        util.Timer timer = new util.Timer();
        Set<Integer> viorows = new HashSet<>();
        HashMap<Integer, Integer> index2length = new HashMap<>();
        HashMap<Integer, Integer> index2vioindex = new HashMap<>();
        ArrayList<Integer> vioindex = new ArrayList<>();
        int minlength = 0;
        int minindex = 0;
        boolean leftFD = true;
        boolean rightFD = true;
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);

            List<DataAndDataAndIndex> values = new ArrayList<>();
            for (int i = groupBegin; i < groupEnd; i++) {
                int index = indexes.get(i);
                values.add(new DataAndDataAndIndex(filteredDataFrameGet(data, index, left), data.get(index, right),
                        index));
            }

            Collections.sort(values);
            int beforeMax = Integer.MIN_VALUE;
            int groupMax = Integer.MIN_VALUE;
            int beforeMaxIndex = -1;
            int groupMaxIndex = -1;
            int rightFdValue = values.get(0).dataRight;
            for (int i = 0; i < values.size(); i++) {
                int r = values.get(i).dataRight;
                if (rightFD && i != 0 && rightFdValue != r) {
                    rightFD = false;
                }
                if (i == 0 || values.get(i - 1).dataLeft != values.get(i).dataLeft) {//left不同
                    if (leftFD && i != 0) {
                        leftFD = false;
                    }
                    if (beforeMax < groupMax) {
                        beforeMax = groupMax;
                        beforeMaxIndex = groupMaxIndex;
                    }
                    groupMax = r;
                    groupMaxIndex = values.get(i).index;
                } else {//left相同看right
                    groupMax = Math.max(groupMax, r);
                    if (groupMax == r)
                        groupMaxIndex = values.get(i).index;
                }
                if (r < beforeMax) {
                    int temp = trueContextLength(values.get(i).index, beforeMaxIndex, right, left.attribute);
                    if(vioindex.isEmpty()){
                        vioindex.add(values.get(i).index);
                        index2vioindex.put(values.get(i).index, beforeMaxIndex);
                        index2length.put(values.get(i).index, temp);
                        minindex = values.get(i).index;
                        minlength = temp;
                    } else if (vioindex.size() < vioNum) {
                        vioindex.add(values.get(i).index);
                        index2vioindex.put(values.get(i).index, beforeMaxIndex);
                        index2length.put(values.get(i).index, temp);
                        if (temp < minlength) {
                            minindex = values.get(i).index;
                            minlength = temp;
                        }
                    } else if (temp > minlength) {
                        vioindex.remove(Integer.valueOf(minindex));
                        index2vioindex.remove(minindex);
                        index2length.remove(minindex);
                        vioindex.add(values.get(i).index);
                        index2vioindex.put(values.get(i).index, beforeMaxIndex);
                        index2length.put(values.get(i).index, temp);
                        minlength = temp;
                        minindex = values.get(i).index;
                        for (int index : vioindex) {
                            if (index2length.get(index) < minlength) {
                                minindex = index;
                                minlength = index2length.get(index);
                            }
                        }
                    }
                }
            }
        }
//        for (int index : vioindex) {
//            viorows.add(index2vioindex.get(index));
//            viorows.add(index);
//        }
        Map<Integer,Set<Integer>> vioMap = new HashMap<>();
        for (int index : vioindex) {
            int indexKey = index2vioindex.get(index);
            if(!vioMap.containsKey(indexKey)){
                Set<Integer> indexValue = new HashSet<>();
                indexValue.add(index);
                vioMap.put(indexKey,indexValue);
            }else{
                vioMap.get(indexKey).add(index);
            }
        }
        if (leftFD) {
            vioMap.put(-1,new HashSet<>());
//            viorows.add(-1);
        }
        if (rightFD) {
            vioMap.put(-2,new HashSet<>());
        }
        validateTime += timer.getTimeUsed();
        return vioMap;
    }
    public Map<Integer,Set<Integer>> swapForSampleRandom(SingleAttributePredicate left, int right, int vioNum) {
        util.Timer timer = new util.Timer();
        Set<Integer> viorows = new HashSet<>();
        HashMap<Integer, Integer> index2length = new HashMap<>();
        HashMap<Integer, Integer> index2vioindex = new HashMap<>();
        ArrayList<Integer> vioindex = new ArrayList<>();
        int minlength = 0;
        int minindex = 0;
        boolean leftFD = true;
        boolean rightFD = true;
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);

            List<DataAndDataAndIndex> values = new ArrayList<>();
            for (int i = groupBegin; i < groupEnd; i++) {
                int index = indexes.get(i);
                values.add(new DataAndDataAndIndex(filteredDataFrameGet(data, index, left), data.get(index, right),
                        index));
            }

            Collections.sort(values);
            int beforeMax = Integer.MIN_VALUE;
            int groupMax = Integer.MIN_VALUE;
            int beforeMaxIndex = -1;
            int groupMaxIndex = -1;
            int rightFdValue = values.get(0).dataRight;
            for (int i = 0; i < values.size(); i++) {
                int r = values.get(i).dataRight;
                if (rightFD && i != 0 && rightFdValue != r) {
                    rightFD = false;
                }
                if (i == 0 || values.get(i - 1).dataLeft != values.get(i).dataLeft) {//left不同
                    if (leftFD && i != 0) {
                        leftFD = false;
                    }
                    if (beforeMax < groupMax) {
                        beforeMax = groupMax;
                        beforeMaxIndex = groupMaxIndex;
                    }
                    groupMax = r;
                    groupMaxIndex = values.get(i).index;
                } else {//left相同看right
                    groupMax = Math.max(groupMax, r);
                    if (groupMax == r)
                        groupMaxIndex = values.get(i).index;
                }
                if (r < beforeMax) {
                    int temp = trueContextLength(values.get(i).index, beforeMaxIndex, right, left.attribute);
                    if (vioindex.size() < vioNum) {
                        vioindex.add(values.get(i).index);
                        index2vioindex.put(values.get(i).index, beforeMaxIndex);
                        index2length.put(values.get(i).index, temp);
                        if (temp < minlength) {
                            minindex = values.get(i).index;
                            minlength = temp;
                        }
                    }
                    if(vioindex.size()==vioNum){
//                        Map<Integer,Set<Integer>> vioMap = new HashMap<>();
//                        Set<Integer> s = new HashSet<>();
//                        s.add(2);
//                        vioMap.put(1,s);
//                        return vioMap;
                        break;
                    }
                }
            }
            if(vioindex.size()==vioNum){
                break;
            }
        }
        Map<Integer,Set<Integer>> vioMap = new HashMap<>();
        for (int index : vioindex) {
            int indexKey = index2vioindex.get(index);
            if(!vioMap.containsKey(indexKey)){
                Set<Integer> indexValue = new HashSet<>();
                indexValue.add(index);
                vioMap.put(indexKey,indexValue);
            }else{
                vioMap.get(indexKey).add(index);
            }
        }
        if (leftFD) {
            vioMap.put(-1,new HashSet<>());
        }
        if (rightFD) {
            vioMap.put(-2,new HashSet<>());
        }
        validateTime += timer.getTimeUsed();
        return vioMap;
    }



    @Override
    public String toString() {
        return "StrippedPartition{" +
                "indexes=" + indexes +
                ", begins=" + begins +
                '}';
    }

    public PLI deepClone() {
        util.Timer timer = new Timer();
        PLI result = new PLI(this.data);
        result.indexes = new ArrayList<>(indexes);
        result.begins = new ArrayList<>(begins);
        cloneTime += timer.getTimeUsed();
        return result;
    }

    public static PLI getStrippedPartition(AttributeSet attributeSet, DataFrame data, boolean isSample) {
        PLICache cachePtr = cache;
        if (cachePtr.containsKey(attributeSet)) {
            return cachePtr.get(attributeSet);
        }
        ArrayList<PLINode> pliList = cachePtr.getCombinationPLI(attributeSet);
        PLINode minClusterNode = null;
        int minCluster = Integer.MAX_VALUE;
        for (PLINode node : pliList) {
            if (node.getClusterNum() < minCluster) {
                minCluster = node.getClusterNum();
                minClusterNode = node;
            }
        }

        AttributeSet minContext = minClusterNode.context;
        PLI minsp = minClusterNode.sp;
        AttributeSet includedAttributes = null;
        if (minClusterNode != null) {
            includedAttributes = new AttributeSet(minClusterNode.context.getValue());
        }
        ArrayList<PLINode> nodesWithMostNewAttributes = new ArrayList<>();

        while (true) {
            PLINode nodeWithMostNewAttributes = null;
            int maxNewAttributes = 0;

            for (PLINode node : pliList) {
                AttributeSet tempAttributeSet = node.context.union(includedAttributes);
                int newAttributes = tempAttributeSet.getSize() - includedAttributes.getSize();
                if (newAttributes > maxNewAttributes) {
                    maxNewAttributes = newAttributes;
                    nodeWithMostNewAttributes = node;
                }
            }

            if (nodeWithMostNewAttributes == null || maxNewAttributes == 0) {
                break;
            }

            nodesWithMostNewAttributes.add(nodeWithMostNewAttributes);
            includedAttributes = includedAttributes.union(nodeWithMostNewAttributes.context);
        }

        nodesWithMostNewAttributes.sort(Comparator.comparingInt(PLINode::getClusterNum));
        for(PLINode node:nodesWithMostNewAttributes){
            minContext = node.context.union(minContext);
            minsp = minCluster>node.clusterNum?minsp.deepClone().product(node.sp):node.sp.deepClone().product(minsp);
            cachePtr.addPLI(minsp,minContext);
        }
        return minsp;

    }

    private int filteredDataFrameGet(DataFrame data, int tuple, SingleAttributePredicate column) {
        int result = data.get(tuple, column.attribute);

        if (column.operator == Operator.GREATEREQUAL) {
            result = -result;
        }
        return result;
    }

    public int trueContextLength(int index1, int index2, int right) {
        int l = 0;
        List<Integer> a = data.getTuple(index1);
        List<Integer> b = data.getTuple(index2);
        for (int i = 0; i < data.getColumnCount(); i++) {
            if (i != right)
                if (Objects.equals(a.get(i), b.get(i)))
                    l++;
        }
        return l;
    }

    public int trueContextLength(int index1, int index2, int right, int left) {
        int l = 0;
        List<Integer> a = data.getTuple(index1);
        List<Integer> b = data.getTuple(index2);
        for (int i = 0; i < data.getColumnCount(); i++) {
            if (i != right && i != left)
                if (Objects.equals(a.get(i), b.get(i)))
                    l++;
        }
        return l;
    }


    public static void setData(DataFrame data) {
        PLI.data = data;
    }

    public int[] getProbingTable() {
        return this.getProbingTable(false);
    }

    public int[] getProbingTable(boolean isCaching) {
        if (this.probingTableCache != null) return this.probingTableCache;

        int[] probingTable = new int[data.getTupleCount()];
        int nextClusterId = singletonValueId + 1;
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1);
            for (int i = groupBegin; i < groupEnd; i++) {
                probingTable[indexes.get(i)] = nextClusterId;
            }
            ++nextClusterId;
        }
        if (isCaching) this.probingTableCache = probingTable;
        return probingTable;
    }

    public int getClusterNum(){
        return begins.size()-1;
    }

    public static void initialCache(){
        PLINode root = cache.root;
        PLI rootSp = new PLI(data);
        root.setNode(rootSp);
        for (int i = 0; i < data.getColumnCount(); i++) {
            AttributeSet context = new AttributeSet().addAttribute(i);
            PLI sp = rootSp.deepClone().product(i);
            root.nextNodes.add(new PLINode(context,sp));
        }
    }
    public boolean isUCC() {
        return this.indexes.isEmpty();
    }
    public int discover(AllOcdTree allOcdTree, int k){//cluster random-k
//        System.out.println("---cluster random-"+k+"---");
        return discover(allOcdTree,k,1);
    }
    public int discover(AllOcdTree allOcdTree, int k,int maxFails){//cluster random-k
//        System.out.println("---cluster random-"+k+"---");
        int count = 0;
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++){
            int groupBegin = begins.get(beginPointer);
            int groupEnd = begins.get(beginPointer + 1)-1;
            if(groupEnd>groupBegin+k-1){
                groupEnd = groupBegin +k-1;
            }
            for(int i = groupBegin;i<groupEnd;++i){
                for(int j = i+1;j<=groupEnd;++j){
                    int newNonOcd = allOcdTree.discoverFromTwo(indexes.get(i),indexes.get(j));
                    count+=newNonOcd;
                    if(newNonOcd==0) {
                        --maxFails;
                        if(maxFails == 0) return count;
                    }
                }
            }
        }
        return count;
    }
    public int discoverRandom(AllOcdTree allOcdTree, int k, int maxFails) {
        int count = 0;
        for (int beginPointer = 0; beginPointer < begins.size() - 1; beginPointer++) {
            int groupBegin = begins.get(beginPointer);
            int originalGroupEnd = begins.get(beginPointer + 1) - 1;
            int groupSize = originalGroupEnd - groupBegin + 1;

            int selectCount = Math.min(k, groupSize);
            if (selectCount < 2) {
                continue;
            }

            List<Integer> groupPositions = new ArrayList<>();
            for (int pos = groupBegin; pos <= originalGroupEnd; pos++) {
                groupPositions.add(pos);
            }

            Collections.shuffle(groupPositions);

            List<Integer> selectedPositions = groupPositions.subList(0, selectCount);

            for (int i = 0; i < selectedPositions.size(); i++) {
                int posI = selectedPositions.get(i);
                int indexI = indexes.get(posI);
                for (int j = i + 1; j < selectedPositions.size(); j++) {
                    int posJ = selectedPositions.get(j);
                    int indexJ = indexes.get(posJ);

                    int newNonOcd = allOcdTree.discoverFromTwo(indexI, indexJ);
                    count += newNonOcd;
                    if (newNonOcd == 0) {
                        maxFails--;
                        if (maxFails == 0) {
                            return count;
                        }
                    }
                }
            }
        }
        return count;
    }
}
