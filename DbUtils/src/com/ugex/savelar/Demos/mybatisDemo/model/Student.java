package com.ugex.savelar.Demos.mybatisDemo.model;

public class Student {
    private int id;
    private String name;
    private int age;
    private int inyear;
    private double tall;
    public Student(){

    }
    public Student(int id) {
        this.id = id;
    }

    public Student(String name, int age, int inyear, double tall) {
        this.name = name;
        this.age = age;
        this.inyear = inyear;
        this.tall = tall;
    }

    public Student(int id, String name, int age, int inyear, double tall) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.inyear = inyear;
        this.tall = tall;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", inyear=" + inyear +
                ", tall=" + tall +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getInyear() {
        return inyear;
    }

    public void setInyear(int inyear) {
        this.inyear = inyear;
    }

    public double getTall() {
        return tall;
    }

    public void setTall(double tall) {
        this.tall = tall;
    }
}
