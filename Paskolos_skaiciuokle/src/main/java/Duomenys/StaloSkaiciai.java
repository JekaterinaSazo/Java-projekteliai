package Duomenys;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class StaloSkaiciai {
    SimpleIntegerProperty index;
    SimpleDoubleProperty total;
    SimpleDoubleProperty interest;
    SimpleDoubleProperty payments;
    SimpleDoubleProperty remainder;

    public StaloSkaiciai(int index, double total, double interest, double payments, double remainder) {
        this.index = new SimpleIntegerProperty(index);
        this.total = new SimpleDoubleProperty(total);
        this.interest = new SimpleDoubleProperty(interest);
        this.payments = new SimpleDoubleProperty(payments);
        this.remainder = new SimpleDoubleProperty(remainder);
    }

    public double getTotal() {
        return total.get();
    }

    public SimpleDoubleProperty totalProperty() {
        return total;
    }

    public void setTotal(double total) {
        this.total.set(total);
    }

    public double getInterest() {
        return interest.get();
    }

    public SimpleDoubleProperty interestProperty() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest.set(interest);
    }

    public double getPayments() {
        return payments.get();
    }

    public SimpleDoubleProperty paymentsProperty() {
        return payments;
    }

    public void setPayments(double payments) {
        this.payments.set(payments);
    }

    public double getRemainder() {
        return remainder.get();
    }

    public SimpleDoubleProperty remainderProperty() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder.set(remainder);
    }

    public int getIndex() {
        return index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }
}
