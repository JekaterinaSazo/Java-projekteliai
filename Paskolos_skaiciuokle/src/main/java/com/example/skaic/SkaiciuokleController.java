package com.example.skaic;

import Duomenys.StaloSkaiciai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;

public class SkaiciuokleController {
    Paskola paskola;

    @FXML
    private Button calc;

    @FXML
    private ComboBox<String> grazgraf;

    @FXML
    private ComboBox<Integer> men;

    @FXML
    private ComboBox<Integer> met;

    @FXML
    private TextField metproc;

    @FXML
    private TextField pasksuma;

    @FXML
    private TextField res;
    @FXML
    private TableColumn<StaloSkaiciai, Integer> index;
    @FXML
    private TableColumn<StaloSkaiciai, Double> total;
    @FXML
    private TableColumn<StaloSkaiciai, Double> interest;
    @FXML
    private TableColumn<StaloSkaiciai, Double> returned;
    @FXML
    private TableColumn<StaloSkaiciai, Double> remainder;
    @FXML
    private TableView<StaloSkaiciai> skaiciaiTableView;
    @FXML
    private Spinner<Integer> atidejimas;
    @FXML
    private DatePicker data;

    @FXML
    public void initialize(){
        ObservableList<String> pasirinkimai = FXCollections.observableArrayList("Linijinė", "Anuitinė");
        grazgraf.setItems(pasirinkimai);
        ObservableList<Integer> menesiai = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        men.setItems(menesiai);
        ObservableList<Integer> metai = FXCollections.observableArrayList();
        atidejimas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 36));
        for(int i = 0; i <= 50; i++){
            metai.add(i);
        }
        met.setItems(metai);
    }
    @FXML
    public void Calculate(ActionEvent actionEvent) {
        Period period = Period.between(LocalDate.now(), data.getValue());
        int atidejimoData = period.getYears() * 12 + period.getMonths();
        int mokLaik = men.getValue() + (met.getValue() * 12);
        System.out.println(atidejimoData + " " + mokLaik);
        if(atidejimoData < mokLaik){
            if(grazgraf.getValue().equals("Linijinė")){
                paskola = new Linijine(Double.parseDouble(pasksuma.getText()), Double.parseDouble(metproc.getText()), met.getValue(), men.getValue(), atidejimas.getValue(), atidejimoData);
                res.setText(Double.toString(((Linijine)paskola).getMokejimas()));
                uzpildytiStala(((Linijine)paskola).linijine());
            } else if(grazgraf.getValue().equals("Anuitinė")){
                paskola = new Anuitine(Double.parseDouble(pasksuma.getText()), Double.parseDouble(metproc.getText()), met.getValue(), men.getValue(), atidejimas.getValue(), atidejimoData);
                res.setText(Double.toString(((Anuitine)paskola).getMokejimas()));;
                uzpildytiStala(((Anuitine)paskola).anuitine());
            }
        }
    }
    public void uzpildytiStala(ArrayList<ArrayList<Double>> rezultatas){
        ObservableList<StaloSkaiciai> stalas = FXCollections.observableArrayList();
        for(int i = 0; i < rezultatas.get(0).size(); i++){
            stalas.add(new StaloSkaiciai(i+1,rezultatas.get(0).get(i),rezultatas.get(1).get(i),rezultatas.get(2).get(i),rezultatas.get(3).get(i)));
        }
        index.setCellValueFactory(new PropertyValueFactory<>("index"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        interest.setCellValueFactory(new PropertyValueFactory<>("interest"));
        returned.setCellValueFactory(new PropertyValueFactory<>("payments"));
        remainder.setCellValueFactory(new PropertyValueFactory<>("remainder"));
        skaiciaiTableView.setItems(stalas);
    }
}
