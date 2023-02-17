package gg.bear.visuals;


import gg.bear.core.DetectionUnit;
import gg.bear.modules.Leap;
import gg.bear.modules.language.LanguageEngine;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class UI extends JFrame {
    public Dimension dimension;
    public DrawablePanel panel;
    private UIState state;

    private JTextArea DEBUG_DATA;
    private JLabel APP_LABEL;
    public JTextField TEXT_OUTPUT;
    public ArrayList<SuggestionButton> AUTOCORRECT_QUERIES;

    public JButton RESTART_BUTTON;

    public UI(Dimension dim) {
        dimension = dim == null ? new Dimension(600, 420) : dim;
        panel = panel == null ? new DrawablePanel(dim) : panel;
        AUTOCORRECT_QUERIES = new ArrayList<>();
        InitializeVisuals();
    }


    public void AppendSuggestion(String sug){
        SuggestionButton append = new SuggestionButton(sug, new Dimension(150, 50));

        AUTOCORRECT_QUERIES.add(append);
        this.panel.add(append);
        this.panel.revalidate();
        this.panel.repaint();
    }

    public void RemoveSuggestions(){
        for(SuggestionButton suggestion : AUTOCORRECT_QUERIES)
            this.panel.remove(suggestion);
        AUTOCORRECT_QUERIES.clear();
        this.panel.revalidate();
        this.panel.repaint();
    }

    public void SetStatus(UIState state){
        this.state = state;
        APP_LABEL.setText(this.state.toString());
    }

    private void InitializeVisuals(){
        panel = new DrawablePanel(dimension);
        DEBUG_DATA = new JTextArea();
        TEXT_OUTPUT = new JTextField();
        APP_LABEL = new JLabel();
        RESTART_BUTTON = new JButton("Clear");

        DEBUG_DATA.setPreferredSize(new Dimension(500, 250));
        DEBUG_DATA.setEditable(false);
        DEBUG_DATA.setFont(new Font("Default", Font.PLAIN, 15));

        TEXT_OUTPUT.setPreferredSize(new Dimension(500, 50));
        APP_LABEL.setPreferredSize(new Dimension(500, 25));
        TEXT_OUTPUT.setFont(new Font("Default", Font.BOLD, 20));
        panel.add(APP_LABEL);
        panel.add(DEBUG_DATA);
        panel.add(TEXT_OUTPUT);
        panel.add(RESTART_BUTTON);

        RESTART_BUTTON.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Leap.language.FinishWord();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Leap.language.SessionWords.clear();
                AUTOCORRECT_QUERIES.clear();
                Leap.language.consecutive = 0;
                TEXT_OUTPUT.setText("");

                RemoveSuggestions();
            }
        });

        this.add(panel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(dimension);

    }


    /* HELPER DEBUGGING FUNCTIONS */
    public void WARN(String... args){
        WARN(true, args);
    }

    public void ERROR(String... args){
        ERROR(true, args);
    }

    public void LOG(String... args){
        LOG(true, args);
    }

    public void WARN(boolean clear, String... args){
        LOG(clear, String.format("[WARN]: %s", BuildEndlineFormatting(args)));
    }

    public void ERROR(boolean clear, String... args){
        LOG(clear, String.format("[ERR]: %s", BuildEndlineFormatting(args)));
    }

    public void LOG(boolean clear, String... args){
        if (clear) DEBUG_DATA.setText(BuildEndlineFormatting(args));
        else DEBUG_DATA.append(BuildEndlineFormatting(args));
    }

    private String BuildEndlineFormatting(String... args){
        StringBuilder ret = new StringBuilder();
        for(String s : args)
            ret.append(String.format("%s\n", s));
        return String.valueOf(ret);
    }


    public void DebugFrame(DetectionUnit detect){
        if(detect != null){
            Leap.visuals.LOG(true, "[Breaks in detection indicates unsure detections or movement]");
            Leap.visuals.LOG(false, "Detected valid points: " + Arrays.toString(detect.in),
                    "\nAnalysis generated: [" + detect + "]",
                    "Consecutive required until letter is appended: " + Leap.language.consecutive + "/" + Leap.config.REQUIRED_CONSECUTIVE_DETECTIONS,
                    Leap.language.consecutive >= Leap.config.REQUIRED_CONSECUTIVE_DETECTIONS ? "\n[Language Engine Appended Character - waiting for change]" : "",
                    "Current Word: " + Leap.language.CurrentWord);
        }
    }

    public enum UIState{
        PAUSED,
        DETECTION;
    }

}

