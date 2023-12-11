package database;

import java.sql.*;

public class App {
    public static void main(String [] args) throws SQLException, ClassNotFoundException {

        String url = "jdbc:mysql://localhost:58623/final_project";
        String username = "msandbox";
        String password = "025631Onur";

        System.out.println("Connecting database ...");
        Connection connection = DriverManager.getConnection(url, username, password);
        /*
        Statement statement = connection.createStatement();
        String sql = "SELECT concat_ws(\" \", term) as ID FROM Class;";
        ResultSet resultSet = statement.executeQuery(sql);

        while(resultSet.next()) {
            String ID = resultSet.getString("id");
            System.out.println(ID);
        }
*/
    }
}