package com.example.kishorebaktha.shoppinglist4;

import java.util.Date;

public class ListItem {

    private String item;
    private String budget;
    private String priority;
   // private String reminder;
    public ListItem(String item, String budget,String priority) {
        this.item = item;
        this.budget = budget;
        this.priority=priority;
       // this.reminder=reminder;

    }
    public ListItem() {

}

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

//    public String getReminder() {
//        return reminder;
//    }
//
//    public void setReminder(String reminder) {
//        this.reminder = reminder;
//    }
}
