package pli;

import sodd.AttributeSet;

import java.util.ArrayList;
import java.util.Comparator;

public class PLICache {
    public PLINode root;

    public PLICache(){
        root = new PLINode(new AttributeSet(0));
    }
    public boolean containsKey(AttributeSet context){
        return findPLI2(root,context);
    }
    public boolean findPLI(AttributeSet context){
        return findPLI2(root,context);
    }
    public boolean findPLI2(PLINode root,AttributeSet context){
        if (context.isEmpty()) {
            return !root.isEmpty();
        }
        if (root.nextNodes.isEmpty()) {
            return false;
        }
        int contextAttribute = context.getFirstAttribute();
        for (PLINode pliNode : root.nextNodes) {
            if (pliNode.attribute == contextAttribute) {
                return findPLI2(pliNode, context.deleteAttribute(contextAttribute));
            }
        }
        return false;
    }
    public void addPLI(PLI sp, AttributeSet context){
        addPLI2(root,sp,context);
    }
    private void addPLI2(PLINode root, PLI sp, AttributeSet context){
        if(context.isEmpty()){
            root.setNode(sp);
            return;
        }

        boolean flag = false;
        int contextAttribute = context.getFirstAttribute();
        for (PLINode contextNode : root.nextNodes) {
            if (contextNode.attribute == contextAttribute) {
                flag = true;
                addPLI2(contextNode,sp,context.deleteAttribute(contextAttribute));
                return;
            }
        }
        if(!flag){
            root.nextNodes.add(new PLINode(root.context.addAttribute(contextAttribute)));
            addPLI2(root.nextNodes.get(root.nextNodes.size()-1),sp,context.deleteAttribute(contextAttribute));
            root.nextNodes.sort(Comparator.comparingInt(PLINode::getAttribute));
        }

    }
    public PLI get(AttributeSet context){
        return getPLI2(root,context);
    }
    public PLI getPLI(AttributeSet context){
        return getPLI2(root,context);
    }
    public PLI getPLI2(PLINode root, AttributeSet context){
        if (context.isEmpty()) {
            return root.sp;
        }
        if (root.nextNodes.isEmpty()) {
            return null;
        }
        int contextAttribute = context.getFirstAttribute();
        for (PLINode pliNode : root.nextNodes) {
            if (pliNode.attribute == contextAttribute) {
                return getPLI2(pliNode, context.deleteAttribute(contextAttribute));
            }
        }
        return null;
    }

    public ArrayList<PLINode> getCombinationPLI(AttributeSet context) {
        ArrayList<PLINode> result = new ArrayList<>();
        getCombinationPLI2(root, context, result);
        return result;
    }

    private void getCombinationPLI2(PLINode root, AttributeSet context, ArrayList<PLINode> result) {

        if(root.nextNodes.isEmpty()||context.isEmpty()){
            return;
        }

        int contextAttribute = context.getFirstAttribute();
        for(PLINode nextNode:root.nextNodes) {
            while (nextNode.attribute > contextAttribute) {
                context = context.deleteAttribute(contextAttribute);
                if(context.isEmpty()){
                    return;
                }
                contextAttribute = context.getFirstAttribute();
            }
            if (nextNode.attribute == contextAttribute) {
                if(!nextNode.isEmpty()){
                    result.add(nextNode);
                }
                getCombinationPLI2(nextNode, context.deleteAttribute(contextAttribute),result);
            }

        }
    }


}
