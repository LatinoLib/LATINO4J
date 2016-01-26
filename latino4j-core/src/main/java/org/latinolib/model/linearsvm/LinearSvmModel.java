package org.latinolib.model.linearsvm;

import com.google.common.base.Preconditions;
import de.bwaldvogel.liblinear.*;
import org.latinolib.bow.SparseVector;
import org.latinolib.bow.VectorEntry;
import org.latinolib.model.LabeledExampleCollection;
import org.latinolib.model.Model;
import org.latinolib.model.Prediction;
import org.latinolib.model.PredictionScore;

import java.io.Serializable;

/**
 * Author saxo
 */
public class LinearSvmModel implements Model<Double, SparseVector>, Serializable {
    private static final long serialVersionUID = -6556601514629098712L;

    private final Parameter parameter;
    private de.bwaldvogel.liblinear.Model model;

    public LinearSvmModel(Parameter parameter) {
        this.parameter = Preconditions.checkNotNull(parameter);
    }

    public Parameter getParameter() {
        return parameter;
    }

    public de.bwaldvogel.liblinear.Model getModel() {
        return model;
    }

    @Override
    public void train(LabeledExampleCollection<Double, SparseVector> dataset) {
        Preconditions.checkNotNull(dataset);

        Feature[][] examples = new Feature[dataset.size()][];
        double[] labels = new double[dataset.size()];
        int maxIndex = 0;
        for (int i = 0; i < examples.length; i++) {
            examples[i] = dataset.get(i).getExample().toArray(new Feature[0]);
            for (int j = 0; j < examples[i].length; j++) {
                if (examples[i][j].getIndex() > maxIndex) {
                    maxIndex = examples[i][j].getIndex();
                }
            }
            labels[i] = dataset.get(i).getLabel();
        }

        Problem problem = new Problem();
        problem.l = examples.length;
        problem.n = maxIndex + 1;
        problem.x = examples;
        problem.y = labels;

        model = Linear.train(problem, parameter);
    }

    @Override
    public Prediction<Double> predict(SparseVector example) {
        Preconditions.checkState(model != null);

        double prediction = Linear.predict(model, example.toArray(new Feature[0]));
        return new Prediction<Double>(new PredictionScore<Double>(prediction, prediction));
    }

}
