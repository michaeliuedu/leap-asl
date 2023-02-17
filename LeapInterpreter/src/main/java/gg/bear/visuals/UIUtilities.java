package gg.bear.visuals;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;
import gg.bear.modules.Leap;

import java.awt.*;

public class UIUtilities {
    //https://developer-archive.leapmotion.com/documentation/csharp/devguide/Leap_Coordinate_Mapping.html#:~:text=The%20Leap%20Motion%20controller%20uses,(%2Dz)%20the%20controller.


    public static Point CalculateCursor(Frame frame){
        InteractionBox bounds = frame.interactionBox();
        for(Hand hand : frame.hands()){
            if(hand.isLeft()){
                Finger finger = hand.fingers().get(0);
                Vector leapPoint = finger.stabilizedTipPosition();
                Vector normalizedPoint = bounds.normalizePoint(leapPoint, false);
                normalizedPoint = normalizedPoint.times(1.5f); //scale
                normalizedPoint = normalizedPoint.minus(new Vector(.25f, .25f, .25f)); // re-center

                float appX = normalizedPoint.getX() * Leap.visuals.dimension.width;
                float appY = (1 - normalizedPoint.getY()) * Leap.visuals.dimension.height;

                return new Point((int) appX, (int) appY);
            }
        }
        //unreachable
        return null;
    }
}
