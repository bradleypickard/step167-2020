package com.google.sps.meltingpot.data;

public class Tag extends DBObject {

    public static final String HIDDEN_KEY = "hidden";

    public String name;
    public boolean hidden;
    public String type;

    public Tag(){super();}

    public Tag(String id) {
      super(id);
    }
}