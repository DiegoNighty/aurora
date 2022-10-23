package com.schoolroyale.aurora.schemas.account;

import com.schoolroyale.aurora.schemas.address.Address;

import java.util.Collection;

public class AccountSession {

    private Collection<Address> addresses;

    private boolean online;
    private boolean premium;
    private boolean bedrockUser;

    private long lastSession;

    public AccountSession(long lastSession, boolean premium, boolean bedrockUser, Collection<Address> addresses) {
        this.lastSession = lastSession;
        this.premium = premium;
        this.bedrockUser = bedrockUser;
        this.addresses = addresses;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public long getLastSession() {
        return lastSession;
    }

    public void setLastSession(long lastSession) {
        this.lastSession = lastSession;
    }

    public Collection<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isBedrockUser() {
        return bedrockUser;
    }

    public void setBedrockUser(boolean bedrockUser) {
        this.bedrockUser = bedrockUser;
    }
}
