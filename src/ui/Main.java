package ui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import exceptions.InvalidCommandException;
import exceptions.NonExistentCountryIdException;
import model.City;
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
        } while (option != 0);

        JOptionPane.showMessageDialog(null, "ENDING APPLICATION");
    }

    public int showMenu() {
        int chooseOption;
        chooseOption = JOptionPane.showOptionDialog(
                null,
                "Select an option",
                "Option picker",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,    // null para icono por defecto.
                new Object[]{
                        "(1) Insert command",
                        "(2) Import data from SQL file",
                        "(3) Exit"
                }, null);
        return chooseOption + 1;
    }

    public void executeOperation(int op){

        switch (op) {
            case 1 -> insertCommand();
            case 2 -> importData();
            case 3 -> option = 0;
            default -> JOptionPane.showMessageDialog(null, "INVALID OPTION", "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void insertCommand(){

        String strCommand = "";

        strCommand = JOptionPane.showInputDialog("Insert the command: ");

        //try{

        if (strCommand.contains("INSERT INTO countries(id, name, population, countryCode) VALUES")) {
            String[] splitStr = strCommand.split(" ", 8);
            List<String> commands = Arrays.asList(splitStr);

            String values = commands.get(commands.size() - 1);
            String values2 = values.replace("(", "");
            String values3 = values2.replace(")", "");

            String[] splitCom = values3.split(", ");
            List<String> commands2 = Arrays.asList(splitCom);

            boolean flag = true;

            for (int i = 0; i < commands2.size(); i++) {
                if (i != 2) {
                    String res = commands2.get(i);
                    char a = res.charAt(0);
                    char b = res.charAt(res.length() - 1);
                    if (a != '\'' && b != '\'') {
                        flag = false;
                        break;
                    }
                }
            }
            try{
            if (invalidCommand(flag)) {
                String values4 = values3.replace("'", "");
                String[] splitCom3 = values4.split(", ");
                List<String> commands3 = Arrays.asList(splitCom3);

                double num = Double.parseDouble(commands3.get(2));
                // System.out.println(num);
                countries.add(new Country(commands3.get(0), commands3.get(1), num, commands3.get(3)));

                JOptionPane.showMessageDialog(null, "THE COUNTRY WAS ADDED");

                fillDB();
                fillDBJson();
                //importData();
            }
            }catch(InvalidCommandException ic){
                ic.printStackTrace();
            }
        } else if (strCommand.contains("INSERT INTO cities(id, name, countryID, population) VALUES")) {
            String[] splitStrcities = strCommand.split(" ", 8);
            List<String> commands = Arrays.asList(splitStrcities);

            String values = commands.get(commands.size() - 1);
            String values2 = values.replace("(", "");
            String values3 = values2.replace(")", "");

            String[] splitCom = values3.split(", ");
            List<String> commands2 = Arrays.asList(splitCom);

            boolean flag = true;

            for (int i = 0; i < commands2.size(); i++) {
                if (i != 3) {
                    String res = commands2.get(i);
                    char a = res.charAt(0);
                    char b = res.charAt(res.length() - 1);
                    if (a != '\'' && b != '\'') {
                        flag = false;
                        break;
                    }
                }
            }
            String values4 = values3.replace("'", "");
            String[] splitCom3 = values4.split(", ");
            List<String> commands3 = Arrays.asList(splitCom3);
            try {
                if (searchByIdEx(commands3.get(2))) {
                    // Nada
                } else {
                    flag = false;
                }
            } catch (NonExistentCountryIdException e) {
                e.printStackTrace();
            }
            try {
                if (invalidCommand(flag)) {
                    double num = Double.parseDouble(commands3.get(3));
                    // System.out.println(num);

                    for (Country c : countries) {

                        if (c.getId().equalsIgnoreCase(commands3.get(2))) {
                            c.addCity(commands3.get(0), commands3.get(1), commands3.get(2), num);
                            break;
                        }

                    }

                    JOptionPane.showMessageDialog(null, "THE CITY WAS ADDED");

                    // importData();
                    fillDB();
                    fillDBJson();

                }
            }catch (InvalidCommandException ic){
                ic.printStackTrace();
            }
        } else if (strCommand.contains("SELECT * FROM countries")) {
            System.out.println(
                    """
                            INFORMATION ABOUT THE COUNTRY/COUNTRIES:
                            """
            );
            if (strCommand.equals("SELECT * FROM countries")) {
                for (Country c : countries) {
                    System.out.println(c.toString());
                }
            } else {
                if (strCommand.contains("WHERE name =")) {
                    // SELECT * FROM countries WHERE name = 'Colombia'
                    String[] splitWHERE = strCommand.split(" ");
                    List<String> whereCommand = Arrays.asList(splitWHERE);
                    boolean flag = true;
                    char a = whereCommand.get(7).charAt(0);
                    char b = whereCommand.get(7).charAt(whereCommand.get(7).length() - 1);
                    if (a != '\'' && b != '\'') {
                        flag = false;
                    }
                    String name = whereCommand.get(7).replace("'", "");
                    try {
                        if (invalidCommand(flag)) {
                            boolean flag2 = false;
                            for (Country c : countries) {
                                if (c.getName().equals(name)) {
                                    System.out.println(c);
                                    flag2=true;
                                    break;
                                }
                            }
                            if(!flag2) System.out.println("THERE IS NO COUNTRY WITH THE NAME:");
                        }
                    }catch (InvalidCommandException e) {
                        e.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE population")) {
                    String[] splitPopulation = strCommand.split(" ");
                    List<String> populationCommand = Arrays.asList(splitPopulation);
                    boolean flag = !populationCommand.get(7).contains("'");
                    try {
                        if (invalidCommand(flag)) {
                            if (populationCommand.get(6).equals("<")) {
                                double population = Double.parseDouble(populationCommand.get(7));
                                if (strCommand.contains("ORDER BY population")) {
                                    ArrayList<Country> countryTemp = new ArrayList<Country>();

                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() < population) {
                                            countryTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryPop(countryTemp);
                                    if(!flag2) System.out.println("THERE IS NO COUNTRY WITH A POPULATION LESS THAN" + population);
                                } else if (strCommand.contains("ORDER BY id")) {
                                    ArrayList<Country> countryTempid = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() < population) {
                                            countryTempid.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryId(countryTempid);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION LESS THAN " + populationCommand.get(7));
                                } else if (strCommand.contains("ORDER BY name")) {
                                    ArrayList<Country> countryTempName = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() < population) {
                                            countryTempName.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryName(countryTempName);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION LESS THAN " + populationCommand.get(7));
                                } else if (strCommand.contains("ORDER BY countryCode")) {
                                    ArrayList<Country> countryCodeTemp = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() < population) {
                                            countryCodeTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryCode(countryCodeTemp);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION LESS THAN " + populationCommand.get(7));
                                } else {
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() < population) {
                                            System.out.println(c);
                                            flag2 = true;
                                        }
                                    }
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION LESS THAN " + populationCommand.get(7));
                                }
                            } else if (populationCommand.get(6).equals(">")) {
                                double population = Double.parseDouble(populationCommand.get(7));
                                if (strCommand.contains("ORDER BY population")) {
                                    ArrayList<Country> countryTemp = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() > population) {
                                            countryTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryPop(countryTemp);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION GREATER THAN " + populationCommand.get(7));
                                } else if (strCommand.contains("ORDER BY id")) {
                                    ArrayList<Country> countryTemp = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() > population) {
                                            countryTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryId(countryTemp);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION GREATER THAN " + populationCommand.get(7));
                                } else if (strCommand.contains("ORDER BY name")) {
                                    ArrayList<Country> countryTemp = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() > population) {
                                            countryTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryName(countryTemp);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION GREATER THAN " + populationCommand.get(7));
                                } else if (strCommand.contains("ORDER BY countryCode")) {
                                    ArrayList<Country> countryTemp = new ArrayList<Country>();
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() > population) {
                                            countryTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryCode(countryTemp);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION GREATER THAN " + populationCommand.get(7));
                                } else {
                                    boolean flag2 = false;
                                    for (Country c : countries) {
                                        if (c.getPopulation() > population) {
                                            System.out.println(c);
                                            flag2 = true;
                                        }
                                    }
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION GREATER THAN " + populationCommand.get(7));
                                }
                            } else if (populationCommand.get(6).equals("=")) {
                                double population = Double.parseDouble(populationCommand.get(7));
                                if (strCommand.contains("ORDER BY")) {
                                    if (strCommand.contains("ORDER BY name")) {
                                        ArrayList<Country> countryTemp = new ArrayList<Country>();

                                        boolean flag2 = false;
                                        for (Country c : countries) {
                                            if (c.getPopulation() == population) {
                                                countryTemp.add(c);
                                                flag2 = true;
                                            }
                                        }
                                        bubbleCountryName(countryTemp);
                                        if (!flag2)
                                            System.out.println("THERE IS NO COUNTRY WITH A POPULATION EQUAL THAN " + populationCommand.get(7));
                                    } else if (strCommand.contains("ORDER BY id")) {
                                        ArrayList<Country> countryTemp = new ArrayList<Country>();
                                        boolean flag2 = false;
                                        for (Country c : countries) {
                                            if (c.getPopulation() == population) {
                                                countryTemp.add(c);
                                                flag2 = true;
                                            }
                                        }
                                        bubbleCountryId(countryTemp);
                                        if (!flag2)
                                            System.out.println("THERE IS NO COUNTRY WITH A POPULATION EQUAL THAN " + populationCommand.get(7));
                                    } else if (strCommand.contains("ORDER BY countryCode")) {
                                        ArrayList<Country> countryTemp = new ArrayList<Country>();
                                        boolean flag2 = false;
                                        for (Country c : countries) {
                                            if (c.getPopulation() == population) {
                                                countryTemp.add(c);
                                                flag2 = true;
                                            }
                                        }
                                        bubbleCountryCode(countryTemp);
                                        if (!flag2)
                                            System.out.println("THERE IS NO COUNTRY WITH A POPULATION EQUAL THAN " + populationCommand.get(7));
                                    }
                                } else if (strCommand.contains("ORDER BY population")) {
                                    boolean flag2 = false;
                                    ArrayList<Country> countryTemp = new ArrayList<Country>();
                                    for (Country c : countries) {
                                        if (c.getPopulation() == population) {
                                            countryTemp.add(c);
                                            flag2 = true;
                                        }
                                    }
                                    bubbleCountryPop(countryTemp);
                                    if (!flag2)
                                        System.out.println("THERE IS NO COUNTRY WITH A POPULATION EQUAL THAN " + populationCommand.get(7));
                                }
                            } else {
                                double population = Double.parseDouble(populationCommand.get(7));
                                boolean flag2 = false;
                                for (Country c : countries) {
                                    if (c.getPopulation() == population) {
                                        System.out.println(c);
                                        flag2 = true;
                                    }
                                }
                                if (!flag2)
                                    System.out.println("THERE IS NO COUNTRY WITH A POPULATION EQUAL THAN " + populationCommand.get(7));
                            }
                        }
                    }catch (InvalidCommandException ic){
                        ic.printStackTrace();
                    }
                } else {
                    System.out.println("INVALID COMMAND");
                }
            }
        } else if (strCommand.contains("SELECT * FROM cities")) {
            System.out.println(
                    """
                            INFORMATION ABOUT THE CITY/CITITES:
                            """
            );
            if (strCommand.equals("SELECT * FROM cities")) {
                for (Country c : countries) {
                    System.out.println(c.getCities().toString());
                }
            } else {
                if (strCommand.contains("WHERE name =")) {
                    String[] splitWHERE = strCommand.split(" ");
                    List<String> whereCommand = Arrays.asList(splitWHERE);
                    if (strCommand.contains("ORDER BY population")) {
                        String city = whereCommand.get(7);
                        char a = whereCommand.get(7).charAt(0);
                        char b = whereCommand.get(7).charAt(whereCommand.get(7).length() - 1);
                        boolean flag2 = a == '\'' && b == '\'';
                        city = city.replace("'", "");
                        try {
                            if (invalidCommand(flag2)) {
                                ArrayList<City> sortCitiesTemp = new ArrayList<>();
                                for (int i = 0; i < countries.size(); i++) {
                                    if (countries.get(i) != null) {
                                        ArrayList<City> citiesTemp = countries.get(i).getCities();
                                        for (City c : citiesTemp) {
                                            if (c.getName().equals(city)) {
                                                sortCitiesTemp.add(c);
                                            }
                                        }
                                    }
                                }
                                if (sortCitiesTemp.size() != 0) {
                                    sortCityPopulation(sortCitiesTemp);
                                } else {
                                    System.out.println("ESTA VACIOOO");
                                }
                            }
                        } catch (InvalidCommandException e) {
                            e.printStackTrace();
                        }
                    } else if (strCommand.contains("ORDER BY name")) {
                        String city = whereCommand.get(7);
                        char a = whereCommand.get(7).charAt(0);
                        char b = whereCommand.get(7).charAt(whereCommand.get(7).length() - 1);
                        boolean flag2 = a == '\'' && b == '\'';
                        city = city.replace("'", "");
                        try {
                            if (invalidCommand(flag2)) {
                                ArrayList<City> sortCitiesTemp = new ArrayList<>();
                                for (int i = 0; i < countries.size(); i++) {
                                    if (countries.get(i) != null) {
                                        ArrayList<City> citiesTemp = countries.get(i).getCities();
                                        for (City c : citiesTemp) {
                                            if (c.getName().equals(city)) {
                                                sortCitiesTemp.add(c);
                                            }
                                        }
                                    }
                                }
                                if (sortCitiesTemp.size() != 0) {
                                    sortCityName(sortCitiesTemp);
                                } else {
                                    System.out.println("ESTA VACIOOO");
                                }
                            }
                        } catch (InvalidCommandException e) {
                            e.printStackTrace();
                        }
                    } else if (strCommand.contains("ORDER BY countryCode")) {
                        String city = whereCommand.get(7);
                        char a = whereCommand.get(7).charAt(0);
                        char b = whereCommand.get(7).charAt(whereCommand.get(7).length() - 1);
                        boolean flag2 = a == '\'' && b == '\'';
                        city = city.replace("'", "");
                        try {
                            if (invalidCommand(flag2)) {
                                ArrayList<City> sortCitiesTemp = new ArrayList<>();
                                for (int i = 0; i < countries.size(); i++) {
                                    if (countries.get(i) != null) {
                                        ArrayList<City> citiesTemp = countries.get(i).getCities();
                                        for (City c : citiesTemp) {
                                            if (c.getName().equals(city)) {
                                                sortCitiesTemp.add(c);
                                            }
                                        }
                                    }
                                }
                                if (sortCitiesTemp.size() != 0) {
                                    sortCityId(sortCitiesTemp);
                                } else {
                                    System.out.println("ESTA VACIOOO");
                                }
                            }
                        } catch (InvalidCommandException e) {
                            e.printStackTrace();
                        }
                    } else if (strCommand.contains("ORDER BY countryID")) {
                        String city = whereCommand.get(7);
                        char a = whereCommand.get(7).charAt(0);
                        char b = whereCommand.get(7).charAt(whereCommand.get(7).length() - 1);
                        boolean flag2 = a == '\'' && b == '\'';
                        city = city.replace("'", "");
                        try {
                            if (invalidCommand(flag2)) {
                                ArrayList<City> sortCitiesTemp = new ArrayList<>();
                                for (int i = 0; i < countries.size(); i++) {
                                    if (countries.get(i) != null) {
                                        ArrayList<City> citiesTemp = countries.get(i).getCities();
                                        for (City c : citiesTemp) {
                                            if (c.getName().equals(city)) {
                                                sortCitiesTemp.add(c);
                                            }
                                        }
                                    }
                                }
                                if (sortCitiesTemp.size() != 0) {
                                    sortCityCountryId(sortCitiesTemp);
                                } else {
                                    System.out.println("ESTA VACIOOO");
                                }
                            }
                        } catch (InvalidCommandException e) {
                            e.printStackTrace();
                        }
                    } else {
                        boolean flag = false;
                        char a = whereCommand.get(7).charAt(0);
                        char b = whereCommand.get(7).charAt(whereCommand.get(7).length() - 1);
                        if (a == '\'' && b == '\'') {
                            flag = true;
                        }
                        String name = whereCommand.get(7).replace("'", "");
                        try{
                            if (invalidCommand(flag)) {
                                for (int i = 0; i < countries.size(); i++) {

                                    if (countries.get(i) != null) {

                                        ArrayList<City> citiesTemp = countries.get(i).getCities();

                                        for (City c : citiesTemp) {
                                            if (c.getName().equals(name)) {
                                                System.out.println(c);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (InvalidCommandException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (strCommand.contains("WHERE population")) {
                    String[] splitPopulation = strCommand.split(" ");
                    List<String> populationCommand = Arrays.asList(splitPopulation);
                    // 6 mayor o igual
                    // 7 population
                    boolean flag = !populationCommand.get(7).contains("'");
                    try {
                        if (invalidCommand(flag)) {
                            if (populationCommand.get(6).equals("<")) {
                                double population = Double.parseDouble(populationCommand.get(7));
                                boolean flag2 = false;
                                for (int i = 0; i < countries.size(); i++) {

                                    if (countries.get(i) != null) {

                                        ArrayList<City> citiesTemp = countries.get(i).getCities();

                                        for (City c : citiesTemp) {
                                            if (c.getPopulation() < population) {
                                                System.out.println(c);
                                                flag2 = true;
                                            }
                                        }
                                    }
                                }
                                if (!flag2)
                                    System.out.println("THERE IS NO CITY WITH A POPULATION LESS THAN " + populationCommand.get(7));
                            } else if (populationCommand.get(6).equals(">")) {
                                double population = Double.parseDouble(populationCommand.get(7));
                                boolean flag2 = false;
                                for (int i = 0; i < countries.size(); i++) {

                                    if (countries.get(i) != null) {

                                        ArrayList<City> citiesTemp = countries.get(i).getCities();

                                        for (City c : citiesTemp) {
                                            if (c.getPopulation() > population) {
                                                System.out.println(c);
                                                flag2 = true;
                                            }
                                        }
                                    }
                                }
                                if (!flag2)
                                    System.out.println("THERE IS NO CITY WITH A POPULATION GREATER THAN " + populationCommand.get(7));
                            } else if (populationCommand.get(6).equals("=")) {
                                double population = Double.parseDouble(populationCommand.get(7));
                                boolean flag2 = false;
                                for (int i = 0; i < countries.size(); i++) {

                                    if (countries.get(i) != null) {

                                        ArrayList<City> citiesTemp = countries.get(i).getCities();

                                        for (City c : citiesTemp) {
                                            if (c.getPopulation() == population) {
                                                System.out.println(c);
                                                flag2 = true;
                                            }
                                        }
                                    }
                                }
                                if (!flag2)
                                    System.out.println("THERE IS NO CITY WITH A POPULATION EQUAL THAN " + populationCommand.get(7));
                            } else {
                                System.out.println("INVALID COMPARATION COMMAND");
                            }
                        }
                    }catch (InvalidCommandException ic){
                        ic.printStackTrace();
                    }
                }
            }
        } else if (strCommand.contains("DELETE FROM")) {
            if (strCommand.contains("cities")) {
                if (strCommand.contains("WHERE country =")) {
                    String[] splitCityElimination = strCommand.split(" ");
                    List<String> deleteCommand = Arrays.asList(splitCityElimination);
                    String country = deleteCommand.get(6);
                    boolean flag = false;
                    char a = country.charAt(0);
                    char b = country.charAt(country.length() - 1);
                    if (a == '\'' && b == '\'') {
                        flag = true;
                    }
                    try{
                        if (invalidCommand(flag)) {
                            country = country.replace("'", "");
                            for (Country c : countries) {
                                if (c.getName().equals(country)) {
                                    c.getCities().clear();
                                }
                            }
                            System.out.println("UPDATE DATABASE: ");
                            fillDB();
                            fillDBJson();
                        }
                    } catch (InvalidCommandException e) {
                        e.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE id = ")) {
                    String[] splitCityElimination = strCommand.split(" ");
                    List<String> deleteCommand = Arrays.asList(splitCityElimination);
                    String id = deleteCommand.get(6);
                    boolean flag = false;
                    char a = id.charAt(0);
                    char b = id.charAt(id.length() - 1);
                    if (a == '\'' && b == '\'') {
                        flag = true;
                    }
                    try {
                        if (invalidCommand(flag)) {
                            id = id.replace("'", "");
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i) != null) {
                                    ArrayList<City> citiesTemp = countries.get(i).getCities();
                                    for (int j = 0; j < citiesTemp.size(); j++) {
                                        if (citiesTemp.get(j).getId().equals(id)) {
                                            countries.get(i).getCities().remove(j);
                                        }
                                    }
                                }
                            }
                            System.out.println("UPDATE DATABASE: ");
                            fillDB();
                            fillDBJson();
                        }
                    }catch(InvalidCommandException ic){
                        ic.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE name = ")) {
                    String[] splitCityElimination = strCommand.split(" ");
                    List<String> deleteCommand = Arrays.asList(splitCityElimination);
                    String name = deleteCommand.get(6);
                    boolean flag = false;
                    char a = name.charAt(0);
                    char b = name.charAt(name.length() - 1);
                    if (a == '\'' && b == '\'') {
                        flag = true;
                    }
                    try {
                        if (invalidCommand(flag)) {
                            name = name.replace("'", "");
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i) != null) {
                                    ArrayList<City> citiesTemp = countries.get(i).getCities();
                                    for (int j = 0; j < citiesTemp.size(); j++) {
                                        if (citiesTemp.get(j).getName().equals(name)) {
                                            countries.get(i).getCities().remove(j);
                                        }
                                    }
                                }
                            }
                            System.out.println("UPDATE DATABASE");
                            fillDB();
                            fillDBJson();
                        }
                    }catch (InvalidCommandException e) {
                        e.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE population = ")) {
                    String[] splitCityElimination = strCommand.split(" ");
                    List<String> deleteCommand = Arrays.asList(splitCityElimination);
                    String population = deleteCommand.get(6);
                    boolean flag = true;
                    try {
                        if (invalidCommand(flag)) {
                            population = population.replace("'", "");
                            Double popu = Double.parseDouble(population);
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i) != null) {
                                    ArrayList<City> citiesTemp = countries.get(i).getCities();
                                    for (int j = 0; j < citiesTemp.size(); j++) {
                                        if (citiesTemp.get(j).getPopulation() == popu) {
                                            countries.get(i).getCities().remove(j);
                                        }
                                    }
                                }
                            }
                            System.out.println("UPDATE DATABASE");
                            fillDB();
                            fillDBJson();
                        }
                    }catch(InvalidCommandException ic){
                        ic.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE population > ")) {
                    String[] splitCityElimination = strCommand.split(" ");
                    List<String> deleteCommand = Arrays.asList(splitCityElimination);
                    String population = deleteCommand.get(6);
                    boolean flag = true;
                    try {
                        if (invalidCommand(flag)) {
                            population = population.replace("'", "");
                            Double popu = Double.parseDouble(population);
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i) != null) {
                                    ArrayList<City> citiesTemp = countries.get(i).getCities();
                                    for (int j = 0; j < citiesTemp.size(); j++) {
                                        if (citiesTemp.get(j).getPopulation() > popu) {
                                            countries.get(i).getCities().remove(j);
                                        }
                                    }
                                }
                            }
                            System.out.println("UPDATE DATABASE");
                            fillDB();
                            fillDBJson();
                        }
                    }catch(InvalidCommandException ic){
                        ic.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE population < ")) {
                    String[] splitCityElimination = strCommand.split(" ");
                    List<String> deleteCommand = Arrays.asList(splitCityElimination);
                    String population = deleteCommand.get(6);
                    boolean flag = true;
                    try {
                        if (invalidCommand(flag)) {
                            population = population.replace("'", "");
                            Double popu = Double.parseDouble(population);
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i) != null) {
                                    ArrayList<City> citiesTemp = countries.get(i).getCities();
                                    for (int j = 0; j < citiesTemp.size(); j++) {
                                        if (citiesTemp.get(j).getPopulation() < popu) {
                                            countries.get(i).getCities().remove(j);
                                        }
                                    }
                                }
                            }
                            System.out.println("UPDATE DATABASE");
                            fillDB();
                            fillDBJson();
                        }
                    }catch (InvalidCommandException e){
                        e.printStackTrace();
                    }
                }
            } else if (strCommand.contains("countries")) {
                String[] splitCityElimination = strCommand.split(" ");
                List<String> deleteCommand = Arrays.asList(splitCityElimination);
                if (strCommand.contains("WHERE name =")) {
                    String country = deleteCommand.get(6);
                    boolean flag = false;
                    char a = country.charAt(0);
                    char b = country.charAt(country.length() - 1);
                    if (a == '\'' && b == '\'') flag = true;
                    try {
                        if (invalidCommand(flag)) {
                            country = country.replace("'", "");
                            boolean flag2 = false;
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i).getName().equals(country)) {
                                    countries.remove(i);
                                    flag2 = true;
                                    break;
                                }
                            }
                            if (!flag2) System.out.println("THERE IS NOT COUNTRY WITH THE NAME " + country);
                            else {
                                System.out.println("UPDATE DATABASE");
                                fillDB();
                                fillDBJson();
                            }

                        }
                    }catch(InvalidCommandException e){
                        e.printStackTrace();
                        }
                } else if (strCommand.contains("WHERE id =")) {
                    String countryId = deleteCommand.get(6);
                    boolean flag = false;
                    char a = countryId.charAt(0);
                    char b = countryId.charAt(countryId.length() - 1);
                    if (a == '\'' && b == '\'') flag = true;
                    try {
                        if (invalidCommand(flag)) {
                            countryId = countryId.replace("'", "");
                            boolean flag2 = false;
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i).getId().equals(countryId)) {
                                    countries.remove(i);
                                    flag2 = true;
                                    break;
                                }
                            }
                            if (!flag2) System.out.println("THERE IS NOT COUNTRY WITH THE ID " + countryId);
                            else {
                                System.out.println("UPDATE DATABASE");
                                fillDB();
                                fillDBJson();
                            }
                        }
                    } catch(InvalidCommandException ic){
                        ic.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE countryCode =")) {
                    String countryCode = deleteCommand.get(6);
                    boolean flag = false;
                    char a = countryCode.charAt(0);
                    char b = countryCode.charAt(countryCode.length() - 1);
                    if (a == '\'' && b == '\'') flag = true;
                    try {
                        if (invalidCommand(flag)) {
                            countryCode = countryCode.replace("'", "");
                            boolean flag2 = false;
                            for (int i = 0; i < countries.size(); i++) {
                                if (countries.get(i).getCountryCode().equals(countryCode)) {
                                    countries.remove(i);
                                    flag2 = true;
                                    break;
                                }
                            }
                            if (!flag2) System.out.println("THERE IS NOT COUNTRY WITH THE COUNTRY CODE " + countryCode);
                            else {
                                System.out.println("UPDATE DATABASE");
                                fillDB();
                                fillDBJson();
                            }
                        }
                    }catch (InvalidCommandException e) {
                        e.printStackTrace();
                    }
                } else if (strCommand.contains("WHERE population")) {
                    double population = Double.parseDouble(deleteCommand.get(6));
                    if (strCommand.contains("WHERE population =")) {
                        for (int i = 0; i < countries.size(); i++) {
                            if (countries.get(i).getPopulation() == population) {
                                countries.remove(i);
                            }
                        }
                        System.out.println("UPDATE DATABASE");
                        fillDB();
                        fillDBJson();
                    } else if (strCommand.contains("WHERE population >")) {
                        for (int i = 0; i < countries.size(); i++) {
                            if (countries.get(i).getPopulation() > population) {
                                countries.remove(i);
                            }
                        }
                        System.out.println("UPDATE DATABASE");
                        fillDB();
                        fillDBJson();
                    } else if (strCommand.contains("WHERE population <")) {
                        for (int i = 0; i < countries.size(); i++) {
                            if (countries.get(i).getPopulation() < population) {
                                countries.remove(i);
                            }
                        }
                        System.out.println("UPDATE DATABASE");
                        fillDB();
                        fillDBJson();
                    } else {
                        System.out.println("INVALID COMMAND");
                    }
                }
            } else {
                System.out.println("INVALID COMMAND");
            }
        } else {
            System.out.println("INVALID COMMAND");
        }

    }

    public void sortCityPopulation(ArrayList<City> sortCities) {
        for (int rojo = 0; rojo < sortCities.size() - 1; rojo++) {
            for (int azul = rojo + 1; azul < sortCities.size(); azul++) {
                if (sortCities.get(rojo).getPopulation() < sortCities.get(azul).getPopulation()) {
                    //NADA
                } else {
                    City valorRojo = sortCities.get(rojo);
                    City valorAzul = sortCities.get(azul);
                    sortCities.set(rojo, valorAzul);
                    sortCities.set(azul, valorRojo);
                }
            }
        }
        for (City c : sortCities) {
            System.out.println(c.toString());
        }
    }

    public void sortCityName(ArrayList<City> sortCities) {
        for (int j = 0; j < sortCities.size(); j++) {
            for (int i = 1; i < sortCities.size() - j; i++) {
                if (sortCities.get(i - 1).getName().compareTo(sortCities.get(i).getName()) < 0) {
                    //NADA
                } else {
                    //SWAP
                    City anterior = sortCities.get(i - 1);
                    City actual = sortCities.get(i);
                    sortCities.set(i, anterior);
                    sortCities.set(i - 1, actual);
                }
            }
        }
        for (City c : sortCities) {
            System.out.println(c.toString());
        }
    }

    public void sortCityId(ArrayList<City> sortCities) {
        for (int j = 0; j < sortCities.size(); j++) {
            for (int i = 1; i < sortCities.size() - j; i++) {
                if (sortCities.get(i - 1).getId().compareTo(sortCities.get(i).getId()) < 0) {
                    //NADA
                } else {
                    //SWAP
                    City anterior = sortCities.get(i - 1);
                    City actual = sortCities.get(i);
                    sortCities.set(i, anterior);
                    sortCities.set(i - 1, actual);
                }
            }
        }
        for (City c : sortCities) {
            System.out.println(c.toString());
        }
    }

    public void sortCityCountryId(ArrayList<City> sortCities) {
        for (int j = 0; j < sortCities.size(); j++) {
            for (int i = 1; i < sortCities.size() - j; i++) {
                if (sortCities.get(i - 1).getCountryCode().compareTo(sortCities.get(i).getCountryCode()) < 0) {
                    //NADA
                } else {
                    //SWAP
                    City anterior = sortCities.get(i - 1);
                    City actual = sortCities.get(i);
                    sortCities.set(i, anterior);
                    sortCities.set(i - 1, actual);
                }
            }
        }
        for (City c : sortCities) {
            System.out.println(c.toString());
        }
    }

    public void bubbleCountryName(ArrayList<Country> c) {
        for (int j = 0; j < c.size(); j++) {
            for (int i = 1; i < c.size() - j; i++) {
                if (c.get(i - 1).getName().compareTo(c.get(i).getName()) < 0) {
                    //NADA
                } else {
                    //SWAP
                    Country anterior = c.get(i - 1);
                    Country actual = c.get(i);
                    c.set(i, anterior);
                    c.set(i - 1, actual);
                }
            }
        }
        for (Country co : c) {
            System.out.println(co.toString());
        }
    }

    public void bubbleCountryId(ArrayList<Country> c) {
        for (int j = 0; j < c.size(); j++) {
            for (int i = 1; i < c.size() - j; i++) {
                if (c.get(i - 1).getId().compareTo(c.get(i).getId()) < 0) {
                    //NADA
                } else {
                    //SWAP
                    Country anterior = c.get(i - 1);
                    Country actual = c.get(i);
                    c.set(i, anterior);
                    c.set(i - 1, actual);
                }
            }
        }
        for (Country co : c) {
            System.out.println(co.toString());
        }
    }

    public void bubbleCountryCode(ArrayList<Country> c) {
        for (int j = 0; j < c.size(); j++) {
            for (int i = 1; i < c.size() - j; i++) {
                if (c.get(i - 1).getCountryCode().compareTo(c.get(i).getCountryCode()) < 0) {
                    //NADA
                } else {
                    //SWAP
                    Country anterior = c.get(i - 1);
                    Country actual = c.get(i);
                    c.set(i, anterior);
                    c.set(i - 1, actual);
                }
            }
        }
        for (Country co : c) {
            System.out.println(co.toString());
        }
    }

    public void bubbleCountryPop(ArrayList<Country> c) {
        for (int rojo = 0; rojo < c.size() - 1; rojo++) {
            for (int azul = rojo + 1; azul < c.size(); azul++) {
                if (c.get(rojo).getPopulation() < c.get(azul).getPopulation()) {
                    //NADA
                } else {
                    Country valorRojo = c.get(rojo);
                    Country valorAzul = c.get(azul);
                    c.set(rojo, valorAzul);
                    c.set(azul, valorRojo);
                }
            }
        }
        for (Country co : c) {
            System.out.println(co.toString());
        }
    }

    public boolean searchByIdEx(String goal) throws NonExistentCountryIdException {

        boolean flag = false;

        for (Country c : countries) {

            if (c.getId().equalsIgnoreCase(goal)) {
                flag = true;
                break;
            }

        }
        if (flag != true) {
            throw new NonExistentCountryIdException();
        }
        return flag;

    }

    public boolean searchById(String goal) {

        boolean flag = false;

        for (Country c : countries) {

            if (c.getId().equalsIgnoreCase(goal)) {
                flag = true;
                break;
            }

        }
        return flag;
    }

    public boolean invalidCommand(boolean flag) throws InvalidCommandException {

        if(flag != true) {
            throw new InvalidCommandException();
        }

        return flag;

    }

    public void importData() {

        try {
            File file = new File("data.sql");
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

                for (Country c : countriesTemp) {
                    if (!searchById(c.getId())) {
                        countries.add(c);
                    }
                }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void fillDB() {

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

    }

    public void fillDBJson() {

        Gson gson = new Gson();
        String json = gson.toJson(countries);
        // System.out.println(json);

        try {
            FileOutputStream fos = new FileOutputStream(new File("data.json"));
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
