package nz.geek.noldus.tetris.pieces;

import javafx.scene.paint.Color;

import java.awt.*;

public class YellowO extends GamePiece {

    public YellowO() {
        this.orientation = GamePiece.ORIENTATION.UP;
    }

    public Color getColor() {
        return Color.YELLOW;
    }

    protected Point[] tilePositions(GamePiece.ORIENTATION orient, int targetX, int targetY) {
        return new Point[] { new Point(targetX,targetY), new Point(targetX-1,targetY), new Point(targetX-1,targetY-1), new Point(targetX,targetY-1) };
    }
}
