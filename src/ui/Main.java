package ui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
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

        try {
            File file = new File("data.json");
            System.out.println("Existe: " + file.exists());
            FileInputStream fis = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String json = "";
            String line;
            while ((line = reader.readLine()) != null) {
                json += line;
            }
            fis.close();

            Gson gson = new Gson();
            Country[] countriesTemp = gson.fromJson(json, Country[].class);

            countries.addAll(Arrays.asList(countriesTemp));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void fillDB() {

        if(countries.size() != 0) {

            Gson gson = new Gson();
            String json = gson.toJson(countries);
            System.out.println(json);

            try {
                FileOutputStream fos = new FileOutputStream(new File("data.sql"));
                fos.write(json.getBytes(StandardCharsets.UTF_8));
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            // Warning message

        }

    }

}
