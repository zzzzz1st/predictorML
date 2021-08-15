package com.calosi.predictorML;

import javax.baja.sys.*;
import javax.baja.nre.annotations.*;
/**
 * This Enum class is needed to choose the number of the variables.
 */
@NiagaraEnum
        (
                range = {
                        @Range(ordinal = 1, value = "oneVariable"),
                        @Range(ordinal = 2, value = "twoVariables"),
                        @Range(ordinal = 3, value = "threeVariables"),
                },
                defaultValue = "oneVariable"
        )

@NiagaraType
public final class BPredictionType extends BFrozenEnum {
    /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
    /*@ $com.calosi.predictorML.BPredictionType(3361970415)1.0$ @*/
    /* Generated Tue Jun 08 19:28:31 CEST 2021 by Slot-o-Matic (c) Tridium, Inc. 2012 */

    /**
     * Ordinal value for oneVariable.
     */
    public static final int ONE_VARIABLE = 1;
    /**
     * Ordinal value for twoVariables.
     */
    public static final int TWO_VARIABLES = 2;
    /**
     * Ordinal value for threeVariables.
     */
    public static final int THREE_VARIABLES = 3;

    /**
     * BPredictionType constant for oneVariable.
     */
    public static final BPredictionType oneVariable = new BPredictionType(ONE_VARIABLE);
    /**
     * BPredictionType constant for twoVariables.
     */
    public static final BPredictionType twoVariables = new BPredictionType(TWO_VARIABLES);
    /**
     * BPredictionType constant for threeVariables.
     */
    public static final BPredictionType threeVariables = new BPredictionType(THREE_VARIABLES);

    /**
     * Factory method with ordinal.
     */
    public static BPredictionType make(int ordinal) {
        return (BPredictionType) oneVariable.getRange().get(ordinal, false);
    }

    /**
     * Factory method with tag.
     */
    public static BPredictionType make(String tag) {
        return (BPredictionType) oneVariable.getRange().get(tag);
    }

    /**
     * Private constructor.
     */
    private BPredictionType(int ordinal) {
        super(ordinal);
    }

    public static final BPredictionType DEFAULT = oneVariable;

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType() {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BPredictionType.class);

    /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
}