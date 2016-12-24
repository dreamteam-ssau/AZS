package edu.ssau.gasstation.modelling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sasha on 30.11.2016.
 */
public class DeterministicFlow implements Flow{
    private double generationTime;
    public DeterministicFlow(double generationTime){
        this.generationTime = generationTime;
    }

    public void setGenerationTime(double generationTime) {
        this.generationTime = generationTime;
    }

    public double getGenerationTime() {
        return generationTime;
    }

    @Override
    public List<Double> getValuesSequence(int n) {
        List<Double> sequence = new ArrayList<>();
        for (double i = 0; i < n; i++){
            sequence.add(i*generationTime);
        }
        return sequence;
    }
}
