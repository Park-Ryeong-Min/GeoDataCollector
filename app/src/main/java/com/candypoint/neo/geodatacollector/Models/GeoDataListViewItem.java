package com.candypoint.neo.geodatacollector.Models;

/**
 * Created by myown on 2018. 4. 2..
 */

public class GeoDataListViewItem {
    private String sectionName;
    //private long sectionNumber;

    public GeoDataListViewItem(String name/*, long number*/){
        this.sectionName = name;
        //this.sectionNumber = number;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
    /*
    public long getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(long sectionNumber) {
        this.sectionNumber = sectionNumber;
    }
    */
}
