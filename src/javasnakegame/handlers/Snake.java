/*
 * Copyright (c) 2021 Mohit Saini, Under MIT License. Use is subject to license terms.
 * 
 */

package javasnakegame.handlers;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Color;
import java.awt.FontMetrics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Snake extends JPanel{
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    private static final byte initialPoints = 5;
    private static final Color snakeBlacked = Color.GRAY;
    private static final Color snakeBlackedLine = new Color(200, 200, 200);
    private static final Color snakeColored = new Color(150, 0, 0);
    private static final Color snakeColoredLine = new Color(225, 240, 240);
    private static AtomicBoolean showContols;
    private static AtomicBoolean isLine;
    private static AtomicInteger turnSnake;
    private static ImageIcon grassImage;
    private static ImageIcon appleImage;
    private static int height, width;
    private static int gridsX, gridsY;
    private static int dgree;
    private static int gridLength;
    private static boolean isImageLoaded;
    private static boolean isAppleLoaded;
    private static boolean isLoose;
    private static boolean isFirstChangePath;
    private static ArrayList<SPoint> pArray;
    private static ArrayList<GPoint> pInUse;
    private static GPoint applePoint;
    private static int score;

    public Snake() {
        initializeVaribles();
        grassImage = new ImageIcon("res//grass.jpg");
        {
            int i = grassImage.getImage().getWidth(null);
            int j = grassImage.getImage().getHeight(null);
            if(i > 0 && j > 0) {
                isImageLoaded = true;
            }
        }
        appleImage = new ImageIcon("res//apple.png");
        {
            int i = appleImage.getImage().getWidth(null);
            int j = appleImage.getImage().getHeight(null);
            if(i > 0 && j > 0) {
                isAppleLoaded = true;
            }
        }
        gridsX = 15;    //<--- no. of grids in X axis of gound
        gridsY = 12;    //<--- no. of grids in Y axis of gound

        width = 600;
        height = 480;   //<--- same ratio with (gridX : gridY)

        gridLength = width / gridsX;
        gridLength = height / gridsY;
        addInitialPoints();
        setRandomApplePoint();
        setOpaque(false);
        setPreferredSize(new Dimension(width, height));
    }
    
    private void initializeVaribles() {
        showContols = new AtomicBoolean(false);
        isLine = new AtomicBoolean(false);
        turnSnake = new AtomicInteger(DOWN);
        dgree = 0;
        isLoose = false;
        score = 0;
    }

    private void addInitialPoints() {
        int x = gridLength * (gridsX - 3) + gridLength/2;
        int y = gridLength;
        int sDgree = getStartingDegreeOf(turnSnake.get());
        boolean side = true;
        
        pArray = new ArrayList<SPoint>();
        pInUse = new ArrayList<GPoint>();

        int j = 1;
        for (int i = 0; i < initialPoints; ++i, ++j) {
            pArray.add(new SPoint(x, y, sDgree, side));
            setPointInUse(pArray.get(i), i, true);
            // System.out.println("(" + x + ", " + y + ")" + " " + sDgree + " " + side);

            if(j % 2 == 0) {
                if(turnSnake.get() == UP) {
                    y = y - gridLength;
                }
                else if(turnSnake.get() == DOWN) {
                    y = y + gridLength;
                }
                else if(turnSnake.get() == LEFT) {
                    x = x - gridLength;
                }
                else if(turnSnake.get() == RIGHT) {
                    x = x + gridLength;
                }
                side = (side == true)? false : true;
            }
            if(sDgree == 0 && side == false) {
                sDgree = 360;
            }
            else if(sDgree == 360 && side == true) {
                sDgree = 0;
            }
            sDgree = (side == true)? sDgree + 90 : sDgree - 90;
            if(sDgree == 0 && side == false) {
                sDgree = 360;
            }
            else if(sDgree == 360 && side == true) {
                sDgree = 0;
            }
        }
    }

    private void setRandomApplePoint() {
        int xMax = (gridsX - 3) * 2;
        int yMax = (gridsY - 3) * 2;
        int x = (int) ((   Math.random()/Math.nextDown(1.0)   ) * xMax);
        int y = (int) ((   Math.random()/Math.nextDown(1.0)   ) * yMax);
        x = x*gridLength/2 + gridLength;
        y = y*gridLength/2 + gridLength;
        if(isThisPointInUse(x, y)) {
            setRandomApplePoint();
        }
        // System.out.println("(" + x + ", " + y + ")");
        applePoint = new GPoint(x, y);
    }
    private boolean isThisPointInUse(int x, int y) {
        final int len = gridLength/2;
        ArrayList<GPoint> apPoints = new ArrayList<GPoint>();
        apPoints.add(new GPoint(x + len  , y        ));
        apPoints.add(new GPoint(x        , y + len  ));
        apPoints.add(new GPoint(x + len  , y + len  ));
        apPoints.add(new GPoint(x + len*2, y + len  ));
        apPoints.add(new GPoint(x + len  , y + len*2));
        // for (int i = 0; i < apPoints.size(); i++) {
        //     System.out.println("(" + apPoints.get(i).getX() + ", " + apPoints.get(i).getY() + ")");
        // }

        for (int i = 0; i < apPoints.size(); i++) {
            for (int j = 0; j < pInUse.size(); j++) {
                if((apPoints.get(i).getX() == pInUse.get(j).getX()) && (apPoints.get(i).getY() == pInUse.get(j).getY())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setPointInUse(SPoint spoint, int index, boolean isAdd) {
        int x = spoint.getX();
        int y = spoint.getY();
        final int sDgree = spoint.getSDgree();
        final boolean side = spoint.getSide();
        final int len = gridLength/2;

        int eDgree;
        {
            eDgree = (side == true)? sDgree + 90 : sDgree - 90;
            if(eDgree == 0 || eDgree == 360) {
                x = x + len;
            } 
            else if(eDgree == 180) {
                x = x - len;
            }
            else if(eDgree == 90) {
                y = y - len;
            }
            else if(eDgree == 270) {
                y = y + len;
            }
        }
        x += len;
        y += len;

        if(isAdd) {
            pInUse.add(index, new GPoint(x, y));
        }
        else {
            pInUse.set(index, new GPoint(x, y));
        }
    }

    public void updateSnake() {
        dgree += 10;

        if(dgree == 90) {
            {
                int x = pInUse.get(pInUse.size()-1).getX();
                int y = pInUse.get(pInUse.size()-1).getY();
                if(x == 0 || x == width || y == 0 || y == height) {
                    isLoose = true;
                }
                else {
                    for (int i = 0; i < pInUse.size()-1; i++) {
                        if(x == pInUse.get(i).getX() && y == pInUse.get(i).getY()) {
                            isLoose = true;
                        }
                    }
                }
            }
            if(isThisPointInUse(applePoint.getX(), applePoint.getY())) {
                setRandomApplePoint();
                pArray.add(0, new SPoint(0, 0, 0, false));
                pInUse.add(0, new GPoint(0, 0));
                score += 20;
                GUIHandler.setScore(score);
            }

            int i;
            for (i = 0; i < pArray.size()-1; i++) {
                pArray.set(i, pArray.get(i + 1));
            }
            updatePath(i);

            int j;
            for (j = 0; j < pInUse.size()-1; j++) {
                pInUse.set(j, pInUse.get(j + 1));
            }
            setPointInUse(pArray.get(i), j, false);
            dgree = 0;
            isFirstChangePath = true;
        }
        repaint();
    }

    private void updatePath(int pointNo) {
        int x = pArray.get(pointNo).getX();
        int y = pArray.get(pointNo).getY();
        boolean side = pArray.get(pointNo).getSide();
        final int turn = turnSnake.get();

        int sDgree = pArray.get(pointNo).getSDgree();
        int eDgree = (side == true)? sDgree + 90 : sDgree - 90;

        // System.out.println("(" + x + ", " + y + ")" + " " + sDgree + " " + eDgree + " " + side);

        if((sDgree == 90 && eDgree == 0) || (sDgree == 270 && eDgree == 360)) {
            if(turn == RIGHT) {
                x = x + gridLength;
                side = (side == true)? false : true;
                eDgree = flipAngle(eDgree);
            }
        }
        else if((sDgree == 360 && eDgree == 270) || (sDgree == 180 && eDgree == 270)) {
            if(turn == DOWN) {
                y = y + gridLength;
                side = (side == true)? false : true;
                eDgree = flipAngle(eDgree);
            }
        }
        else if((sDgree == 90 && eDgree == 180) || (sDgree == 270 && eDgree == 180)) {
            if(turn == LEFT) {
                x = x - gridLength;
                side = (side == true)? false : true;
                eDgree = flipAngle(eDgree);
            }
        }
        else if((sDgree == 180 && eDgree == 90) || (sDgree == 0 && eDgree == 90)) {
            if(turn == UP) {
                y = y - gridLength;
                side = (side == true)? false : true;
                eDgree = flipAngle(eDgree);
            }
        }
        if(eDgree == 0 && side == false) {
            eDgree = 360;
        }
        else if(eDgree == 360 && side == true) {
            eDgree = 0;
        }
        // System.out.println("(" + x + ", " + y + ")" + " " + sDgree + " " + eDgree + " " + side);
        // System.out.println("");
        pArray.set(pointNo, new SPoint(x, y, eDgree, side));
    }

    private int flipAngle(int angle) {
        if(angle == 0 || angle == 90) {
            angle += 180;
        }
        else if (angle == 180 || angle == 270 || angle == 360) {
            angle -= 180;
        }
        return angle;
    }

    public static void setShowControls(boolean value) {
        showContols.set(value);
    }
    public static boolean getShowControls() {
        return showContols.get();
    }

    public static void setIsLine(boolean value) {
        isLine.set(value);
    }
    public static boolean getIsLine() {
        return isLine.get();
    }
    public static int getLastPointX() {
        int x = pArray.get(pArray.size()-1).getX();
        return x;
    }
    public static int getLastPointY() {
        int y = pArray.get(pArray.size()-1).getY();
        return y;
    }

    public void setTurnSnake(int turn) {
        if(isFirstChangePath && turnSnake.get() != turn) {
            int last = pArray.size() - 1;
            changePath(last, turn);
            turnSnake.set(turn);
        }
        else {
            turnSnake.set(turn);
        }
    }
    public static int getTurnSnake() { 
        return turnSnake.get();
    }

    public void changePath(int pointNo, int turn) {
        SPoint point = pArray.get(pointNo);
        int x = point.getX();
        int y = point.getY();
        boolean side = point.getSide();

        int sDgree = point.getSDgree();
        int eDgree = (side == true)? sDgree + 90 : sDgree - 90;

        if((sDgree == 90 && eDgree == 0 && (turn == UP))  || (sDgree == 90 && eDgree == 180 && (turn == UP))) {
            y = y - gridLength;
            sDgree = 270;
            side = (side == true)? false : true;
        }
        else if((sDgree == 0 && eDgree == 90 && (turn == RIGHT)) || (sDgree == 360 && eDgree == 270 && (turn == RIGHT))) {
            x = x + gridLength;
            sDgree = 180;
            side = (side == true)? false : true;
        }
        else if((sDgree == 270 && eDgree == 180 && (turn == DOWN)) || (sDgree == 270 && eDgree == 360 && (turn == DOWN))) {
            y = y + gridLength;
            sDgree = 90;
            side = (side == true)? false : true;
        }
        else if((sDgree == 180 && eDgree == 90 && (turn == LEFT)) || (sDgree == 180 && eDgree == 270 && (turn == LEFT))) {
            x = x - gridLength;
            sDgree = (side == true)? 360 : 0;
            side = (side == true)? false : true;
        }

        point.setX(x);
        point.setY(y);
        point.setSDgree(sDgree);
        point.setSide(side);
        pArray.set(pointNo, point);
        setPointInUse(pArray.get(pointNo), pointNo, false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if(showContols.get()) {
            g2d.setPaint(new Color(240, 240, 240));
            g2d.fillRect(0, 0, width, height);

            g2d.setPaint(new Color(0, 0, 0, 30));
            for(int i = gridLength; i < width; i += gridLength) {
                g2d.drawLine(i, 0, i, height);
            }
            for(int i = gridLength; i < height; i += gridLength) {
                g2d.drawLine(0, i, width, i);
            }
    
            g2d.setPaint(new Color(0, 0, 0, 10));
            for(int i = gridLength/2; i < width; i += gridLength) {
                g2d.drawLine(i, 0, i, height);
            }
            for(int i = gridLength/2; i < height; i += gridLength) {
                g2d.drawLine(0, i, width, i);
            }
        }
        else {
            if(isImageLoaded) {
                int x = 0;
                int y = 0;
                for(int h = 3*gridLength; h <= height; h += 3*gridLength) {
                    y = h - 3*gridLength;
                    for (int w = 3*gridLength; w <= width; w += 3*gridLength) {
                        x = w - 3*gridLength;
                        g2d.drawImage(grassImage.getImage(), x, y, 3*gridLength, 3*gridLength, null);
                    }
                }
            }
            else {
                g2d.setPaint(new Color(159, 194, 16));
                g2d.fillRect(0, 0, width, height);
            }
            g2d.setPaint(new Color(133, 160, 6));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(0, 0, width, height);
        }
        if(isAppleLoaded) {
            final int x = applePoint.getX();
            final int y = applePoint.getY();
            final int extra = 10;
            g2d.drawImage(appleImage.getImage(), x - extra, y - extra, gridLength + extra*2, gridLength + extra*2, null);
        }
        else {
            final int x = applePoint.getX();
            final int y = applePoint.getY();
            g2d.setPaint(Color.RED);
            g2d.fillOval(x, y, gridLength, gridLength);
        }


        if(isLine.get() == true) {
            g2d.setPaint(new Color(0, 0, 0, 100));
            g2d.setStroke(new BasicStroke(3));
    
            int x, y;
            int len = gridLength;
            int sDgree;
            boolean side;
           for (int i = 0; i < pArray.size(); i++)
            {
                x = pArray.get(i).getX();
                y = pArray.get(i).getY();
                sDgree = pArray.get(i).getSDgree();
                side = pArray.get(i).getSide();
    
                if(i == 0) {
                    int start = (side == true)? sDgree + dgree : sDgree - dgree;
    
                    int dgreeLen = (side == true)? (90 - dgree) : -(90 - dgree);
    
                    g2d.drawArc(x, y, len, len, start, dgreeLen);
                }
                else if(i == pArray.size() - 1) {
                    g2d.drawArc(x, y, len, len, sDgree, (side == true)? dgree : -dgree);
                }
                else {
                    g2d.drawArc(x, y, len, len, sDgree, (side == true)? 90 : -90);
                }
            }
        }
        else {

            int x, y;
            int sDgree;
            boolean side;
            int len;
            
            len = gridLength;

            final Color snakeSkinColor = (showContols.get())? snakeBlacked : snakeColored;
            final Color snakeLineColor = (showContols.get())? snakeBlackedLine : snakeColoredLine;
            g2d.setPaint(snakeSkinColor);
            g2d.setStroke(new BasicStroke(13));

            for (int i = 0; i < pArray.size(); i++)
            {
                x = pArray.get(i).getX();
                y = pArray.get(i).getY();
                sDgree = pArray.get(i).getSDgree();
                side = pArray.get(i).getSide();
    
                if(i == 0 || i == 1 || i == 2) {
                    printTail(g2d, i);
                }
                else if(i == pArray.size() - 1) {
                    g2d.setStroke(new BasicStroke(13));
                    g2d.drawArc(x, y, len, len, sDgree, (side == true)? dgree : -dgree);
                }
                else {
                    g2d.setStroke(new BasicStroke(13));
                    g2d.drawArc(x, y, len, len, sDgree, (side == true)? 90 : -90);
                }
            }

            //<---- Center Line on Snake

            g2d.setPaint(snakeLineColor);
            g2d.setStroke(new BasicStroke(1));

            for (int i = 0; i < pArray.size(); i++)
            {
                x = pArray.get(i).getX();
                y = pArray.get(i).getY();
                sDgree = pArray.get(i).getSDgree();
                side = pArray.get(i).getSide();
    
                if(i == 0) {
                    // int start = (side == true)? sDgree + dgree : sDgree - dgree;
                    // int dgreeLen = (side == true)? (90 - dgree) : -(90 - dgree);
                    // g2d.drawArc(x, y, len, len, start, dgreeLen);
                }
                else if(i == pArray.size() - 1) {
                    g2d.drawArc(x, y, len, len, sDgree, (side == true)? dgree : -dgree);
                }
                else {
                    g2d.drawArc(x, y, len, len, sDgree, (side == true)? 90 : -90);
                }
            }

            //  <--- Printing Head of Snake

            g2d.setPaint(snakeSkinColor);
            final int lPoint = pArray.size()-1;
            x = pArray.get(lPoint).getX() + gridLength/2;
            y = pArray.get(lPoint).getY() + gridLength/2;
            sDgree = pArray.get(lPoint).getSDgree();
            side = pArray.get(lPoint).getSide();

            int eDgree = (side == true)? (dgree) : -(dgree);
            int pointX = x + (int) ((gridLength/2 + 1) * Math.cos(Math.toRadians(sDgree + eDgree)));
            int pointY = y - (int) ((gridLength/2 + 1) * Math.sin(Math.toRadians(sDgree + eDgree)));
            g2d.fillOval(pointX - 9, pointY - 9, 18, 18);

            final int angleGap = 30;
            if(dgree >= angleGap) {
                eDgree = (side == true)? (dgree - angleGap) : -(dgree - angleGap);
                pointX = x + (int) ((gridLength/2 + 1) * Math.cos(Math.toRadians(sDgree + eDgree)));
                pointY = y - (int) ((gridLength/2 + 1) * Math.sin(Math.toRadians(sDgree + eDgree)));
                g2d.fillOval(pointX - 12, pointY - 12, 24, 24);
            }
            else {
                final int l2Point = pArray.size()-2;
                x = pArray.get(l2Point).getX() + gridLength/2;
                y = pArray.get(l2Point).getY() + gridLength/2;
                sDgree = pArray.get(l2Point).getSDgree();
                side = pArray.get(l2Point).getSide();
    
                eDgree = (side == true)? (dgree + (90-angleGap)) : -(dgree + (90-angleGap));
                pointX = x + (int) ((gridLength/2 + 1) * Math.cos(Math.toRadians(sDgree + eDgree)));
                pointY = y - (int) ((gridLength/2 + 1) * Math.sin(Math.toRadians(sDgree + eDgree)));
                g2d.fillOval(pointX - 12, pointY - 12, 24, 24);
            }

            g2d.setPaint(snakeLineColor);
            final int eyeAngle = 15;
            final int eyePixelGap = 8;
            if(dgree >= eyeAngle) {
                final int point = pArray.size()-1;
                x = pArray.get(point).getX() + gridLength/2;
                y = pArray.get(point).getY() + gridLength/2;
                sDgree = pArray.get(point).getSDgree();
                side = pArray.get(point).getSide();

                int length = gridLength/2 + 1 + eyePixelGap/2;
                eDgree = (side == true)? (dgree - eyeAngle) : -(dgree - eyeAngle);
                pointX = x + (int) (length * Math.cos(Math.toRadians(sDgree + eDgree)));
                pointY = y - (int) (length * Math.sin(Math.toRadians(sDgree + eDgree)));
                g2d.fillOval(pointX - 2, pointY - 2, 4, 4);

                length = gridLength/2 + 1 - eyePixelGap/2;
                eDgree = (side == true)? (dgree - eyeAngle) : -(dgree - eyeAngle);
                pointX = x + (int) (length * Math.cos(Math.toRadians(sDgree + eDgree)));
                pointY = y - (int) (length * Math.sin(Math.toRadians(sDgree + eDgree)));
                g2d.fillOval(pointX - 2, pointY - 2, 4, 4);
            }
            else {
                final int l2Point = pArray.size()-2;
                x = pArray.get(l2Point).getX() + gridLength/2;
                y = pArray.get(l2Point).getY() + gridLength/2;
                sDgree = pArray.get(l2Point).getSDgree();
                side = pArray.get(l2Point).getSide();

                int length = gridLength/2 + 1 + eyePixelGap/2;
                eDgree = (side == true)? (dgree + (90-eyeAngle)) : -(dgree + (90-eyeAngle));
                pointX = x + (int) (length * Math.cos(Math.toRadians(sDgree + eDgree)));
                pointY = y - (int) (length * Math.sin(Math.toRadians(sDgree + eDgree)));
                g2d.fillOval(pointX - 2, pointY - 2, 4, 4);

                length = gridLength/2 + 1 - eyePixelGap/2;
                eDgree = (side == true)? (dgree + (90-eyeAngle)) : -(dgree + (90-eyeAngle));
                pointX = x + (int) (length * Math.cos(Math.toRadians(sDgree + eDgree)));
                pointY = y - (int) (length * Math.sin(Math.toRadians(sDgree + eDgree)));
                g2d.fillOval(pointX - 2, pointY - 2, 4, 4);
            }
        }

        if(isLine.get()) {
            int px;
            int py;
            g2d.setPaint(Color.BLACK);
            for (int i = 0; i < pInUse.size(); i++) {
                px = pInUse.get(i).getX();
                py = pInUse.get(i).getY();
                g2d.fillOval(px - 3, py - 3, 6, 6);
                // System.out.print("(" + px + ", " + py + "), ");
            }
            // System.out.println("");
        }

        if(isLoose == true) {
            printResult(g2d);
            return;
        }
    }

    private void printTail(Graphics2D g2d, int i) {
        int x, y;
        int sDgree;
        boolean side;
        int len;
        
        len = gridLength;

        x = pArray.get(i).getX();
        y = pArray.get(i).getY();
        sDgree = pArray.get(i).getSDgree();
        side = pArray.get(i).getSide();

        if(i == 0) {
            int d;
            int start = 0;
            int dgreeLen = 0;
            boolean print = true;
            for (int j = 1; j <= 6; j++) {
                print = true;
                if( dgree >= 90-(j-1)*15 ) {
                    print = false;
                }
                else {
                    d = dgree + (j-1)*15;
                    start = (side == true)? sDgree + d : sDgree - d;

                    d = (90 - (j-1) * 15) - dgree;
                    dgreeLen = (side == true)? d : -d;
                }

                if(print) {
                    g2d.setStroke(new BasicStroke(j));
                    g2d.drawArc(x, y, len, len, start, dgreeLen);
                }
            }
        }
        else if(i == 1) {
            int d;
            int start = 0;
            int dgreeLen = 0;
            boolean print = true;

            for (int j = 7; j <= 12; j++) {
                print = true;
                if( dgree >= 90-(j-6)*15 ) {
                    print = false;
                }
                else {
                    d = dgree + (j-6)*15;
                    start = (side == true)? sDgree + d : sDgree - d;

                    d = (90 - (j-6) * 15) - dgree;
                    dgreeLen = (side == true)? d : -d;
                }

                if(print) {
                    g2d.setStroke(new BasicStroke(j));
                    g2d.drawArc(x, y, len, len, start, dgreeLen);
                }
            }
            for (int j = 1; j <= 6; j++) {
                if(dgree > (90 - (j-1)*15) ) {
                    d = dgree - (90 - (j-1)*15);
                    start = (side == true)? sDgree + d : sDgree - d;
                    d = (j-1) * 15;
                    dgreeLen = (side == true)? (90 - d) : -(90 - d);
                }
                else {
                    d = dgree;
                    start = sDgree;
                    dgreeLen = (side == true)? d : -d;
                }

                g2d.setStroke(new BasicStroke(j));
                g2d.drawArc(x, y, len, len, start, dgreeLen);
            }
        }
        else if(i == 2) {
            int start = (side == true)? sDgree + dgree : sDgree - dgree;
            int dgreeLen = (side == true)? (90 - dgree) : -(90 - dgree);
            g2d.setStroke(new BasicStroke(13));
            g2d.drawArc(x, y, len, len, start, dgreeLen);

            int d;
            for (int j = 7; j <= 12; j++) {
                if(dgree > (90 - (j-6)*15) ) {
                    d = dgree - (90 - (j-6)*15);
                    start = (side == true)? sDgree + d : sDgree - d;
                    d = (j-6) * 15;
                    dgreeLen = (side == true)? (90 - d) : -(90 - d);
                }
                else {
                    d = dgree;
                    start = sDgree;
                    dgreeLen = (side == true)? d : -d;
                }

                g2d.setStroke(new BasicStroke(j));
                g2d.drawArc(x, y, len, len, start, dgreeLen);
            }
        }

    }

    public void printResult(Graphics2D g2d) {

        Controler.stopSnaku();

        final int dailogHeight = 180;
        Color color = (showContols.get())? new Color(0, 0, 0, 100) : new Color(255, 255, 255, 150);
        g2d.setPaint(color);
        g2d.fillRect(0, height/2 - dailogHeight/2, width, dailogHeight);
        
        setFont(new Font("", Font.BOLD, 45));
        FontMetrics f_metrics = getFontMetrics(getFont());
        int textHeight = f_metrics.getHeight() - f_metrics.getDescent();
        color = (showContols.get())? new Color(255, 255, 255, 220) : new Color(0, 0, 0, 150);
        g2d.setPaint(color);
        
        int sideGap = width/2 - 120;
        String messageOne = "Game Over";
        g2d.drawString(messageOne, sideGap, height/2 - textHeight/2);

        sideGap = width/2 - 150;
        String messageTwo = "Your Score : " + score;
        g2d.drawString(messageTwo, sideGap, height/2 + textHeight/2 + 10);

    }

    private int getStartingDegreeOf(int turn) {
        if(turn == UP) {
            return -90;
        } 
        else if(turn == DOWN) {
            return 90;
        }
        else if(turn == LEFT) {
            return 0;
        }
        else if(turn == RIGHT) {
            return 180;
        }
        return 0;
    }

    private class SPoint {
        private int x;
        private int y;
        private boolean side;
        private int sDgree;

        private SPoint(int x, int y, int sDgree, boolean side) {
            this.x = x;
            this.y = y;
            this.side = side;
            this.sDgree = sDgree;
        }

        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public int getSDgree() {
            return sDgree;
        }
        public boolean getSide() {
            return side;
        }

        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }
        public void setSDgree(int sDgree) {
            this.sDgree = sDgree;
        }
        public void setSide(boolean side) {
            this.side = side;
        }
    }
    public class GPoint {
        private int x;
        private int y;

        private GPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }
    }
}
