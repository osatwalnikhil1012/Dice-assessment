package com.nikhilosatwal.diceassessment;

import java.io.Serializable;

public class Repository implements Serializable {
    
    private String name;
    private String owner;
    private String language;
    private String starCount;
    private String desc;
    private String avatar;

    private final String nameStart = "Name : ";
    private final String ownerStart = "Owner : ";
    private final String languageStart = "Language : ";
    private final String starCountStart = "Star Number : ";

    private final String descStart = "Description : ";

    public Repository(String name, String owner, String language, String starCount, String desc, String avatar) {
        this.name = name;
        this.owner = owner;
        this.language = language;
        this.starCount = starCount;
        this.desc = desc;
        this.avatar = avatar;
    }

    public String getName() {
        return this.nameStart + name;
    }

    public String getOwner() {
        return this.ownerStart + owner;
    }

    public String getLanguage() {
        return this.languageStart + language;
    }

    public String getStarCount() {
        return this.starCountStart + starCount;
    }

    public String getDesc() {
        return this.descStart + desc;
    }

    public String getAvatar() {
        return avatar;
    }
}
