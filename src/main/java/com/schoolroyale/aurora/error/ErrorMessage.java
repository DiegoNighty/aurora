package com.schoolroyale.aurora.error;

import java.util.Date;

public record ErrorMessage(int code, Date timestamp, String message, String description) {
}

