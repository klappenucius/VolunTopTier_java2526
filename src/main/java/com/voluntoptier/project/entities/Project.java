package com.voluntoptier.project.entities;

import java.time.LocalDate;

/**
 * A volunteering project. Hours worked on it are not stored here; they are the sum of the
 * approved hours of its project assignments.
 */
public class Project extends DBitem {

    protected String name;
    protected Address address;
    protected Integer totalHours;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int volunteersNeeded;

    private Project(ProjectBuilder builder) {
        super(builder.id);
        this.name = builder.name;
        this.address = builder.address;
        this.totalHours = builder.totalHours;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.volunteersNeeded = builder.volunteersNeeded;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Integer getTotalHours() { return totalHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getVolunteersNeeded() { return volunteersNeeded; }
    public void setVolunteersNeeded(int volunteersNeeded) { this.volunteersNeeded = volunteersNeeded; }

    @Override
    public String toString() {
        return name +
                ", total duration: " + totalHours +
                ", start date: " + startDate +
                ", end date: " + endDate +
                ", number of volunteers: " + volunteersNeeded;
    }

    @Override
    public void print() {
        System.out.println(this);
    }

    /**
     * Builds a {@link Project}; the constructor takes the required fields, the address is optional.
     */
    public static class ProjectBuilder {
        private final int id;
        private final String name;
        private final Integer totalHours;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final int volunteersNeeded;

        private Address address = null;

        /**
         * @param id               database id (0 for a project not saved yet)
         * @param name             project name
         * @param totalHours       total hours the project needs
         * @param startDate        first day of the project
         * @param endDate          last day of the project
         * @param volunteersNeeded number of volunteers the project needs
         */
        public ProjectBuilder(int id, String name, Integer totalHours, LocalDate startDate, LocalDate endDate,
                              int volunteersNeeded) {
            this.id = id;
            this.name = name;
            this.totalHours = totalHours;
            this.startDate = startDate;
            this.endDate = endDate;
            this.volunteersNeeded = volunteersNeeded;
        }

        /**
         * @param address where the project takes place
         * @return this builder
         */
        public ProjectBuilder address(Address address) {
            this.address = address;
            return this;
        }

        /**
         * @return the finished project
         */
        public Project build() {
            return new Project(this);
        }
    }
}
