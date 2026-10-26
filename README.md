VOLUNTOPTIER

PURPOSE

This is a web app for logging (volunteer) work done on projects.
Admins create users, projects as needed >
Moderators assign users (volunteers) to these projects >
Users log the hours they've worked on these projects on a daily basis >
Moderators check (approve) these logs + can see worklogs and stats for each project

BUSINESS LOGIC

Regular users:
  see their project assignments
  log their hours
  see their worklogs
  see & edit their personal information

Moderators (in addition to everything regular users can):
  see all users and project assignments
  approve/reject worklogs

Admins (in addition to everything regular users can):
  see & edit (create, update, delete) all users and projects

Every data change is recorded in a change log

ARCHITECTURE
(todo)

TECH STACK

Java 24
SpringBoot 3.5
Spring JDBC
H2 DB
Logback
Maven

RUNNING LOCALLY

Requirements: JDK 24 and Maven OR IntelliJ IDEA

1) Set the db password as an environment variable (DB_PASSWORD):
     In IntelliJ: Run > Edit Configurations > select the Spring Boot configuration > Environment Variables
2) Make sure data/credentials.txt exists; 1 line per user in the following format: username, sha56-hash-of-password
3) Start the app from project root: mvn spring-boot:run
