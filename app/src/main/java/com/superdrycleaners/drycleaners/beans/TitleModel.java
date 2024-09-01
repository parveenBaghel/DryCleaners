package com.superdrycleaners.drycleaners.beans;

public class TitleModel {
    private String Tittle;
    private int Icon;

    public TitleModel(String tittle, int icon) {
        Tittle = tittle;
        Icon = icon;
    }


    public String getTittle() {
        return Tittle;
    }

    public int getIcon() {
        return Icon;
    }

    public void setTittle(String tittle) {
        Tittle = tittle;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }
}
