package com.calosi.predictorML;

import javax.baja.file.BIFile;
import javax.baja.job.BJobService;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import java.util.Properties;

@NiagaraAction(name = "totalBenchmark", flags = Flags.ASYNC)
@NiagaraType
public class BPrevisionService extends BAbstractService {
    /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
    /*@ $com.calosi.predictorML.BPrevisionService(4078234651)1.0$ @*/
    /* Generated Tue Jun 15 22:33:57 CEST 2021 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Action "totalBenchmark"
////////////////////////////////////////////////////////////////

    /**
     * Slot for the {@code totalBenchmark} action.
     *
     * @see #totalBenchmark()
     */
    public static final Action totalBenchmark = newAction(Flags.ASYNC, null);

    /**
     * Invoke the {@code totalBenchmark} action.
     *
     * @see #totalBenchmark
     */
    public void totalBenchmark() {
        invoke(totalBenchmark, null, null);
    }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType() {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BPrevisionService.class);

    /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
     * update function is invoked by the extension. It looks at the current month and chooses the right model.
     * Then it lookes at the strategies file to see the right algorithm.
     */
    public synchronized void update(BMLPointExt pExt, double newValue) throws Exception {
        BOrd strategiesPath = BOrd.make("file:^MLFiles/Strategies/strategies.properties");
        BIFile propertiesIFile = (BIFile) strategiesPath.get(this);
        Properties properties = new Properties();
        properties.load(propertiesIFile.getInputStream());
        BAbsTime time = BAbsTime.now();
        int month = time.getMonth().getMonthOfYear();
        BOrd modelOrd = null;
        if (month >= 6 && month <= 9)
            modelOrd = pExt.getMlModelSummer();
        else if (month >= 1 && month <= 3)
            modelOrd = pExt.getMlModelWinter();
        assert modelOrd != null;
        String preparedKey = modelOrd.toString();
        String[] arrayPreparedKey = preparedKey.split("/");
        if (arrayPreparedKey.length > 2) {
            //creates the key for the strategies file. then it looks at the algorithm to use.
            preparedKey = arrayPreparedKey[arrayPreparedKey.length - 1];
            preparedKey = (arrayPreparedKey[arrayPreparedKey.length - 3] + "_" + preparedKey.substring(0, preparedKey.length() - 6));
        }
        String strategy = properties.getProperty(preparedKey);
        if (strategy != null) {
            Predictor predictor = PredictorFactory.makePredictor(pExt, modelOrd, newValue);
            predictor.setStrategyType(strategy);
            predictor.predict();
        }

    }

    /**
     * doTotalBenchmark function invokes the BenchmarkJob.
     */
    public void doTotalBenchmark() {
        BJobService.getService().submit(new BMLBenchmarkJob(), null);
    }

    @Override
    public Type[] getServiceTypes() { // Since this service only implements a single service Type (itself),
        // return a static array of one Type element
        return serviceTypes;
    }

    @Override
    public BIcon getIcon() {
        return icon;
    }

    private static final Type[] serviceTypes = new Type[]{TYPE};
    private static final BIcon icon = BIcon.std("charts/pie.png");
}