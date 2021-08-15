package com.calosi.predictorML;

import weka.core.DenseInstance;
import weka.core.Instances;

import javax.baja.naming.BOrd;

public class ThreeVariablesPredictor extends Predictor {
    ThreeVariablesPredictor(BMLPointExt pExt, BOrd modelOrd, double newValue) {
        super(pExt, modelOrd);
        values = new double[4];
        values[0] = 10;
        values[1] = newValue;
        values[2] = pExt.getSecondVariable().getValue();
        values[3] = pExt.getThirdVariable().getValue();
    }

    @Override
    public double predict() throws Exception {
        Instances instances = loadArff();
        if (instances.numAttributes() == 4) {
            instances.setClassIndex(0);
            DenseInstance testInstance = new DenseInstance(1.0, values);
            testInstance.setDataset(instances); // To associate with instances object
            testInstance.setClassMissing();
            double result = classifier.classifyInstance(testInstance);
            pointExt.setPredictionOutput("Il punto è stato cambiato. i valori nuovi sono : " + values[1] +
                    " | " + values[2] + " | " + values[3] + "\n" +
                    "Il valore predittivo potrebbe essere : " + result);
            return result;
        } else {
            pointExt.setPredictionOutput("Hai scelto il modello sbagliato. prova ad inserire il modello giusto.");
            return -99;
        }
    }
}
