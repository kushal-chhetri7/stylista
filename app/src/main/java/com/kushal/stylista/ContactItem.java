package com.kushal.stylista;

public class ContactItem {
    private int iconResId;
    private String title;
    private String detail;
    private boolean expanded;

    public ContactItem(int iconResId, String title, String detail) {
        this.iconResId = iconResId;
        this.title = title;
        this.detail = detail;
        this.expanded = false;
    }

    public int getIconResId() { return iconResId; }
    public String getTitle() { return title; }
    public String getDetail() { return detail; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}

