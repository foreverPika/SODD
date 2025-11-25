package contexttree;

import sodd.AttributeSet;
import util.Timer;

public class ContextTree {
    public ContextNode root;
    public AttributeSet schema;
    public int columnNum;
    public util.Timer timer;
    public static long expandTime=0;
    public static long testTime = 0;
    public boolean isComplete;
    public byte completeLevel;

    public ContextTree(AttributeSet schema, int columnNum) {
        this.root = new ContextNode();
        this.schema = schema;
        this.columnNum = columnNum;
        this.timer = new Timer();
        this.isComplete = false;
        this.completeLevel = -1;
    }

    public int findContext(AttributeSet context) {//找到准确的context，0为non，1为valid，2为待验证，3为不存在
        return findContext2(root, context);
    }

    private int findContext2(ContextNode root, AttributeSet context) {
        if (context.isEmpty()) {
            return root.isValid;
        }
        if (root.nextNodes.isEmpty()) {
            return 3;
        }
        int contextAttribute = context.getFirstAttribute();
        for (ContextNode contextNode : root.nextNodes) {
            if (contextNode.attribute == contextAttribute) {
                return findContext2(contextNode, context.deleteAttribute(contextAttribute));
            }
        }
        return 3;
    }

    public int addNonNodeOld(AttributeSet context) {
//        if(isComplete){
//            return 0;
//        }
//        int contextSize = context.getSize();
//        if(contextSize<=completeLevel){
//            return 0;
//        }
        if (context.isEmpty()) {
            if (root.isValid != 0) {
                root.isValid = 0;
                expandNode(root);
                return 1;
            }
            return 0;
        } else {
            root.isValid = 0;
            return addNonNode2(root, context);
//            return addNonNode2(root, context,0,context.getSize());

        }
    }
    private int addNonNode2(ContextNode root, AttributeSet context) {
        int count = 0;
        if (root.nextNodes.isEmpty()) {
            expandNode(root);
            ++count;
            root.isValid = 0;
        }
        for (ContextNode nextNode : root.nextNodes) {
            int firstAttribute = context.getFirstAttribute();
            if (nextNode.attribute == firstAttribute) {
                if (context.getSize() == 1) {
                    if (nextNode.isValid != 0) {
                        nextNode.isValid = 0;
                        expandNode(nextNode);
                        ++count;
//                        return count;
                    }
                    return count;
                } else {
                    root.isValid = 0;

                    context = context.deleteAttribute(firstAttribute);
                    count += addNonNode2(nextNode, context);
                }
            }
        }
        return count;
    }
    public int addNonNode(AttributeSet context) {
        return addNonNodeNew(root,context);
    }
    private int addNonNodeNew(ContextNode root, AttributeSet context) {
        int count = 0;
        if (root.nextNodes.isEmpty()) {
            expandNode(root);
            ++count;
            root.isValid = 0;
        }
        if(context.isEmpty()){
            return count;
        }
        int firstAttribute = context.getFirstAttribute();
        for (ContextNode nextNode : root.nextNodes) {

            if (nextNode.attribute == firstAttribute) {
                context = context.deleteAttribute(firstAttribute);

                count += addNonNodeNew(nextNode, context);
                if(context.isEmpty()){
                    return count;
                }else{
                    firstAttribute = context.getFirstAttribute();
                }
            }
        }
        return count;
    }
    private int addNonNode2(ContextNode root, AttributeSet context, int level,int contetxSize) {
        int count = 0;
        if (root.nextNodes.isEmpty()) {
            expandNode(root);
            ++count;
            root.isValid = 0;
        }
        for (ContextNode nextNode : root.nextNodes) {
            int firstAttribute = context.getFirstAttribute();
            if (nextNode.attribute == firstAttribute) {
                if (context.getSize() == 1) {
                    if (nextNode.isValid != 0) {
                        nextNode.isValid = 0;
                        expandNode(nextNode);
                        ++count;
//                        return count;
                    }
                    return count;
                } else {
                    root.isValid = 0;

                    context = context.deleteAttribute(firstAttribute);
                    --contetxSize;
                    count += addNonNode2(nextNode, context,level + 1,contetxSize);
                    if(contetxSize+level+1<=completeLevel){
                        return count;
                    }
                }
            }
        }
        return count;
    }

    protected void expandNode(ContextNode node) {
        long tmp = timer.getTimeUsed();
        for (int i = node.attribute + 1; i < columnNum; i++) {
            if (schema.containAttribute(i)) {
                node.nextNodes.add(new ContextNode(node.context.addAttribute(i)));
            }
        }
        expandTime += timer.getTimeUsed() - tmp;
    }
}
