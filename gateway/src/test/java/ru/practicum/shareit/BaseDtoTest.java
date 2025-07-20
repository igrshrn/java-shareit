package ru.practicum.shareit;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public abstract class BaseDtoTest<T> {
    @Autowired
    protected JacksonTester<T> json;

    protected final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
}
