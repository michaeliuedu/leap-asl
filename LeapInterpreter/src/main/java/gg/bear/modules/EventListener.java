package gg.bear.modules;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;
import gg.bear.core.DetectionUnit;
import gg.bear.helper.Logger;
import gg.bear.helper.Utilities;
import gg.bear.visuals.SuggestionButton;
import gg.bear.visuals.UI;
import gg.bear.visuals.UIUtilities;
import jdk.jshell.SourceCodeAnalysis;

import java.io.IOException;

public class EventListener extends Listener {

    int INTERVAL_FRAMES = 0;

    @Override
    public void onInit(Controller controller) {
        Logger.LOG("Application and Leap Java modules initialized");
    }

    @Override
    public void onConnect(Controller controller) {
        Logger.LOG("Leap service online");
    }

    @Override
    public void onDisconnect(Controller controller) { Logger.LOG("Leap service disconnected");}

    @Override
    public void onExit(Controller controller) {
        Logger.LOG("Program terminated safely");
    }

    @Override
    public void onFrame(Controller controller) {
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        Frame frame = controller.frame();

        if(Utilities.PauseRequested(frame)) {
            Leap.visuals.panel.cursor = UIUtilities.CalculateCursor(frame);
            Leap.visuals.repaint();
            Leap.visuals.SetStatus(UI.UIState.PAUSED);

            for(SuggestionButton button : Leap.visuals.AUTOCORRECT_QUERIES)
                for(Gesture g : frame.gestures())
                    if(g.type() == Gesture.Type.TYPE_SCREEN_TAP && button.IsHovering()){
                        Leap.language.SessionWords.set(Leap.language.SessionWords.size() - 1, button.suggestion);
                        Leap.visuals.TEXT_OUTPUT.setText(Leap.language.Stringify());
                    }

            return;
        }

        Leap.visuals.SetStatus(UI.UIState.DETECTION);
        INTERVAL_FRAMES++;

        if(!Utilities.MeetsPreconditions(frame)) {
            try {
                Leap.language.FinishWord();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Leap.visuals.WARN("Right signing hand not in frame. Existing word appended into Arraylist");
            return;
        }

        if(INTERVAL_FRAMES < Leap.config.INTERVALS)
            return;

        DetectionUnit detect = Leap.detection.DetectFrame(frame);

        Leap.visuals.DebugFrame(detect);
        Leap.language.Compute(detect.Class);


        INTERVAL_FRAMES = 0;
    }

}