package com.startingblue.fourtooncookie.translation.sample;


import com.startingblue.fourtooncookie.translation.annotation.TranslatableField;
import jakarta.persistence.Id;

public class SampleEntity {

    @Id
    public Long id;

    @TranslatableField
    public String translatableField;

    @TranslatableField
    public String translatableField2;

    public String unTranslatableField;

    public String unTranslatableField2;

    public SampleEntity(Long id, String translatableField, String translatableField2, String unTranslatableField, String unTranslatableField2) {
        this.id = id;
        this.translatableField = translatableField;
        this.translatableField2 = translatableField2;
        this.unTranslatableField = unTranslatableField;
        this.unTranslatableField2 = unTranslatableField2;
    }
}
