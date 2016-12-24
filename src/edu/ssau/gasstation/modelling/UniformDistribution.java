package edu.ssau.gasstation.modelling;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Sasha on 30.11.2016.
 */
public class UniformDistribution implements ProbabilityDistribution {
    private double a;
    private double b;
    public UniformDistribution(double a, double b){
        this.a = a;
        this.b = b;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    @Override
    public double generateValue() {
        return ThreadLocalRandom.current().nextDouble(a,b+1);
    }
}
