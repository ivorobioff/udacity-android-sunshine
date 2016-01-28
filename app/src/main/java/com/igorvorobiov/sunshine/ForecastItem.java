package com.igorvorobiov.sunshine;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * @author Igor Vorobiov<igor.vorobioff@gmail.com>
 */
public class ForecastItem {
    private Double max;
    private Double min;
    private GregorianCalendar date;
    private String description;

    ForecastItem(GregorianCalendar day, String description, Double max, Double min){
        this.date = day;
        this.description = description;
        this.max = max;
        this.min = min;
    }

    public String getDay(){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(date.getTimeInMillis());
    }

    public String getDescription(){
        return description;
    }

    public Integer getMax(){
        return max.intValue();
    }

    public Integer getMin(){
        return min.intValue();
    }
}
