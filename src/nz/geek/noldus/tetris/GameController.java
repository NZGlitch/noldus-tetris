package nz.geek.noldus.tetris;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nz.geek.noldus.tetris.panes.LevelPane;
import nz.geek.noldus.tetris.panes.LinesPane;
import nz.geek.noldus.tetris.panes.ScorePane;
import nz.geek.noldus.tetris.pieces.GamePiece;

import javax.annotation.Resource;
import javax.sound.midi.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static nz.geek.noldus.tetris.GameController.GAMESTATE.*;


public class GameController extends Application implements Runnable, EventHandler<KeyEvent> {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        primaryStage.setTitle("Noldus Tetris");

        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);

        Canvas canvas = new Canvas(300, 600);
        grid.add(canvas, 0,0,1,4);

        levelPane = new LevelPane(this, 300, 150);
        grid.add(levelPane, 1,0);

        linesPane = new LinesPane(this,300, 150);
        grid.add(linesPane, 1,1);

        scorePane = new ScorePane(this,300, 150);
        grid.add(scorePane, 1,2);

        nextPieceCanvas = new Canvas(300, 150);
        grid.add(nextPieceCanvas, 1,3);

        Scene scene = new Scene(grid, 600, 600);

        gc = new GameCanvas(canvas);
        gc.reset();

        scene.setOnKeyPressed(this);

        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(this).start();
        draw();
    }

    @Override
    public void stop() throws Exception {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
        System.exit(0);
    }

    public int getLevel() {
        return level;
    }

    public long getScore() {
        return score;
    }

    public int getLines() {
        return completedLines;
    }

    enum GAMESTATE { WAITING, INPLAY, PAUSED, TERMINATE }


     static final Map<Integer,Integer> levelSpeeds = new HashMap();
     static {
        levelSpeeds.put(1,500);
        levelSpeeds.put(2,450);
        levelSpeeds.put(3,400);
        levelSpeeds.put(4,350);
        levelSpeeds.put(5,300);
        levelSpeeds.put(6,250);
        levelSpeeds.put(7,200);
        levelSpeeds.put(8,150);
        levelSpeeds.put(9,100);
        levelSpeeds.put(10,50);
     }




    private int completedLines = 0;
    private int level = 1;
    private int initialLevel = 1;
    private GameCanvas gc;
    private GamePiece currentPiece;
    private GamePiece nextPiece;
    private long score = 0;
    private Sequence sequence;
    private Sequencer sequencer;
    private Canvas nextPieceCanvas;
    private GAMESTATE state = WAITING;
    private LevelPane levelPane;
    private ScorePane scorePane;
    private LinesPane linesPane;




    public void handle(final KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.UP) {
            if (currentPiece != null) {
                currentPiece.rotate(gc);
                draw();
            }
        } else if (keyEvent.getCode() == KeyCode.LEFT) {
            if (currentPiece != null) {
                currentPiece.moveLeft(gc);
                draw();
            }
        } else if (keyEvent.getCode() == KeyCode.RIGHT) {
            if (currentPiece != null) {
                currentPiece.moveRight(gc);
                draw();
            }
        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            if (currentPiece != null) {
                instantDrop();
                draw();
            }
        } else if (keyEvent.getCode() == KeyCode.P) {
            pause();
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            startGame();
        }
    }


    public synchronized void reset() {
        gc.reset();
        currentPiece = null;
        nextPiece = GamePiece.getRandomPiece();
        level = initialLevel;
        completedLines = 0;
        score = 0;
        resetMusic();
        draw();
    }

    private synchronized void instantDrop() {
        if (currentPiece != null) {
            int dist = gc.instantDrop(currentPiece);
            score += level*3*dist;
            currentPiece = null;
            checkLines();
            draw();
        }
    }

    private synchronized void draw() {

        levelPane.draw();
        if (nextPiece != null)
            drawNextPiece();
        scorePane.draw();
        linesPane.draw();
        if (state != PAUSED) {
            gc.drawState(state);
            if (currentPiece!=null)
                gc.drawGamePiece(currentPiece);
        } else {
            gc.blank();

        }
    }

    private synchronized void step() {
        if (currentPiece == null) {
            currentPiece = nextPiece;
            nextPiece = GamePiece.getRandomPiece();
        }
        draw();
        if (gc.drawGamePiece(currentPiece)) {
            if (gc.pieceLanded(currentPiece)) {
                currentPiece = null;
                checkLines();
            }
            else {
                //freefall drop, increment score
                currentPiece.drop(gc);
                score += level;
            }
        } else {
            //Can not draw current piece - should be game over
            state = WAITING;
            sequencer.stop();
        }

    }

    private void checkLines() {
        while (gc.clearLine()) {
            completedLines++;
            draw();

            //Check for level change
            int earnedLevel = 1;

            if (completedLines<= 0) {
                earnedLevel = 1;
            } else if ((completedLines >= 1) && (completedLines <= 90)) {
                earnedLevel = 1 + ((completedLines - 1) / 10);
            } else if (completedLines >= 91) {
                earnedLevel = 10;
            }
            int expectedLevel = Math.max(initialLevel, earnedLevel);
            if (expectedLevel > level) {
                levelChange(expectedLevel);
            }
        }
    }

    private void levelChange(int newLevel) {
        this.level = newLevel;
        updateMusic();
    }


    public void run() {
        while (state != TERMINATE) {
            switch (state) {
                case INPLAY: {
                    step();
                    try {
                        Thread.sleep((levelSpeeds.get(level)));
                    } catch (InterruptedException e) {
                    }
                    break;
                }
                default:  try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

        }
    }

    private void drawNextPiece() {
        Point[] pieceCoords = nextPiece.tileCenterPositions();
        GraphicsContext gx = nextPieceCanvas.getGraphicsContext2D();
        gx.setStroke(Color.WHITE);
        gx.setFill(Color.WHITE);
        gx.fillRect(0,0,300,300);
        //step 2: draw the pieces
        for (Point p : pieceCoords) {
            nextPiece.getRepTile().draw(gx,(p.x+3)*30, (p.y+2)*30, 30, 30);
        }
    }

    private void resetMusic() {
        if (sequencer != null) {
            sequencer.stop();
        }
        try {



            InputStream in = getClass().getResourceAsStream("tetris.midi");
            //BufferedReader reader = new BufferedReader(new InputStreamReader(in));



            // From file
            //sequence = MidiSystem.getSequence(new File("tetris.midi"));

            sequence = MidiSystem.getSequence(in);

            // Create a sequencer for the sequence
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Integer.MAX_VALUE);

            float tf = 1f+(((float)(level-1))*0.1f);
            sequencer.setTempoFactor(tf);

            // Start playing
            sequencer.start();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (MidiUnavailableException e) {
        } catch (InvalidMidiDataException e) {
        }
    }

    private void updateMusic() {
        float tf = 1f+(((float)(level-1))*0.1f);
        sequencer.setTempoFactor(tf);
    }

    private void pause() {
        if (state == INPLAY) {
            state = PAUSED;
            sequencer.stop();
            draw();
        } else if (state == PAUSED) {
            state = INPLAY;
            sequencer.start();
            draw();
        }
    }

    private synchronized void startGame() {
        if (state == WAITING) {
            reset();
            state = INPLAY;
        }
    }
}
