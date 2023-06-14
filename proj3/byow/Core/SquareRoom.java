package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;

public class SquareRoom extends Room{
    private final Point floorBottomLeftCorner;
    private final Point floorUpperRightCorner;

    public SquareRoom(int xmin, int xmax, int ymin, int ymax, Random random) {
        int x1 = RandomUtils.uniform(random, xmin + 1, xmax);
        int x2 = RandomUtils.uniform(random, xmin + 1, xmax);

        int y1 = RandomUtils.uniform(random, ymin + 1, ymax);
        int y2 = RandomUtils.uniform(random, ymin + 1, ymax);

        int width = Math.abs(x1 - x2) + 1;
        int height = Math.abs(y1 - y2) + 1;

        int area = width * height;

        int maxArea = (xmax - xmin + 1) * (ymax - ymin + 1);
        double areaProportion = (double) area / maxArea;

        while (width < Engine.MIN_SQUARE_ROOM_WIDTH
                || height < Engine.MIN_SQUARE_ROOM_HEIGHT
                || areaProportion < Engine.MIN_SQUARE_ROOM_PROPORTION) {
            x1 = RandomUtils.uniform(random, xmin, xmax + 1);
            x2 = RandomUtils.uniform(random, xmin, xmax + 1);

            y1 = RandomUtils.uniform(random, ymin, ymax + 1);
            y2 = RandomUtils.uniform(random, ymin, ymax + 1);

            width = Math.abs(x1 - x2) + 1;
            height = Math.abs(y1 - y2) + 1;

            area = width * height;

            areaProportion = (double) area / maxArea;
        }

        floorBottomLeftCorner = new Point(Math.min(x1, x2), Math.min(y1, y2));
        floorUpperRightCorner = new Point(Math.max(x1, x2), Math.max(y1, y2));

        center = new Point();
        center.x = (floorBottomLeftCorner.x + floorUpperRightCorner.x) / 2;
        center.y = (floorBottomLeftCorner.y + floorUpperRightCorner.y) / 2;
    }

    @Override
    protected void drawWall(TETile[][] canvas) {
        for (int i = floorBottomLeftCorner.x - 1; i <= floorUpperRightCorner.x + 1; i++) {
            if (0 <= i && i < canvas.length) {
                canvas[i][floorBottomLeftCorner.y - 1] = Tileset.WALL;
                canvas[i][floorUpperRightCorner.y + 1] = Tileset.WALL;
            }
        }

        for (int i = floorBottomLeftCorner.y - 1; i <= floorUpperRightCorner.y + 1; i++) {
            if (0 <= i && i < canvas[0].length) {
                canvas[floorBottomLeftCorner.x - 1][i] = Tileset.WALL;
                canvas[floorUpperRightCorner.x + 1][i] = Tileset.WALL;
            }
        }
    }

    @Override
    protected void drawFloor(TETile[][] canvas) {
        for (int i = floorBottomLeftCorner.x; i <= floorUpperRightCorner.x; i++) {
            for (int j = floorBottomLeftCorner.y; j <= floorUpperRightCorner.y; j++) {
                canvas[i][j] = Tileset.FLOOR;
            }
        }
    }


}
