/*
 * Copyright (c) 2021 Mohit Saini, Under MIT License. Use is subject to license terms.
 * 
 */

package javasnakegame.handlers;

import java.awt.Container;
import javax.swing.JPanel;

public class Controler implements Runnable {
    public static final int LOW = 100;
    public static final int MEDIUM = 50;
    public static final int HIGH = 20;
    private static Snake snake = new Snake();
    private static Thread runThread;
    private static Controler target;
    private static int sleepTime = HIGH;

    @Override
    public void run() {
        while (true) {
            snake.updateSnake();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void setSpeed(int speed) {
        sleepTime = speed;
    }
    public static int getSpeed() {
        return sleepTime;
    }
    
    public Controler(JPanel gound) {
        gound.add(snake);
        target = this;
        runThread = new Thread(target);
    }
    
    public static void runSnaku() {
        runThread.start();
    }
    
    public static void stopSnaku() {
        runThread.stop();
    }

    public static void runAgain(Container gound, boolean value) {
        runThread.stop();
        snake.setVisible(false);
        snake = new Snake();
        gound.add(snake);
        runThread = new Thread(target);
        snake.repaint();
        Snake.setShowControls(value);
    }

    public static void pause() {
        runThread.suspend();
    }
    public static void resume() {
        runThread.resume();
    }

    public static void showCtrl() {
        Snake.setShowControls(true);
        GUIHandler.repaintPanel();
        snake.repaint();
    }
    public static void hideCtrl() {
        Snake.setShowControls(false);
        Snake.setIsLine(false);
        GUIHandler.repaintPanel();
        snake.repaint();
    }

    public static void showLine() {
        snake.repaint();
    }
    public static void hideLine() {
        snake.repaint();
    }

    public static void turnUp() {
        int turn = Snake.getTurnSnake();
        if(turn == Snake.LEFT || turn == Snake.RIGHT) {
            snake.setTurnSnake(Snake.UP);
        }
    }
    public static void turnDown() {
        int turn = Snake.getTurnSnake();
        if(turn == Snake.LEFT || turn == Snake.RIGHT) {
            snake.setTurnSnake(Snake.DOWN);
        }
    }
    public static void turnLeft() {
        int turn = Snake.getTurnSnake();
        if(turn == Snake.UP || turn == Snake.DOWN) {
            snake.setTurnSnake(Snake.LEFT);
        }
    }
    public static void turnRight() {
        int turn = Snake.getTurnSnake();
        if(turn == Snake.UP || turn == Snake.DOWN) {
            snake.setTurnSnake(Snake.RIGHT);
        }
    }
}
