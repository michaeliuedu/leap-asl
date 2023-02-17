package leap.internal;

import leap.Leap;
import com.leapmotion.leap.*;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;

public class EventListener extends Listener {
    public void onInit(Controller controller) {
        Leap.FRAME.WarnOutputData(
                "[SUCCESS]: Initialized components\n" +
                "[ERROR] No service or device connected");

        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        Leap.FRAME.WarnOutputData("[SUCCESS]: Service and device connected");
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        Leap.FRAME.WarnOutputData("[SUCCESS]: Service disconnected");
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        Leap.FRAME.WarnOutputData("[SUCCESS]: Service exited");
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        try {
            if(frame.hands().count() == 1){
                Preprocessing.UpdateDatapoint(frame);
            }
            else {
                Leap.FRAME.WarnOutputData("[ERROR] Preconditions not yet met: No hands detected");
            }
        } catch (IOException | CsvException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}