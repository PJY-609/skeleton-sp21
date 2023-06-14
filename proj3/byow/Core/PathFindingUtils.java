package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Point;
import java.util.*;

public class PathFindingUtils {
    private static int calcManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }


    private static double calcWeight(Point pos, Point src, Point dst) {
        return calcManhattanDistance(pos, dst);
    }

    private static class DistanceWeightedPoint extends Point implements Comparable<DistanceWeightedPoint> {
        private final double weight;

        private final DistanceWeightedPoint prev;

        private DistanceWeightedPoint(Point pos, DistanceWeightedPoint prev, double weight) {
            super(pos);

            this.prev = prev;

            this.weight = weight;
        }

        @Override
        public int compareTo(DistanceWeightedPoint o) {
            if (o.weight < weight) {
                return 1;
            } else if (o.weight > weight) {
                return -1;
            }

            return 0;
        }
    }

    public static List<Point> shortest(Point src, Point dst, TETile[][] canvas) {
        HashSet<Point> closed = new HashSet<>();
        PriorityQueue<DistanceWeightedPoint> distPriorityQueue = new PriorityQueue<>();

        double weight = calcWeight(dst, dst, src);
        DistanceWeightedPoint dwp = new DistanceWeightedPoint(dst, null, weight);
        distPriorityQueue.add(dwp);

        int[] dX = {1, 0, -1, 0};
        int[] dY = {0, 1, 0, -1};

        Point point;
        while (!distPriorityQueue.isEmpty()) {
            dwp = distPriorityQueue.poll();
            point = new Point(dwp);
            closed.add(point);

            if (src.equals(point)) {
                break;
            }

            for (int i = 0; i < 4; i++) {
                Point candidatePoint = new Point(point);
                candidatePoint.translate(dX[i], dY[i]);

                if (!closed.contains(candidatePoint) && isValildPosition(candidatePoint, canvas)) {
                    weight = calcWeight(candidatePoint, dst, src);
                    DistanceWeightedPoint dwpToAdd = new DistanceWeightedPoint(candidatePoint, dwp, weight);
                    distPriorityQueue.add(dwpToAdd);
                }
            }
        }

        List<Point> path = new LinkedList<>();
        dwp = dwp.prev;
        point = new Point(dwp);
        while (!point.equals(dst)) {
            path.add(point);
            dwp = dwp.prev;
            point = new Point(dwp);
        }

        return path;
    }

    private static boolean isValildPosition(Point point, TETile[][] canvas) {
        boolean xInRange = 0 <= point.x && point.x < canvas.length;
        boolean yInRange = 0 <= point.y && point.y < canvas[0].length;

        if (xInRange && yInRange) {
            return (!canvas[point.x][point.y].equals(Tileset.WALL) && !canvas[point.x][point.y].equals(Tileset.NOTHING));
        }

        return false;
    }
}
