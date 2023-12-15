package database;

import ui.UserApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeCalculate {

    public static double calculateGrade(String username) throws SQLException {

        String SQL = "SELECT\n" +
                "a.category, sum(grade) as sum_of_grade, (sum(grade)*c.weight)/(sum(a.point)) as last_grade, c.weight\n" +
                "FROM StudentsGrades sg\n" +
                "LEFT JOIN Assignment a on a.name = sg.assignment_name \n" +
                "LEFT JOIN Category c on a.category = c.name\n" +
                "WHERE sg.id = ?\n" +
                "GROUP BY a.category;";

        Connection conn = UserApp.connect();
        PreparedStatement ps = conn.prepareStatement(SQL);

        int student_id = StudentManagment.lookupStudentIdByUsername(username);

        if(student_id == -1) {
            UserApp.warningLine();
            System.out.println("This student DNE");
            UserApp.warningLine();
            return -1;
        }
        ps.setInt(1, student_id);

        ResultSet rs = ps.executeQuery();
        int student_grade = 0;
        while(rs.next()) {
            student_grade += rs.getInt("last_grade");
        }

        UserApp.successfulProcess();
        return student_grade;
    }
}
