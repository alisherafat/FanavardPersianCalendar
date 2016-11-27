package com.fanavard.alisherafat.fanavardpersiancalendar.models;

/**
 * helps to modeling Google Events that exist in Account of user
 */
public class GoogleEvent {
    private String title;
    private String persianTime;

    public GoogleEvent(String title, String persianTime) {
        this.title = title;
        this.persianTime = persianTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPersianTime() {
        return persianTime;
    }

    public void setPersianTime(String persianTime) {
        this.persianTime = persianTime;
    }
}
