package nz.geek.noldus.tetris.panes;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import nz.geek.noldus.tetris.GameController;

public class LinesPane extends Canvas{
    private GameController gc;
    private GraphicsContext context;

    public LinesPane(GameController gc, int w, int h) {
        super(w,h);
        this.gc = gc;
        context =this.getGraphicsContext2D();


    }

    public void draw() {

        //Clear box
        context.setFill(Color.WHITE);
        context.setStroke(Color.WHITE);
        context.fillRect(0,0,getWidth(), getHeight());

        context.setFill(Color.BLACK);
        context.setStroke(Color.BLACK);

        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.TOP);
        context.fillText(
                "Lines",
                Math.round(this.getWidth()  / 2),
                Math.round(this.getHeight() / 3)
        );

        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.BOTTOM);
        context.fillText(
                String.valueOf(gc.getLines()),
                Math.round(this.getWidth()  / 2),
                Math.round((this.getHeight() / 3)*2)
        );
    }

}
