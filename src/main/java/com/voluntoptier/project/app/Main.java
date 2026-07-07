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

/**
 * The entry point of the Volunteer Project Management application.
 * <p>
 * This class handles input for creating projects and users, initializes
 * the data model, and triggers user interaction through the console.
 * </p>
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Main method that runs the application.
     * <p>
     * It prompts the user to input project and user data,
     * creates {@link Project}, {@link Administrator}, and {@link RegularUser}
     * objects, and then allows user interaction through action menus.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        logger.info("APPLICATION STARTED");
        Scanner sc = new Scanner(System.in);
        Set<Project> projects = new HashSet<>();
        Set<User> users = new HashSet<>();

        // adding code that deserializes objects from JSON files
        // and then serializes them back at the end


        //HARD-CODED VALUES FOR FASTER CHECKS:
        /*projects[0] = new Project.ProjectBuilder()
                .id(1)
                .name("Community Garden")
                .location("Springfield")
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 6, 15))
                .durationHours(120)
                .build();

        projects[1] = new Project.ProjectBuilder()
                .id(2)
                .name("Beach Cleanup")
                .location("Miami")
                .startDate(LocalDate.of(2025, 4, 10))
                .endDate(LocalDate.of(2025, 4, 20))
                .durationHours(40)
                .build();

        projects[2] = new Project.ProjectBuilder()
                .id(3)
                .name("Food Drive")
                .location("New York")
                .startDate(LocalDate.of(2025, 1, 5))
                .endDate(LocalDate.of(2025, 2, 1))
                .durationHours(60)
                .build();

        projects[3] = new Project.ProjectBuilder()
                .id(4)
                .name("Park Restoration")
                .location("Denver")
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 9, 30))
                .durationHours(200)
                .build();

        projects[4] = new Project.ProjectBuilder()
                .id(5)
                .name("School Supply Drive")
                .location("Seattle")
                .startDate(LocalDate.of(2025, 5, 15))
                .endDate(LocalDate.of(2025, 7, 15))
                .durationHours(100)
                .build();

        Address addr1 = new Address("Maple Street", 12, "Springfield", 12345, "USA");
        Address addr2 = new Address("Ocean Ave", 88, "Miami", 33101, "USA");
        Address addr3 = new Address("Broadway", 101, "New York", 10001, "USA");
        Address addr4 = new Address("Pine Lane", 77, "Denver", 80201, "USA");
        Address addr5 = new Address("Rainier Blvd", 54, "Seattle", 98101, "USA");

        Worklog[] wl1 = {
                new Worklog(projects[0], 10),
                new Worklog(projects[1], 5)
        };
        Worklog[] wl2 = {
                new Worklog(projects[2], 8),
                new Worklog(projects[4], 12)
        };
        Worklog[] wl3 = {
                new Worklog(projects[1], 7)
        };
        Worklog[] wl4 = {
                new Worklog(projects[3], 20),
                new Worklog(projects[4], 5)
        };
        Worklog[] wl5 = {
                new Worklog(projects[2], 15),
                new Worklog(projects[3], 25)
        };

        admins[0] = new Administrator(
                1,
                "Alice",
                "Johnson",
                "alice.johnson@voluntoptier.org",
                LocalDate.of(1985, 5, 20),
                addr1,
                wl1
        );

        admins[1] = new Administrator(
                2,
                "Bob",
                "Williams",
                "bob.williams@voluntoptier.org",
                LocalDate.of(1990, 9, 15),
                addr2,
                wl2
        );

        regularUsers[0] = new RegularUser(
                3,
                "Charlie",
                "Davis",
                "charlie.davis@voluntoptier.org",
                LocalDate.of(1995, 3, 10),
                addr3,
                wl3
        );

        regularUsers[1] = new RegularUser(
                4,
                "Diana",
                "Miller",
                "diana.miller@voluntoptier.org",
                LocalDate.of(1998, 8, 22),
                addr4,
                wl4
        );

        regularUsers[2] = new RegularUser(
                5,
                "Ethan",
                "Brown",
                "ethan.brown@voluntoptier.org",
                LocalDate.of(2001, 11, 5),
                addr5,
                wl5
        );*/

    }
}