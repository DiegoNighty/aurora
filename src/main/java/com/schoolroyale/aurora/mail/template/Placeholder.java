package com.schoolroyale.aurora.mail.template;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Placeholder {

    private final String placeholder;
    private final String value;

}
