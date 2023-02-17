package leap;

import com.leapmotion.leap.*;
import leap.internal.*;
import leap.visuals.Mainframe;

import java.io.IOException;

public class Leap {

    public static final long TICKS_PER_CAPTURE = 30;
    public static long TICKS_CURRENT = 0;
    public static boolean NEXT_TICK_READY = false;
    public static Mainframe FRAME;
    public static String GESTURE = "a";
    public static String STATIC_FILE = "filtered_post";
    public static String DYNAMIC_FILE = "rapidynamic";
    public static int THIS_SESSION = 0;

    public static void main(String[] args) {
        EventListener LISTENER = new EventListener();
        Controller CONTROLLER = new Controller();

        FRAME = new Mainframe();
        CONTROLLER.addListener(LISTENER);

        try {
            int DISPOSED = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CONTROLLER.removeListener(LISTENER);
    }
}

