package nz.geek.noldus.tetris.pieces;

import javafx.scene.paint.Color;
import nz.geek.noldus.tetris.GameCanvas;
import nz.geek.noldus.tetris.GameTile;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class GamePiece {

    public synchronized Point[] tileCenterPositions() {
        int sx = x; int sy = y;
        this.x=1; this.y=1;
        Point[] ret = tilePositions();
        this.x = sx; this.y = sy;
        return ret;

    }

    enum PIECES { BLUEJ, CYANI, GREENS, ORANGEL, PURPLET, REDZ, YELLOWO }

    enum ORIENTATION { RIGHT, UP, LEFT, DOWN};
    private int x = 5;
    private int y = 0;
    protected ORIENTATION orientation;

    private final GameTile[] tiles = new GameTile[] {
            new GameTile(Color.BLACK, getColor()),
            new GameTile(Color.BLACK, getColor()),
            new GameTile(Color.BLACK, getColor()),
            new GameTile(Color.BLACK, getColor()),
    };


    private static Random random = new Random();

    public static GamePiece getRandomPiece() {
        PIECES piece = PIECES.values()[random.nextInt(7)];
        switch (piece) {
            case BLUEJ: return new BlueJ();
            case CYANI: return new CyanI();
            case GREENS: return new GreenS();
            case ORANGEL: return new OrangeL();
            case PURPLET: return new PurpleT();
            case REDZ: return new RedZ();
            case YELLOWO: return new YellowO();
            default: return null;
        }
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }


    public abstract Color getColor();

    protected abstract Point[] tilePositions(ORIENTATION oreintation, int x, int y);

    public Point[] tilePositions() {
        return tilePositions(orientation, this.getX(), this.getY());
    }

    public GameTile getRepTile() {
        return tiles[0];
    }

    public Map<Point,GameTile> tileMap() {
        Map<Point, GameTile> tileMap = new HashMap();
        Point[] arr = tilePositions();
        for (int i = 0; i<4; i++) {
            tileMap.put(arr[i], tiles[i]);
        }
        return tileMap;
    }

    public synchronized void rotate(GameCanvas canvas) {

        ORIENTATION newO = ORIENTATION.values()[((orientation.ordinal()+5)%4)];
        for (Point p : tilePositions(newO, x, y)) {
            if (!canvas.pointEmpty(p)) return;
        }
        this.orientation = newO;

    }



    public synchronized boolean drop(GameCanvas canvas) {
        for (Point p : tilePositions(orientation, x, y+1)) {
            if (!canvas.pointEmpty(p)) return false;
        }
        y++;
        return true;
    }


    //Drop the piece into final positon, return the number of iterations required
    public synchronized int instantDrop(GameCanvas canvas) {
        int nextY = y+1;
        for (Point p : tilePositions(orientation, x, nextY)) {
            if (!canvas.pointEmpty(p)) return 0;
        }
        y=nextY;
        return 1+instantDrop(canvas);

    }

    public synchronized void moveLeft(GameCanvas canvas) {
        for (Point p : tilePositions(orientation, x-1, y)) {
            if (p.x < 0) return;
            if (!canvas.pointEmpty(p)) return;
        }
        x--;
    }

    public synchronized void moveRight(GameCanvas canvas) {
        for (Point p : tilePositions(orientation, x+1, y)) {
            if (p.x > 9) return;
            if (!canvas.pointEmpty(p)) return;
        }
        x++;
    }



}
