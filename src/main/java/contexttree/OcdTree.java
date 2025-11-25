package contexttree;

import sodd.AttributePair;
import sodd.AttributeSet;

public class OcdTree extends ContextTree{
    public AttributePair ocd;

    public OcdTree(AttributeSet schema,AttributePair ocd,int columnNum){
        super(schema,columnNum);
        this.ocd = ocd;
    }

    public int findOcd(AttributeSet context){//找到准确的ocd
        return findContext(context);
    }
    public boolean findGeneration(AttributeSet context){//找context子集成立的ocd是否存在
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

    public static void main(String[] args) {
//        AttributeSet schema = new AttributeSet();
//        int columnNum = 6;
//        for (int i = 0; i < columnNum - 2; i++) {
//            schema = schema.addAttribute(i);
//        }
//        AttributePair ocd = new AttributePair(new SingleAttributePredicate(4, Operator.LESSEQUAL),5);
//        OcdTree ocdTree = new OcdTree(schema,ocd, columnNum);
//        AttributeSet attributeSet1 = schema.deleteAttribute(0);//ABC
//        AttributeSet attributeSet2 = new AttributeSet().addAttribute(0);//A
//        AttributeSet attributeSet3 = new AttributeSet().addAttribute(2);//C
//        ocdTree.addNonNode(attributeSet2);
//        ocdTree.addNonNode(attributeSet1);
//        ocdTree.root.nextNodes.get(0).nextNodes.get(0).setIsValid(1);//AB验证为正确
//        boolean res = ocdTree.findGeneration(attributeSet1.deleteAttribute(3));
//        boolean res2 = ocdTree.findGeneration(attributeSet2.addAttribute(3));
//
//        System.out.println(res);
//        System.out.println(res2);
    }

}
