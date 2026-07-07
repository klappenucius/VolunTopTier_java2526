package com.voluntoptier.project.entities;

import java.time.LocalDate;

/**
 * Represents a volunteer project, including its basic metadata such as
 * ID, name, location, duration, and start/end dates.
 * <p>
 * The class uses the Builder design pattern to ensure flexible and
 * safe object construction through {@link ProjectBuilder}.
 * </p>
 */
public class Project {
    private int id;
    private String name;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationHours;
    private Double additionalSubsidy;

    /**
     * Builder class for {@link Project}. Provides a fluent API for
     * constructing {@link Project} instances.
     */
    public static class ProjectBuilder {
        private int id;
        private String name;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        private int durationHours;
        private Double additionalSubsidy;

        public ProjectBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ProjectBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProjectBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ProjectBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public ProjectBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public ProjectBuilder durationHours(int durationHours) {
            this.durationHours = durationHours;
            return this;
        }

        public ProjectBuilder additionalSubsidy(Double additionalSubsidy) {
            this.additionalSubsidy = additionalSubsidy;
            return this;
        }

        /**
         * Builds and returns a {@link Project} object based on the
         * provided builder parameters.
         *
         * @return a fully constructed {@link Project} instance
         */
        public Project build() {
            return new Project(this);
        }
    }

    // Private constructor — only the builder can call this
    private Project(ProjectBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.location = builder.location;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.durationHours = builder.durationHours;
        this.additionalSubsidy = builder.additionalSubsidy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public Double getAdditionalSubsidy() {
        return additionalSubsidy;
    }
    public void setAdditionalSubsidy(Double additionalSubsidy) {
        this.additionalSubsidy = additionalSubsidy;
    }

    @Override
    public String toString() {
        return "Project{" +
                "(1) id=" + id +
                ", (2) name='" + name + '\'' +
                ", (3) location='" + location + '\'' +
                ", (4) startDate=" + startDate +
                ", (5) endDate=" + endDate +
                ", (6) durationHours=" + durationHours +
                '}';
    }

    @Override
    public boolean equals (Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Project project = (Project) object;
        if (id != project.id) return false;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
