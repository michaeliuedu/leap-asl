package gg.bear.modules;

public class LeapConfig{ //struct
    public Mode DETECTION_MODE;
    public float DNN_THRESHOLD;
    public int INTERVALS;
    public int REQUIRED_CONSECUTIVE_DETECTIONS;
    public float STATIC_VELOCITY_XYZ;

    public enum Mode{
        PICKY, //TODO: Implement case conditions (State machine)
        CONVENTIONAL
    }

    public LeapConfig(Mode d, float t, int i, int r, float s){
        DETECTION_MODE = d;
        DNN_THRESHOLD =  t;
        INTERVALS = i;
        REQUIRED_CONSECUTIVE_DETECTIONS = r;
        STATIC_VELOCITY_XYZ = s;
    }
}