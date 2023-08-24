package com.example.retroassigment;

public class ModelClass {

    String type,setup,punchline;
    int id;

    public ModelClass(String type, String setup, String punchline, int id) {
        this.type = type;
        this.setup = setup;
        this.punchline = punchline;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getSetup() {
        return setup;
    }

    public String getPunchline() {
        return punchline;
    }

    public int getId() {
        return id;
    }
}
