package com.voluntoptier.project.controllers;

import com.voluntoptier.project.app.AppContext;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.service.AuthService;
import com.voluntoptier.project.service.UserService;
import com.voluntoptier.project.utils.SessionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    public UserService userService;

    @FXML
    private void onLoginClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(AuthService.validateCreds(username, password)) {
            SessionUtil.setCurrentUser(userService.fetchByUsername(username));
            loadMainScreen();
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/voluntoptier/project/view/mainScreen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            errorLabel.setText("Failed to load main screen.");
        }
    }
}

