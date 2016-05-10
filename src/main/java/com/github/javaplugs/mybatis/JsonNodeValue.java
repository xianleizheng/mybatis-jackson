/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Vladislav Zablotsky
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. 
 */
package com.github.javaplugs.mybatis;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.NoSuchElementException;

/**
 * Value container that transfer JSON from/into DB.
 * Main feature of this container is lazy initializing while reading values from DB.
 * It will build JsonNode object only at first call of {@link JsonNodeValue#get()}.
 */
public class JsonNodeValue {

    public static JsonNodeValue EMPTY = new JsonNodeValue();

    private final String source;

    private boolean dbSource;

    private JsonNode value;

    private JsonNodeValue() {
        this.source = null;
        this.value = null;
    }

    private JsonNodeValue(String source) {
        if (source != null) {
            source = source.trim();
            this.source = source.isEmpty() ? null : source;
        } else {
            this.source = null;
        }
        this.value = null;
    }

    private JsonNodeValue(JsonNode value) {
        this.value = value;
        this.source = null;
    }

    /**
     * Build value container from JsonNode object.
     *
     * @param node JSON node or null
     */
    public static JsonNodeValue from(JsonNode node) {
        return node == null ? EMPTY : new JsonNodeValue(node);
    }

    /**
     * Build value container from JSON string.
     * NOTE that if input is not JSON than exception in {@link JsonNodeValue#get()} will be thrown.
     *
     * @param json JSON string or null
     */
    public static JsonNodeValue from(String json) {
        if (json == null || json.isEmpty()) {
            return EMPTY;
        }
        json = json.trim();
        return json.isEmpty() ? EMPTY : new JsonNodeValue(json);
    }

    static JsonNodeValue fromDb(String json) {
        JsonNodeValue v = from(json);
        if (v.isPresent()) {
            v.dbSource = true;
        }
        return v;
    }

    /**
     * Test input value and return not null - value from input or empty object.
     */
    public static JsonNodeValue orEmpty(JsonNodeValue node) {
        return node == null || node.isNotPresent() ? EMPTY : node;
    }

    /**
     * Check if nested value is present.
     */
    public boolean isPresent() {
        return value != null || source != null;
    }

    /**
     * Check if nested value does not exists.
     */
    public boolean isNotPresent() {
        return !isPresent();
    }

    /**
     * Return JSON node value (will parse node from string at first call).
     * WARNING if object constructed using JSON as string, and this string is invalid,
     * that exception will be thrown.
     *
     * @return Not null value.
     * @throws NoSuchElementException if value is not present.
     * @throws RuntimeException On JSON parsing errors.
     */
    public JsonNode get() throws NoSuchElementException, RuntimeException {
        if (!isPresent()) {
            throw new NoSuchElementException("No value present");
        }

        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    try {
                        value = ReaderWriter.readTree(source);
                    } catch (Exception ex) {
                        throw new RuntimeException("Can not parse JSON string. " + ex.getMessage(), ex);
                    }
                }
            }
        }
        return value;
    }

    /**
     * See {@link JsonNodeValue#get()}
     */
    public JsonNode getValue() {
        return get();
    }

    boolean hasDbSource() {
        return this.dbSource && this.source != null;
    }

    String getSource() {
        return this.source;
    }
}
