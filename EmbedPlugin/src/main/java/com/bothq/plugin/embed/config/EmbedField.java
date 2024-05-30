package com.bothq.plugin.embed.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmbedField {
    String title;
    String value;
    boolean inline;
}
