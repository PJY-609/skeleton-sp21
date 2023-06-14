package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.*;

public class RandomWalkRoom extends Room {
    private final HashSet<Point> floorPoints;
    private final HashSet<Point> wallPoints;

    public RandomWalkRoom(int xmin, int xmax, int ymin, int ymax, Random random) {
        center = new Point();
        center.x = (xmin + xmax) / 2;
        center.y = (ymin + ymax) / 2;

        int width = (xmax - xmin + 1);
        int height = (ymax - ymin + 1);
        int walkLength = width * height;

        Point currentPoint = center;
        floorPoints = new HashSet<>();
        floorPoints.add(currentPoint);
        wallPoints = new HashSet<>();


        int[] dX = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dY = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < walkLength; i++) {
            Point randomPoint = new Point(currentPoint);
            int direction = RandomUtils.uniform(random, Integer.MAX_VALUE) % 4;
            if (direction == 0) {
                randomPoint.translate(1, 0);
            } else if (direction == 1) {
                randomPoint.translate(-1, 0);
            } else if (direction == 2) {
                randomPoint.translate(0, 1);
            } else {
                randomPoint.translate(0, -1);
            }

            if (xmin < randomPoint.x && randomPoint.x < xmax && ymin < randomPoint.y && randomPoint.y < ymax) {
                floorPoints.add(randomPoint);
                currentPoint = randomPoint;

                for (int t = 0; t < 8; t++) {
                    Point wallPoint = new Point(randomPoint);
                    wallPoint.translate(dX[t], dY[t]);
                    wallPoints.add(wallPoint);
                }
            }

        }
    }

    @Override
    protected void drawWall(TETile[][] canvas) {
        for (Point point: wallPoints) {
            canvas[point.x][point.y] = Tileset.WALL;
        }
    }

    @Override
    protected void drawFloor(TETile[][] canvas) {
        for (Point point: floorPoints) {
            canvas[point.x][point.y] = Tileset.FLOOR;
        }
    }

}
