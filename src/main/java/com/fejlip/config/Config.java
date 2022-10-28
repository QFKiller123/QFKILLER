package com.fejlip.config;

public class Config {
    private boolean enabled;
    private boolean debug;
    private int bedDelay;

    public Config() {
        this.enabled = false;
        this.bedDelay = 90;
        this.debug = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean toggleEnabled() {
        this.enabled = !this.enabled;
        return this.enabled;
    }

    public int getBedDelay() {
        return bedDelay;
    }

    public void setBedDelay(int bedDelay) {
        this.bedDelay = bedDelay;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean toggleDebug() {
        this.debug = !this.debug;
        return this.debug;
    }
}
