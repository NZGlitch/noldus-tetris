package nz.geek.noldus.tetris;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import nz.geek.noldus.tetris.pieces.GamePiece;

import java.awt.*;
import java.util.Map;

public class GameCanvas {

    private GameTile[][] gameArea;
    private final Canvas canvas;
    private GraphicsContext context;

    private final GameTile emptyTile = new GameTile(Color.WHITE, Color.WHITE);

    private final double width;
    private final double height;
    private final double cw;
    private final double ch;

    public GameCanvas(Canvas canvas) {
        this.canvas = canvas;
        cw = (canvas.getWidth()/10D);
        ch = (canvas.getHeight()/20D);
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();

    }

    public void reset() {
        gameArea = new GameTile[20][10];
        context = canvas.getGraphicsContext2D();
        for (int y = 0; y<20; y++)
            for (int x = 0; x<10; x++)
                gameArea[y][x] = emptyTile;
    }

    public void drawState(GameController.GAMESTATE state) {
        //black background
        context.setFill(Color.BLACK);
        context.setStroke(Color.BLACK);
        context.fillRect(0,0,width,height);
        for (int y = 0; y<20; y++)
            for (int x = 0; x<10; x++) {
                if (x == 0 && y == 19)
                    gameArea[y][x].draw(context, (x*cw)+1, y*ch, cw-1, ch-1);
                else if (x == 9 && y == 19)
                    gameArea[y][x].draw(context, (x*cw), y*ch, cw-1, ch-1);
                else if (x == 0)
                    gameArea[y][x].draw(context, (x*cw)+1, y*ch, cw-1, ch);
                else if (x == 9)
                    gameArea[y][x].draw(context, (x*cw), y*ch, cw-1, ch);
                else if (y == 19)
                    gameArea[y][x].draw(context, x*cw, y*ch, cw, ch-1);
                else
                    gameArea[y][x].draw(context, x*cw, y*ch, cw, ch);
            }
        if (state == GameController.GAMESTATE.WAITING) {
            context.setFill(Color.BLACK);
            context.setStroke(Color.BLACK);
            context.setTextAlign(TextAlignment.CENTER);
            context.setTextBaseline(VPos.TOP);
            context.fillText(
                    "Press Enter to start a new game",
                    Math.round(canvas.getWidth() / 2),
                    Math.round(canvas.getHeight() / 10)
            );
        }
    }

    //Returns true if the current piece can be drawn
    public boolean drawGamePiece(GamePiece currentPiece) {
        Point[] pieceCoords = currentPiece.tilePositions();
        boolean outside = false;
        //step one, check all intended tiles are empty
        for (Point p : pieceCoords) {
            if (p.y < 0) outside = true;
            else if (gameArea[p.y][p.x] != emptyTile) return false;

        }
        //if any part is outside, drop the piece and try again
        if (outside) {
            if (currentPiece.drop(this))
                return drawGamePiece(currentPiece);
            else
                return false;
        } else {
            //step 2: draw the pieces
            for (Point p : pieceCoords) {
                currentPiece.getRepTile().draw(context,p.x*cw, p.y*ch, cw, ch);
            }
            return true;
        }
    }

    public int instantDrop(GamePiece piece) {
        int dist = 0;
        while(!pieceLanded(piece)) {
            dist++;
            piece.drop(this);
        }
        return dist;
    }

    //retuns true if any part of the piece is on top of another (or at the bottom). Also adds piece to the board permanently if this is the case
    public boolean pieceLanded(GamePiece currentPiece) {
        for (Point p : currentPiece.tilePositions()) {
            if (p.y == 19) return addPiece(currentPiece);
            if (gameArea[p.y+1][p.x] != emptyTile) return addPiece(currentPiece);
        }

        return false;
    }

    private boolean addPiece(GamePiece piece) {
        Map<Point, GameTile> pMap = piece.tileMap();
        for (Point p : pMap.keySet()) {
            gameArea[p.y][p.x] = pMap.get(p);
        }
        return true;
    }

    public boolean pointEmpty(Point p) {
        //System.out.println("x:"+p.x+"y:"+p.y);
        if (p.y > 19 || p.x < 0 || p.x > 9) return false;
        int fixY = p.y;
        if (p.y < 0) fixY  = 0;
        return gameArea[fixY][p.x] == emptyTile;
    }

    public boolean clearLine() {
        for (int y = 19; y >=0; y--) {
            boolean line = true;
            for (int x = 0; x < 10; x++) {
                if (gameArea[y][x] == emptyTile) {
                    line = false;
                    break;
                }
            }
            if (line) {
                shiftDown(y);
                return true;
            }
        }
        return false;
    }

    private void shiftDown(int y) {
        for (; y>0; y--) {
            for (int x = 0; x < 10; x++) gameArea[y][x] = gameArea[y-1][x];
        }
        for (int x = 0; x < 10; x++) gameArea[0][x] = emptyTile;
    }

    public void blank() {
        //black background
        context.setFill(Color.WHITE);
        context.setStroke(Color.WHITE);
        context.fillRect(0,0,width,height);

            context.setFill(Color.BLACK);
            context.setStroke(Color.BLACK);
            context.setTextAlign(TextAlignment.CENTER);
            context.setTextBaseline(VPos.TOP);
            context.fillText(
                    "PAUSED - press 'p' to resume",
                    Math.round(canvas.getWidth()  / 2),
                    Math.round(canvas.getHeight() / 10)
            );

    }
}
