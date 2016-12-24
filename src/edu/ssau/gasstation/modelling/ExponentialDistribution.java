package edu.ssau.gasstation.modelling;

import java.util.Random;

/**
 * Created by Sasha on 30.11.2016.
 */
public class ExponentialDistribution implements ProbabilityDistribution {
    private double rate;
    public ExponentialDistribution(double rate){
        this.rate = rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public double generateValue() {
        Random rand = new Random();
        return Math.log(1-rand.nextDouble())/(-rate);
    }
}
