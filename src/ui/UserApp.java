package ui;
import java.sql.*;
import java.util.Scanner;
import database.*;
//-------------------------------------------------------------//
//              CS 410 - Final Project - Onur Keles            //
//-------------------------------------------------------------//


public class UserApp {
    static Scanner kb = new Scanner(System.in);
    public static void main(String [] args) throws SQLException {

        welcome();
        int decision;
        while(true) {
            showOptions();
            System.out.print("Please input the desired action: ");
            decision = inputOption(1,6);
            if(decision == 6) { break; }
            chooseOption(decision);
        }
        goodbye();
    }



    public static void chooseOption(int decision) throws SQLException {
        int option;
        switch (decision) {
            case(1):
                newLine();
                System.out.println("1: Create a class");
                System.out.println("2: List classes");
                System.out.println("3: Activate a class");
                System.out.println("4: Show the current Activate Class");
                System.out.println("5: Return to Main Screen");
                System.out.print("Please input the desired action: ");
                option = inputOption(1,5);
                ClassManagement.chooseOption(option, kb);
                break;
            case(2):
                if(checkCurrentClass()) {
                    System.out.println("1: Show Categories");
                    System.out.println("2: Add a category");
                    System.out.println("3: Show Assignment");
                    System.out.println("4: Add Assignment");
                    System.out.println("5: Return to Main Screen");
                    System.out.print("Please input the desired action: ");
                    option = inputOption(1, 5);
                    CatandAssignManagement.chooseOption(option, kb);

                }
                else {
                    noCurrentClass();
                }
                break;
            case(3):
                if(checkCurrentClass()) {
                    System.out.println("1: Add a new Student");
                    System.out.println("2: Enroll a student into a class");
                    System.out.println("3: Show Students");
                    System.out.println("4: Show specific Students");
                    System.out.println("5: Grade a Student");
                    System.out.println("6: Return to Main Screen");
                    System.out.print("Please input the desired action: ");
                    option = inputOption(1, 6);
                    StudentManagment.chooseOption(option, kb);

                }
                else {
                    noCurrentClass();
                }
                break;
            case(4):
                if(checkCurrentClass()) {
                    System.out.println("1: Show student's grade");
                    System.out.println("2: Show the gradebook");
                    System.out.println("3: Return to Main Screen");
                    System.out.print("Please input the desired action: ");
                    option = inputOption(1, 3);
                    GradeReport.chooseOption(option, kb);
                }
                else {
                    noCurrentClass();
                }
                break;
            case(5):
                if(checkCurrentClass()) {
                    System.out.println("Please give the necessary information;");
                    System.out.print("Username of the Student: ");
                    String username = kb.nextLine();
                    System.out.println(GradeCalculate.calculateGrade(username));
                }
                else {
                    noCurrentClass();
                }
                break;
        }
    }



    public static void headerLine() { System.out.println("|---------------------------------|"); }
    public static void newLine() { System.out.println("-----------------------------------"); }
    public static void warningLine() { System.out.println("!!!-----------------------------!!!"); }

    public static void welcome() {
        headerLine();
        System.out.println("Welcome to class management Program");
        headerLine();
    }
    public static void goodbye() {
        headerLine();
        System.out.println("You exited from the System");
    }


    public static void showOptions() {
        System.out.println("Your options are:");
        System.out.println("1: Class Management");
        System.out.println("2: Category and Assignment Management");
        System.out.println("3: Student Management");
        System.out.println("4: Grade Reporting");
        System.out.println("5: Grade Calculation");
        System.out.println("6: Exit from the system");
    }

    public static void successfulProcess() {
        //System.out.println("Your process completed successfully");
        headerLine();
    }

    public static void noCurrentClass() {
        warningLine();
        System.out.println("This system still doesn't have an active class to use this option! \n" +
                           "Please activate a class from \"Class Management\" Option (1) first");
        warningLine();
    }

    public static boolean checkCurrentClass() {
        return !ClassManagement.current_active_class.equals("");
    }

    public static int inputOption(int bottomlimit, int toplimit) {
        int option = Integer.parseInt(kb.nextLine());
        //System.out.println("Please input the desired action: ");
        while(option < bottomlimit || option > toplimit) {
            System.out.print("Your input is not valid!\nPlease give a valid input: ");
            option = Integer.parseInt(kb.nextLine());
        }
        return option;
    }

    public static Connection connect() throws SQLException{

        String url = "jdbc:mysql://localhost:58623/final_project";
        String username = "msandbox";
        String password = "025631Onur";

        //System.out.println("Connecting database ...");
        return DriverManager.getConnection(url, username, password);
    }
}

