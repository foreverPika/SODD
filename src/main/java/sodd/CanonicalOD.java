package sodd;

import java.util.*;

import dependencydiscover.dataframe.DataFrame;
import dependencydiscover.predicate.Operator;
import dependencydiscover.predicate.SingleAttributePredicate;
import pli.PLI;

public class CanonicalOD implements Comparable<CanonicalOD> {

    public AttributeSet context;
    public int right;
    public SingleAttributePredicate left;
    public static int splitCheckCount = 0;
    public static int swapCheckCount = 0;

    @Override
    public int compareTo(CanonicalOD o) {
        int attributeCountDifference = context.getAttributeCount() - o.context.getAttributeCount();

        if (attributeCountDifference != 0)
            return attributeCountDifference;
        long contextValueDiff = context.getValue() - o.context.getValue();

        if (contextValueDiff != 0)
            return (int) contextValueDiff;

        int rightDiff = right - o.right;
        if (rightDiff != 0)
            return rightDiff;
        if (left != null) {
            int leftDiff = left.attribute - o.left.attribute;
            if (leftDiff != 0)
                return leftDiff;
            if (left.operator == o.left.operator)
                return 0;
            if (left.operator == Operator.LESSEQUAL)
                return -1;
        }
        return 0;

    }

    public CanonicalOD(AttributeSet context, SingleAttributePredicate left, int right) {
        this.context = context;
        this.right = right;
        this.left = left;
    }

    public CanonicalOD(AttributeSet context, int right) {
        this.context = context;
        this.right = right;
        this.left = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(context).append(" : ");
        if (left == null) {
            sb.append("[] -> ");
        } else {
            sb.append(left).append(" ~ ");
        }
        sb.append(right + 1).append("<=");
        return sb.toString();
    }


    public Map<Integer,Set<Integer>> validForSample(DataFrame data, double errorRateThreshold, boolean isSample, int vioNum) {

        PLI sp = PLI.getStrippedPartition(context, data, isSample);

        if (sp.isUCC()) {
            Map<Integer,Set<Integer>> resMap = new HashMap<>();
            if(left != null){//ocd
                resMap.put(-1,new HashSet<>());
                resMap.put(-2,new HashSet<>());
            }
            return resMap;
        }

        if (errorRateThreshold == -1f) {

            if (left == null) {
                splitCheckCount++;
                return sp.splitForSample(right, vioNum);
//                return sp.splitForSampleRandom(right, vioNum);
            }

            else {
                swapCheckCount++;
                return sp.swapForSampleNew(left, right, vioNum);
//                return sp.swapForSampleRandom(left, right, vioNum);
            }
        }
        return new HashMap<>();
    }
//    public Set<Integer> validForSample2(DataFrame data, double errorRateThreshold, boolean isSample, int vioNum) {
//
//        StrippedPartition2 sp = StrippedPartition2.getStrippedPartition(context, data, isSample);
//
//        if (sp.isUCC()) {
//            Set<Integer> res=new HashSet<>();
//            if(left != null){//ocd
//                res.add(-1);
//                res.add(-2);
//            }
//
//            return res;
//        }
//
//        if (errorRateThreshold == -1f) {
//
//            if (left == null) {
//                splitCheckCount++;
//                return sp.splitForSample(right, vioNum);
//            }
//
//            else {
//                swapCheckCount++;
//                return sp.swapForSampleNew(left, right, vioNum);
//            }
//        }
//        return new HashSet<>();
//    }



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CanonicalOD))
            return false;
        CanonicalOD that = (CanonicalOD) o;
        if(left ==null||that.left ==null){
            return right == that.right &&
                    left == that.left &&
                    context.equals(that.context);
        }
        return right == that.right &&
                left.equals(that.left) &&
                context.equals(that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, right, left);
    }

    public String toHashString() {
        StringBuilder sb = new StringBuilder();
        sb.append(context.toOrderedList());
        if (left != null)
            sb.append(left.toHashString());

        sb.append(right);
        return sb.toString();
    }
    public static CanonicalOD fromString(String line) {
        try {
            String[] parts = line.split(" : ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("not exist' : '");
            }


            String contextStr = parts[0];
            AttributeSet context = AttributeSet.fromString(contextStr);


            String[] rightParts = parts[1].split(" ~ | -> ");
            if (rightParts.length != 2) {
                throw new IllegalArgumentException("not exist' ~ '或' -> '");
            }


            String rightStr = rightParts[1].replaceAll("<=", "").trim();
            int right = Integer.parseInt(rightStr) - 1;


            String leftStr = rightParts[0].trim();
            SingleAttributePredicate left = null;
            if (!leftStr.equals("[]")) {
                left = parseLeftPredicate(leftStr);
            }

            return new CanonicalOD(context, left, right);
        } catch (Exception e) {
            throw new IllegalArgumentException("not know: " + line, e);
        }
    }

    public static SingleAttributePredicate parseLeftPredicate(String s) {
        int opBegin;
        for (opBegin = 0; opBegin < s.length(); opBegin++) {
            char c = s.charAt(opBegin);
            if (c < '0' || c > '9')
                break;
        }
        return SingleAttributePredicate.getInstance(
                Integer.parseInt(s.substring(0, opBegin)) - 1,
                Operator.fromString(s.substring(opBegin))
        );
    }


}
