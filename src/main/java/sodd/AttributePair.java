package sodd;

import java.security.InvalidParameterException;
import java.util.Objects;

import dependencydiscover.predicate.Operator;
import dependencydiscover.predicate.SingleAttributePredicate;

public class AttributePair {
    public final SingleAttributePredicate left;
    public final int right;

    public AttributePair(SingleAttributePredicate left, int right) {
        if (left.attribute == right) {
            throw new InvalidParameterException("two attributes cannot be the same");
        }
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AttributePair))
            return false;
        AttributePair that = (AttributePair) o;
        return right == that.right && left.equals(that.left) || right == that.right && left == that.left || right == that.left.attribute && left.attribute == that.right;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(left.attribute) + Objects.hashCode(right);
    }

    @Override
    public String toString() {
        return String.format("{%s,%d}", left, right + 1);
    }

    public static void main(String[] args) {
        SingleAttributePredicate s1 = new SingleAttributePredicate(1, Operator.LESSEQUAL);
        SingleAttributePredicate s2 = new SingleAttributePredicate(1, Operator.LESSEQUAL);
        AttributePair p1 = new AttributePair(s1, 3);
        AttributePair p2 = new AttributePair(s2, 3);

        boolean tmp = p1.equals(p2);
        System.out.println(tmp);
    }
}
