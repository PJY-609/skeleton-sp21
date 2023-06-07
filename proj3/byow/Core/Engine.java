package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import jdk.jshell.execution.Util;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;

    public static final int BSP_TREE_DEPTH = 4;

    public static final double MIN_BSP_WH_RATIO = 0.45;
    public static final double MIN_BSP_HW_RATIO = 0.45;

    public static final int MIN_BSP_WIDTH = 8;
    public static final int MIN_BSP_HEIGHT = 8;

    public static final int MIN_SQUARE_ROOM_WIDTH = 3;
    public static final int MIN_SQUARE_ROOM_HEIGHT = 3;

    public static final double MIN_SQUARE_ROOM_PROPORTION = 0.5;

    private static final Font LARGE_FONT = new Font("Monaco", Font.BOLD, 32);

    private static final Font MEDIUM_FONT = new Font("Monaco", Font.BOLD, 24);

    private static final Font SMALL_FONT = new Font("Monaco", Font.BOLD, 14);

    private static final File GAME_FILE = Utils.join(System.getProperty("user.dir"), "game.txt");

    private StringBuilder instructionBuilder = new StringBuilder();
    private Random random = new Random();

    private TETile[][] world;

    private Point avatarPosition = new Point();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        instructionBuilder = new StringBuilder();

        showStartMenuPage();
        handleStartMenuInput();
        runGame();
    }

    private void initWorld() {
        world = createEmptyWorld();

        MapGenerator mapGenerator = new MapGenerator(WIDTH, HEIGHT, BSP_TREE_DEPTH, random);
        mapGenerator.draw(world);

        placeAvatarRandomly();
    }

    private void placeAvatarRandomly() {
        while (true) {
            int x = RandomUtils.uniform(random, 0, WIDTH);
            int y = RandomUtils.uniform(random, 0, HEIGHT);

            if (world[x][y].equals(Tileset.FLOOR)) {
                world[x][y] = Tileset.AVATAR;
                avatarPosition.setLocation(x, y);
                break;
            }
        }
    }

    private void replayGame(String instruction) {
        for (char c: instruction.toCharArray()) {
            c = Character.toUpperCase(c);
            if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                moveAvatar(c, false);
            }
        }
    }

    private void runGame() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toUpperCase(c);

                if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                    moveAvatar(c, true);
                    repaintWorld();
                } else if (c == ':') {
                    instructionBuilder.append(c);
                } else if (c == 'Q' && instructionBuilder.charAt(instructionBuilder.length() - 1) == ':') {
                    instructionBuilder.append(c);
                    saveGame();
                    System.exit(0);
                    break;
                }
            }
        }
    }


    private void runGame(String instruction) {
        for (char c: instruction.toCharArray()) {
            c = Character.toUpperCase(c);

            if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                moveAvatar(c, true);
            } else if (c == ':') {
                instructionBuilder.append(c);
            } else if (c == 'Q' && instructionBuilder.charAt(instructionBuilder.length() - 1) == ':') {
                instructionBuilder.append(c);
                saveGame();
                break;
            }
        }
    }

    private void saveGame() {
        String instruction = instructionBuilder.toString();
        Utils.writeObject(GAME_FILE, instruction);
    }

    private void loadGame() {
        String instruction = Utils.readContentsAsString(GAME_FILE);
        instructionBuilder.insert(0, instruction);

        int[] randomSeedLoc = locateRandomSeed(instruction);
        long randomSeed = Long.parseLong(instruction.substring(randomSeedLoc[0] + 1, randomSeedLoc[1] - 1));
        random = new Random(randomSeed);

        initWorld();

        replayGame(instruction);
    }


    private void repaintWorld() {
        StdDraw.clear(Color.BLACK);
        ter.renderFrame(world);
        StdDraw.show();
    }

    private void moveAvatar(char c, boolean keepTrack) {
        Point targetPosition = new Point(avatarPosition);

        if (c == 'W') {
            targetPosition.translate(0, 1);
        } else if (c == 'A') {
            targetPosition.translate(-1, 0);
        } else if (c == 'S') {
            targetPosition.translate(0, -1);
        } else if (c == 'D') {
            targetPosition.translate(1, 0);
        }

        if (0 <= targetPosition.x && targetPosition.x < WIDTH && 0 <= targetPosition.y && targetPosition.y < HEIGHT
                && world[targetPosition.x][targetPosition.y].equals(Tileset.FLOOR)) {
            if (keepTrack) {
                instructionBuilder.append(c);
            }
            world[avatarPosition.x][avatarPosition.y] = Tileset.FLOOR;
            world[targetPosition.x][targetPosition.y] = Tileset.AVATAR;
            avatarPosition = targetPosition;
        }

    }

    private void showStartMenuPage() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(LARGE_FONT);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "CS61B: BYOW");

        StdDraw.setFont(MEDIUM_FONT);
        StdDraw.text((double) WIDTH / 2, (double) (HEIGHT - 8) / 2, "New Game (N)");
        StdDraw.show();

        StdDraw.text((double) WIDTH / 2, (double) (HEIGHT - 12) / 2, "Load Game (L)");
        StdDraw.show();

        StdDraw.text((double) WIDTH / 2, (double) (HEIGHT - 16) / 2, "Quit (Q)");
        StdDraw.show();
    }

    private void handleStartMenuInput() {
        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                c = Character.toUpperCase(c);

                if (c == 'N') {
                    instructionBuilder.append(c);
                    createNewGame();
                    repaintWorld();
                    break;
                } else if (c == 'L') {
                    instructionBuilder.append(c);
                    loadGame();
                    repaintWorld();
                    break;
                } else if (c == 'Q') {
                    instructionBuilder.append(c);
                    System.exit(0);
                    break;
                }
            }
        }
    }

    private void createNewGame() {
        handleRandomSeedInput();

        instructionBuilder.append("S");

        Utils.writeObject(GAME_FILE, instructionBuilder.toString());

        initWorld();
    }



    private void handleRandomSeedInput() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "Random seed: ");
        StdDraw.show();

        StringBuilder stringBuilder = new StringBuilder();

        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                if (c == '\n') {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.show();
                    break;
                }

                stringBuilder.append(c);
                StdDraw.clear(Color.BLACK);
                StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "Random seed: " + stringBuilder);
                StdDraw.show();
            }
        }

        String randomSeedStr = stringBuilder.toString();
        instructionBuilder.append(randomSeedStr);
        instructionBuilder.append("S");
        long randomSeed = Long.parseLong(randomSeedStr);
        random = new Random(randomSeed);
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
        instructionBuilder = new StringBuilder();

        if (input.startsWith("L")) {
            instructionBuilder.append("L");
            loadGame();
            runGame(input.substring(1));
            return world;
        }


        int[] randomSeedLoc = locateRandomSeed(input);
        long randomSeed = 2023;
        if (randomSeedLoc[0] + 2 < randomSeedLoc[1]) {
            randomSeed = Long.parseLong(input.substring(randomSeedLoc[0] + 1, randomSeedLoc[1] - 1));
        }
        random = new Random(randomSeed);

        instructionBuilder.append(input, randomSeedLoc[0], randomSeedLoc[1]);

        initWorld();
        runGame(input.substring(randomSeedLoc[1]));
        return world;
    }


    private static int[] locateRandomSeed(String input){
        Pattern pattern = Pattern.compile("[Nn][0-9]+[Ss]");
        Matcher matcher = pattern.matcher(input);

        int[] ret = new int[2];
        if (matcher.find()) {
            ret[0] = matcher.start();
            ret[1] = matcher.end();
        }

        return ret;
    }

    public static void main(String[] args){
        Engine engine = new Engine();
        engine.interactWithInputString("LSWASWDDDSSA:Q");

    }

    private static TETile[][] createEmptyWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        TETile[] row = new TETile[HEIGHT];
        Arrays.fill(row, Tileset.NOTHING);
        Arrays.setAll(world, a -> Arrays.copyOf(row, row.length));

        return world;
    }
}
