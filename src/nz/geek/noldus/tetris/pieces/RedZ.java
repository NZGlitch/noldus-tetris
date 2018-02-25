package nz.geek.noldus.tetris.pieces;

import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Random;

public class RedZ extends GamePiece{

    public RedZ() {

        this.orientation = (GamePiece.ORIENTATION.values()[new Random().nextInt(4)]);
    }

    public Color getColor() {
        return Color.RED;
    }


    protected Point[] tilePositions(GamePiece.ORIENTATION orient, int targetX, int targetY) {

        switch (orient) {
            case UP:
            case DOWN: return new Point[] { new Point(targetX,targetY), new Point(targetX-1,targetY), new Point(targetX-1,targetY+1), new Point(targetX,targetY-1) };
            default: return new Point[] { new Point(targetX,targetY), new Point(targetX-1,targetY), new Point(targetX,targetY+1), new Point(targetX+1,targetY+1) };
        }

    }
}
