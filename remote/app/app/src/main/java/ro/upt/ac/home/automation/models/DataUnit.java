package ro.upt.ac.home.automation.models;

import java.util.StringTokenizer;

public class DataUnit {

    public DataUnit(int value, String date, String time) {
        this.mValue = value;
        this.mDate = date;
        this.mTime = time;
    }

    public int getDataUnitValue() {
        return mValue;
    }

    public String getDataUnitDate() {
        return mDate;
    }

    public String getDataUnitTime() {
        StringTokenizer timeTokenizer = new StringTokenizer(mTime, ".");

        return timeTokenizer.nextToken();
    }

    private int     mValue;
    private String  mDate;
    private String  mTime;


}
