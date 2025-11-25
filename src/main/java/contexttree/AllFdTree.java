package contexttree;

import dependencydiscover.dataframe.DataFrame;
import sodd.AttributeSet;

import java.util.*;

public class AllFdTree {
    public Map<Integer,FdTree> allFdTree;
    public ContextSet ncover;
    public int columnNum;
    public AttributeSet schema;
//    public util.Timer timer;
//    public long validTime;
//    public long addNonNodeTime;
    public int addNonNodeTimes;
    public DataFrame data;

    public AllFdTree(int columnNum, AttributeSet schema, DataFrame data){
        this.columnNum = columnNum;
        this.schema = schema;
        this.data = data;
        allFdTree = new HashMap<>();
//        this.timer = new Timer();
        for (int i = 0; i < columnNum; i++) {
            allFdTree.put(i,new FdTree(schema.deleteAttribute(i),i,columnNum));
        }
//        validTime = 0;
//        addNonNodeTime = 0;
        this.addNonNodeTimes = 5;
    }



    public boolean isComplete(){
        for(FdTree tree: allFdTree.values()){
            if(!tree.isComplete){
                return false;
            }
        }
        return true;
    }

    public int discoverFromVioSet(ViorowsSet viorowsSet){
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
    public int discoverFromData(DataFrame data) {
        int count = 0;
        AttributeSet equal = new AttributeSet();
        List<Integer> diff = new ArrayList<>();
        int row = data.getRowCount();
        int col = data.getColumnCount();
        for (int i = 0; i < row; i++) {
            for (int j = i + 1; j < row; j++) {
                List<Integer> tuple1 = data.getTuple(i);
                List<Integer> tuple2 = data.getTuple(j);
                for (int k = 0; k < col; k++) {
                    int num1 = tuple1.get(k);
                    int num2 = tuple2.get(k);
                    if (num1 == num2) {
                        equal = equal.addAttribute(k);
                    } else {
                        diff.add(k);
                    }
                }
                for (int x = 0; x < diff.size(); x++) {
//                    long tmp = timer.getTimeUsed();
                    count += allFdTree.get(diff.get(x)).addNonFdNode(equal);
//                    addNonNodeTime += timer.getTimeUsed() - tmp;
                }
                equal = new AttributeSet();
                diff = new ArrayList<>();
            }
        }
        return count;
    }
    public int discoverFromTwo(int index1,int index2) {
        int count = 0;
        AttributeSet equal = new AttributeSet();
        List<Integer> diff = new ArrayList<>();
        int col = data.getColumnCount();
        List<Integer> tuple1 = data.getTuple(index1);
        List<Integer> tuple2 = data.getTuple(index2);
        for (int k = 0; k < col; k++) {
            int num1 = tuple1.get(k);
            int num2 = tuple2.get(k);
            if (num1 == num2) {
                equal = equal.addAttribute(k);
            } else {
                diff.add(k);
            }
        }
        for (int x = 0; x < diff.size(); x++) {
//            long tmp = timer.getTimeUsed();
            count += allFdTree.get(diff.get(x)).addNonFdNode(equal);
//            addNonNodeTime += timer.getTimeUsed() - tmp;
        }
        return count;
    }
}
