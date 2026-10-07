package com.voluntoptier.project.controllers;

import com.voluntoptier.project.entities.ProjectAssignment;
import com.voluntoptier.project.service.ProjectAssignmentService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class DashboardScreenController {
    @FXML
    private TableView<ProjectAssignment> projectAssignmentsTable;
    @FXML private TableColumn<ProjectAssignment, String> projectNameColumn;
    @FXML private TableColumn<ProjectAssignment, String> approvedVsTotalHoursColumn;
    @FXML private TableColumn<ProjectAssignment, Integer> pendingApprovalColumn;
    @FXML private TableColumn<ProjectAssignment, Void> logWorkColumn;
    @FXML private Label errorLabel;

    public ProjectAssignmentService projectAssignmentService;

    @FXML
    public void initialize() {
        projectNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProjectAssignment,String>,
                        ObservableValue<String>>() {
            public ObservableValue<String> call(
                    TableColumn.CellDataFeatures<ProjectAssignment, String> param) {
                return new ReadOnlyStringWrapper(
                        param.getValue().getProject().getName());
                }
            });

        approvedVsTotalHoursColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProjectAssignment, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(
                    TableColumn.CellDataFeatures<ProjectAssignment, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getHoursWorked().approvedHours() + " / " +
                        param.getValue().getExpectedHours());
            }
        });

        pendingApprovalColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProjectAssignment, Integer>,
                ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(
                    TableColumn.CellDataFeatures<ProjectAssignment, Integer> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getHoursWorked().pendingApproval());
            }

        });

        logWorkColumn.setCellFactory(new Callback<TableColumn<ProjectAssignment, Void>,
                TableCell<ProjectAssignment, Void>> () {
            public TableCell<ProjectAssignment, Void> call (TableColumn<ProjectAssignment, Void> param) {
                return new TableCell<ProjectAssignment, Void> () {
                    private Button logWorkBtn = new Button("Log work");
                    {
                        logWorkBtn.setOnAction(event -> {
                            ProjectAssignment projectAssignment = getTableView().getItems().get(getIndex());
                            onLogWorkClicked(projectAssignment);
                        });
                }
            };
        }
    });
    }

    private void onLogWorkClicked(ProjectAssignment projectAssignment) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/voluntoptier/project/view/logWork.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) projectAssignmentsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            errorLabel.setText("Failed to load main screen.");
        }
    }


}




