package database;
import ui.*;

import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
public class ClassManagement {
    private static String course_no = "";
    private static int section = -1;
    private static String term = "";
    public static String current_active_class= "";        // This should be a result from sql (?)
    public static int current_active_section = -1;
    public static String current_active_term = "";
    public static void chooseOption(int option, Scanner kb) throws SQLException {
        UserApp.newLine();

        switch (option) {
            case(5):
                break;
            case(1):
                System.out.println( "Please give the necessary information;");
                System.out.print("Class number: ");
                String course_no = kb.nextLine();
                System.out.print("Section number: ");
                int section_no = Integer.parseInt(kb.nextLine());
                System.out.print("Term: ");
                String term = kb.nextLine();
                System.out.print("Description: ");
                String description = kb.nextLine();

                createClass(course_no,section_no,term,description);
                break;
            case(2):

                listClasses();
                break;
            case(3):
                //Get related values from database
                activateClass(kb);    //put those related values in the parameter
                break;
            case(4):
                showCurrentActivateClass();
                break;
        }
        UserApp.successfulProcess();
            }



    //parameters will come from table
    private static void createClass(String course_no, int section_no, String term, String description) {
        System.out.println("creating a class");

        String SQL = "INSERT INTO Class(course_no,section_no,term,description) "
                + "VALUES(?,?,?,?)";
        long id = 0;
        try (Connection conn = UserApp.connect();
             PreparedStatement ps = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, course_no);
            ps.setInt(2, section_no);
            ps.setString(3, term);
            ps.setString(4, description);

            int affectedRows = ps.executeUpdate();
            System.out.println("Affected rows:" + affectedRows);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void listClasses() throws SQLException {
        System.out.println("Listing Classes");
        UserApp.newLine();

        Connection conn = UserApp.connect();
        Statement statement = conn.createStatement();
        String sql = "SELECT * FROM Class;";
        ResultSet resultSet = statement.executeQuery(sql);

        System.out.printf("course_no | section_no | term | description\n");
        while(resultSet.next()) {

            String course_no = resultSet.getString("course_no");
            int section_no = resultSet.getInt("section_no");
            String term = resultSet.getString("term");
            String description = resultSet.getString("description");

            System.out.printf("%s | %d | %s | %s\n",course_no,section_no,term,description);
            UserApp.newLine();
        }

    }

    private static void activateClass(Scanner kb) throws SQLException {

        System.out.println( "Please give the necessary information;\ncourse_no | term | section");
        String info = kb.nextLine();
        String [] info_splitted = info.split(" ");
        int len = info_splitted.length;
        String SQL = "";

        switch (len) {
            case(1):
                SQL = "SELECT course_no FROM Class WHERE course_no = ?";
                break;
            case(2):
                SQL = "SELECT course_no FROM Class WHERE course_no = ? AND term = ?";
                break;
            case(3):
                SQL = "SELECT course_no FROM Class WHERE course_no = ? AND term = ? AND section_no = ?";
                break;
        }

        if(len > 0) {
            course_no = info_splitted[0];
            if(len < 3 && !checkSections(SQL, len)) {
                UserApp.warningLine();
                System.out.println("TWO SECTIONS");
                return;}
        }
        if(len > 1) {
            term = info_splitted[1];
        }
        if(len > 2) {
            section = Integer.parseInt(info_splitted[2]);
        }

        /*switch (len) {
            case(1):
                course_no = info_splitted[0];
                SQL = "SELECT course_no FROM Class WHERE course_no = ?";
                if(!checkSections(SQL, len)) {

                    System.out.println("TWO SECTIONS");
                    return;}
                break;
            case(2):
                course_no = info_splitted[0];
                term = info_splitted[1];
                SQL = "SELECT course_no FROM Class WHERE course_no = ? AND term = ?";
                if(!checkSections(SQL, len))
                    return;
                break;
            case(3):
                course_no = info_splitted[0];
                term = info_splitted[1];
                section = Integer.parseInt(info_splitted[2]);
                SQL = "SELECT course_no FROM Class WHERE course_no = ? AND term = ? AND section_no = ?";
                break;
        }*/

        if(doesClassExists(SQL, len)) {
            SQL = "SELECT term, section_no FROM Class WHERE course_no = ?";
            current_active_class = course_no;
            setClassDetails(SQL);
        }
        else {
            System.out.println("error or class DNE");
        }
    }

    private static void setClassDetails(String SQL) throws SQLException {
        Connection conn = UserApp.connect();
        PreparedStatement ps = conn.prepareStatement(SQL);

        ps.setString(1,current_active_class);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {

            current_active_term = rs.getString("term");
            current_active_section= rs.getInt("section_no");
        }
    }

    private static boolean doesClassExists(String SQL, int len) throws SQLException {
        Connection conn = UserApp.connect();

        PreparedStatement ps = conn.prepareStatement(SQL);

        if(len > 0)
            ps.setString(1, course_no);
        if(len > 1)
            ps.setString(2,term);
        if(len > 2)
            ps.setInt(3, section);

        ResultSet rs = ps.executeQuery();

        return rs.next();
    }

    private static boolean checkSections(String SQL, int len) throws SQLException {

        Connection conn = UserApp.connect();
        Statement statement = conn.createStatement();
        PreparedStatement ps = conn.prepareStatement(SQL);

        if(len > 0) {
        ps.setString(1, course_no);
        }
        if(len > 1) {
            ps.setString(2,term);
        }

        ResultSet rs = ps.executeQuery();
        int count = 0;
        while(rs.next()) {
            count++;
        }
        return count < 2;
    }

    private static void showCurrentActivateClass() {
        System.out.printf("Current active class is: %s | %d | %s%n", current_active_class, current_active_section, current_active_term);
    }
}

