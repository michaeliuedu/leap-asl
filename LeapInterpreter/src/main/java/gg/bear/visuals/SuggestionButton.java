package gg.bear.visuals;

import gg.bear.helper.Logger;
import gg.bear.modules.Leap;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class SuggestionButton extends JButton {
    public String suggestion;
    public Dimension dimension;

    public SuggestionButton(String sug, Dimension dim){
        super(sug);
        suggestion = sug.toUpperCase(Locale.ROOT);
        dimension = dim;
        super.setPreferredSize(dim);
    }

    public boolean IsHovering(){
        Point cursor = Leap.visuals.panel.cursor;
        return (cursor.x >= super.getX() && cursor.x <= super.getX() + dimension.width) && (cursor.y >= super.getY() && cursor.y <= super.getY() + dimension.height);
    }
}
