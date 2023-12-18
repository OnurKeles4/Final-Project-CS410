package database;

import ui.UserApp;

import java.sql.*;
import java.util.Scanner;

public class StudentManagment {
    //takes necessary info from the user for option, then starts the process.
    public static void chooseOption(int option, Scanner kb) throws SQLException {
        UserApp.newLine();
        switch (option) {
            case(6):
                break;
            case(1):
                System.out.println("Please give the necessary information;");
                System.out.println("Username of the Student");
                String username = kb.nextLine();
                System.out.println("Name of the Student");
                String name = kb.nextLine();

                addNewStudent(username, name);
                break;
            case(2):
                System.out.println("Please give the necessary information;");
                System.out.println("Username of the Student");
                username = kb.nextLine();
                enrollStudent(username);
                break;
            case(3):
                showAllStudents();
                break;
            case(4):
                System.out.println("Please give the necessary information;");
                System.out.println("Name of the Student");
                name = kb.nextLine();
                searchStudents(name);
                break;
            case(5):
                System.out.println("Please give the necessary information;");
                System.out.print("Username of the Student: ");
                username = kb.nextLine();
                System.out.print("Name of the Assignment: ");
                String assignment_name = kb.nextLine();
                double maxScoreOnAssignment = lookupMaxPossibleScoreForAssigment(assignment_name);
                System.out.printf("Please grade the student according to point of the Assignment %f: ", maxScoreOnAssignment);
                double grade = Double.parseDouble(kb.nextLine());


                gradeStudent(username, assignment_name, grade);
                break;
        }
        UserApp.successfulProcess();
    }


    //Search a student from the database with name.
    private static void searchStudents(String name) throws SQLException {

        String SQL = "SELECT name FROM Student WHERE name = ?";

        Connection con = UserApp.connect();
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString (1, name.toLowerCase());
        ResultSet rs = ps.executeQuery();


        while (rs.next()) {
            String searched_name = rs.getString("name");
            System.out.printf("Student named: %s\n", searched_name);
        }

    }
    //Show all the students
    private static void showAllStudents() throws SQLException {
        System.out.println("Showing All Students");
        UserApp.newLine();

        Connection conn = UserApp.connect();
        Statement statement = conn.createStatement();
        String sql = "SELECT * FROM Student;";
        ResultSet rs = statement.executeQuery(sql);

        System.out.printf("id | username | name\n");
        while(rs.next()) {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String name = rs.getString("name");

            System.out.printf("%d | %s | %s\n",id,username,name);
            UserApp.newLine();
        }
    }
    //Enroll an existing student to a new class.
    private static void enrollExstingStudent(String username) throws SQLException {
        String SQL = "UPDATE StudentsClasses SET course_no = ? WHERE id = ?";
            if (doesStudentExist(username))
            if(!isStudentEnrolled(username))  {
                try (
                        Connection con = UserApp.connect();
                        Statement statement = con.createStatement();
                        PreparedStatement ps = con.prepareStatement(SQL)) {

                        int student_id = lookupStudentIdByUsername(username);

                        ps.setString(1, ClassManagement.current_active_class);
                        ps.setInt(2, student_id);


                    int affectedRows = ps.executeUpdate();
                    System.out.println(affectedRows);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            else {
                System.out.println("This student already enrolled into this course");
            }
            else {
                System.out.println("This student is not in the database");
            }
    }
    //add a new student to the student table, checks that is student already exists or enrolled.
    private static void addNewStudent(String username, String name) throws SQLException {
        System.out.println("Adding a new Student");
        int isStudentEnrolled = doesStudentEnrolled(username);
        try {
            if(doesStudentExist(username)) {
                if (isStudentEnrolled == 0) {
                    UserApp.warningLine();
                    System.out.println("Warning! This Student already exists in the system, this" +
                            "prompt only enrolled this student into system");
                    UserApp.warningLine();

                    enrollExstingStudent(username);
                }
                else if(isStudentEnrolled == 1){
                    UserApp.warningLine();
                    System.out.println("Warning! This Student already exists in the system and student is enrolled" +
                            "to this class");
                    UserApp.warningLine();
                }
                else {
                    System.out.println("ERROR IN THE SYSTEM!");
                }
            }

            else{ enrollNewStudent(username, name); }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    //checks does the student enrolled to the current class. Returns -1 if error, 1, if enrolled, 0 if not.
    private static int doesStudentEnrolled(String username) throws SQLException {
        String SQL = "SELECT id FROM StudentsClasses WHERE id = ? AND course_no = ?";

        int student_id = lookupStudentIdByUsername(username);
        if(student_id == -1) {
            System.out.println("ERROR");
            return -1;
        }
        Connection con = UserApp.connect();
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setInt (1, student_id);
        ps.setString(2, ClassManagement.current_active_class);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return 1;            //Student exists
        }
        return 0;               //Student DNE
    }

    //Enroll a new student to the StudentsClasses table with id, takes username from the user then finds the id of that username.
    private static void enrollStudent(String username) throws SQLException {
        String SQL = "INSERT INTO StudentsClasses(id, course_no) "
                + "VALUES(?,?)";
        int isStudentEnrolled = doesStudentEnrolled(username);


        if(isStudentEnrolled == 0) {
            Connection con = UserApp.connect();
            Statement statement = con.createStatement();
            PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            int student_id = lookupStudentIdByUsername(username);
            if (student_id == -1) {
                System.out.println("ERROR");
                return;
            }

            ps.setInt(1, student_id);
            ps.setString(2, ClassManagement.current_active_class);

            int affectedRows = ps.executeUpdate();
            System.out.println("Affected Rows:" + affectedRows);
        }
        else if(isStudentEnrolled == 1){
            System.out.println("This student is already enrolled to this class");
        }
        else {
            System.out.println("Error");
        }
    }
    //enroll a new student. Both add new values to Student and StudentsClasses table.
    private static void enrollNewStudent(String username, String name) {
        String SQL = "INSERT INTO Student(username, name) "
                + "VALUES(?,?)";
        String SQL2 = "INSERT INTO StudentsClasses(id, course_no) "
                + "VALUES(?,?)";
        try (
                Connection con = UserApp.connect();
                Statement statement = con.createStatement();

                PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement ps2 = con.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, username);
                ps.setString(2, name);

            int affectedRows = ps.executeUpdate();
            int student_id = lookupStudentIdByUsername(username);
            ps2.setInt(1,student_id);
            ps2.setString(2,ClassManagement.current_active_class);
            int affectedRows2 = ps2.executeUpdate();
            // check the affected rows
            System.out.println(affectedRows +" and " + affectedRows2);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    //does student exists in student table
    public static boolean doesStudentExist(String username) throws SQLException {

        String SQL = "SELECT name FROM Student WHERE username = ?";
        Connection con = UserApp.connect();
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString (1, username.toLowerCase());
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return true;            //Student exists
        }
        return false;               //Student DNE
    }

    // is student enrolled to the current class.
    private static boolean isStudentEnrolled(String username) throws SQLException {
            String SQL = "SELECT course_no FROM StudentsClasses WHERE id = ?";

            Connection con = UserApp.connect();
            PreparedStatement ps = con.prepareStatement(SQL);
            int student_id = lookupStudentIdByUsername(username);
            ps.setInt(1, student_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String str = rs.getString("course_no");
                if (str.equals(ClassManagement.current_active_class))
                    return true;            //Student exists
            }

        return false;
    }

    //takes student username from the user. Looks to the table to get student's id.
    public static int lookupStudentIdByUsername(String username){
        String SQL = "SELECT id FROM Student WHERE username = ?";
        try {

            Connection con = UserApp.connect();
            Statement statement = con.createStatement();
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();

            if(rs.next())
                return rs.getInt("id");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    // Takes username of the student, assignment name, and grade for the assignment. Informs user about maximum limit for the assignment
    // checks if the student is already graded. If they are, update their grade, if not add a new value to StudentsGrades table.
    private static void gradeStudent(String username, String assignment_name, double grade) throws SQLException{

        System.out.println("Grading a Student");
        // Look up the student
        int student_id =  lookupStudentIdByUsername(username);
        System.out.println("Student ID: " + student_id);
        // Look up max points
        double maxScoreOnAssignment = lookupMaxPossibleScoreForAssigment(assignment_name);
        if(maxScoreOnAssignment < grade) {
            UserApp.warningLine();
            System.out.println("Warning! This grade is over point limit!");
        }

        System.out.println("Max possible grade: " + maxScoreOnAssignment);
        if(isStudentGraded(student_id, assignment_name)) { updateStudentGrade(student_id, grade, assignment_name);}
        else { submitnewGradebyStudentID(student_id, assignment_name, grade);}

    }

    // Add a new value to the StudentsGrade table.
    private static void submitnewGradebyStudentID(int student_id, String assignment_name,double grade) {
        String  SQL = "INSERT INTO StudentsGrades(assignment_name, id, grade, course_no) "
                + "VALUES(?,?,?,?)";
        try (
                Connection con = UserApp.connect();
                Statement statement = con.createStatement();        // Statement.RETURN_GENERATED_KEYS INSERT ICIN SADECE
                PreparedStatement ps = con.prepareStatement(SQL,  Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, assignment_name);
            ps.setInt(2, student_id);
            ps.setDouble(3, grade);
            ps.setString(4, ClassManagement.current_active_class);

            int affectedRows = ps.executeUpdate();
            System.out.println("Affected rows:" + affectedRows);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    // Update the value with student's id in StudentsGrade table.
    private static void updateStudentGrade(int student_id, double grade, String assignment_name) throws SQLException {
        String SQL = "UPDATE StudentsGrades SET grade = ? WHERE id = ? AND assignment_name = ?";
        try (
                Connection con = UserApp.connect();
                Statement statement = con.createStatement();        // Statement.RETURN_GENERATED_KEYS INSERT ICIN SADECE
                PreparedStatement ps = con.prepareStatement(SQL)) {

            ps.setDouble(1, grade);
            ps.setInt(2, student_id);
            ps.setString(3, assignment_name);

            int affectedRows = ps.executeUpdate();
            System.out.println("Affected rows:" + affectedRows);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    // Checks the what is the maximum possible grade for the given assignment name
    private static double lookupMaxPossibleScoreForAssigment(String assignment_name) {
        String SQL = "SELECT point FROM Assignment WHERE name = ?";
        double point = -1;
        try {Connection con = UserApp.connect();
            Statement statement = con.createStatement();
            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1, assignment_name);

            ResultSet rs = ps.executeQuery();
            if(rs.next())
                point = rs.getDouble("point");
            else {
                System.out.println("Something went wrong!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return point;
    }
    //check if the student is already graded.
    private static boolean isStudentGraded(int student_id, String assignment_name) throws SQLException {
        String SQL = "SELECT grade assingment_name FROM StudentsGrades WHERE id = ? AND assignment_name = ? AND course_no = ?";
        Connection con = UserApp.connect();
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setInt(1, student_id);
        ps.setString(2, assignment_name);
        ps.setString(3, ClassManagement.current_active_class);
        ResultSet rs = ps.executeQuery();

            if(rs.next())
                return true;            //Student Graded
        return false;               //Student doesn't have a grade
    }
}


