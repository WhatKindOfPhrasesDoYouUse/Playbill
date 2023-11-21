package com.vyatsu.playbill;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateEditor extends PropertyEditorSupport {

    private final DateTimeFormatter formatter;

    public LocalDateEditor(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(LocalDate.parse(text, formatter));
    }

    @Override
    public String getAsText() {
        LocalDate value = (LocalDate) getValue();
        return value != null ? value.format(formatter) : "";
    }
}

