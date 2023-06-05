package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;

public abstract class Room {
    protected Point center;

    protected void drawWall(TETile[][] canvas) {}

    protected void drawFloor(TETile[][] canvas) {}

    public void draw(TETile[][] canvas, TETile tile) {
        if (tile.equals(Tileset.FLOOR)) {
            drawFloor(canvas);
        } else if (tile.equals(Tileset.WALL)) {
            drawWall(canvas);
        }
    }
}
