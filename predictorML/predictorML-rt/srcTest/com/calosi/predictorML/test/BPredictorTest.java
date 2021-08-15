package com.calosi.predictorML.test;

import com.calosi.predictorML.BMLPointExt;
import com.calosi.predictorML.BPredictionType;
import com.calosi.predictorML.BPrevisionService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.baja.control.BNumericWritable;
import javax.baja.file.BIFile;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatusEnum;
import javax.baja.sys.BDouble;
import javax.baja.sys.BStation;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.test.BTest;
import javax.baja.test.BTestNg;
import javax.baja.util.BServiceContainer;

import static java.lang.Thread.sleep;
import static org.testng.Assert.assertEquals;

@NiagaraType
public class BPredictorTest extends BTestNg {
    /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
    /*@ $com.calosi.predictorML.test.BPredictorTest(2979906276)1.0$ @*/
    /* Generated Thu Jun 17 21:13:28 CEST 2021 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType() {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BPredictorTest.class);

    /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/
    private TestStationHandler handler;
    private BStation station = null;
    /**
     * setup function creates a station based on a given bog file.
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        BOrd xmlOrd = BOrd.make("module://predictorMLTest/stations/Train00/config.bog");
        BIFile bogFile = (BIFile) xmlOrd.get(this);
        System.out.println(bogFile.toString());
        handler = BTest.createTestStation(xmlOrd);
        handler.startStation();
        station = handler.getStation();
        System.out.println("Setup Finished +++++++++++++++++");
    }
    /**
     * stationName test case checks if the created test station is Train00.
     */
    @Test(groups = "a")
    public void stationName() {
        assertEquals("Train00", station.getStationName());
    }
    /**
     * totalBenchmark test case creates a prediction service, adds it on a test station.
     * Then it invokes the total benchmark to see if it creates the right model.
     */
    @Test(groups = "a")
    public void totalBenchmark() throws InterruptedException {
        BPrevisionService previsionService = new BPrevisionService();
        BServiceContainer services = station.getServices();
        services.add("PrevisionService", previsionService);
        previsionService.doTotalBenchmark();
        sleep(3000);
        BOrd bOrd = BOrd.make("file:^MLFiles/Plants/PAM_IMOLA/models/CDD_DAILY.model");
        BIFile file = (BIFile) bOrd.get(station);
        assertEquals(file.getFileName(), "CDD_DAILY.model");
    }
    /**
     * checkPredictionType test case creates a oneVariable enum type. Then it checks if the integer value of this type is equal to one.
     * This test case depends on the setup and the total benchmark.
     */
    @Test(dependsOnGroups = "a")
    public void checkPredictionType() {
        BPredictionType predictionType = BPredictionType.make("oneVariable");
        BMLPointExt bmlPointExt = new BMLPointExt();
        bmlPointExt.setPredictionType(predictionType);
        assertEquals(bmlPointExt.getPredictionType().getOrdinal(), 1);
    }
    /**
     * setAndCheckOutput test case creates a numeric writable and adds a machine learning point extension to it.
     * Then it sees if the output changes. This test case depends on the setup and the total benchmark.
     */
    @Test(dependsOnGroups = "a")
    public void setAndCheckOutput() throws InterruptedException {
        BMLPointExt bmlPointExt = new BMLPointExt();
        BOrd bOrd = BOrd.make("file:^MLFiles/Plants/PAM_IMOLA/models/CDD_DAILY.model");
        bmlPointExt.setMlModelSummer(bOrd);
        bOrd = BOrd.make("file:^MLFiles/Plants/PAM_IMOLA/models/HDD_DAILY.model");
        bmlPointExt.setMlModelWinter(bOrd);
        BNumericWritable numericTest = new BNumericWritable();
        numericTest.add("bml", bmlPointExt);
        station.add("testWr", numericTest);
        BDouble bDouble = BDouble.make(1.5);
        numericTest.set(bDouble);
        sleep(3000);
        assertEquals(bmlPointExt.getPredictionOutput().substring(0, 2), "Il");
    }
    /**
     * cleanup function clears the test station.
     */
    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        handler.stopStation();
        handler.releaseStation();
        handler = null;
        BStatusEnum bStatusEnum = new BStatusEnum();
    }
}
