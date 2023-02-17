package gg.bear.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.leapmotion.leap.*;
import gg.bear.modules.Leap;

public class Utilities {

    //Dynamic constants
    public final static Vector JVelocity = new Vector(100, 100, 100);
    public final static Vector ZVelocity = new Vector(-50, 25, 50);
    public final static Vector StaticVelocity = Vector.zero();


    private static Float DistanceBetween(Vector A, Vector B){
        return (float) (Math.abs(Math.sqrt(Math.pow(B.getX() - A.getX(), 2) + Math.pow(B.getY() - A.getY(), 2) + Math.pow(B.getZ() - A.getZ(), 2))));
    }

    public static Collection<? extends Float> NormalizeFingerLength(Frame FRAME)
    {
        ArrayList<Float> RETURN_VALUE = new ArrayList<Float>();

        for (Hand HAND : FRAME.hands()){
            for (Finger FINGER : HAND.fingers())
                RETURN_VALUE.add(DistanceBetween(HAND.palmPosition(), FINGER.tipPosition()));
            break;
        }
        return RETURN_VALUE;
    }

    public static ArrayList<Float> NormalizeAdjacentAngles(Frame FRAME) {
        ArrayList<Float> RETURN_VALUE = new ArrayList<Float>();

        for (Hand HAND : FRAME.hands()){
            for (int i = 0; i < HAND.fingers().count() - 1; i++)
                RETURN_VALUE.add((float) ((Math.pow(DistanceBetween(HAND.fingers().get(i + 1).tipPosition(), HAND.palmPosition()), 2) +
                        Math.pow(DistanceBetween(HAND.fingers().get(i).tipPosition(), HAND.palmPosition()), 2) -
                        Math.pow(DistanceBetween(HAND.fingers().get(i).tipPosition(), HAND.fingers().get(i + 1).tipPosition()), 2)) / (2 *
                        Math.pow(DistanceBetween(HAND.fingers().get(i + 1).tipPosition(), HAND.palmPosition()), 2) *
                        Math.pow(DistanceBetween(HAND.fingers().get(i).tipPosition(), HAND.palmPosition()), 2))));
            break;
        }
        return RETURN_VALUE;
    }


    public static ArrayList<Float> ConsecutiveFingersDistance(Frame FRAME){
        ArrayList<Float> RETURN_VALUE = new ArrayList<Float>();

        for(Hand HAND : FRAME.hands()){
            for(int i = 0; i < HAND.fingers().count() - 1; i++)
                for(int j = i + 1; j < HAND.fingers().count(); j++)
                    RETURN_VALUE.add(DistanceBetween(HAND.fingers().get(j).tipPosition(), HAND.fingers().get(i).tipPosition()));

            break;
        }
        return RETURN_VALUE;
    }


    public static Float PalmCurvatureRadius(Frame FRAME){
        return (Float)(FRAME.hands().count() > 0 ? FRAME.hands().get(0).sphereRadius() : 0);
    }

    public static String GetClass(int result){
        result ++;
        return result > 0 && result < 27 ? String.valueOf((char)(result + 64)) : null;
    }

    public static boolean MeetsPreconditions(Frame frame){
        return (frame.hands().count() == 1 && frame.isValid() && frame.fingers().count() == 5 && frame.hands().get(0).isValid() && frame.hands().get(0).isRight());
    }

    public static boolean PauseRequested(Frame frame){
        return frame.isValid() && frame.hands().count() == 1 && frame.hands().get(0).isLeft();
//        for(Gesture gesture : frame.gestures()) //DEPRECIATED: GESTURE BASED TOGGLING
//            if(gesture.type() == Gesture.Type.TYPE_CIRCLE)
//                return true;
    }

    public static  boolean IsOrienting(Frame frame){
        float STATICVELOCITY = Leap.config.STATIC_VELOCITY_XYZ;
        return !(Math.abs(frame.hands().get(0).palmVelocity().getX()) < STATICVELOCITY && Math.abs(frame.hands().get(0).palmVelocity().getY()) < STATICVELOCITY
                && Math.abs(frame.hands().get(0).palmVelocity().getZ()) < STATICVELOCITY);
    }

    public static Float Percentify(Float value, int places) {
        Double scale = Math.pow(10, places);
        return (float) (Math.round(value * scale) / scale) * 100;
    }


    public static void RestartLeapService() throws IOException, InterruptedException {

        Logger.LOG("Starting service: operating for 500 milliseconds - waiting for execution");

        String[] script = new String[]{"net", "stop", "LeapService"};
        System.out.println(Runtime.getRuntime().exec(script));

        TimeUnit.MILLISECONDS.sleep(500);

        script = new String[]{"net", "start", "LeapService"};
        Runtime.getRuntime().exec(script);

        Logger.LOG("Leap service restarted");

    }

}
