package com.example.fetch_exercise;

public class Item {
    private int id;
    private int listID;
    private String name;

    public Item(int id, int listID, String name){
        this.id = id;
        this.listID = listID;
        this.name = name;
    }

    public int getID(){
        return id;
    }

    public int getListID(){
        return listID;
    }

    public String getName(){
        return name;
    }
}
