package com.voluntoptier.project.app;

import java.time.LocalDate;
import java.util.*;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
//import jdk.incubator.stream.Gatherers;
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("APPLICATION STARTED");
        Scanner sc = new Scanner(System.in);
        Set<Project> projects = new HashSet<>();
        Set<User> users = new HashSet<>();
    }
}