package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    public static final int BSP_TREE_DEPTH = 5;

    public static final double MIN_BSP_WH_RATIO = 0.45;
    public static final double MIN_BSP_HW_RATIO = 0.45;

    public static final int MIN_BSP_WIDTH = 8;
    public static final int MIN_BSP_HEIGHT = 8;

    public static final int MIN_SQUARE_ROOM_WIDTH = 3;
    public static final int MIN_SQUARE_ROOM_HEIGHT = 3;

    public static final double MIN_SQUARE_ROOM_PROPORTION = 0.5;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        TETile[][] finalWorldFrame = createEmptyWorld();

        long randomSeed = parseRandomSeedFromInputString(input);
        Random random = new Random(randomSeed);

        BSPTree bspTree = new BSPTree(WIDTH, HEIGHT, BSP_TREE_DEPTH, random);
        bspTree.draw(finalWorldFrame);

        return finalWorldFrame;
    }

    private static long parseRandomSeedFromInputString(String input){
        Pattern pattern = Pattern.compile("[Nn][0-9]+[Ss]");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String matched = matcher.group(0);
            return Long.parseLong(matched.substring(1, matched.length() - 1));
        }

        return 123;
    }

    public static void main(String[] args){
        System.out.println(parseRandomSeedFromInputString("N5197880843569031643S"));
//        Random random = new Random();
//        BSPTree bspTree = new BSPTree(WIDTH, HEIGHT, BSP_TREE_DEPTH, random);
//
//
//        TERenderer ter = new TERenderer();
//        ter.initialize(WIDTH, HEIGHT);
//
//        TETile[][] world = createEmptyWorld();
//
//        bspTree.draw(world);
//
//        ter.renderFrame(world);
    }

    private static TETile[][] createEmptyWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        TETile[] row = new TETile[HEIGHT];
        Arrays.fill(row, Tileset.NOTHING);
        Arrays.setAll(world, a -> Arrays.copyOf(row, row.length));

        return world;
    }
}
