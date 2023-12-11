package database;

import ui.UserApp;

import java.sql.*;
import java.util.Scanner;

public class CatandAssignManagement {


    public static void chooseOption(int option, Scanner kb) throws SQLException {
        UserApp.newLine();
        switch (option) {
            case(5):
                break;
            case(1):
                showCategory();
                break;
            case(2):
                System.out.println( "Please give the necessary information;");
                System.out.print("Name: ");
                String name = kb.nextLine();
                System.out.print("Weight: ");
                Double weight = Double.parseDouble(kb.nextLine());

                addCategory(name, weight);
                break;
            case(3):
                showAssingment();
                break;
            case(4):
                String category = "";

                    System.out.println("Please give the necessary information;");
                    System.out.print("Name: ");
                    name = kb.nextLine();
                    System.out.print("Description: ");
                    String description = kb.nextLine();
                    System.out.print("Point: ");
                    Double point = Double.parseDouble(kb.nextLine());
                do {
                    System.out.print("Category: ");
                    category = kb.nextLine();
                    if(isCategoryExists(category))
                        break;
                    else {
                        System.out.println("This category Doesn't exists in our current table please try again");
                    }
                }while(true);
                addAssingment(name, description, point, category);
                break;
        }
        UserApp.successfulProcess();
    }


    private static void addAssingment(String name, String description, double point, String category) {

        System.out.println("Adding a Assignment");

        String SQL = "INSERT INTO Assignment(name, category, description, point, course_no) "
                + "VALUES(?,?,?,?,?)";
        long id = 0;

        try (
                Connection con = UserApp.connect();
                PreparedStatement ps = con.prepareStatement(SQL,
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setString(3, description);
            ps.setDouble(4, point);
            ps.setString(5, ClassManagement.current_active_class);


            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println("Affected rows:" + affectedRows);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }



    private static void showAssingment() throws SQLException {
        System.out.println("Showing Assignment");
        UserApp.newLine();
        Connection conn = UserApp.connect();

        String SQL = "SELECT * FROM Assignment WHERE course_no = ?;";
        System.out.printf("name | description | point | category | course_no\n");
        PreparedStatement ps = conn.prepareStatement(SQL);
        ps.setString(1,ClassManagement.current_active_class);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            //System.out.println("a");
            String name = rs.getString("name");
            String description = rs.getString("description");
            double point = rs.getDouble("point");
            String category = rs.getString("category");
            String course_no = rs.getString("course_no");
            System.out.printf("%s | %s | %f | %s | %s\n",name,description,point, category, course_no);
            UserApp.newLine();
        }
    }

    private static void addCategory(String name, double weight) {
        System.out.println("Adding a category");

        String SQL = "INSERT INTO Category(name,weight,course_number) "
                + "VALUES(?,?, ?)";;
        try (Connection conn = UserApp.connect();

                PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            if(!isCategoryExists(name)) {
                ps.setString(1, name);
                ps.setDouble(2, weight);
                ps.setString(3, ClassManagement.current_active_class);
            }
            else {
                System.out.printf("Category \"%s\" already exists\n", name);
            }
            int affectedRows = ps.executeUpdate();
            System.out.println("Affected rows:" + affectedRows);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private static void showCategory() throws SQLException {
        System.out.println("Showing Categories");
        UserApp.newLine();

        Connection conn = UserApp.connect();
        Statement statement = conn.createStatement();
        String sql = "SELECT * FROM Category;";
        ResultSet rs = statement.executeQuery(sql);

        System.out.printf("name | weight| course_number\n");
        while(rs.next()) {
            String name = rs.getString("name");
            double weight = rs.getDouble("weight");
            String course_number = rs.getString("course_number");

            System.out.printf("%s | %f | %s\n",name,weight, course_number);
            UserApp.newLine();
        }
    }

    private static boolean isCategoryExists(String category) throws SQLException {

        String SQL = "SELECT name FROM Category WHERE name = ?";

        Connection con = UserApp.connect();
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString (1, category);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return true;            //Category exists
        }
        return false;               //Category DNE
    }

}
