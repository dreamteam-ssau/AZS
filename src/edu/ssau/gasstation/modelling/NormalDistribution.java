package edu.ssau.gasstation.modelling;

import java.util.Random;

/**
 * Created by Sasha on 30.11.2016.
 */
public class NormalDistribution implements ProbabilityDistribution {
    private double mean;
    private double variance;

    public NormalDistribution(double mean, double variance){
        this.mean = mean;
        this.variance = variance;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return variance;
    }

    @Override
    public double generateValue() {
        Random rand = new Random();
        return mean + variance*rand.nextGaussian();
    }
}
