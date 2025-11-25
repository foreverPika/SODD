package contexttree;

import java.util.*;

public class ViorowsSet {
    public Set<List<Integer>> viorowsSet;
    public ViorowsSet(){
        viorowsSet = new HashSet<>();
    }

    public Set<List<Integer>> getSet(){
        return viorowsSet;
    }

    public int getSize(){
        int count = 0;
        if(!viorowsSet.isEmpty()){
            for(List<Integer> l : viorowsSet){
                count += l.size()-1;
            }
        }
        return count;
    }

    public boolean add(List<Integer> list){
        if(list.size()<2) return false;
        else viorowsSet.add(list);
        return true;
    }

    public boolean add(Map<Integer,Set<Integer>> map){
        for (Map.Entry<Integer,Set<Integer>> entry:map.entrySet()){
            List<Integer> resList = new ArrayList<>();
            resList.add(entry.getKey());
            resList.addAll(entry.getValue());
            viorowsSet.add(resList);
        }
        return true;
    }
    public void clear(){
        viorowsSet=new HashSet<>();
    }
    public boolean isEmpty(){
        return viorowsSet.isEmpty();
    }
}
