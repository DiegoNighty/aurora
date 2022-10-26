package com.schoolroyale.aurora.schemas.account;

import com.schoolroyale.aurora.schemas.mail.AccountMail;

import java.util.Collection;

public class AccountCredential {

    private String username;
    private String password;
    private Collection<AccountMail> mails;

    public AccountCredential(String username, String password, Collection<AccountMail> mails) {
        this.username = username;
        this.password = password;
        this.mails = mails;
    }

    public AccountCredential addMail(String mail) {
        mails.add(new AccountMail(mail, mails.isEmpty()));
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<AccountMail> getMails() {
        return mails;
    }

    public void setMails(Collection<AccountMail> mails) {
        this.mails = mails;
    }
}
