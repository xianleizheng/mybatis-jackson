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

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Map PostgreSQL JSON type to value container with jackson JsonNode inside.
 * Use JSON string representation as intermediate data format.
 *
 * @see JsonNodeValue
 */
@MappedTypes({JsonNodeValue.class})
public class JsonNodeValueTypeHandler extends BaseTypeHandler<JsonNodeValue> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNodeValue parameter, JdbcType jdbcType) throws SQLException {
        if (parameter.isPresent()) {
            String json;
            if (parameter.hasDbSource()) {
                json = parameter.getSource();
            } else {
                try {
                    json = ReaderWriter.write(parameter.get());
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }
            ps.setString(i, json);
        } else {
            ps.setString(i, null);
        }
    }

    @Override
    public JsonNodeValue getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonSource = rs.getString(columnName);
        return JsonNodeValue.fromDb(jsonSource);
    }

    @Override
    public JsonNodeValue getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonSource = rs.getString(columnIndex);
        return JsonNodeValue.fromDb(jsonSource);
    }

    @Override
    public JsonNodeValue getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonSource = cs.getString(columnIndex);
        return JsonNodeValue.fromDb(jsonSource);
    }
}
