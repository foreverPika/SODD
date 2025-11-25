package sodd;

import contexttree.*;
import dependencydiscover.dataframe.DataFrame;
import dependencydiscover.sampler.ClusterSampler;
import util.Gateway;
import pli.PLI;
import util.Timer;
import util.Util;

import java.util.*;
import java.io.IOException;

public class SODD {
    private final long timeLimit;
    private boolean complete = true;
    private static Queue<ContextNode> queue = new LinkedList<>();
    private long validTime;
    //    private long findTime;
//    private long fdFindTime;
//    private long ocdFindTime;
//    private long fdSampleTime;
    private int allOcdTreeNum;
    private long ocdFinishTime;
    private long fdFinishTime;
    //    private long testTime;
    private long sampleDiscoverTime;
    private int tupleNum;
    private int columnNum;
    private final Set<CanonicalOD> result = new HashSet<>();

    private List<Set<AttributeSet>> contextInEachLevel;

    private HashMap<AttributeSet, AttributeSet> cc;

    private HashMap<AttributeSet, Set<AttributePair>> cs;

    private int level;

    private AttributeSet schema;

    private DataFrame data;

    List<Integer> ocdViorows = new ArrayList<>();
    Set<Integer> viorows = new HashSet<>();

    ViorowsSet viorowsSet = new ViorowsSet();
    Set<Integer> vioAttribute = new HashSet<>();
    private double errorRateThreshold = -1f;

    Gateway traversalGateway;

    int odcount = 0, fdcount = 0, ocdcount = 0, odcan = 0, odOnSample = 0;
    private boolean turn2Sample;
    private int thisLevelNodes;
    private int nextLevelNodes;

    Timer timer = new Timer();
    public long sampleTime;

    public SODD(long timeLimit, double errorRateThreshold) {
        this.timeLimit = timeLimit;
        this.errorRateThreshold = errorRateThreshold;
    }

    public SODD(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    private boolean timeUp() {
        return timer.getTimeUsed() >= timeLimit;
    }

    public boolean isComplete() {
        return complete;
    }

    private AllOcdTree allOcdTree;
    private AllFdTree allFdTree;
    private int wrongNum;
    private int completeOcdTreeNum;
    private int completeFdTreeNum;

    private void initialize(DataFrame data, int tupleNum, int wrongNum) {
        traversalGateway = new Gateway.ComplexTimeGateway();
        this.viorows.clear();
        this.data = data;
        this.tupleNum = tupleNum;
        this.validTime = 0;
//        this.findTime = 0;
//        this.testTime = 0;
        this.completeOcdTreeNum = 0;
        this.sampleDiscoverTime = 0;
//        this.fdFindTime = 0;
//        this.ocdFindTime = 0;
//        this.fdSampleTime = 0;
        this.turn2Sample = false;
        this.wrongNum = wrongNum;
        this.thisLevelNodes = 0;
        this.nextLevelNodes = 0;
        AttributeSet emptySet = new AttributeSet();
        schema = new AttributeSet();
        for (int i = 0; i < data.getColumnCount(); i++) {
            schema = schema.addAttribute(i);
        }
        this.columnNum = data.getColumnCount();
        allOcdTree = new AllOcdTree(columnNum, schema, data, 100);
        allFdTree = new AllFdTree(columnNum, schema, data);
        allOcdTreeNum = allOcdTree.allOcdTree.size();

    }

    public Set<CanonicalOD> discover(DataFrame data) {
        //ocd
        System.out.println();
        long tmp;
        int newNonOcd = 0;
        do {
            int count = validateOcd();
            turn2Sample = false;
            tmp = timer.getTimeUsed();
            if (!viorowsSet.isEmpty()) {
                newNonOcd = allOcdTree.discoverFromVioSet(viorowsSet);
            }
            sampleDiscoverTime += timer.getTimeUsed() - tmp;
            viorows.clear();
            viorowsSet.clear();
        } while (!allOcdTree.isComplete());
        this.ocdFinishTime = timer.getTimeUsed();
        System.out.println("ocdValidTime:" + validTime + "ms, ocdTotalTime:" + ocdFinishTime + "ms");
        System.out.println("OCD finish find fd:" + fdcount + ", ocd:" + ocdcount);
        //fd
        turn2Sample = false;
        int newNonFd = 0;
        do {
            int count = validateFd();
            turn2Sample = false;
            if (!viorowsSet.isEmpty()) {
                newNonFd = allFdTree.discoverFromVioSet(viorowsSet);
            }
            viorows.clear();
            viorowsSet.clear();
        } while (!allFdTree.isComplete());
        this.fdFinishTime = timer.getTimeUsed() - ocdFinishTime;
        odcount = fdcount + ocdcount;
        //over
        System.out.println();
        if (isComplete()) {
            Util.out("SODnew Finish");
        } else {
            Util.out("SODnew Time Limited");
        }
        return result;
    }

    public int validateOcd() {
        int count = 0;
        Iterator<OcdTree> iterator = allOcdTree.allOcdTree.values().iterator();
        while (iterator.hasNext()) {
            OcdTree tree = iterator.next();

            int newNonOcdNode = allOcdTree.addNonOcd(tree.ocd);
            int tmp = validateTree(tree);
            count += tmp;
            if (turn2Sample) {
                return count;
            }
            if (tree.isComplete) {
                queue.clear();
                allOcdTree.ncover.remove(tree.ocd);
                iterator.remove();
                thisLevelNodes = 0;
                nextLevelNodes = 0;
            }
        }
        return count;
    }

    private int validateTree(OcdTree tree) {
        int count = 0;
        if (queue.isEmpty()) {
            queue.offer(tree.root);
            ++thisLevelNodes;
        }
        while (!queue.isEmpty()) {
            if (viorowsSet.getSize() >= wrongNum) {
                turn2Sample = true;
                return count;
            }
            ContextNode node = queue.poll();
            --thisLevelNodes;

            for (ContextNode nextNode : node.nextNodes) {
                queue.offer(nextNode);
                ++nextLevelNodes;
            }
            if (thisLevelNodes == 0) {
                thisLevelNodes = nextLevelNodes;
                nextLevelNodes = 0;
                ++tree.completeLevel;
            }
            if (node.nextNodes.isEmpty() && node.isValid == 2) {//Leaf node and not verified
                count += validateNode(tree, node);
            }

        }
        tree.isComplete = true;

        return count;
    }

    private int validateNode(OcdTree tree, ContextNode node) {
        AttributeSet context = node.context;
        int left = tree.ocd.left.attribute;
        int right = tree.ocd.right;
        FdTree leftFdTree = allFdTree.allFdTree.get(left);
        FdTree rightFdTree = allFdTree.allFdTree.get(right);

//        long tempfind = timer.getTimeUsed();
        boolean leftFdFind = leftFdTree.findGeneration(context);
        boolean rightFdFind = rightFdTree.findGeneration(context);

        if (leftFdFind || rightFdFind) {
//            findTime += timer.getTimeUsed() - tempfind;
//            fdFindTime += timer.getTimeUsed() - tempfind;
            node.isValid = 1;
            if (leftFdFind && !rightFdFind) {
                if (rightFdTree.findContext(context) == 3) {
                    rightFdTree.addFD(context, 2);
                }
            } else if (!leftFdFind) {
                if (leftFdTree.findContext(context) == 3) {
                    leftFdTree.addFD(context, 2);
                }
            }
            return 0;
        }
        boolean ocdFind = tree.findGeneration(context);
        if (ocdFind) {
//            findTime += timer.getTimeUsed() - tempfind;
//            ocdFindTime += timer.getTimeUsed() - tempfind;
            node.isValid = 1;
            if (rightFdTree.findContext(context) == 3) {
                rightFdTree.addFD(context, 2);
            }
            if (leftFdTree.findContext(context) == 3) {
                leftFdTree.addFD(context, 2);
            }
            return 0;
        }

//        findTime += timer.getTimeUsed() - tempfind;


        //(3){context}:ocd
        CanonicalOD ocd = new CanonicalOD(context, tree.ocd.left, right);
        long temp = timer.getTimeUsed();
        ++odcan;
        Map<Integer, Set<Integer>> addvio = ocd.validForSample(data, errorRateThreshold, false, tupleNum);
        validTime += timer.getTimeUsed() - temp;
        if (!addvio.isEmpty()) {//ocd invalid or fd valid
            int res = 0;
            if (addvio.containsKey(-1) && !addvio.containsKey(-2)) {//add rightFdTree unknown node
                if (rightFdTree.findContext(context) == 3) {
                    rightFdTree.addFD(context, 2);
                }
            } else if (!addvio.containsKey(-1)) {
                if (leftFdTree.findContext(context) == 3) {
                    leftFdTree.addFD(context, 2);
                }
            }

            if (addvio.containsKey(-1)) {//context->left valid
                CanonicalOD od = new CanonicalOD(context, left);
                result.add(od);
                leftFdTree.addFD(context, 1);
                ++fdcount;
                ++res;
                node.isValid = 1;
                addvio.remove(-1);
            }
            if (addvio.containsKey(-2)) {//context->right valid
                CanonicalOD od = new CanonicalOD(context, right);
                result.add(od);
                rightFdTree.addFD(context, 1);
                ++fdcount;
                ++res;
                node.isValid = 1;
                addvio.remove(-2);
            }
            if (addvio.isEmpty()) {//fd valid
                return res;
            }
        }
        if (addvio.isEmpty()) {//{context}:left~right valid
            result.add(ocd);
            ++ocdcount;
            node.isValid = 1;
            for (int i = node.attribute + 1; i < columnNum; i++) {
                if (i == left) {
                    if (rightFdTree.findContext(context.addAttribute(i)) == 3) {
                        rightFdTree.addFD(context.addAttribute(i), 2);
                    }
                } else if (i == right) {
                    if (leftFdTree.findContext(context.addAttribute(i)) == 3) {
                        leftFdTree.addFD(context.addAttribute(i), 2);
                    }
                } else {
                    if (rightFdTree.findContext(context.addAttribute(i)) == 3) {
                        rightFdTree.addFD(context.addAttribute(i), 2);
                    }
                    if (leftFdTree.findContext(context.addAttribute(i)) == 3) {
                        leftFdTree.addFD(context.addAttribute(i), 2);
                    }
                }
            }
            return 1;
        }

        viorowsSet.add(addvio);
        ocdViorows.clear();

        for (int i = node.attribute + 1; i < columnNum; ++i) {
            if (i != left && i != right) {
                ContextNode newNode = new ContextNode(node.context.addAttribute(i));
                node.nextNodes.add(newNode);
                queue.offer(newNode);
            }
        }
        node.isValid = 0;
        return 0;

    }


    public int validateFd() {
        int count = 0;
        for (FdTree tree : allFdTree.allFdTree.values()) {
            if (tree.isComplete) {
                continue;
            }
            count += validateFdTree(tree);
            if (turn2Sample) {
                return count;
            }
        }
        return count;
    }

    private int validateFdTree(FdTree tree) {
        int count = 0;
        Queue<ContextNode> fdqueue = new LinkedList<>();
        fdqueue.offer(tree.root);
        while (!fdqueue.isEmpty()) {
            if (viorowsSet.getSize() >= wrongNum) {
                turn2Sample = true;
                return count;
            }
            ContextNode node = fdqueue.poll();
            for (ContextNode nextNode : node.nextNodes) {
                fdqueue.offer(nextNode);
            }
            if (node.nextNodes.isEmpty() && node.isValid == 2) {
                count += validateFdNode(tree, node);
            }

        }
        tree.isComplete = true;
        return count;
    }

    private int validateFdNode(FdTree tree, ContextNode node) {
        AttributeSet context = node.context;
        int right = tree.right;
        FdTree rightFdTree = allFdTree.allFdTree.get(right);
        if (rightFdTree.findGeneration(context)) {
            node.isValid = 1;
            return 0;
        }
        CanonicalOD od = new CanonicalOD(context, right);
        long temp = timer.getTimeUsed();
        ++odcan;
        Map<Integer, Set<Integer>> addvio = od.validForSample(data, errorRateThreshold, false, tupleNum);
        validTime += timer.getTimeUsed() - temp;

        if (addvio.isEmpty()) {
            result.add(od);
            ++fdcount;
            node.isValid = 1;
            return 1;
        }
        node.isValid = 0;
        for (int i = node.attribute + 1; i < columnNum; i++) {
            if (i == right) {
                continue;
            }
            AttributeSet newContext = node.context.addAttribute(i);
            if (!tree.findGeneration(newContext) && !allFdTree.allFdTree.get(i).findGeneration(context)) {
                node.nextNodes.add(new ContextNode(newContext));
            }
        }
        viorowsSet.add(addvio);
        return 0;

    }

    static class MemoryMonitor implements Runnable {
        private volatile boolean running = true;
        private long peakMemory = 0;
        private final Runtime runtime = Runtime.getRuntime();

        @Override
        public void run() {
            while (running) {
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                peakMemory = Math.max(peakMemory, usedMemory);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        public void stopMonitoring() {
            running = false;
        }

        public long getPeakMemory() {
            return peakMemory;
        }
    }

    public static void main(String[] args) throws IOException {
        MemoryMonitor monitor = new MemoryMonitor();
        Thread monitorThread = new Thread(monitor);
        monitorThread.setDaemon(true);
        monitorThread.start();

        try {
            DataFrame data = DataFrame.fromCsv(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
            Runtime r = Runtime.getRuntime();
            PLI.setData(data);
            SODD f = new SODD(30000000, -1f);
            f.initialize(data, Integer.parseInt(args[2]), Integer.parseInt(args[3]));

            int maxFails = args.length > 6 ? Integer.parseInt(args[6]) : 1;
            Timer spTime = new Timer();
            ClusterSampler sampler = new ClusterSampler();
            int sampleNonOcd = sampler.firstSample(f.allOcdTree, maxFails);
            System.out.println("sampleNonOcd:" + sampleNonOcd);
            f.sampleTime = spTime.getTimeUsed();

            f.discover(data);
            long endTime = f.timer.getTimeUsed();
            System.out.println(args[0]);

//            System.out.println("firstSampleTime:" + f.sampleTime + "ms, validTime:" + f.validTime + "ms, totalTime:" + (endTime + f.sampleTime) + "ms");
            System.out.println("firstSampleTime:" + f.sampleTime + "ms, inversionTime:" + f.allOcdTree.addNonNodeTime +
                    "ms, discoverTime:" + (endTime - f.validTime - f.allOcdTree.addNonNodeTime)
                    + "ms, validTime:" + f.validTime + "ms, totalTime:" + (endTime + f.sampleTime) + "ms");
            int allOd = f.fdcount + f.ocdcount;
            System.out.println("all od:" + allOd + "; fd:" + f.fdcount + "; ocd:" + f.ocdcount + "; odcan:" + f.odcan);

            System.out.println("ocdFinishTime:" + f.ocdFinishTime + "ms, fdFinishTime:" + f.fdFinishTime + "ms");
//            System.out.println(f.result);
        } finally {
            monitor.stopMonitoring();
            try {
                monitorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("MemoryCost: " + monitor.getPeakMemory() / 1024 / 1024 + "MB");
        }

        System.exit(0);
    }

//    public static void writeToFile(Set<CanonicalOD> sodList, String filePath) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//            for (CanonicalOD sod : sodList) {
//                writer.write(sod.toString());
//                writer.newLine();
//            }
//            System.out.println("filePath：" + filePath);
//        } catch (IOException e) {
//            System.err.println("wrong message：" + e.getMessage());
//        }
//    }
//
//    public static List<CanonicalOD> readFromFile(String filePath) {
//        List<CanonicalOD> sodList = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                CanonicalOD sod = CanonicalOD.fromString(line);
//                sodList.add(sod);
//            }
//            System.out.println("filePath：" + filePath);
//        } catch (IOException e) {
//            System.err.println("wrong message：" + e.getMessage());
//        }
//        return sodList;
//    }
//    public static void writeToFileTxt(Set<CanonicalOD> sodList, String filePath) {
//        Path path = Paths.get(filePath);
//        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
//            writer.write("Origin,Destination,Value\n");
//
//            for (CanonicalOD od : sodList) {
//                writer.write(od.toString());
//                writer.newLine();
//            }
//
//            System.out.println("filePath：" + filePath);
//        } catch (IOException e) {
//            System.err.println("wrong message：" + e.getMessage());
//        }
//    }
//        public static void main(String[] args) throws IOException {
////            DataFrame data = DataFrame.fromCsv(args[0],Integer.parseInt(args[1]));
//            DataFrame data = DataFrame.fromCsv(args[0], Integer.parseInt(args[1]),Double.parseDouble(args[4]),Double.parseDouble(args[5]));
//
//            Runtime r = Runtime.getRuntime();
//            PLI.setData(data);
//            SODD f = new SODD(30000000, -1f);
//            f.initialize(data, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
//
//
//            Timer spTime = new Timer();
//            ClusterSampler sampler = new ClusterSampler();
//            int sampleNonOcd = sampler.firstSample(f.allOcdTree);
////            int sampleNonOcd = sampler.firstSample(f.allOcdTree,100,data.getTupleCount());
//            System.out.println("sampleNonOcd:" + sampleNonOcd);
//            f.sampleTime = spTime.getTimeUsed();
//
//            f.discover(data);
//            long endTime = f.timer.getTimeUsed();
//            System.out.println(args[0]);
//    //        System.out.println("firstSampleTime:" + f.sampleTime + "ms, sampleDicoverTime:" + f.sampleDiscoverTime
//    //                + "ms, inversionTime:" + f.allOcdTree.addNonNodeTime + "ms, fdSampleTime:" + f.fdSampleTime +
//    //                "ms, dicoverTime:" + (endTime - f.validTime - f.sampleDiscoverTime - f.allOcdTree.addNonNodeTime - f.fdSampleTime)
//    //                + "ms, validTime:" + f.validTime + "ms, totalTime:" + (endTime + f.sampleTime) + "ms");
//            System.out.println("firstSampleTime:" + f.sampleTime + "ms, inversionTime:" + f.allOcdTree.addNonNodeTime +
//                    "ms, discoverTime:" + (endTime - f.validTime - f.allOcdTree.addNonNodeTime)
//                    + "ms, validTime:" + f.validTime + "ms, totalTime:" + (endTime + f.sampleTime) + "ms");
//
//            long startM = r.freeMemory();
//            long endM = r.totalMemory();
//            System.out.println("MemoryCost: " + String.valueOf((endM - startM) / 1024 / 1024) + "MB");
//            int allOd = f.fdcount + f.ocdcount;
//            System.out.println("all od:" + allOd + "; fd:" + f.fdcount + "; ocd:" + f.ocdcount+"; odcan:"+ f.odcan);
//
//            System.out.println("ocdFinishTime:" + f.ocdFinishTime + "ms, fdFinishTime:" + f.fdFinishTime + "ms");
//            System.out.println(f.result);
////            writeToFileTxt(f.result,"ncv1m-result.txt");
//            System.exit(0);
//        }
}
