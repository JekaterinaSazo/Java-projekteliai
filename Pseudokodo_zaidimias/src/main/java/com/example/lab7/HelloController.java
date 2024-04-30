package com.example.lab7;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TextArea codeArea;
    Processing p;

    public void codeRunner(ActionEvent actionEvent) throws InterruptedException {
        String[] lines = codeArea.getText().split("\n");
        p.runCode(lines);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        p = new Processing();
        Thread t = new Thread(p);
        t.start();
    }
}