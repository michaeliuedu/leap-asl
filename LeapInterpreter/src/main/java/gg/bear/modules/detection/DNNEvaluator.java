package gg.bear.modules.detection;

import gg.bear.helper.Logger;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class DNNEvaluator implements AutoCloseable{

    private final MultiLayerNetwork model;

    public DNNEvaluator() throws IOException, UnsupportedKerasConfigurationException, InvalidKerasConfigurationException {
        String f_name = "post/sequential_e40.h5"; //e100 possible overfitting | e50 possible underfitting e60 -> (col*3) pre1200 (40->stable)
        model = KerasModelImport.importKerasSequentialModelAndWeights(new ClassPathResource(f_name).getFile().getPath(), true);
        Logger.LOG("Model loaded: " + this.toString());
    }

    public String EvaluateFull(float[] args) { return model.output(Nd4j.create(args, new int[]{1, 20}), false).toStringFull(); }
    public String EvaluateFull(double[] args) { return model.output(Nd4j.create(args, new int[]{1, 20}), false).toStringFull(); }

    public int Evaluate(float[] args){
        return model.predict(Nd4j.create(args, new int[]{1, 20}))[0];
    }
    public int Evaluate(double[] args){ return model.predict(Nd4j.create(args, new int[]{1, 20}))[0]; }

    public float EvaulateConfidence(float[] args){
        INDArray out = model.output(Nd4j.create(args, new int[]{1, 20}), false);
        return Float.parseFloat(String.valueOf(out.maxNumber()));
    }

    public float EvaulateConfidence(double[] args){
        INDArray out = model.output(Nd4j.create(args, new int[]{1, 20}), false);
        return Float.parseFloat(String.valueOf(out.maxNumber()));
    }

    @Override
    public String toString(){
        return model.getLayerNames().toString();
    }

    public String summarize(){
        return model.summary();
    }

    @Override
    public void close() throws Exception {
        model.clear();
        model.close();
    }
}


