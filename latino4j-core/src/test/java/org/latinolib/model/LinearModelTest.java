package org.latinolib.model;

import de.bwaldvogel.liblinear.Parameter;

import org.junit.Test;
import org.latinolib.SparseVector;
//import org.latinolib.VectorEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.latinolib.model.LinearModels.*;

/**
 * Author mIHA
 */
public class LinearModelTest
{
    private static LabeledDataset<Double, SparseVector> readDataset(BufferedReader reader) throws IOException, ParseException {
        String line;
        LabeledDataset<Double, SparseVector> ds = new LabeledDataset<Double, SparseVector>();
        while ((line = reader.readLine()) != null) {
            SparseVector vec = new SparseVector();
            Pattern labelPattern = Pattern.compile("^(?<label>[+-]?\\d+([.]\\d+)?)(\\s|$)");
            Pattern featurePattern = Pattern.compile("(?i)(?<feature>(\\d+|qid)):(?<weight>[-]?[\\d\\.]+)");
            if (!line.startsWith("#")) {
                Matcher labelMatch = labelPattern.matcher(line);
                labelMatch.find();
                double label = Double.parseDouble(labelMatch.group("label"));
                Matcher match = featurePattern.matcher(line);
                while (match.find()) {
                    int feature = Integer.parseInt(match.group("feature"));
                    double weight = NumberFormat.getInstance(Locale.US).parse(match.group("weight")).doubleValue();
                    //vec.add(feature, weight);
                    vec.add(new FeatureEntry(feature, weight));
                }
                ds.add(label, vec);
            }
        }
        return ds;
    }

    private void testInduction(String folder, Parameter parameter, Double accuracyMin, Double accuracyMax) throws IOException, ParseException {
        InputStream is = LinearModelTest.class.getResourceAsStream(folder + "/train.dat");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        LabeledDataset<Double, SparseVector> ds = readDataset(reader);
        reader.close();
//        for (LabeledExampleEntry<Double, SparseVector> le : ds) {
//            System.out.print(le.getLabel() + " ");
//            for (VectorEntry item : le.getExample()) {
//                System.out.print(item.getIndex() + ":" + item.getData() + " ");
//            }
//            System.out.println();
//        }
        LinearModel model = new LinearModel(parameter);
        model.train(ds); // WARNME: this should work with standard sparse vectors not just vectors that contain FeatureEntries
        is = LinearModelTest.class.getResourceAsStream(folder + "/test.dat");
        reader = new BufferedReader(new InputStreamReader(is));
        ds = readDataset(reader);
        reader.close();
        int correct = 0;
        for (LabeledExampleEntry<Double, SparseVector> le : ds) { // WARNME: why is this not simply LabeledExample?
            Prediction<Double> p = model.predict(le.getExample());
            Double bestLabel = p.getBest().getLabel();
            if (bestLabel.equals(le.getLabel())) { correct++; }
        }
        double accuracy = (double)correct / (double)ds.size();
        assertTrue(accuracy >= accuracyMin && accuracy <= accuracyMax);
    }

    @Test
    public void testInductionSvmClassifier() throws IOException, ParseException {
        testInduction("inductive", SVM_CLASSIFIER.getDefaultParameter(), 0.97, 0.98);
    }

    @Test
    public void testInductionLogisticRegression() throws IOException, ParseException {
        testInduction("inductive", LOGISTIC_REGRESSION.getDefaultParameter(), 0.97, 0.98);
    }

    @Test
    public void testInductionCrammerSinger() throws IOException, ParseException {
        testInduction("inductive", SVM_MULTICLASS.getDefaultParameter(), 0.97, 0.98);
    }

    @Test
    public void testMulticlassSvmClassifier() throws IOException, ParseException {
        testInduction("multiclass", SVM_CLASSIFIER.getDefaultParameter(), 0.6, 0.7);
    }

    @Test
    public void testMulticlassLogisticRegression() throws IOException, ParseException {
        testInduction("multiclass", LOGISTIC_REGRESSION.getDefaultParameter(), 0.6, 0.7);
    }

    @Test
    public void testMulticlassCrammerSinger() throws IOException, ParseException {
        testInduction("multiclass", SVM_MULTICLASS.getDefaultParameter(), 0.6, 0.7);
    }

    @Test
    public void testRegression() throws IOException, ParseException {
        InputStream is = LinearModelTest.class.getResourceAsStream("regression/train.dat");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        LabeledDataset<Double, SparseVector> ds = readDataset(reader);
        reader.close();
        LinearModel model = new LinearModel(SVM_REGRESSION.getDefaultParameter());
        model.train(ds);
        is = LinearModelTest.class.getResourceAsStream("regression/test.dat");
        reader = new BufferedReader(new InputStreamReader(is));
        ds = readDataset(reader);
        reader.close();
        double mae = 0;
        for (LabeledExampleEntry<Double, SparseVector> le : ds) {
            Prediction<Double> p = model.predict(le.getExample());
            Double value = p.getBest().getLabel();
            System.out.println(value);
            mae += Math.abs(value - le.getLabel());
        }
        mae /= (double)ds.size();
        assertTrue(mae >= 25.0 && mae <= 35.0);
    }
}