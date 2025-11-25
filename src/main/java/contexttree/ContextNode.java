package contexttree;

import sodd.AttributeSet;

import java.util.ArrayList;

public class ContextNode {
    public byte attribute;
    public AttributeSet context;
    public ArrayList<ContextNode> nextNodes;
    public byte isValid;//invalid 0,valid 1,unknown 2

    public ContextNode() {
        context = new AttributeSet(0);
        nextNodes = new ArrayList<ContextNode>();
        isValid = 2;
        attribute = -1;
    }

    public ContextNode(AttributeSet newContext) {
        context = newContext;
        nextNodes = new ArrayList<ContextNode>();
        isValid = 2;
        attribute = (byte)this.context.getLastAttribute();
    }

    public void setIsValid(int isValid) {
        this.isValid = (byte) isValid;
    }
    public int getAttribute(){
        return attribute;
    }
}
