package com.example.skaic;

import java.util.ArrayList;

public class Paskola {
    protected double kaina;
    protected double palukanos;
    protected int metai;
    protected int menesiai;
    int atidejimas;
    int atidejimo_data;
    public Paskola(double kaina, double palukanos, int metai, int menesiai, int atidejimas, int atidejimo_data) {
        this.kaina = kaina;
        this.palukanos = (palukanos/100.0) / 12.0;
        this.metai = metai;
        this.menesiai = menesiai;
        this.atidejimas = atidejimas;
        this.atidejimo_data = atidejimo_data;
    }
    public double getMokejimas(){
        double mokejimas = kaina * palukanos + kaina / (metai * 12 + menesiai);
        return mokejimas;
    }
}

