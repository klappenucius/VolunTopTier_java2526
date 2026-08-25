package com.voluntoptier.project.app;

import java.sql.Connection;
import java.util.*;

import com.voluntoptier.project.controllers.LoginController;
import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.exceptions.*;
import com.voluntoptier.project.utils.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private AppContext appContext;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Connection connection = DatabaseUtil.connectToDatabase();

        appContext = new AppContext(connection);

        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));

        LoginController loginController = new LoginController();
        loginController.userService = appContext.userService;
        loader.setController(loginController);

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Voluntoptier");
        stage.show();
    }

    public static void main(String[] args) {
        logger.info("APPLICATION STARTED");
        launch(args);
    }
}