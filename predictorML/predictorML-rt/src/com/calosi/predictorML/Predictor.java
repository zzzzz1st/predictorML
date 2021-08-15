package com.calosi.predictorML;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

import javax.baja.file.BIFile;
import javax.baja.naming.BOrd;

import java.io.IOException;
/**
 * Predictor class is the parent class for each predictor. Every predictor has a strategy type and has a predict function to see the future.
 * They also have the loadArff method to load the data for the Weka api. The values attribute is an array of new values.
 */
public abstract class Predictor {
    Predictor(BMLPointExt pointExt, BOrd modelOrd) {
        this.pointExt = pointExt;
        this.modelOrd = modelOrd;
    }

    String getStrategyType() {
        return strategyType;
    }

    void setStrategyType(String strategyType) throws Exception {
        this.strategyType = strategyType;
        BIFile modelFile = (BIFile) modelOrd.get(pointExt);
        switch (strategyType) {
            case "SMOreg":
                classifier = (SMOreg) SerializationHelper.read(modelFile.getInputStream());
                break;
            case "MultilayerPerceptron":
                classifier = (MultilayerPerceptron) SerializationHelper.read(modelFile.getInputStream());
                break;
            default:
                classifier = (LinearRegression) SerializationHelper.read(modelFile.getInputStream());
        }
    }

    double predict() throws Exception {
        return -99;
    }

    Instances loadArff() throws IOException {
        ArffLoader arffLoader = new ArffLoader();
        String modelOrdString = modelOrd.toString();
        modelOrdString = modelOrdString.replace("models", "arff");
        modelOrdString = modelOrdString.replace(".model", ".arff");
        BOrd arffOrd = BOrd.make(modelOrdString);
        BIFile arffFile = (BIFile) arffOrd.get(pointExt);
        arffLoader.setSource(arffFile.getInputStream());
        return arffLoader.getDataSet();
    }

    protected String strategyType = "";
    protected Classifier classifier;
    protected double[] values;
    protected BMLPointExt pointExt;
    protected BOrd modelOrd;
}
