package com.example.RouteCrafter.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class AboutMeController {
    @FXML
    public Button closebtn;
    public Button copyrepolink_button;
    public Text myname;

    @FXML
    protected void close() {
        Stage stage = (Stage) closebtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void copyRepoLink() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString("https://github.com/Yoinksawse/MetroSystemPA");
        clipboard.setContent(content);
    }
}