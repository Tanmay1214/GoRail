package com.example.gorail.model;

public class Passenger {
    public String name;
    public int age;

    private String id;
    public String gender;
    public String berth;

    public Passenger() {
    }

    public Passenger(String id, String name, int age, String gender, String berth) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.berth = berth;
    }

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getBerth() { return berth; }

    // Setters (important for Firebase deserialization)
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBerth(String berth) { this.berth = berth; }
}
