package pli;

import sodd.AttributeSet;

import java.util.ArrayList;

public class PLINode {
    public PLI sp;
    public AttributeSet context;
    public byte attribute;
    public int clusterNum;
    public ArrayList<PLINode> nextNodes;

    public PLINode() {
        this.sp = null;
        this.context = new AttributeSet();
        this.nextNodes = new ArrayList<>();
    }

    public PLINode(AttributeSet context) {
        this.sp = null;
        this.context = context;
        this.nextNodes = new ArrayList<>();
        if(!context.isEmpty()){
            this.attribute = (byte) context.getLastAttribute();
        }else{
            this.attribute = -1;
        }
    }

    public PLINode(AttributeSet context, PLI sp) {
        this.sp = sp;
        this.clusterNum = sp.getClusterNum();
        this.context = context;
        this.attribute = (byte) context.getLastAttribute();
        this.nextNodes = new ArrayList<>();
    }
    public void setNode(PLI sp){
        this.sp = sp;
        this.clusterNum = sp.getClusterNum();
    }

    public boolean isEmpty() {
        return this.sp == null;
    }
    public int getAttribute(){
        return this.attribute;
    }
    public int getClusterNum(){
        return this.clusterNum;
    }
}
