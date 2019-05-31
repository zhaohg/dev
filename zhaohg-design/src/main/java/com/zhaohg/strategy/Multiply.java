package com.zhaohg.strategy;

public class Multiply extends AbstractCalculator implements ICalculator {

    @Override
    public int calculate(String exp) {
        int arrayInt[] = split(exp, "\\*");
        return arrayInt[0] * arrayInt[1];
    }

    @Override
    public int calculate(int num1, int num2) {
        return 0;
    }
}
