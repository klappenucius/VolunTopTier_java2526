package com.voluntoptier.project.controllers;

import com.voluntoptier.project.app.AppContext;
import com.voluntoptier.project.entities.User;
import com.voluntoptier.project.service.AuthService;
import com.voluntoptier.project.service.UserService;
import com.voluntoptier.project.utils.SessionUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            // add paths to different options/screens, depending on role
        };
    }
}

