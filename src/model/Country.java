package model;

import java.util.ArrayList;

public class Country {

    private String id;
    private String name;
    private double population;
    private String countryCode;
    private ArrayList<City> cities;

    public Country(String id, String name, double population, String countryCode) {
        this.id = id;
        this.name = name;
        this.population = population;
        this.countryCode = countryCode;
        this.cities = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPopulation() {
        return population;
    }

    public void setPopulation(double population) {
        this.population = population;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", population=" + population +
                ", countryCode='" + countryCode + '\'' +
                ", cities=" + cities.size() +
                '}';
    }

    // MORE METHODS ....

    public void addCity(String id, String name, String countryId, double population) {
        City c = new City(id, name, countryId, population);
        cities.add(c);
        System.out.println("CITIES OF " + this.getName());
        for (City ci : cities) {
            System.out.println(ci.getName());
        }
    }

}
