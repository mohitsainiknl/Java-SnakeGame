/*
 * Copyright (c) 2021 Mohit Saini, Under MIT License. Use is subject to license terms.
 * 
 */

package javasnakegame.handlers;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class GUIHandler {
    public final Font font = new Font("", Font.PLAIN, 18);
    public final Color backColor = Color.WHITE;
    JFrame frame;
    Container contentPane;
    JPanel groundPanel;
    GridBagPanel boardPanel;
    static JLabel scoreLabel;
    MyButton startButton, pauseButton, showCtrlButton;
    MyButton lineButton, speedButton;
    MyButton upButton, leftButton, downButton, rightButton;
    ImageIcon bkImage;
    static GridBagPanel panel;

    boolean isRuning = false;
    boolean isPushed = false;
    boolean isShowingCtrl = false;

    public void handle() {
        prepareGUI();
        addListeners();
        
        contentPane.setFocusable(true);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void prepareGUI() {
        frame = new JFrame("Java-SnakeGame");

        groundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardPanel = new GridBagPanel(5, 5, 5, 5, new Color(0, 0, 0, 0));
        boardPanel.setOpaque(false);
        groundPanel.setOpaque(false);
        new Controler(groundPanel);

        startButton = new MyButton(" Start ", 150, 40, font);
        pauseButton = new MyButton(" Pause ", 134, 40, font);
        showCtrlButton = new MyButton(" ", 134, 40, font);
        lineButton = new MyButton(" ", 134, 40, font);
        speedButton = new MyButton(" ", 134, 40, font);
        {
            Icon rightArrow = new ImageIcon("res//Right-Arrow.png");
            Icon leftArrow = new ImageIcon("res//Left-Arrow.png");
            Icon upArrow = new ImageIcon("res//Up-Arrow.png");
            Icon downArrow = new ImageIcon("res//Down-Arrow.png");
            int i1 = upArrow.getIconWidth();
            int j1 = upArrow.getIconHeight();
            int i2 = downArrow.getIconWidth();
            int j2 = downArrow.getIconHeight();
            int i3 = leftArrow.getIconWidth();
            int j3 = leftArrow.getIconHeight();
            int i4 = rightArrow.getIconWidth();
            int j4 = rightArrow.getIconHeight();
            if(
                (i1 > 0 && j1 > 0) &&
                (i2 > 0 && j2 > 0) &&
                (i3 > 0 && j3 > 0) &&
                (i4 > 0 && j4 > 0))
                {
                upButton = new MyButton(upArrow, 39, 39, font);
                downButton = new MyButton(downArrow, 39, 39, font);
                leftButton = new MyButton(leftArrow, 39, 39, font);
                rightButton = new MyButton(rightArrow, 39, 39, font);
            }
            else {
                upButton = new MyButton("^", 39, 39, font);
                downButton = new MyButton("_", 39, 39, font);
                leftButton = new MyButton("<", 39, 39, font);
                rightButton = new MyButton(">", 39, 39, font);
            }
        }
        pauseButton.setEnabled(false);
        setArrowEnabled(false);

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()+2));

        isShowingCtrl = Snake.getShowControls();
        bkImage = new ImageIcon("res//Wood-Texture.jpg");
        {
            String text;
            if(isShowingCtrl) {
                text = "Hide Controls";
            }
            else {
                text = "Show Controls";
                lineButton.setVisible(false);
                speedButton.setVisible(false);
            }
            showCtrlButton.setText(text);
        }
        {
            String text = (Snake.getIsLine())? "As Snake" : "As Line";
            lineButton.setText(text);
        }
        {
            int speed = Controler.getSpeed();
            String text = (speed == Controler.LOW)? "Low Speed" : ((speed == Controler.HIGH)? "High Speed" : "Med. Speed");
            speedButton.setText(text);
        }
        panel = new GridBagPanel(7, 7, 7, 0, null) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                if(isShowingCtrl == false) {
                    setScoreColor(Color.WHITE);
                    g2d.setPaint(new Color(132, 106, 93));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.drawImage(bkImage.getImage(), 0, 0, getWidth(), getHeight(), null);
                    g2d.setPaint(new Color(70, 47, 33));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(0, 0, getWidth(), getHeight());
                }
                else {
                    setScoreColor(Color.BLACK);
                    g2d.setPaint(Color.WHITE);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        contentPane = frame.getContentPane();
        contentPane.add(panel, BorderLayout.CENTER);
        panel.add(groundPanel, 0, 0, 1, 1, 0, 0.1);
        panel.add(boardPanel, 1, 0, 1, 1, 0.1, 0.1);

        boardPanel.setInsets(
            5, 5, 5, 5
        );
        boardPanel.add(scoreLabel, 0, 1, 3, 1, 0.1, 0.1);
        boardPanel.add(lineButton, 0, 2, 3, 1, 0.1, 0.0);
        boardPanel.add(startButton, 0, 3, 3, 1, 0.1, 0.0);

        boardPanel.add(pauseButton, 0, 4, 3, 1, 0.1, 0.0);
        boardPanel.add(showCtrlButton, 0, 5, 3, 1, 0.1, 0.0);
        boardPanel.add(speedButton, 0, 6, 3, 1, 0.1, 0.0);

        boardPanel.add(new JLabel(), 0, 7, 3, 1, 0.1, 0.1);
        boardPanel.setInsets(
            5, 2, 2, 2
        );
        boardPanel.add(upButton, 1, 8, 1, 1, 0.1, 0.0);
        boardPanel.setInsets(
            2, 5, 10, 2
        );
        boardPanel.add(leftButton, 0, 9, 1, 1, 0.1, 0.0);
        boardPanel.setInsets(
            2, 2, 10, 2
        );
        boardPanel.add(downButton, 1, 9, 1, 1, 0.1, 0.0);
        boardPanel.setInsets(
            2, 2, 10, 5
        );
        boardPanel.add(rightButton, 2, 9, 1, 1, 0.095, 0.0);
    }

    public static void repaintPanel() {
        panel.repaint();
    }
    public static void setScore(int score) {
        scoreLabel.setText("Score : " + score);
    }
    public static void setScoreColor(Color color) {
        scoreLabel.setForeground(color);
        scoreLabel.repaint();
    }
    private void setArrowEnabled(boolean value) {
        upButton.setEnabled(value);
        downButton.setEnabled(value);
        leftButton.setEnabled(value);
        rightButton.setEnabled(value);
    }

    private class MyButton extends JButton {
        private MyButton(String text, int width, int height, Font font) {
            super(text);
            setValues(width, height, font);
        }
        private MyButton(Icon icon, int width, int height, Font font) {
            super(icon);
            setValues(width, height, font);
        }
        private void setValues(int width, int height, Font font) {
            setPreferredSize(new Dimension(width, height));
            setFont(font);
            setFocusable(false);
        }
    }


    private void addListeners() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isRuning) {
                    Controler.runAgain(groundPanel, isShowingCtrl);
                    startButton.setText("Start");
                    pauseButton.setText("Pause");
                    pauseButton.setEnabled(false);
                    setArrowEnabled(false);
                    isRuning = false;
                    isPushed = false;
                }
                else {
                    Controler.runSnaku();
                    startButton.setText("ReStart");
                    pauseButton.setEnabled(true);
                    setArrowEnabled(true);
                    isRuning = true;
                }
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPushed) {
                    Controler.resume();
                    pauseButton.setText("Pause ");
                    setArrowEnabled(true);
                    isPushed = false;
                } else {
                    Controler.pause();
                    pauseButton.setText("Resume");
                    setArrowEnabled(false);
                    isPushed = true;
                }
            }
        });
        showCtrlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isShowingCtrl) {
                    Controler.hideCtrl();
                    showCtrlButton.setText("Show Controls");
                    isShowingCtrl = false;
                    lineButton.setVisible(false);
                    speedButton.setVisible(false);
                } else {
                    Controler.showCtrl();
                    showCtrlButton.setText("Hide Controls");
                    isShowingCtrl = true;
                    lineButton.setVisible(true);
                    speedButton.setVisible(true);
                }
            }
        });
        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Snake.getIsLine()) {
                    Controler.hideLine();
                    lineButton.setText("As Line");
                    Snake.setIsLine(false);
                } else {
                    Controler.showLine();
                    lineButton.setText("As Snake");
                    Snake.setIsLine(true);
                }
            }
        });
        speedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed = Controler.getSpeed();
                // System.out.println("Speed " + speed);
                if(speed == Controler.LOW) {
                    Controler.setSpeed(Controler.MEDIUM);
                    speedButton.setText("Med. Speed");
                }
                else if(speed == Controler.MEDIUM) {
                    Controler.setSpeed(Controler.HIGH);
                    speedButton.setText("High Speed");
                }
                else if(speed == Controler.HIGH) {
                    Controler.setSpeed(Controler.LOW);
                    speedButton.setText("Low Speed");
                }
            }
        });
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controler.turnUp();
            }
        });
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controler.turnDown();
            }
        });
        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controler.turnLeft();
            }
        });
        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Controler.turnRight();
            }
        });
        contentPane.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                String key = KeyEvent.getKeyText(e.getKeyCode());
                if(key.equals("Space")) {
                    pauseButton.doClick();
                }
                else if(key.equals("Enter")) {
                    startButton.doClick();
                }
                else if(key.equals("Up")) {
                    upButton.doClick();
                }
                else if(key.equals("Down")) {
                    downButton.doClick();
                }
                else if(key.equals("Left")) {
                    leftButton.doClick();
                }
                else if(key.equals("Right")) {
                    rightButton.doClick();
                }
            }
        });
        groundPanel.addMouseListener(new MouseAdapter() { // We can also use Mouse Click to move Snake...
            @Override
            public void mousePressed(MouseEvent e) {
                if(!isPushed && isRuning) {
                    int x = e.getX();
                    int y = e.getY();
                    // System.out.println("Click " + x + " " + y);
                    int turn = Snake.getTurnSnake();
                    int x1, y1;
                    x1 = Snake.getLastPointX();
                    y1 = Snake.getLastPointY();
                    if(turn == Snake.LEFT || turn == Snake.RIGHT) {
                        if(y1 > y) {
                            if(turn == Snake.RIGHT) upButton.doClick();
                            else  upButton.doClick();
                        }
                        else if(y1 < y) {
                            if(turn == Snake.RIGHT) downButton.doClick();
                            else  downButton.doClick();
                        }
                    }
                    else if(turn == Snake.DOWN || turn == Snake.UP) {
                        if(x1 > x) {
                            if(turn == Snake.DOWN) leftButton.doClick();
                            else  leftButton.doClick();
                        }
                        else if(x1 < x) {
                            if(turn == Snake.DOWN) rightButton.doClick();
                            else  rightButton.doClick();
                        }
                    }
                }
                else if(isPushed) {
                    pauseButton.doClick();
                }
                else {
                    startButton.doClick();
                }
            }
        });
    }
}
