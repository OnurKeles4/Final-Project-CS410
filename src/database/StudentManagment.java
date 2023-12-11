package database;

import ui.UserApp;

import java.sql.*;
import java.util.Scanner;

public class StudentManagment {
    // To add the class to student (or student to class), add a new column of "class name" to student table.
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
                enrollExstingStudent(username);
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

    private static void addNewStudent(String username, String name) throws SQLException {
        System.out.println("Adding a new Student");

        try {
            if(doesStudentExist(username)) {
                UserApp.warningLine();
                System.out.println("Warning! This Student already exists in the system, this" +
                                   "prompt only enrolled this student into system");
                UserApp.warningLine();

                enrollExstingStudent(username);
            }
            else{ enrollNewStudent(username, name); }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        * try (
                Connection con = UserApp.connect();
                Statement statement = con.createStatement();
                PreparedStatement ps2 = con.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            if(flag) {
                System.out.println("flag doğru!!!");
                ps.setString(1, username);
                ps.setString(2, name);
                ps2.setString(1,username);
                ps2.setString(2,ClassManagement.current_active_class);
                System.out.println("flag sonlandı!!!!!!!!1");

            }
            else {
                    ps2.setString(1, username);
                    ResultSet rs = statement.executeQuery(SQL2);
                    student_id = rs.getInt("id");

                ps.setString(1, ClassManagement.current_active_class);
                ps.setInt(2, student_id);

            }
            int affectedRows = ps.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(affectedRows);


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }*/
    }

    private static void enrollNewStudent(String username, String name) {
        String SQL = "INSERT INTO Student(username, name) "             //What about id??
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
        // check ıf points makes sense
        // submit grade
            // update grade
            // insert
        /*
        * try {Connection con = UserApp.connect();
            Statement statement = con.createStatement();

            PreparedStatement ps = con.prepareStatement(SQL);
            ps.setString(1,username);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
            student_id = rs.getInt("id");
            else
                return;


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }*/
        /*
            * int affectedRows = ps.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs2 = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs2.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(affectedRows);*/
        /*
        * try(Connection con = UserApp.connect();
        Statement statement = con.createStatement();)
        {
            SQL = "SELECT points FROM Assignment WHERE name = ?";

            PreparedStatement ps3 = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps3.setString(1, assignment_name);

            ResultSet rs = statement.executeQuery(SQL);
            double point = rs.getDouble("point");

            if(point < grade) {
                UserApp.warningLine();
                System.out.println("Warning! This grade is over point limit!");
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }*/
        /*
        *

        if (isStudentGraded(student_id)) {
            // update grade
            updateGrade()
        } else {
            // Create new grade
            createGrade()
        }*/
        /*
        * boolean flag = true;
        //System.out.println("öğrenci vaar mı " + doesStudentExist(username));
        try {
            if (isStudentGraded(student_id)) {
                //System.out.println("Warning! This Student already exists in the system, this" +
                        //"prompt only enrolled this student into system");
                SQL = "UPDATE StudentsGrades SET grade = ? WHERE id = ?";
                flag = false;
            } else {
                SQL = "INSERT INTO StudentsGrades(assignment_name, id, grade) "
                        + "VALUES(?,?,?)";

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
*/
        /*
        * try (
                Connection con = UserApp.connect();
                Statement statement = con.createStatement();        // Statement.RETURN_GENERATED_KEYS INSERT ICIN SADECE
                PreparedStatement ps2 = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            if(flag) {
                ps2.setString(1, assignment_name);
                ps2.setInt(2, student_id);
                ps2.setDouble(3, grade);
            }
            else {
                ps2.setDouble(1, grade);
                ps2.setInt(2, student_id);
            }
            //check that if the point is over point of the assignment
            //*** DOESN'T WORK !



            int affectedRows = ps2.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs2 = ps2.getGeneratedKeys()) {
                    if (rs2.next()) {
                        id = rs2.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(affectedRows);


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
*/
    }

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


