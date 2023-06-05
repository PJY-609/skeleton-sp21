package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Point;
import java.util.*;

public class BSPTree {
    private final Random random;

    private final int width;

    private final int height;

    private final BSPNode root;

    private final List<Point> roomCenters;

    private final HashSet<Point> corridorFloorPoints;

    private final HashSet<Point> corridorWallPoints;

    public static class BSPNode {
        private BSPNode left;
        private BSPNode right;

        private final boolean isLeaf;

        private Point bottomLeftCorner;

        private Point upperRightCorner;

        public Room room;

        public BSPNode() {
            isLeaf = true;
            room = null;
        }

        public BSPNode(BSPNode left, BSPNode right){
            isLeaf = false;
            this.left = left;
            this.right = right;
        }

        public BSPNode(int xmin, int xmax, int ymin, int ymax, Random random){
            isLeaf = true;

            if (RandomUtils.uniform(random,2) == 1) {
                room = new SquareRoom(xmin + 1, xmax - 1, ymin + 1, ymax - 1, random);
            } else {
                room = new RandomWalkRoom(xmin + 1, xmax - 1, ymin + 1, ymax - 1, random);
            }


            bottomLeftCorner = new Point(xmin, ymin);
            upperRightCorner = new Point(xmax, ymax);
        }

        public void draw(TETile[][] canvas) {
            if (bottomLeftCorner == null || upperRightCorner == null) {
                return;
            }

            for (int i = bottomLeftCorner.x; i <= upperRightCorner.x; i++) {
                canvas[i][bottomLeftCorner.y] = Tileset.WALL;
                canvas[i][upperRightCorner.y] = Tileset.WALL;
            }

            for (int i = bottomLeftCorner.y; i <= upperRightCorner.y; i++) {
                canvas[bottomLeftCorner.x][i] = Tileset.WALL;
                canvas[upperRightCorner.x][i] = Tileset.WALL;
            }
        }
    }


    public BSPTree(int width, int height, int depth, Random random) {
        assert depth > 0;

        this.width = width;
        this.height = height;

        this.random = random;

        roomCenters = new LinkedList<>();

        corridorFloorPoints = new HashSet<>();
        corridorWallPoints = new HashSet<>();

        root = buildTreeRecursiveHelper(depth, 0, width - 1, 0, height - 1);

        connectRoomsWithCorridors();
    }

    private BSPNode buildTreeRecursiveHelper(int depth, int xmin, int xmax, int ymin, int ymax) {
        // space is too small, return empty leaf node
        int width = xmax - xmin + 1;
        int height = ymax - ymin + 1;
        if (width <= Engine.MIN_BSP_WIDTH || height <= Engine.MIN_BSP_HEIGHT){
            return new BSPNode();
        }

        // return leaf node
        if (depth == 1) {
            BSPNode bspNode = new BSPNode(xmin, xmax, ymin, ymax, random);
            roomCenters.add(bspNode.room.center);
            return bspNode;
        }

        depth--;

        int[] splitResult = randomBinaryPartition(xmin, xmax, ymin, ymax);

        BSPNode left;
        BSPNode right;
        if (splitResult[0] > -1) {
            left = buildTreeRecursiveHelper(depth, xmin, splitResult[0] - 1, ymin, ymax);
            right = buildTreeRecursiveHelper(depth, splitResult[0], xmax, ymin, ymax);
        } else {
            left = buildTreeRecursiveHelper(depth, xmin, xmax, ymin, splitResult[1] - 1);
            right = buildTreeRecursiveHelper(depth, xmin, xmax, splitResult[1], ymax);
        }

        return new BSPNode(left, right);
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

        int[] dX = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dY = {-1, 0, 1, -1, 1, -1, 0, 1};

        while (currentPoint.x != dstPoint.x) {
            int dx = dstPoint.x - currentPoint.x > 0 ? 1 : -1;
            int dy = 0;

            if (RandomUtils.uniform(random) < 0.2) {
                dx = 0;
                dy = RandomUtils.uniform(random, -1, 2);
            }

            currentPoint.translate(dx, dy);

            if (0 < currentPoint.x && currentPoint.x < width - 1 && 0 < currentPoint.y && currentPoint.y < height - 1) {
                corridorFloorPoints.add(currentPoint);
                currentPoint = new Point(currentPoint);

                for (int i = 0; i < 8; i++) {
                    Point wallPoint = new Point(currentPoint);
                    wallPoint.translate(dX[i], dY[i]);
                    corridorWallPoints.add(wallPoint);
                }
            }
        }

        while (currentPoint.y != dstPoint.y) {
            int dx = 0;
            int dy = dstPoint.y - currentPoint.y > 0 ? 1 : -1;

            if (RandomUtils.uniform(random) < 0.2) {
                dx = RandomUtils.uniform(random, -1, 2);
                dy = 0;
            }

            currentPoint.translate(dx, dy);

            if (0 < currentPoint.x && currentPoint.x < width - 1 && 0 < currentPoint.y && currentPoint.y < height - 1) {
                corridorFloorPoints.add(currentPoint);
                currentPoint = new Point(currentPoint);

                for (int i = 0; i < 8; i++) {
                    Point wallPoint = new Point(currentPoint);
                    wallPoint.translate(dX[i], dY[i]);
                    corridorWallPoints.add(wallPoint);
                }
            }
        }

        while (currentPoint.x != dstPoint.x) {
            int dx = dstPoint.x - currentPoint.x > 0 ? 1 : -1;
            int dy = 0;

            currentPoint.translate(dx, dy);

            if (0 < currentPoint.x && currentPoint.x < width - 1 && 0 < currentPoint.y && currentPoint.y < height - 1) {
                corridorFloorPoints.add(currentPoint);
                currentPoint = new Point(currentPoint);

                for (int i = 0; i < 8; i++) {
                    Point wallPoint = new Point(currentPoint);
                    wallPoint.translate(dX[i], dY[i]);
                    corridorWallPoints.add(wallPoint);
                }
            }
        }
    }


    private void draw(TETile[][] canvas, TETile tile){
        Queue<BSPNode> queue = new LinkedList<>();

        queue.add(root);

        while (!queue.isEmpty()) {
            BSPNode node = queue.remove();
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
