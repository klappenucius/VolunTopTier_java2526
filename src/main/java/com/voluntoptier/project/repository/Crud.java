package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;
import com.voluntoptier.project.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;


public sealed interface Crud permits UserCrud, ProjectCrud, IncentiveCrud, ProviderCrud, AddressCrud, ProjectAssignmentCrud, IncentiveAssignmentCrud {

    DBitem add(DBitem item);
    DBitem getById(int id);
    boolean update(DBitem item);
    boolean delete(int id);
}

