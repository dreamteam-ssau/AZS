package edu.ssau.gasstation.topology;

/**
 * Created by andrey on 04.12.16.
 */
public class Office implements TopologyItem {
  private double cashAmount;
  private double MAX = 70;
  private double PRICE = 2;

  public Office() {
    this.cashAmount = 0;
  }

  public void payForFuel(int fuelID, double volume) {
    cashAmount += volume * (PRICE);
    //todo запрос из бд стоимости соответствующего типа топлива
    //todo вызов инкасации при достежении размера кассы в 700 000 рублей
  }

  private void callEncashment() {
    //todo информировать о вызови инкасации (либо отрисовка приезда инкасации)
  }

  public boolean isFull() {
    if (cashAmount >= MAX) {
      return true;
    } else {
      return false;
    }
  }

  public double getCashAmount() {
    return cashAmount;
  }

  public void setCashAmount(double cashAmount) {
    this.cashAmount = cashAmount;
  }
}
