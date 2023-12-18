package database;

import ui.UserApp;

import java.sql.*;
import java.util.Scanner;

public class GradeReport {

    //takes necessary info from the user for option, then starts the process.
    public static void chooseOption(int option, Scanner kb) throws SQLException {
        UserApp.newLine();
        switch (option) {
            case(5):
                break;
            case(1):
                System.out.println("Please give the necessary information;");
                System.out.print("Username of the Student: ");
                String username = kb.nextLine();
                studentGrades(username);
                break;
            case(2):
                showGradebook();
                break;

        }
        UserApp.successfulProcess();
    }

    //Lists the all grades related to current class
    private static void showGradebook() throws SQLException {
        String SQL = "SELECT sg.id, s.username,s.name, grade, assignment_name, course_no FROM StudentsGrades sg\n" +
                "LEFT JOIN Student s on sg.id = s.id WHERE course_no = ? ORDER BY sg.category";
        Connection conn = UserApp.connect();
        PreparedStatement ps = conn.prepareStatement(SQL);

        ps.setString(1,ClassManagement.current_active_class);
        ResultSet rs = ps.executeQuery();
        String course_no = "";
        if(rs.next())
            course_no = rs.getString("course_no");
        System.out.println("Classes Grades: Student ID | Student Name| Username | Assignment Name | Grade for Course: " +course_no);

        while(rs.next()) {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String s_name = rs.getString("name");
            String a_name = rs.getString("assignment_name");
            double grade = rs.getDouble("grade");

//System.out.printf("%26d | %-11s | %s | %s | %f\n",id,username,s_name, a_name, grade);
            System.out.printf("%26d | %s | %s | %s | %.2f\n",id,username,s_name, a_name, grade);
        }
    }

    //Lists all grades of the given student, ordered by category.
    private static void studentGrades(String username) throws SQLException {
        String SQL = "SELECT sg.grade, sg.assignment_name, sg.course_no, a.category FROM StudentsGrades sg\n" +
                "LEFT JOIN Assignment a on sg.assignment_name = a.name WHERE id = ? order by a.category;";

        int student_id = StudentManagment.lookupStudentIdByUsername(username);

        Connection conn = UserApp.connect();
        PreparedStatement ps = conn.prepareStatement(SQL);

        ps.setInt(1, student_id);
        ResultSet rs = ps.executeQuery();
        System.out.println("Student Grades: Assignment Name | Username | Grade | Course Number | Category");
        while(rs.next()) {
            String a_name = rs.getString("assignment_name");
            double grade = rs.getDouble("grade");
            String course_no = rs.getString("course_no");
            String category = rs.getString("category");

            System.out.printf("%31s | %s | %.2f | %s | %s \n", a_name, username, grade, course_no, category);
        }
    }

}
