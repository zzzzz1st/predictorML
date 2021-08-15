package com.calosi.predictorML;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import javax.baja.file.*;
import javax.baja.job.BSimpleJob;
import javax.baja.naming.BOrd;
import javax.baja.naming.OrdQuery;
import javax.baja.naming.UnresolvedException;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import java.io.IOException;
import java.util.*;

@NiagaraType
public class BMLBenchmarkJob extends BSimpleJob {
    /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
    /*@ $com.calosi.predictorML.BMLBenchmarkJob(2979906276)1.0$ @*/
    /* Generated Sat Jun 05 14:32:58 CEST 2021 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

    @Override
    public Type getType() {
        return TYPE;
    }

    public static final Type TYPE = Sys.loadType(BMLBenchmarkJob.class);

    /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    /**
     * Callback when this job is invoked. For this job, the goal is to
     * get the csv files under each plant folders and filter each of them.
     * After that save the model and the arff format of the same data.
     */
    @Override
    public void run(Context context) throws Exception {
        BOrd root = BOrd.make("file:^MLFiles/Plants");
        BOrd strategiesOrd = BOrd.make("file:^MLFiles/Strategies/strategies.properties");
        BOrd child;
        Instances data;
        BDirectory childDir;
        BIFile[] csvFiles = new BIFile[0];
        BDirectory rootDir = (BDirectory) root.get(this);
        BIFile[] dirs = rootDir.listFiles();
        for (BIFile plantDirectory : dirs) {
            //deleting non csv files on each plant and list the csv files.
            if (plantDirectory instanceof BDirectory) {
                deleteNonCSVFiles(plantDirectory);
                String preparedOrd = plantDirectory.toString().concat("/csv");
                child = BOrd.make(preparedOrd);
                childDir = (BDirectory) child.get(this);
                csvFiles = childDir.listFiles();
            }
            //filters the csv file and saves it as an arff format. then finds the best algorithm for the data.
            for (BIFile csvFile : csvFiles) {
                System.out.println("File name : " + csvFile.getFileName());
                data = filter(csvFile);
                instanceArffSaver(data, csvFile);
                findAndSaveStrategy(data, plantDirectory, csvFile);
            }
        }
        // when everything is finished, it saves the strategy file.
        createAndStoreStrategyFile(strategiesOrd);
    }

    /**
     * filter function filters a csv file and returns the file
     * as an Instances object of WEKA api.
     */
    private Instances filter(BIFile csvFile) throws Exception {
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(csvFile.getInputStream());
        Instances instances = csvLoader.getDataSet();
        //removes the first column which is the timestamp.
        Remove remove = new Remove();
        String[] remOpts = new String[]{"-R", "1"};
        remove.setOptions(remOpts);
        assert instances != null;
        instances.setClassIndex(1);
        remove.setInputFormat(instances);
        instances = Filter.useFilter(instances, remove);
        return instances;
    }

    /**
     * instanceArffSaver function saves an Instances object as an ARFF format raw data
     * on the SSD. It saves the file on a relative path to the main csv file.
     */
    private void instanceArffSaver(Instances instancesForSaving, BIFile csvFile) throws Exception {
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(instancesForSaving);
        String preparedOrd = csvFile.getFilePath().toString();
        //takes the csv path and changes it to the arff path. Then saves the arff file.
        preparedOrd = preparedOrd.replace(".csv", ".arff");
        preparedOrd = preparedOrd.replace("csv", "arff");
        BOrd child = BOrd.make(preparedOrd);
        OrdQuery[] queries = child.parse();
        FilePath filePath = (FilePath) queries[queries.length - 1];
        BIFile file = BFileSystem.INSTANCE.makeFile(filePath);
        arffSaver.setDestination(file.getOutputStream());
        arffSaver.writeBatch();
    }

    /**
     * findAndSaveStrategy function gets an Instances object and tries some algorithms on the data.
     * After that it takes the algorithm with the maximum correlation coefficient and
     * saves the name of the data associated with the algorithm name.
     */
    private void findAndSaveStrategy(Instances data, BIFile plantDirectory, BIFile csvFile) throws Exception {
        Map<Double, Classifier> classifierMap = new HashMap<>();
        // Evaluates each algorithm on the data and saves the maximum correlation coefficient in the maxValueInMap variable.
        classifierMap.putAll(buildAndEvaluate("LinearRegression", data));
        classifierMap.putAll(buildAndEvaluate("SMOreg", data));
        classifierMap.putAll(buildAndEvaluate("MultilayerPerceptron", data));
        double maxValueInMap = (Collections.max(classifierMap.keySet()));
        Classifier classifier = classifierMap.get(maxValueInMap);
        saveClassifier(classifier, csvFile);
        properties.setProperty(plantDirectory.getFileName() + "_" + csvFile.getFileName().substring(0, csvFile.getFileName().length() - 4), classifier.getClass().getSimpleName());
    }

    /**
     * buildAndEvaluate function gets an algorithm name and a data. It takes
     * 80 percent of the data to train the model and 20 percent to test the model.
     * After that it saves the correlation coefficient associated with the name of the algorithm
     * as a HashMap.
     */
    private Map<Double, Classifier> buildAndEvaluate(String classifierName, Instances data) throws Exception {
        int trainSize = (int) Math.round(data.numInstances() * 0.8);
        int testSize = data.numInstances() - trainSize;
        Instances train = new Instances(data, 0, trainSize);
        Instances test = new Instances(data, trainSize, testSize);
        train.setClassIndex(0);
        test.setClassIndex(0);
        Evaluation eval = new Evaluation(train);
        Classifier classifier;
        switch (classifierName) {
            case "SMOreg":
                classifier = new SMOreg();
                break;
            case "MultilayerPerceptron":
                classifier = new MultilayerPerceptron();
                break;
            default:
                classifier = new LinearRegression();
        }
        classifier.buildClassifier(train);
        eval.evaluateModel(classifier, test);
        Map<Double, Classifier> resultMap = new HashMap<>();
        resultMap.put(eval.correlationCoefficient(), classifier);
        System.out.println("Classifier Type : " + classifier.getClass().getSimpleName());
        System.out.println("Score : " + eval.correlationCoefficient());
        return resultMap;
    }

    /**
     * saveClassifier function takes a Classifier object and saves it as a .model file.
     * It saves the file on a relative path to the main csv file.
     */
    private void saveClassifier(Classifier classifier, BIFile csvFile) throws Exception {
        String preparedOrd = csvFile.getFilePath().toString();
        //takes the csv path and changes it to the models path. Then saves the model file.
        preparedOrd = preparedOrd.replace(".csv", ".model");
        preparedOrd = preparedOrd.replace("csv", "models");
        BOrd child = BOrd.make(preparedOrd);
        OrdQuery[] queries = child.parse();
        FilePath filePath = (FilePath) queries[queries.length - 1];
        BIFile file = BFileSystem.INSTANCE.makeFile(filePath);
        SerializationHelper.write(file.getOutputStream(), classifier);
    }

    /**
     * createAndStoreStrategyFile function takes a strategy file path and saves a strategy file (.properties) in the path.
     */
    private void createAndStoreStrategyFile(BOrd strategiesPath) throws IOException {
        OrdQuery[] queries = strategiesPath.parse();
        FilePath filePath = (FilePath) queries[queries.length - 1];
        BIFile file = BFileSystem.INSTANCE.makeFile(filePath);
        properties.store(file.getOutputStream(), "Prediction service system files. please DO NOT overwrite !!");
    }

    /**
     * deleteNonCSVFiles function takes a plant directory and deletes arff and model paths.
     */
    private void deleteNonCSVFiles(BIFile plantDirectory) throws IOException {
        String preparedOrd = plantDirectory.toString().concat("/models");
        BOrd subOrd = BOrd.make(preparedOrd);
        BIFile subDir = null;
        try {
            subDir = (BIFile) subOrd.get(this);
        } catch (UnresolvedException ignored) {
        }
        if (subDir != null)
            subDir.delete();
        preparedOrd = plantDirectory.toString().concat("/arff");
        subOrd = BOrd.make(preparedOrd);
        try {
            subDir = (BIFile) subOrd.get(this);
        } catch (UnresolvedException ignored) {
        }
        if (subDir != null)
            subDir.delete();
    }

    /**
     * doCancelFunction function is needed when the user cancels the job.
     */
    public void doCancel(Context cx) { // Overridden here to set the canceled flag
        super.doCancel(cx);
    }

    private final Properties properties = new Properties();
}
