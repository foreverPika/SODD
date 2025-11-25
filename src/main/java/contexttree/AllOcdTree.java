package contexttree;

import dependencydiscover.dataframe.DataFrame;
import dependencydiscover.predicate.Operator;
import dependencydiscover.predicate.SingleAttributePredicate;
import sodd.AttributePair;
import sodd.AttributeSet;
import util.Timer;

import java.util.*;

public class AllOcdTree {
    public Map<AttributePair, OcdTree> allOcdTree;
    public OcdSet ncover;
    public int columnNum;
    public AttributeSet schema;
    private DataFrame data;
    public util.Timer timer;
//    public long validTime;
//    public long addNonOcdTime;
    public long addNonNodeTime;
    public int addNonNodeTimes;
//    public long completeTime;
//    public long inversionNothingTime;
    public int totalTimes;

    public AllOcdTree(int columnNum, AttributeSet schema, DataFrame data, int addNonNodeTimes) {
        this.columnNum = columnNum;
        this.schema = schema;
        this.data = data;
//        this.addNonOcdTime = 0;
        this.addNonNodeTime = 0;
        this.addNonNodeTimes = addNonNodeTimes;
//        this.completeTime = 0;
//        this.inversionNothingTime = 0;
        this.totalTimes = 100;

        this.timer = new Timer();
        this.ncover = new OcdSet(schema);
//        validTime = 0;
        allOcdTree = new HashMap<>();
        AttributeSet newContext;
        for (int i = 0; i < columnNum; i++) {
            for (int j = i + 1; j < columnNum; j++) {
                newContext = schema.deleteAttribute(i).deleteAttribute(j);
                AttributePair attributePair = new AttributePair(new SingleAttributePredicate(i, Operator.LESSEQUAL), j);
                allOcdTree.put(attributePair, new OcdTree(newContext, attributePair, columnNum));
                attributePair = new AttributePair(new SingleAttributePredicate(i, Operator.GREATEREQUAL), j);
                allOcdTree.put(attributePair, new OcdTree(newContext, attributePair, columnNum));
            }
        }
    }

    public void removeTree(AttributePair ocd) {
        allOcdTree.remove(ocd);
        ncover.remove(ocd);
    }

    public boolean isComplete() {
//        long tmp = timer.getTimeUsed();
        for (OcdTree tree : allOcdTree.values()) {
            if (!tree.isComplete) {
                return false;
            }
        }
//        completeTime += timer.getTimeUsed() - tmp;
        return true;
    }

    private boolean isComplete2(ContextNode root) {
        if (root.nextNodes.isEmpty()) {
            if (root.isValid == 2) {
                return false;
            } else {
                return true;
            }
        } else {
            for (ContextNode nextNode : root.nextNodes) {
                if (!isComplete2(nextNode)) {
                    return false;
                }
            }
            return true;
        }
    }
    public int addNonOcd(AttributePair ocd) {
//        System.out.println("inversion");
        long tmp = timer.getTimeUsed();
        int count = 0;
        OcdTree ocdTree = this.allOcdTree.get(ocd);
        ContextSet originContextSet = ncover.getContextSet(ocd);
        List<HashSet<AttributeSet>> nonOcdContext = originContextSet.getOcdLevels();
        int level = columnNum - 2;

        for (java.util.ListIterator<HashSet<AttributeSet>> iterator = nonOcdContext.listIterator(nonOcdContext.size()); iterator.hasPrevious(); ) {
            HashSet<AttributeSet> contextSet = iterator.previous();
            if(contextSet.isEmpty()){
                --level;
                continue;
            }
            if(level<=ocdTree.completeLevel) {
                break;
            }
            for (AttributeSet context : contextSet) {
//                long inversionNothingTmp = timer.getTimeUsed();1

                int thisCount = 0;
                int flag = ocdTree.findContext(context);
                if(flag == 2||flag == 3){
                    thisCount = ocdTree.addNonNode(context);
                }

//                int thisCount = ocdTree.addNonNode(context);
                count += thisCount;
            }

            --level;
        }
        ncover.clean(ocd);
        addNonNodeTime += timer.getTimeUsed() - tmp;
        return count;
    }
//    public int addNonOcdInversion(AttributePair ocd) {
//        long tmp = timer.getTimeUsed();
//        int count = 0;
//        OcdTree ocdTree = this.allOcdTree.get(ocd);
//        ContextSet originContextSet = ncover.getContextSet(ocd);
////        if (!originContextSet.isChange) {
////            System.out.println("notchange");
////            return 0;
////        }
//        List<HashSet<AttributeSet>> nonOcdContext = originContextSet.getOcdLevels();
//        int level = columnNum - 2;
//        for (java.util.ListIterator<HashSet<AttributeSet>> iterator = nonOcdContext.listIterator(nonOcdContext.size()); iterator.hasPrevious(); ) {
//            HashSet<AttributeSet> contextSet = iterator.previous();
//            if(contextSet.isEmpty()){
//                --level;
//                continue;
//            }
//            if(level<=ocdTree.completeLevel) {
//                break;
//            }
//
//            double threshold = ncover.getThreshold(level);
//            int levelAllTimes = 0;
//            int thisLevelNothingTimes = 0;
//            for (AttributeSet context : contextSet) {
//                long inversionNothingTmp = timer.getTimeUsed();
//                int thisCount = ocdTree.addNonNode(context);
//
//                count += thisCount;
//                ++levelAllTimes;
//                if (thisCount == 0) {
//                    ++thisLevelNothingTimes;
//                    inversionNothingTime += timer.getTimeUsed() - inversionNothingTmp;
//                }
//                if ((double) thisLevelNothingTimes / levelAllTimes > threshold) {
//                    System.out.println("inversionwrong："+thisLevelNothingTimes+"/"+levelAllTimes+",   level"+level+"——threshold:"+threshold);
//                    break;
//                }
//            }
//
//            --level;
//        }
//        ncover.clean(ocd);
//        addNonNodeTime += timer.getTimeUsed() - tmp;
////        if (nonthingTimes >= 2) {
////            System.out.println("nothingInversionTime:" + nonthingTimes);
////        }
//        return count;
//    }

    public int discoverFromTwo(int index1, int index2) {
        return discoverFromTwo(index1, index2, data);
    }

    public int discoverFromData(DataFrame data) {
        int count = 0;
        int row = data.getRowCount();
        for (int i = 0; i < row; i++) {
            for (int j = i + 1; j < row; j++) {
                count += discoverFromTwo(i, j, data);
            }
        }
        return count;
    }

    public int discoverWindowFromData(DataFrame data, int window, int bu) {
        int count = 0;
        for (int i = 0; i < data.getRowCount() - 1; i += bu) {
            int j = i + window - 1;
            count += discoverFromTwo(i, j, data);
        }
        return count;
    }

    public int discoverFromVioSet(ViorowsSet viorowsSet) {
        int count = 0;
        for (List<Integer> viorows : viorowsSet.getSet()) {
            count += discoverFromListFirst(viorows);
        }
        return count;
    }

    public int discoverFromListFirst(List<Integer> viorows) {
        int count = 0;
        int first = viorows.get(0);
        for (int i = 1; i < viorows.size(); i++) {
            count += discoverFromTwo(first, viorows.get(i));
        }
        return count;
    }

    public int discoverFromTwo(int index1, int index2, DataFrame data) {
        int count = 0;
        AttributeSet equal = new AttributeSet();
        List<Integer> less = new ArrayList<>();
        List<Integer> great = new ArrayList<>();
        int col = data.getColumnCount();
        AttributePair ocd;
        SingleAttributePredicate left;
        List<Integer> tuple1 = data.getTuple(index1);
        List<Integer> tuple2 = data.getTuple(index2);
        for (int k = 0; k < col; k++) {
            int num1 = tuple1.get(k);
            int num2 = tuple2.get(k);
            if (num1 < num2) {
                less.add(k);
            } else if (num1 == num2) {
                equal = equal.addAttribute(k);
            } else {
                great.add(k);
            }
        }
        for (int x = 0; x < less.size(); x++) {
            int first = less.get(x);
            for (int y = x + 1; y < less.size(); y++) {
                int num1 = first;
                int num2 = less.get(y);
                if (num1 > num2) {
                    int tmp = num1;
                    num1 = num2;
                    num2 = tmp;
                }
                left = new SingleAttributePredicate(num1, Operator.GREATEREQUAL);
                ocd = new AttributePair(left, num2);
//                long tmp = timer.getTimeUsed();
//                count += allOcdTree.get(ocd).addNonNode(equal);
                if (ncover.add(equal, ocd)) ++count;
//                addNonOcdTime += timer.getTimeUsed() - tmp;

            }
            //less和greater触发less
            for (int y = 0; y < great.size(); y++) {
                int num1 = first;
                int num2 = great.get(y);
                if (num1 > num2) {
                    int tmp = num1;
                    num1 = num2;
                    num2 = tmp;
                }

                left = new SingleAttributePredicate(num1, Operator.LESSEQUAL);
                ocd = new AttributePair(left, num2);
//                long tmp = timer.getTimeUsed();
//                count += allOcdTree.get(ocd).addNonNode(equal);
                if (ncover.add(equal, ocd)) ++count;

//                addNonOcdTime += timer.getTimeUsed() - tmp;

            }
        }
        for (int x = 0; x < great.size(); x++) {
            int num1 = great.get(x);
            for (int y = x + 1; y < great.size(); y++) {
                int num2 = great.get(y);
                if (num1 > num2) {
                    int tmp = num1;
                    num1 = num2;
                    num2 = tmp;
                }
                left = new SingleAttributePredicate(num1, Operator.GREATEREQUAL);
                ocd = new AttributePair(left, num2);
//                long tmp = timer.getTimeUsed();
//                count += allOcdTree.get(ocd).addNonNode(equal);
                if (ncover.add(equal, ocd)) ++count;
//                addNonOcdTime += timer.getTimeUsed() - tmp;

            }
        }
        return count;
    }
}
