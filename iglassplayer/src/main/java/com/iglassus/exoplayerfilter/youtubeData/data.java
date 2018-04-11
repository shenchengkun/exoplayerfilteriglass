package com.iglassus.exoplayerfilter.youtubeData;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;

public class data {
    public String channel;
    long count;
    public String date;
    public String duration = "";
    public String id;
    public String imageId;
    public Bitmap myBitmap;
    public String title;
    public int type;
    boolean unlocked = false;
    public View view;

    public boolean isUnlocked() {
        return this.unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    String getDateFomart(String dateparsed) {
        Date result = null;
        String format = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            result = sdf.parse(dateparsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate dataDate = new LocalDate(result.getTime());
        LocalDate now = LocalDate.now();
        int wks = Weeks.weeksBetween(dataDate, now).getWeeks();
        if (wks == 0) {
            if (Days.daysBetween(dataDate, now).getDays() == 0) {
                return "Today";
            }
            return Days.daysBetween(dataDate, now).getDays() + " days ago";
        } else if (wks <= 4) {
            return wks + " weeks ago";
        } else {
            if (wks < 52) {
                return (wks / 4) + " months ago";
            }
            return (wks / 52) + " years ago";
        }
    }

    public data(String ImageId, String title, String chan, int type, Context c, String id) {
        this.title = title;
        this.imageId = ImageId;
        this.channel = chan;
        this.type = type;
        this.id = id;
        try {
            this.myBitmap = Picasso.with(c).load(this.imageId).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public data(String ImageId, String title, String chan, int type) {
        this.title = title;
        this.imageId = ImageId;
        this.channel = chan;
        this.type = type;
    }

    public data(String ImageId, String title, String channelTitle, String dateText, int type, String id) {
        this.title = title;
        this.imageId = ImageId;
        this.channel = channelTitle;
        this.type = type;
        this.date = getDateFomart(dateText);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Bitmap getMyBitmap() {
        return this.myBitmap;
    }

    public String getDate() {
        return this.date;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getImageId() {
        return this.imageId;
    }

    public int getType() {
        return this.type;
    }

    public String getDuration() {
        return dur(this.duration);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCount() {
        if (this.count > 0) {
            return " • " + format(this.count) + " Views";
        }
        return "";
    }

    public void setCount(long count) {
        this.count = count;
    }

    String dur(String data) {
        try {
            String dataCopy = data.replace("PT", "");
            int h = dataCopy.indexOf("H");
            int m = dataCopy.indexOf("M");
            int s = dataCopy.indexOf("S");
            String finalData = "";
            if (dataCopy.contains("H") && dataCopy.contains("M") && dataCopy.contains("S")) {
                finalData = pad(dataCopy.substring(0, h)) + ":" + pad(dataCopy.substring(h + 1, m)) + ":" + pad(dataCopy.substring(m + 1, s));
            }
            if (dataCopy.contains("H") && dataCopy.contains("M")) {
                return pad(dataCopy.substring(0, h)) + ":" + pad(dataCopy.substring(h + 1, m));
            }
            if (dataCopy.contains("M") && dataCopy.contains("S")) {
                return pad(dataCopy.substring(h + 1, m)) + ":" + pad(dataCopy.substring(m + 1, s));
            }
            if (dataCopy.contains("M")) {
                return pad(dataCopy.substring(h + 1, m)) + ":00";
            }
            if (dataCopy.contains("S")) {
                return "00:" + pad(dataCopy.substring(m + 1, s));
            }
            return finalData;
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println(data);
            return "";
        }
    }

    String pad(String data) {
        return data.length() > 1 ? data : "0" + data;
    }

    public String format(long num) {
        long temp = num / 1000000;
        if (temp > 0) {
            return temp + "M";
        }
        temp = num / 1000;
        if (temp > 0) {
            return temp + "K";
        }
        return String.valueOf(num);
    }
}

