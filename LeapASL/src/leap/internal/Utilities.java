package leap.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

public class Utilities {
    //All methods only process one hand for ASL with break


    private static Float DistanceBetween(Vector A, Vector B){ //abs likely redundant
        return (float) (Math.abs(Math.sqrt(Math.pow(B.getX() - A.getX(), 2) + Math.pow(B.getY() - A.getY(), 2) + Math.pow(B.getZ() - A.getZ(), 2))));
    }


    //Depreciated?
    private static ArrayList<Vector> NormalizeFingerPosition(Frame FRAME)
    {
        ArrayList<Vector> RETURN_VALUE = new ArrayList<Vector>();

        for (Hand HAND : FRAME.hands()){
            for (Finger FINGER : HAND.fingers())
                RETURN_VALUE.add(HAND.palmPosition().minus(FINGER.tipPosition()));
            break;
        }
        return RETURN_VALUE;

    }


    public static ArrayList<Float> NormalizeFingerLength(Frame FRAME)
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
                RETURN_VALUE.add((float) Math.acos((Math.pow(DistanceBetween(HAND.fingers().get(i + 1).tipPosition(), HAND.palmPosition()), 2) +
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
        return (FRAME.hands().count() > 0 ? FRAME.hands().get(0).sphereRadius() : 0);
    }


    public static long ReturnLines(String FILE_NAME){
        Path path = Paths.get(FILE_NAME);
        long lines = 0;
        try {
            lines = Files.lines(path).count();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
