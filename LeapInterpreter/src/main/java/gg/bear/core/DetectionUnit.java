package gg.bear.core;

import gg.bear.helper.Utilities;
import gg.bear.modules.Leap;

public class DetectionUnit{
    public int ClassEncoded;
    public boolean reasonable;
    public String Class;
    public float confidence;
    public float[] in;

    public int svm;
    public int dnn;

    public DetectionUnit(float[] in){
        this(DetectionEngine.svm.Evaluate(in), DetectionEngine.dnn.Evaluate(in), DetectionEngine.dnn.EvaulateConfidence(in), in);
    }

    public DetectionUnit(int s, int d, float c, float[] i){
        svm = s;
        dnn = d;
        confidence = c;
        in = i;

        ClassEncoded = -1;
        DetermineReasonable();
        DetermineDetection();
    }

    protected void DetermineReasonable(){
        reasonable = !(confidence >= Leap.config.DNN_THRESHOLD && svm != dnn); //If the DNN Model is below threshold accuracy, throw it out
    }

    protected void DetermineDetection(){
        if(reasonable){
            ClassEncoded = svm;
            Class = Utilities.GetClass(ClassEncoded);
        }
    }

    @Override
    public String toString(){
        return String.format("%s ~ DNN Confidence: %s%%", this.Class, Utilities.Percentify(this.confidence, 2));
    }

}
