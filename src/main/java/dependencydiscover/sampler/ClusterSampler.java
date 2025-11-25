package dependencydiscover.sampler;

import contexttree.AllOcdTree;
import pli.PLINode;
import pli.PLI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusterSampler extends Sampler {
    private static final int LOW_BOUND = 10;
    private static final int UPPER_BOUND = 100;
    long seed = -1;

    public int firstSample(AllOcdTree allOcdTree) {
        int count = 0;
        PLI.initialCache();
        PLINode root = PLI.cache.root;
        for (PLINode node : root.nextNodes) {
            PLI sp = node.sp;
//            count += sp.discover(allOcdTree);
            count += sp.discover(allOcdTree, 2);
//            count += sp.discover(allOcdTree, 4);
        }
        return count;
    }
    public int firstSample(AllOcdTree allOcdTree,int maxFails) {
        int count = 0;
        PLI.initialCache();
        PLINode root = PLI.cache.root;
        for (PLINode node : root.nextNodes) {
            PLI sp = node.sp;
//            count += sp.discover(allOcdTree,2,maxFails);
            count += sp.discoverRandom(allOcdTree,2,maxFails);
        }
        return count;
    }

//    public int firstSample(AllOcdTree allOcdTree, int rows, int tupleCount) {
//        System.out.println("---random-" + rows + "---");
//        int count = 0;
//        PLI.initialCache();
//        PLINode root = PLI.cache.root;
//        Set<Integer> uniqueNumbers = new HashSet<>();
//        List<Integer> resultList = new ArrayList<>();
//
//        while (uniqueNumbers.size() < rows && uniqueNumbers.size() < tupleCount) {
//            int randomNum = random.nextInt(tupleCount);
//            if (uniqueNumbers.add(randomNum)) {
//                resultList.add(randomNum);
//            }
//        }
//        for (int i = 0; i < resultList.size() - 1; i++) {
//            for (int j = i + 1; j < resultList.size(); j++) {
//                int newNonOcd = allOcdTree.discoverFromTwo(i, j);
//                count += newNonOcd;
//            }
//        }
//        return count;
//    }
}
