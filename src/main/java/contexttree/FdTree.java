package contexttree;

import sodd.AttributeSet;

import java.util.Comparator;

public class FdTree extends ContextTree {
    public int right;


    public FdTree(AttributeSet schema, int right, int columnNum) {
        super(schema, columnNum);
        this.right = right;
    }


    public int addNonFdNode(AttributeSet context){
        if(isComplete){
            return 0;
        }
        if(context.isEmpty()){
            if(root.isValid == 2){
                root.isValid =0;
                expandNode(root);
                return 1;
            }
            return 0;
        }
        return addNonFdNode2(root,context);
    }
    private int addNonFdNode2(ContextNode root,AttributeSet context) {
        int count = 0;
        if(root.nextNodes.isEmpty()){
            if(root.isValid!=2){//leaf node and validated
                return 0;
            }else{//without validating
                root.isValid = 0;
                expandNode(root);
            }
        }
        //not leaf node and context not empty
        for(ContextNode node:root.nextNodes){
            int firstAttribute = context.getFirstAttribute();
            while (node.attribute > firstAttribute) {//nextnode:D context:ABCDE
                context = context.deleteAttribute(firstAttribute);
                if(context.isEmpty()){
                    return count;
                }
                firstAttribute = context.getFirstAttribute();
            }
            if (node.attribute == firstAttribute) {
                if(context.getSize() == 1){
                    if(node.isValid == 2){
                        node.isValid = 0;
                        expandNode(node);
                        ++count;
                    }
                    return count;
                }else{
                    context = context.deleteAttribute(firstAttribute);
                    count+=addNonFdNode2(node,context);
                }
            }
        }

        return count;
    }
    public void addFD(AttributeSet context,int isValid) {//isValid为1则添加正确fd，为2则添加候选fd
        addFD2(root, context, isValid);
    }
    private void addFD2(ContextNode root, AttributeSet context, int isValid) {
        if (context.isEmpty()) {
            root.isValid = (byte) isValid;
//            if(isValid == 0){
//                Set<Integer> s = new HashSet<>();
//                for(ContextNode nextNode:root.nextNodes){
//                    s.add(nextNode.attribute);
//                }
//                for (int i = root.attribute + 1; i < columnNum; i++) {
//                    if (schema.containAttribute(i)&&!s.contains(i)) {
//                        root.nextNodes.add(new ContextNode(root.context.addAttribute(i)));
//                    }
//                }
//                root.nextNodes.sort(Comparator.comparingInt(ContextNode::getAttribute));
//            }
            return;
        } else {
            root.isValid = 0;
        }
        boolean flag = false;
        int contextAttribute = context.getFirstAttribute();
        for (ContextNode contextNode : root.nextNodes) {
            if (contextNode.attribute == contextAttribute) {
                flag = true;
                addFD2(contextNode,context.deleteAttribute(contextAttribute),isValid);
                return;
            }
        }
        if(!flag){
            root.nextNodes.add(new ContextNode(root.context.addAttribute(contextAttribute)));
            addFD2(root.nextNodes.get(root.nextNodes.size()-1),context.deleteAttribute(contextAttribute),isValid);
            root.nextNodes.sort(Comparator.comparingInt(ContextNode::getAttribute));
        }
    }
    public int findFD(AttributeSet context){
        return findContext(context);
    }
    public boolean findGeneration(AttributeSet context){//找context子集成立的fd是否存在
        return findGeneration2(root,context);
    }
    private boolean findGeneration2(ContextNode root,AttributeSet context){
        if(root.nextNodes.isEmpty()||context.isEmpty()){
            if(root.isValid == 1){
                return true;
            } else {
                return false;
            }
        }
        int contextAttribute = context.getFirstAttribute();
        for(ContextNode nextNode:root.nextNodes) {
            while (nextNode.attribute > contextAttribute) {//nextnode:D context:ABCDE
                context = context.deleteAttribute(contextAttribute);
                if(context.isEmpty()){
                    return false;
                }
                contextAttribute = context.getFirstAttribute();
            }
            if (nextNode.attribute == contextAttribute) {
                boolean tmp = findGeneration2(nextNode, context.deleteAttribute(contextAttribute));
                if (tmp) {
                    return true;
                }
            }
        }
        return false;
    }
}
