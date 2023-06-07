package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Point;
import java.util.*;

public class MapGenerator {
    private final Random random;

    private final int width;

    private final int height;

    private final BSPTreeNode bspTreeRoot;

    private final List<Point> roomCenters;

    private final HashSet<Point> corridorFloorPoints;

    private final HashSet<Point> corridorWallPoints;

    public static class BSPTreeNode {
        private BSPTreeNode left;
        private BSPTreeNode right;

        private final boolean isLeaf;

        public Room room;

        public BSPTreeNode() {
            isLeaf = true;
            room = null;
        }

        public BSPTreeNode(BSPTreeNode left, BSPTreeNode right){
            isLeaf = false;
            this.left = left;
            this.right = right;
        }

        public BSPTreeNode(int xmin, int xmax, int ymin, int ymax, Random random){
            isLeaf = true;

            if (RandomUtils.uniform(random,2) == 1) {
                room = new SquareRoom(xmin + 1, xmax - 1, ymin + 1, ymax - 1, random);
            } else {
                room = new RandomWalkRoom(xmin + 1, xmax - 1, ymin + 1, ymax - 1, random);
            }
        }
    }


    public MapGenerator(int width, int height, int depth, Random random) {
        assert depth > 0;

        this.width = width;
        this.height = height;

        this.random = random;

        roomCenters = new LinkedList<>();

        corridorFloorPoints = new HashSet<>();
        corridorWallPoints = new HashSet<>();

        bspTreeRoot = buildTreeRecursiveHelper(depth, 0, width - 1, 0, height - 1);

        connectRoomsWithCorridors();
    }

    private BSPTreeNode buildTreeRecursiveHelper(int depth, int xmin, int xmax, int ymin, int ymax) {
        // space is too small, return empty leaf node
        int width = xmax - xmin + 1;
        int height = ymax - ymin + 1;
        if (width <= Engine.MIN_BSP_WIDTH || height <= Engine.MIN_BSP_HEIGHT){
            return new BSPTreeNode();
        }

        // return leaf node
        if (depth == 1) {
            BSPTreeNode bspTreeNode = new BSPTreeNode(xmin, xmax, ymin, ymax, random);
            roomCenters.add(bspTreeNode.room.center);
            return bspTreeNode;
        }

        depth--;

        int[] splitResult = randomBinaryPartition(xmin, xmax, ymin, ymax);

        BSPTreeNode left;
        BSPTreeNode right;
        if (splitResult[0] > -1) {
            left = buildTreeRecursiveHelper(depth, xmin, splitResult[0] - 1, ymin, ymax);
            right = buildTreeRecursiveHelper(depth, splitResult[0], xmax, ymin, ymax);
        } else {
            left = buildTreeRecursiveHelper(depth, xmin, xmax, ymin, splitResult[1] - 1);
            right = buildTreeRecursiveHelper(depth, xmin, xmax, splitResult[1], ymax);
        }

        return new BSPTreeNode(left, right);
    }



    private int[] randomBinaryPartition(int xmin, int xmax, int ymin, int ymax){
        int height = ymax - ymin + 1;
        int width = xmax - xmin + 1;

        int[] ret = new int[2];

        double minRatio;
        double ratio1;
        double ratio2;

        double midRatio = RandomUtils.gaussian(random, 0.5, 0.25);
        midRatio  = Math.max(0.01, Math.min(0.99, midRatio));

        // Vertically partition or horizontally partition on a regular basis
        if (RandomUtils.uniform(random, 2) > 0) {
            ret[0] = -1;
            ret[1] = (int) ((ymax + ymin) * midRatio); // space between [ymin + 1, ymax - 1]
            minRatio = Engine.MIN_BSP_HW_RATIO;
            ratio1 = (double) (ret[1] - ymin) / width;
            ratio2 = (double) (ymax - ret[1] + 1) / width;
        } else {
            ret[1] = -1;
            ret[0] = (int) ((xmax + xmin) * midRatio); // space between [xmin + 1, xmax - 1]
            minRatio = Engine.MIN_BSP_WH_RATIO;
            ratio1 = (double) (ret[0] - xmin + 1) / height;
            ratio2 = (double) (xmax - ret[0] + 1) / height;
        }

        // Keep partitioning until conditions satisfied
        while ((ratio1 < minRatio || ratio2 < minRatio)) {
            midRatio = RandomUtils.gaussian(random, 0.5, 0.25);
            midRatio  = Math.max(0.01, Math.min(0.99, midRatio));

            if (RandomUtils.uniform(random, 2) > 0) {
                ret[0] = -1;
                ret[1] = (int) ((ymax + ymin) * midRatio); // space between [ymin + 1, ymax - 1]
                minRatio = Engine.MIN_BSP_HW_RATIO;
                ratio1 = (double) (ret[1] - ymin) / width;
                ratio2 = (double) (ymax - ret[1] + 1) / width;
            } else {
                ret[1] = -1;
                ret[0] = (int) ((xmax + xmin) * midRatio); // space between [xmin + 1, xmax - 1]
                minRatio = Engine.MIN_BSP_WH_RATIO;
                ratio1 = (double) (ret[0] - xmin + 1) / height;
                ratio2 = (double) (xmax - ret[0] + 1) / height;
            }
        }

        return ret;
    }

    private Comparator<Point> createRoomCenterComparator(Point targetCenter) {
        return new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                int distance1 = Math.abs(o1.x - targetCenter.x) + Math.abs(o1.y - targetCenter.y);
                int distance2 = Math.abs(o2.x - targetCenter.x) + Math.abs(o2.y - targetCenter.y);

                if (distance1 < distance2) {
                    return -1;
                } else if (distance1 > distance2) {
                    return 1;
                }
                return 0;
            }
        };
    }

    private Point findClosestRoomCenter(Point target, List<Point> points) {
        Comparator<Point> roomCenterComparator = createRoomCenterComparator(target);
        PriorityQueue<Point> priorityQueue = new PriorityQueue<>(roomCenterComparator);
        priorityQueue.addAll(points);
        return priorityQueue.poll();
    }

    private void connectRoomsWithCorridors() {
        List<Point> centers = new LinkedList<>(roomCenters);
        Point startCenter = centers.remove(RandomUtils.uniform(random, centers.size()));
        Point selectCenter = startCenter;

        while (!centers.isEmpty()) {
            Point closestPoint = findClosestRoomCenter(selectCenter, centers);
            centers.remove(closestPoint);
            connectTwoRooms(selectCenter, closestPoint);
            selectCenter = closestPoint;
        }

        connectTwoRooms(startCenter, selectCenter);
    }


    private void connectTwoRooms(Point srcPoint, Point dstPoint) {
        Point currentPoint = new Point(srcPoint);

        while (currentPoint.x != dstPoint.x || currentPoint.y != dstPoint.y) {
            int dx = 0;
            int dy = 0;

            if (currentPoint.x != dstPoint.x) {
                dx = (dstPoint.x - currentPoint.x) / Math.abs(dstPoint.x - currentPoint.x);
            } else {
                dy = (dstPoint.y - currentPoint.y) / Math.abs(dstPoint.y - currentPoint.y);
            }

            if (RandomUtils.uniform(random) < 0.2) {
                if (dx != 0) {
                    dx = 0;
                    dy = RandomUtils.uniform(random, -1, 2);
                } else {
                    dx = RandomUtils.uniform(random, -1, 2);
                    dy = 0;
                }
            }

            currentPoint.translate(dx, dy);

            if (0 < currentPoint.x && currentPoint.x < width - 1 && 0 < currentPoint.y && currentPoint.y < height - 1) {
                addCorridorPoint(currentPoint);
                currentPoint = new Point(currentPoint);
            }
        }
    }

    private void addCorridorPoint(Point currentPoint) {
        int[] dX = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dY = {-1, 0, 1, -1, 1, -1, 0, 1};

        if (0 < currentPoint.x && currentPoint.x < width - 1 && 0 < currentPoint.y && currentPoint.y < height - 1) {
            corridorFloorPoints.add(currentPoint);

            for (int i = 0; i < 8; i++) {
                Point wallPoint = new Point(currentPoint);
                wallPoint.translate(dX[i], dY[i]);
                corridorWallPoints.add(wallPoint);
            }
        }
    }

    private void draw(TETile[][] canvas, TETile tile){
        Queue<BSPTreeNode> queue = new LinkedList<>();

        queue.add(bspTreeRoot);

        while (!queue.isEmpty()) {
            BSPTreeNode node = queue.remove();
            if (node.isLeaf && node.room != null) {
                node.room.draw(canvas, tile);
            } else if (!node.isLeaf) {
                queue.add(node.left);
                queue.add(node.right);
            }
        }

        HashSet<Point> corridorPoints = corridorFloorPoints;
        if (tile.equals(Tileset.WALL)) {
            corridorPoints = corridorWallPoints;
        }

        for (Point point: corridorPoints) {
            canvas[point.x][point.y] = tile;
        }
    }

    public void draw(TETile[][] canvas) {
        draw(canvas, Tileset.WALL);
        draw(canvas, Tileset.FLOOR);
    }
}
