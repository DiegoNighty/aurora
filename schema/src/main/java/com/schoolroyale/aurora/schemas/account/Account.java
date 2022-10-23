package com.schoolroyale.aurora.schemas.account;

import java.util.UUID;

public record Account(String id, AccountSession session, AccountCredential credential) {

    public static Account create(UUID id, AccountSession session, AccountCredential credential) {
        return new Account(id.toString(), session, credential);
    }

}