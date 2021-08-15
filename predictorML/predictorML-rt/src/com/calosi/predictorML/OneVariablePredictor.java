package com.calosi.predictorML;

import weka.core.DenseInstance;
import weka.core.Instances;

import javax.baja.naming.BOrd;

public class OneVariablePredictor extends Predictor {
    OneVariablePredictor(BMLPointExt pExt, BOrd modelOrd, double newValue) {
        super(pExt, modelOrd);
        values = new double[2];
        values[0] = 10;
        values[1] = newValue;
    }

    @Override
    public double predict() throws Exception {
        Instances instances = loadArff();
        if (instances.numAttributes() == 2) {
            instances.setClassIndex(0);
            DenseInstance testInstance = new DenseInstance(1.0, values);
            testInstance.setDataset(instances); // To associate with instances object
            testInstance.setClassMissing();
            double result = classifier.classifyInstance(testInstance);
            pointExt.setPredictionOutput("Il punto è stato cambiato. il valore nuovo è : " + values[1] + "\n" +
                    "Il valore predittivo potrebbe essere : " + result);
            return result;
        } else {
            pointExt.setPredictionOutput("Hai scelto il modello sbagliato. prova ad inserire il modello giusto.");
            return -99;
        }
    }
}
