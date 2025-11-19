package model;

import java.io.Serializable;

public class user implements Serializable {

    protected int id;
    protected String name;
    protected String email;

    public user() {}

    public user(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Email: " + email;
    }
}
