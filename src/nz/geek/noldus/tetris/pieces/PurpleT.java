package nz.geek.noldus.tetris.pieces;

import javafx.scene.paint.Color;

import java.awt.*;

public class PurpleT extends GamePiece {

    public PurpleT() {
        this.orientation = ORIENTATION.UP;
    }

    public Color getColor() {
        return Color.PURPLE;
    }


    protected Point[] tilePositions(ORIENTATION orient, int targetX, int targetY) {

        switch (orient) {
            case LEFT: return new Point[] { new Point(targetX,targetY), new Point(targetX,targetY-1), new Point(targetX,targetY+1), new Point(targetX-1,targetY) };
            case UP: return new Point[] { new Point(targetX,targetY), new Point(targetX-1,targetY), new Point(targetX+1,targetY), new Point(targetX,targetY+1) };
            case DOWN: return new Point[] {  new Point(targetX,targetY), new Point(targetX-1,targetY), new Point(targetX+1,targetY), new Point(targetX,targetY-1) };
            default: return new Point[] { new Point(targetX,targetY), new Point(targetX,targetY-1), new Point(targetX,targetY+1), new Point(targetX+1,targetY) };
        }

    }
}
