package de.kritzelbit.gt.gui;


public class Alert {
    
    private String title;
    private String line1;
    private String line2;

    public Alert(String title, String line1, String line2) {
        this.title = title;
        this.line1 = line1;
        this.line2 = line2;
    }

    public String getTitle() {
        return title;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }
    
}
