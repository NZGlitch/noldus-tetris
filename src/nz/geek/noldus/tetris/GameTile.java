package nz.geek.noldus.tetris;

import javafx.scene.canvas.GraphicsContext;

import javafx.scene.paint.Color;

public class GameTile {

    private final Color border;
    private final Color fill;

    public GameTile(Color border, Color fill) {
        this.border = border;
        this.fill = fill;
    }

    public void draw(GraphicsContext context, double x, double y, double cw, double ch) {

        context.setStroke(border);
        context.setFill(border);
        context.fillRect(x,y,cw,ch);
        context.setStroke(fill);
        context.setFill(fill);
        context.fillRect(x + 1, y  + 1, cw - 2, ch - 2);
    }
}
