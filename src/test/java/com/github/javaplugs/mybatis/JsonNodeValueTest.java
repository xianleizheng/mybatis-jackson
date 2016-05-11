package com.github.javaplugs.mybatis;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class JsonNodeValueTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"{}", true},
            {"[]", true},
            {"null", true},
            {"1", false},
            {"{test:1}", false},
            {"[1,2,3]", false}
        });
    }

    private String input;

    private boolean expected;

    public JsonNodeValueTest(String input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void emptyOrNot() {
        JsonNodeValue value = JsonNodeValue.from(input);
        assertThat(value.isEmpty()).isEqualTo(expected);
    }
}
