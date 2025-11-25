package dependencydiscover.predicate;

import java.util.ArrayList;
import java.util.List;

import dependencydiscover.dataframe.DataFrame;

public class SingleAttributePredicate implements AbstractPredicate {
    final public int attribute;
    final public Operator operator;

    static private List<List<SingleAttributePredicate>> cache = new ArrayList<>();

    public static SingleAttributePredicate getInstance(int attribute, Operator operator) {
        while (attribute >= cache.size()) {
            List<SingleAttributePredicate> list = new ArrayList<>();
            for (Operator value : Operator.values()) {

                list.add(new SingleAttributePredicate(cache.size(), value));
            }
            cache.add(list);
        }
        return cache.get(attribute).get(operator.toInt());
    }

    public SingleAttributePredicate(int attribute, Operator operator) {
        this.attribute = attribute;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return (attribute + 1) + operator.toString();
    }

    public String toHashString() {
        return Integer.toString(attribute);
    }

    public static SingleAttributePredicate fromString(String s) {
        int opBegin;
        for (opBegin = 0; opBegin < s.length(); opBegin++) {
            char c = s.charAt(opBegin);
            if (c < '0' || c > '9')
                break;
        }
        return SingleAttributePredicate.getInstance(Integer.parseInt(s.substring(0, opBegin)) - 1,
                Operator.fromString(s.substring(opBegin)));
    }

    public boolean violate(DataFrame data, int tuple1, int tuple2) {
        return operator.violate(data.get(tuple1, attribute), data.get(tuple2, attribute));
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SingleAttributePredicate other = (SingleAttributePredicate) obj;
        return attribute == other.attribute && operator == other.operator;
    }

    @Override
    public int hashCode() {
        return attribute;
    }

    public static void main(String[] args) {
        SingleAttributePredicate s1 = new SingleAttributePredicate(1,Operator.LESSEQUAL);
        SingleAttributePredicate s2 = new SingleAttributePredicate(1,Operator.LESSEQUAL);
        boolean tmp = s1.equals(s2);
        System.out.println(tmp);
    }
}
