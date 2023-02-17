package gg.bear.visuals;


import gg.bear.helper.Logger;

import javax.swing.*;
import java.awt.*;

public class DrawablePanel extends JPanel {
    public Point cursor;

    DrawablePanel(Dimension dim){
        setDoubleBuffered(true);
        setPreferredSize(dim);
    }

    @Override
    public void paint(Graphics g){  //TODO: Replace paint with paintComponent for smoother results - fix underlaying crosshair
        super.paint(g);
        if (cursor != null) {
            g.setColor(getForeground());
            g.drawLine(cursor.x, cursor.y, cursor.x + 5, cursor.y + 5);
            g.drawLine(cursor.x + 5, cursor.y, cursor.x, cursor.y + 5);
        }
    }


}