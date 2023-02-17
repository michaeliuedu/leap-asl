package gg.bear.modules.detection;

import gg.bear.helper.Logger;
import jakarta.xml.bind.JAXBException;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder;
import org.nd4j.common.io.ClassPathResource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SVMEvaluator {
    private final Evaluator evaluator;
    public SVMEvaluator() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        evaluator = new LoadingModelEvaluatorBuilder().load(new ClassPathResource("post/svm.pmml").getFile()).build();
        evaluator.verify();
        Logger.LOG("Model loaded: " + this.toString());
    }

    public Map<String, ?> EvaluateFull(float[] args){

        Map<String, FieldValue> ARGUMENTS = new LinkedHashMap<>();
        List<InputField> INPUTS = evaluator.getInputFields();

        for(int i = 0; i < INPUTS.size(); i++){
            FieldValue value = INPUTS.get(i).prepare(args[i]);
            ARGUMENTS.put(INPUTS.get(i).getName(), value);
        }

        return evaluator.evaluate(ARGUMENTS);
    }

    public Map<String, ?> EvaluateFull(double[] args){

        Map<String, FieldValue> ARGUMENTS = new LinkedHashMap<>();
        List<InputField> INPUTS = evaluator.getInputFields();

        for(int i = 0; i < INPUTS.size(); i++){
            FieldValue value = INPUTS.get(i).prepare(args[i]);
            ARGUMENTS.put(INPUTS.get(i).getName(), value);
        }

        return evaluator.evaluate(ARGUMENTS);
    }


    public int Evaluate(float[] args){
        String str = String.valueOf(this.EvaluateFull(args));
        return Integer.parseInt(((str.charAt(38)) + ((str.charAt(38 + 1) != ',') ? String.valueOf(str.charAt(38 + 1)) : "")));
    }

    public int Evaluate(double[] args){
        String str = String.valueOf(this.EvaluateFull(args));
        return Integer.parseInt(((str.charAt(38)) + ((str.charAt(38 + 1) != ',') ? String.valueOf(str.charAt(38 + 1)) : "")));
    }

    @Override
    public String toString(){
        return evaluator.getSummary();
    }

}
