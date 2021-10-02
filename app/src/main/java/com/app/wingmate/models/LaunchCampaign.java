package com.app.wingmate.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("LaunchCampaign")
public class LaunchCampaign extends ParseObject {
    private boolean launchCampaign;

    public LaunchCampaign(){

    }


    public boolean isLaunchCampaign() {
        return launchCampaign;
    }

    public void setLaunchCampaign(boolean launchCampaign) {
        this.launchCampaign = launchCampaign;
    }
}
