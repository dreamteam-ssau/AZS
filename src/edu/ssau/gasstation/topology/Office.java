package edu.ssau.gasstation.topology;

/**
 * Created by andrey on 04.12.16.
 */
public class Office implements TopologyItem{
    private double cashAmount;

    public Office() {
        this.cashAmount = 0;
    }

    public void payForFuel(int fuelID, double volume){
        //todo запрос из бд стоимости соответствующего типа топлива
        //todo вызов инкасации при достежении размера кассы в 700 000 рублей
    }

    private void callEncashment(){
        //todo информировать о вызови инкасации (либо отрисовка приезда инкасации)
    }
}
