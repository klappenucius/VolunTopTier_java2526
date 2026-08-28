package com.voluntoptier.project.repository;

import com.voluntoptier.project.entities.DBitem;


public sealed interface Crud permits UserCrud, ProjectCrud, AddressCrud, ProjectAssignmentCrud {

    DBitem add(DBitem item);
    DBitem getById(int id);
    boolean update(DBitem item);
    boolean delete(int id);
}

