package com.schoolroyale.aurora.schemas.address;

public record Address(String country, String number, long lastUse) {

    Address newUse(long lastUse) {
        return new Address(country, number, lastUse);
    }

}
