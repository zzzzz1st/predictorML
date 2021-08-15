package com.calosi.predictorML;

import javax.baja.naming.BOrd;
/**
 * PredictorFactory is a factory which creates a predictor based on the chosen type.
 */
public final class PredictorFactory {
    public static Predictor makePredictor(BMLPointExt pExt, BOrd modelOrd, double newValue) {
        int predictorType = pExt.getPredictionType().getOrdinal();
        if (predictorType == 1)
            return new OneVariablePredictor(pExt, modelOrd, newValue);
        else if (predictorType == 2)
            return new TwoVariablesPredictor(pExt, modelOrd, newValue);
        else return new ThreeVariablesPredictor(pExt, modelOrd, newValue);
    }
}
