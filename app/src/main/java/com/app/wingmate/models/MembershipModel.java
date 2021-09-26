package com.app.wingmate.models;

public class MembershipModel {
    String packageText;
    String packagePrice;
    Boolean isSelected;

    public MembershipModel() {
    }

    public MembershipModel(String packageText, String packagePrice, Boolean isSelected) {
        this.packageText = packageText;
        this.packagePrice = packagePrice;
        this.isSelected = isSelected;
    }

    public String getPackageText() {
        return packageText;
    }

    public void setPackageText(String packageText) {
        this.packageText = packageText;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }


}
