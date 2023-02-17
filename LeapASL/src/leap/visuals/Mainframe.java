package leap.visuals;

import leap.Leap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Mainframe extends JFrame{
    private JPanel MAIN_PANEL;
    private JButton BUTTON_DATA;
    private JTextArea OUTPUT_DATA;
    private JTextField EDIT_GESTURE;
    public Mainframe(){
        InitializeVisuals();
        InitializeEvents();
    }

    public void WarnOutputData(String str){
        OUTPUT_DATA.setText(str + "\n");
    }

    public void SetOutputData(String[] str, boolean clear){
        StringBuilder TEMP = new StringBuilder("");
        for(String s : str)
            TEMP.append(s + '\n');

        if (clear)
            OUTPUT_DATA.setText(TEMP.toString() + "\n");
        else
            OUTPUT_DATA.append(TEMP.toString() + "\n");
    }

    public void SetOutputData(String str, boolean clear){
        if (clear)
            OUTPUT_DATA.setText(str + "\n");
        else
            OUTPUT_DATA.append(str + "\n");
    }


    public void SetOutputData(String[] str){
        SetOutputData(str, true);
    }


    private void InitializeVisuals(){
        MAIN_PANEL = new JPanel();
        BUTTON_DATA = new JButton("CAPTURE");
        OUTPUT_DATA = new JTextArea();
        EDIT_GESTURE = new JTextField();

        BUTTON_DATA.setPreferredSize(new Dimension(720, 40));
        OUTPUT_DATA.setPreferredSize(new Dimension(500, 250));
        OUTPUT_DATA.setEditable(false);
        EDIT_GESTURE.setPreferredSize(new Dimension(500, 50));
        EDIT_GESTURE.setText(Leap.GESTURE);

        MAIN_PANEL.add(BUTTON_DATA);
        MAIN_PANEL.add(OUTPUT_DATA);
        MAIN_PANEL.add(EDIT_GESTURE);

        this.add(MAIN_PANEL, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(720, 500);
    }

    private void InitializeEvents(){
        BUTTON_DATA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Leap.NEXT_TICK_READY = true;
            }
        });

        EDIT_GESTURE.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (EDIT_GESTURE.getText().length() >= 1 )
                    e.consume();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                EDIT_GESTURE.setText(EDIT_GESTURE.getText().toLowerCase());

                if (!EDIT_GESTURE.getText().isEmpty() && !EDIT_GESTURE.getText().isBlank()){
                    Leap.GESTURE = EDIT_GESTURE.getText();
                    WarnOutputData("[SUCCESS] Changed gesture: " + Leap.GESTURE);
                }
                else if(EDIT_GESTURE.getText().isEmpty() || EDIT_GESTURE.getText().isBlank())
                    WarnOutputData("[WARN] Waiting for input data, blank");
                else
                    WarnOutputData("[ERROR] Erroneous character inputted as gesture, current gesture: " + Leap.GESTURE);
            }

        });
    }
}
