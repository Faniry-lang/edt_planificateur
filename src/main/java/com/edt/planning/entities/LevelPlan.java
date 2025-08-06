package com.edt.planning.entities;

// seconde, premiere, terminale, etc.
public class LevelPlan {
    Integer id;
    String nomLevel;

    public LevelPlan(Integer id, String nomLevel) {
        this.id = id;
        this.nomLevel = nomLevel;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getNomLevel()
    {
        return this.nomLevel;
    }

    public void setNomLevel(String nomLevel)
    {
        this.nomLevel = nomLevel;
    }
}
