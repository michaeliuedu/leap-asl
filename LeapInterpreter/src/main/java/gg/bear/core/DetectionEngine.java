package gg.bear.core;

import com.leapmotion.leap.Frame;
import gg.bear.helper.Utilities;
import gg.bear.modules.detection.DNNEvaluator;
import gg.bear.modules.detection.SVMEvaluator;
import jakarta.xml.bind.JAXBException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class DetectionEngine {

    public static SVMEvaluator svm;
    public static DNNEvaluator dnn;

    public DetectionEngine() throws JAXBException, IOException, ParserConfigurationException, SAXException, UnsupportedKerasConfigurationException, InvalidKerasConfigurationException {
        svm = svm == null ? new SVMEvaluator() : svm;
        dnn = dnn == null ? new DNNEvaluator() : dnn;
    }

    public DetectionUnit DetectFrame(Frame frame){
        ArrayList<Float> values = new ArrayList<>();

        values.addAll(Utilities.NormalizeFingerLength(frame));
        values.addAll(Utilities.NormalizeAdjacentAngles(frame));
        values.addAll(Utilities.ConsecutiveFingersDistance(frame));
        values.add(Utilities.PalmCurvatureRadius(frame));

        float[] in = new float[20];
        for(int i = 0; i < 20; i++) in[i] = values.get(i);


        DetectionUnit detection = new DetectionUnit(in);
        if(!detection.reasonable || Utilities.IsOrienting(frame))
            return null;
//        System.out.println("[" + Leap.language.CurrentWord + "]: " + detection.Class + " | confidence: " + Utilities.Percentify(detection.confidence, 3) + " v-> " + frame.hands().get(0).palmVelocity());
        return detection;
    }

}

