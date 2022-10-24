package ui;

import java.util.ArrayList;
import java.util.Scanner;
import model.Country;

import javax.swing.*;

public class Main {

    public ArrayList<Country> countries;
    public Scanner sc;
    public static int option;

    public Main() {
        sc = new Scanner(System.in);
        countries = new ArrayList<>();
    }

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "STARTING APPLICATION");

        Main myMain = new Main();

        myMain.loadDB();

        do {
            option = myMain.showMenu();
            myMain.executeOperation(option);
        } while(option != 0);

        JOptionPane.showMessageDialog(null, "ENDING APPLICATION");

    }

    public int showMenu() {
        int chooseOption;
        chooseOption =  JOptionPane.showOptionDialog(
                null,
                "Select an option",
                "Option picker",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,    // null para icono por defecto.
                new Object[] {
                        "(1) Insert command",
                        "(2) Import data from SQL file",
                        "(3) Exit"
                }, null);
        return chooseOption + 1;
    }

    public void executeOperation(int op) {
        switch (op) {
            case 1 -> insertCommand();
            case 2 -> importData();
            case 3 -> option = 0;
            default -> JOptionPane.showMessageDialog(null, "INVALID OPTION", "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void insertCommand() {

    }

    public void importData() {

    }

    public void loadDB() {

    }

}
