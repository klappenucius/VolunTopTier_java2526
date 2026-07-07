package com.voluntoptier.project.core;

import com.voluntoptier.project.entities.*;
import com.voluntoptier.project.exceptions.ObjectNotFoundException;
import com.voluntoptier.project.core.Pipeline;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class SearchService {

    private SearchService() {}

    public static List<Project> searchProjects(
            User actor,
            String query,
            Set<Project> projects
    ) throws ObjectNotFoundException {

        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(projects, "projects");

        String q = normalize(query);

        List<Project> results = new Pipeline<>(projects)
                .filter(p -> p != null && p.getName() != null
                        && p.getName().toLowerCase().contains(q))
                .toList();

        if (results.isEmpty()) {
            throw new ObjectNotFoundException("No project matches: " + query);
        }
        return results;
    }

    public static List<User> searchUsers(
            User actor,
            String query,
            Set<User> users
    ) throws ObjectNotFoundException {

        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(users, "users");

        // Permission rule: only admins may search users
        if (!(actor instanceof Administrator)) {
            throw new SecurityException("Only administrators can search users.");
        }

        String q = normalize(query);

        List<User> results = new Pipeline<>(users)
                .filter(u -> u != null
                        && ((u.getFirstName() != null && u.getFirstName().toLowerCase().contains(q))
                        || (u.getLastName() != null && u.getLastName().toLowerCase().contains(q))))
                .toList();

        if (results.isEmpty()) {
            throw new ObjectNotFoundException("No user matches: " + query);
        }
        return results;
    }

    private static String normalize(String query) {
        if (query == null) return "";
        return query.trim().toLowerCase();
    }
}
