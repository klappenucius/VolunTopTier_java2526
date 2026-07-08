package com.voluntoptier.project.entities;

import java.time.LocalDate;

public class Project extends DBitem {

    protected String name;
    protected Address address;
    protected Integer totalHours;
    protected Integer hoursWorked;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected int volunteersNeeded;

    private Project(ProjectBuilder builder) {
        super(builder.id);
        this.name = builder.name;
        this.address = builder.address;
        this.totalHours = builder.totalHours;
        this.hoursWorked = builder.hoursWorked;
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

    public Integer getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Integer hoursWorked) { this.hoursWorked = hoursWorked; }

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
                ", hours worked: " + hoursWorked +
                ", start date: " + startDate +
                ", end date: " + endDate +
                ", number of volunteers: " + volunteersNeeded;
    }

    public void print() {
        System.out.println(this.toString());
    }

    public static class ProjectBuilder {
        private final String id;
        private final String name;
        private final Integer totalHours;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final int volunteersNeeded;

        private Address address;
        private Integer hoursWorked = 0;

        public ProjectBuilder(String id, String name, Integer totalHours, LocalDate startDate, LocalDate endDate, int volunteersNeeded) {
            this.id = id;
            this.name = name;
            this.totalHours = totalHours;
            this.startDate = startDate;
            this.endDate = endDate;
            this.volunteersNeeded = volunteersNeeded;
        }

        public ProjectBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public ProjectBuilder hoursWorked(Integer hoursWorked) {
            this.hoursWorked = hoursWorked;
            return this;
        }

        public Project build() {
            return new Project(this);
        }
    }
}
