package com.example.skaic;

import java.util.ArrayList;

public class Linijine extends Paskola{
    public Linijine(double kaina, double palukanos, int metai, int menesiai, int atidejimas, int atidejimo_data) {
        super(kaina, palukanos, metai, menesiai, atidejimas, atidejimo_data);
    }
    ArrayList<ArrayList<Double>> linijine(){
        ArrayList<ArrayList<Double>> returned = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> total = new ArrayList<Double>();
        ArrayList<Double> interest = new ArrayList<Double>();
        ArrayList<Double> payments = new ArrayList<Double>();
        ArrayList<Double> remainder = new ArrayList<Double>();
        double t, i, p, r;
        if(atidejimo_data == 0){
            for(int j = 0; j < atidejimas; j++){
                p = 0.0;
                i = super.kaina * super.palukanos;
                t = i;
                r = super.kaina;
                total.add(Math.round(t*100.0)/100.0);
                interest.add(Math.round(i*100.0)/100.0);
                payments.add(Math.round(p*100.0)/100.0);
                remainder.add(Math.round(r*100.0)/100.0);
            }
        }
        i = super.kaina * super.palukanos;
        p = super.kaina / ((super.metai * 12) + super.menesiai);
        r = super.kaina - p;
        t = i + p;
        total.add(Math.round(t*100.0)/100.0);
        interest.add(Math.round(i*100.0)/100.0);
        payments.add(Math.round(p*100.0)/100.0);
        remainder.add(Math.round(r*100.0)/100.0);
        if(atidejimo_data == 1){
            for(int j = 0; j < atidejimas; j++){
                total.add(Math.round((r * super.palukanos)*100.0)/100.0);
                interest.add(Math.round((r * super.palukanos)*100.0)/100.0);
                payments.add(0.0);
                remainder.add(Math.round(r*100.0)/100.0);
            }
        }
        for(int j = 1; j < ((super.metai * 12) + super.menesiai); j++){
            if(atidejimo_data == j){
                for(int k = 0; k < atidejimas; k++){
                    total.add(Math.round((r * super.palukanos)*100.0)/100.0);
                    interest.add(Math.round((r * super.palukanos)*100.0)/100.0);
                    payments.add(0.0);
                    remainder.add(Math.round(r*100.0)/100.0);
                }
            }
            i = r * super.palukanos;
            r -= p;
            t = i + p;
            total.add(Math.round(t*100.0)/100.0);
            interest.add(Math.round(i*100.0)/100.0);
            payments.add(Math.round(p*100.0)/100.0);
            remainder.add(Math.round(r*100.0)/100.0);
        }
        returned.add(total);
        returned.add(interest);
        returned.add(payments);
        returned.add(remainder);
        return returned;
    }

    @Override
    public double getMokejimas() {
        return super.getMokejimas();
    }
}
