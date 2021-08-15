package com.calosi.predictorML;

import javax.baja.control.BPointExtension;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.Facet;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatusNumeric;
import javax.baja.status.BStatusValue;
import javax.baja.sys.*;

@NiagaraProperty(name = "predictionType", type = "BPredictionType", defaultValue = "BPredictionType.oneVariable")
@NiagaraProperty(name = "mlModelWinter", type = "baja:Ord", defaultValue = "BOrd.make(\"file:^MLFiles/Plants\")")
@NiagaraProperty(name = "mlModelSummer", type = "baja:Ord", defaultValue = "BOrd.make(\"file:^MLFiles/Plants\")")
@NiagaraProperty(name = "secondVariable", type = "BStatusNumeric", flags = Flags.SUMMARY, defaultValue = "new BStatusNumeric()")
@NiagaraProperty(name = "thirdVariable", type = "BStatusNumeric", flags = Flags.SUMMARY, defaultValue = "new BStatusNumeric()")
@NiagaraProperty(name = "predictionOutput", type = "BString", flags = Flags.READONLY | Flags.SUMMARY, facets = {@Facet(name = "BFacets.MULTI_LINE", value = "true")}, defaultValue = " ")
@NiagaraType
public class BMLPointExt extends BPointExtension {
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.calosi.predictorML.BMLPointExt(2633510450)1.0$ @*/
/* Generated Mon Jun 14 08:25:48 CEST 2021 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Property "predictionType"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code predictionType} property.
   * @see #getPredictionType
   * @see #setPredictionType
   */
  public static final Property predictionType = newProperty(0, BPredictionType.oneVariable, null);
  
  /**
   * Get the {@code predictionType} property.
   * @see #predictionType
   */
  public BPredictionType getPredictionType() { return (BPredictionType)get(predictionType); }
  
  /**
   * Set the {@code predictionType} property.
   * @see #predictionType
   */
  public void setPredictionType(BPredictionType v) { set(predictionType, v, null); }

////////////////////////////////////////////////////////////////
// Property "mlModelWinter"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code mlModelWinter} property.
   * @see #getMlModelWinter
   * @see #setMlModelWinter
   */
  public static final Property mlModelWinter = newProperty(0, BOrd.make("file:^MLFiles/Plants"), null);
  
  /**
   * Get the {@code mlModelWinter} property.
   * @see #mlModelWinter
   */
  public BOrd getMlModelWinter() { return (BOrd)get(mlModelWinter); }
  
  /**
   * Set the {@code mlModelWinter} property.
   * @see #mlModelWinter
   */
  public void setMlModelWinter(BOrd v) { set(mlModelWinter, v, null); }

////////////////////////////////////////////////////////////////
// Property "mlModelSummer"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code mlModelSummer} property.
   * @see #getMlModelSummer
   * @see #setMlModelSummer
   */
  public static final Property mlModelSummer = newProperty(0, BOrd.make("file:^MLFiles/Plants"), null);
  
  /**
   * Get the {@code mlModelSummer} property.
   * @see #mlModelSummer
   */
  public BOrd getMlModelSummer() { return (BOrd)get(mlModelSummer); }
  
  /**
   * Set the {@code mlModelSummer} property.
   * @see #mlModelSummer
   */
  public void setMlModelSummer(BOrd v) { set(mlModelSummer, v, null); }

////////////////////////////////////////////////////////////////
// Property "secondVariable"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code secondVariable} property.
   * @see #getSecondVariable
   * @see #setSecondVariable
   */
  public static final Property secondVariable = newProperty(Flags.SUMMARY, new BStatusNumeric(), null);
  
  /**
   * Get the {@code secondVariable} property.
   * @see #secondVariable
   */
  public BStatusNumeric getSecondVariable() { return (BStatusNumeric)get(secondVariable); }
  
  /**
   * Set the {@code secondVariable} property.
   * @see #secondVariable
   */
  public void setSecondVariable(BStatusNumeric v) { set(secondVariable, v, null); }

////////////////////////////////////////////////////////////////
// Property "thirdVariable"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code thirdVariable} property.
   * @see #getThirdVariable
   * @see #setThirdVariable
   */
  public static final Property thirdVariable = newProperty(Flags.SUMMARY, new BStatusNumeric(), null);
  
  /**
   * Get the {@code thirdVariable} property.
   * @see #thirdVariable
   */
  public BStatusNumeric getThirdVariable() { return (BStatusNumeric)get(thirdVariable); }
  
  /**
   * Set the {@code thirdVariable} property.
   * @see #thirdVariable
   */
  public void setThirdVariable(BStatusNumeric v) { set(thirdVariable, v, null); }

////////////////////////////////////////////////////////////////
// Property "predictionOutput"
////////////////////////////////////////////////////////////////
  
  /**
   * Slot for the {@code predictionOutput} property.
   * @see #getPredictionOutput
   * @see #setPredictionOutput
   */
  public static final Property predictionOutput = newProperty(Flags.READONLY | Flags.SUMMARY, " ", BFacets.make(BFacets.MULTI_LINE, true));
  
  /**
   * Get the {@code predictionOutput} property.
   * @see #predictionOutput
   */
  public String getPredictionOutput() { return getString(predictionOutput); }
  
  /**
   * Set the {@code predictionOutput} property.
   * @see #predictionOutput
   */
  public void setPredictionOutput(String v) { setString(predictionOutput, v, null); }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BMLPointExt.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private BPrevisionService previsionService;

    @Override
    public void started() {
        previsionService = (BPrevisionService) Sys.getService(BPrevisionService.TYPE);
        if (previsionService == null)
            throw new IllegalStateException("Prevision service is not available.");
    }
    /**
     * onExecute function is invoked when the parent point changes. It updates the Prevision Service with his adress.
     */
    @Override
    public void onExecute(BStatusValue bStatusValue, Context context) {
        if (!bStatusValue.isNull()) {
            try {
                double newValue = Double.parseDouble(bStatusValue.getNvalue().toString().replace(",", "."));
                previsionService.update(this, newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * changed function is invoked when the second or third variable slot changes. It updates the Prevision Service with his adress.
     */
    @Override
    public void changed(Property property, Context context) {
        if (isRunning()) {
            if (property == secondVariable) {
                try {
                    double newParentValue = Double.parseDouble(getParentPoint().getStatusValue().getNvalue().toString().replace(",", "."));
                    previsionService.update(this, newParentValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (property == thirdVariable) {
                try {
                    double newParentValue = Double.parseDouble(getParentPoint().getStatusValue().getNvalue().toString().replace(",", "."));
                    previsionService.update(this, newParentValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
