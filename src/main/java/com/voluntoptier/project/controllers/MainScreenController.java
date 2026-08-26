package com.voluntoptier.project.controllers;

import com.voluntoptier.project.entities.Role;
import com.voluntoptier.project.utils.PermissionsUtil;
import com.voluntoptier.project.utils.SessionUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainScreenController {

    @FXML private Button projectsBtn;
    @FXML
    private Button incentivesBtn;
    @FXML private Button logWorkBtn;
    @FXML private Button projectAssignmentsBtn;
    @FXML private Button approveWorklogsBtn;
    @FXML private Button projectAdministrationBtn;
    @FXML private Button userAdministrationBtn;

    @FXML
    public void initialize() {
        Role role = SessionUtil.getCurrentUser().getRole();

        projectAssignmentsBtn.setVisible(PermissionsUtil.isModerator(role));
        projectAssignmentsBtn.setManaged(PermissionsUtil.isModerator(role));

        approveWorklogsBtn.setVisible(PermissionsUtil.isModerator(role));
        approveWorklogsBtn.setManaged(PermissionsUtil.isModerator(role));

        projectAdministrationBtn.setVisible(PermissionsUtil.isAdmin(role));
        projectAdministrationBtn.setManaged(PermissionsUtil.isAdmin(role));

        userAdministrationBtn.setVisible(PermissionsUtil.isAdmin(role));
        userAdministrationBtn.setManaged(PermissionsUtil.isAdmin(role));
    }

    private void onProjectsClicked() {
        // route to the next screen which will handle search&display of projects
        // according to business logic
    }
    private void onIncentivesClicked() {
        // route to the next screen which will handle search&display of incentives
        // according to business logic
    }
    private void onLogMyWorkClicked() {
        // route to the next screen which will handle logging work
        // according to business logic
    }
    private void onProjectAssignmentsClicked() {
        // route to the next screen which will handle search&display of project assignments
        // according to business logic
    }
    private void onApproveWorklogsClicked() {
        // route to the next screen which will handle search&display of project assignments
        // according to business logic
    }
    private void onProjectAdministrationClicked() {
        // route to the next screen which will handle administration of projects (crud)
        // according to business logic
    }
    private void onUserAdministrationClicked() {
        // route to the next screen which will handle administration of users (crud)
        // according to business logic
    }
}
