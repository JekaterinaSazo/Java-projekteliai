package com.example.skaic;

import java.util.ArrayList;

public class Anuitine extends Paskola{
    public Anuitine(double kaina, double palukanos, int metai, int menesiai, int atidejimas, int atidejimo_data) {
        super(kaina, palukanos, metai, menesiai, atidejimas, atidejimo_data);
    }
    ArrayList<ArrayList<Double>> anuitine(){
        ArrayList<ArrayList<Double>> returned = new ArrayList<>();
        ArrayList<Double> total = new ArrayList<>();
        ArrayList<Double> interest = new ArrayList<>();
        ArrayList<Double> payments = new ArrayList<>();
        ArrayList<Double> remainder = new ArrayList<>();
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
        t = (super.kaina * super.palukanos) / (1 - Math.pow((1 + super.palukanos),-1 * ((super.metai * 12) + super.menesiai)));
        i = super.kaina * super.palukanos;
        p = t - i;
        r = super.kaina - p;
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
            i = r * palukanos;
            p = t - i;
            r -= p;
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
    // t - bendras mokejimas, i - palukanos, p - paskolos atmokejimas, r - paskolos likutis
    @Override
    public double getMokejimas(){
        double mokejimas = (super.kaina * super.palukanos) / (1 - Math.pow((1 + super.palukanos),-1 * ((super.metai * 12) + super.menesiai)));
        return Math.round(mokejimas * 100) / 100.0;
    }
}
