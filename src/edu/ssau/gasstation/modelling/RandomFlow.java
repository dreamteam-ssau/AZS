package edu.ssau.gasstation.modelling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sasha on 30.11.2016.
 */
public class RandomFlow implements Flow {
    private ProbabilityDistribution distribution;
    public RandomFlow(ProbabilityDistribution distribution){
        this.distribution = distribution;
    }

    public void setDistribution(ProbabilityDistribution distribution) {
        this.distribution = distribution;
    }

    public ProbabilityDistribution getDistribution() {
        return distribution;
    }

    @Override
    public List<Double> getValuesSequence(int n) {
        List<Double> sequence = new ArrayList<>();
        for (int i=0; i<n; i++){
            sequence.add(distribution.generateValue());
        }
        return sequence;
    }
}
