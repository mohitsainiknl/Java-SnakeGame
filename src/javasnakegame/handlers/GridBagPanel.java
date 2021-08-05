/*
 * Copyright (c) 2021 Mohit Saini, Under MIT License. Use is subject to license terms.
 * 
 */

package javasnakegame.handlers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class GridBagPanel extends JPanel {
    public static final int NORTH = GridBagConstraints.NORTH;
    public static final int SOUTH = GridBagConstraints.SOUTH;
    public static final int EAST = GridBagConstraints.EAST;
    public static final int WEST= GridBagConstraints.WEST;
    public static final int CENTER= GridBagConstraints.CENTER;
    GridBagLayout gbl;
    GridBagConstraints gbc;
    Insets insets;
    int anchor = CENTER;
    int ipadx = 0;
    int ipady = 0;
    
    public GridBagPanel(int top, int left, int down, int right, Color color) {
        gbl = new GridBagLayout();
        gbc = new GridBagConstraints();
        insets = new Insets(top, left, down, right);
    
        gbc.fill = GridBagConstraints.BOTH;

        setBackground(color);
        setBorder(new MatteBorder(top, left, down, right, color));
        setLayout(gbl);
    }

    public void setInsets(int top, int left, int down, int right) {
        insets = new Insets(top, left, down, right);
    }

    public void setipad(int x, int y) {
        ipadx = x;
        ipady = y;
    }

    public void setAchor(int value) {
        anchor = value;
    }

    public Component add(Component comp, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
        gbc.insets = insets;
        gbc.anchor = anchor;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        
        add(comp, gbc);

        return comp;
    }
}
