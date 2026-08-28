package com.voluntoptier.project.repository;

import java.sql.Connection;

public class WorklogCrud implements Crud{

    private final Connection connection;
    private final ProjectAssignmentCrud projectAssignmentCrud;

    public WorklogCrud(Connection connection, ProjectAssignmentCrud projectAssignmentCrud) {
        this.connection = connection;
        this.projectAssignmentCrud = projectAssignmentCrud;
    }

    
}
