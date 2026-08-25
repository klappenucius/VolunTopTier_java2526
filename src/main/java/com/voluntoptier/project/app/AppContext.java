package com.voluntoptier.project.app;

import com.voluntoptier.project.repository.AddressCrud;
import com.voluntoptier.project.repository.ProjectAssignmentCrud;
import com.voluntoptier.project.repository.ProjectCrud;
import com.voluntoptier.project.repository.UserCrud;
import com.voluntoptier.project.service.AddressService;
import com.voluntoptier.project.service.ChangeLogService;
import com.voluntoptier.project.service.ProjectAssignmentService;
import com.voluntoptier.project.service.ProjectService;
import com.voluntoptier.project.service.UserService;

import java.io.IOException;
import java.sql.Connection;

public class AppContext {
    public final ChangeLogService changeLogService;
    public final AddressService addressService;
    public final UserService userService;
    public final ProjectService projectService;
    public final ProjectAssignmentService projectAssignmentService;

    public AppContext(Connection connection) throws IOException {
        this.changeLogService = new ChangeLogService();

        AddressCrud addressCrud = new AddressCrud(connection);
        this.addressService = new AddressService(addressCrud, changeLogService);

        UserCrud userCrud = new UserCrud(connection, addressCrud);
        this.userService = new UserService(userCrud, changeLogService, addressService);

        ProjectCrud projectCrud = new ProjectCrud(connection, addressCrud);
        this.projectService = new ProjectService(projectCrud, changeLogService, addressService);

        ProjectAssignmentCrud projectAssignmentCrud = new ProjectAssignmentCrud(connection, userCrud, projectCrud);
        this.projectAssignmentService = new ProjectAssignmentService(
                projectAssignmentCrud, changeLogService, projectService, userService);
    }
}
