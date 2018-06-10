package org.itstep;

import java.sql.*;
import java.util.Scanner;

public class Application {

    private static final String GROUP_NAME_REGEXP = "[\\w]{1,10}";
    private static final String INPUT_ERROR = "Input error";
    private static final String ACTION_NUMBER_REGEXP = "[1-8]";
    private static final String NAME_REGEXP = "[A-ЩЭ-ЯA-Z][а-яa-z]+";
    private static final String AGE_REGEXP = "[0-9]{2}";
    private static final String EMAIL_REGEXP = "([^.][a-zA-Z0-9._-]+)@([^.@])(.+)\\.[a-z]{2,6}";

    private static final String MENU = "Enter a number corresponding to an action:\n" +
            "\t1 - Add a group\n" +
            "\t2 - Add a student\n" +
            "\t3 - Show list of students\n" +
            "\t4 - Show list of groups\n" +
            "\t5 - Find a student by name\n" +
            "\t6 - Remove a student\n" +
            "\t7 - Remove a group\n" +
            "\t8 - Exit";

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/academy" +
            "?characterEncoding=UTF-8&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private static final String DROP_STUDENT = "DROP TABLE IF EXISTS student\n";
    private static final String DROP_GROUP = "DROP TABLE IF EXISTS `group`\n";

    private static final String CREATE_STUDENT = "CREATE TABLE IF NOT EXISTS student\n" +
            "(\n" +
            "   id INT PRIMARY KEY AUTO_INCREMENT,\n" +
            "   first_name VARCHAR(255) NOT NULL,\n" +
            "   last_name VARCHAR(255) NOT NULL,\n" +
            "   age INT,\n" +
            "   email VARCHAR(255) NOT NULL UNIQUE,\n" +
            "   group_id INT,\n" +
            "   CONSTRAINT first_last_unique UNIQUE (first_name, last_name)\n" +
            ")";

    private static final String CREATE_GROUP = "CREATE TABLE IF NOT EXISTS `group`\n" +
            "(\n" +
            "   id INT PRIMARY KEY AUTO_INCREMENT,\n" +
            "   name VARCHAR(255) NOT NULL UNIQUE\n" +
            ")\n";

    private static final String ALTER_STUDENT = "ALTER TABLE student \n" +
            "ADD CONSTRAINT student_group_fk FOREIGN KEY (group_id) REFERENCES `group`(id)\n";
    public static final String ENTER_A_STUDENT_FIRST_NAME = "Enter a student first name. Example: Mykola";
    public static final String ENTER_A_STUDENT_LAST_NAME = "Enter a student last name. Example: Muntian";
    public static final String ENTER_STUDENT_AGE = "Enter student's age";
    public static final String ENTER_STUDENT_EMAIL = "Enter student's email";
    public static final String ENTER_A_GROUP_NAME = "Enter a group name. Example: group_1";


    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASS)) {
            Statement stmt = conn.createStatement();

            initDatabase(stmt);

            System.out.println("Connected");

            //addGroup(stmt);
//            ResultSet result = stmt.executeQuery("SELECT * FROM `group`");
//            while (result.next()) {
//                System.out.format("%d\t%s%n", result.getInt(1),
//                        result.getString("name"));
//            }

            performAction(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initDatabase(Statement stmt) throws SQLException {
        System.out.println(DROP_STUDENT);
        stmt.execute(DROP_STUDENT);

        System.out.println(CREATE_STUDENT);
        stmt.execute(CREATE_STUDENT);

        System.out.println(DROP_GROUP);
        stmt.execute(DROP_GROUP);

        System.out.println(CREATE_GROUP);
        stmt.execute(CREATE_GROUP);

        System.out.println(ALTER_STUDENT);
        stmt.execute(ALTER_STUDENT);
    }

    private static String dataInput(String message, String regExp) {
        while (true) {
            System.out.println(message);
            Scanner input = new Scanner(System.in);
            if (input.hasNext(regExp)) {
                return input.nextLine();
            } else {
                System.out.println(INPUT_ERROR);
            }
        }
    }

    public static int selectAction() {
        String input = dataInput(MENU, ACTION_NUMBER_REGEXP);
        return Integer.parseInt(input);
    }


    public static void performAction(Statement stmt) throws SQLException {
        while (true) {
            switch (selectAction()) {
                case 1:
                    addGroup(stmt);
                    break;
                case 2:
                    addStudent(stmt);
                    break;
                case 3:
                    studentListDisplay(stmt);
                    break;
                case 4:
                    groupListDisplay(stmt);
                    break;
                case 5:
                    findStudentByName(stmt);
                    break;
                case 6:
                    removeStudent(stmt);
                    break;
                case 7:
                    removeGroup(stmt);
                    break;
                case 8:
                    return;
                default:
                    System.out.println("incorrect argument");
            }
        }
    }

    private static void exit() {
        return;
    }

    private static void removeGroup(Statement stmt) throws SQLException {
        String groupName = dataInput(ENTER_A_GROUP_NAME, GROUP_NAME_REGEXP);
        String sql = String.format("DELETE FROM `group` WHERE name='%s'", groupName);
        stmt.executeUpdate(sql);
    }

    private static void removeStudent(Statement stmt) throws SQLException {
        String firstName = dataInput(ENTER_A_STUDENT_FIRST_NAME, NAME_REGEXP);
        String lastName = dataInput(ENTER_A_STUDENT_LAST_NAME, NAME_REGEXP);
        String sql = String.format("DELETE FROM student WHERE first_name='%s' AND last_name='%s'", firstName, lastName);
        stmt.executeUpdate(sql);
    }

    private static void findStudentByName(Statement stmt) throws SQLException {
        String firstName = dataInput(ENTER_A_STUDENT_FIRST_NAME, NAME_REGEXP);
        String lastName = dataInput(ENTER_A_STUDENT_LAST_NAME, NAME_REGEXP);
        String sql = String.format("SELECT FROM student WHERE first_name='%s' AND last_name='%s'", firstName, lastName);

//        stmt.executeQuery("SELECT * FROM `group`");

        ResultSet result = stmt.executeQuery(sql);
        System.out.println("Student by name " + result);
        while (result.next()) {
            System.out.format("%d\t%s%n", result.getInt(1),
                    result.getString("name"));
        }
    }

    private static void groupListDisplay(Statement stmt) throws SQLException {
        System.out.println("Group list:");
        ResultSet result = stmt.executeQuery("SELECT * FROM `group`");
        while (result.next()) {
            System.out.format("%d\t%s%n", result.getInt(1),
                    result.getString("name"));
        }
    }

    private static void studentListDisplay(Statement stmt) throws SQLException {
        System.out.println("student list display");
        ResultSet result = stmt.executeQuery("SELECT * FROM `group`");
        while (result.next()) {
            System.out.format("%d\t%s%n", result.getInt(1),
                    result.getString("first_name"));
        }
    }

    private static void addGroup(Statement stmt) throws SQLException {
        String groupName = dataInput("Enter a group name. Example: group_1", GROUP_NAME_REGEXP);
        String sql = String.format("INSERT INTO `group`(name) VALUES('%s')", groupName);
        stmt.executeUpdate(sql);
    }


    private static void addStudent(Statement stmt) throws SQLException {
        String firstName = dataInput(ENTER_A_STUDENT_FIRST_NAME, NAME_REGEXP);
        String lastName = dataInput(ENTER_A_STUDENT_LAST_NAME, NAME_REGEXP);
        int age = Integer.parseInt(dataInput(ENTER_STUDENT_AGE, AGE_REGEXP));
        String email = dataInput(ENTER_STUDENT_EMAIL, EMAIL_REGEXP);
        String groupName = dataInput(ENTER_A_GROUP_NAME, GROUP_NAME_REGEXP);
        int groupId = Integer.parseInt(getGroupIdByName(stmt, groupName));

        String sql = String.format("INSERT INTO student (first_name, last_name, age, email, group_id)" +
                " VALUES('%s','%s', '%d', '%s', '%d')", firstName, lastName, age, email, groupId);
        stmt.executeUpdate(sql);
    }

    private static String getGroupIdByName(Statement stmt, String groupName) throws SQLException {
        String sql = String.format("SELECT * FROM `group` WHERE name='%s'", groupName);
        ResultSet result = stmt.executeQuery(sql);
        return result.getString("id");
    }
}
