package nz.geek.noldus.tetris.pieces;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Random;

public class CyanI extends GamePiece {

    public CyanI() {
        this.orientation = GamePiece.ORIENTATION.LEFT;
    }

    public Color getColor() {
        return Color.CYAN;
    }

    protected Point[] tilePositions(GamePiece.ORIENTATION orient, int targetX, int targetY) {
        switch (orient) {
            case UP:
            case DOWN: return new Point[] { new Point(targetX,targetY-1), new Point(targetX,targetY), new Point(targetX,targetY+1), new Point(targetX,targetY-2) };
            case LEFT:
            case RIGHT: return new Point[] { new Point(targetX,targetY), new Point(targetX-1,targetY), new Point(targetX+1,targetY), new Point(targetX-2,targetY) };
        }
        return null;
    }

}
