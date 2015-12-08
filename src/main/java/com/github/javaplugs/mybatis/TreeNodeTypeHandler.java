/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Vladislav Zablotsky
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/**
 * Map PostgreSQL JSON type to jackson JsonNode.
 * Just use json string representation as intermediate data format.
 *
 * @see JsonNode
 */
@MappedTypes({JsonNode.class, TreeNode.class, ArrayNode.class, ObjectNode.class})
public class TreeNodeTypeHandler extends BaseTypeHandler<TreeNode> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TreeNode parameter, JdbcType jdbcType) throws SQLException {
        StringWriter sw = new StringWriter();
        JsonFactory f = new JsonFactory();
        try (JsonGenerator jgen = f.createGenerator(sw)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeTree(jgen, parameter);
            ps.setString(i, sw.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public TreeNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonSource = rs.getString(columnName);
        if (jsonSource != null) {
            return fromString(jsonSource);
        }
        return null;
    }

    @Override
    public TreeNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonSource = rs.getString(columnIndex);
        if (jsonSource != null) {
            return fromString(jsonSource);
        }
        return null;
    }

    @Override
    public TreeNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonSource = cs.getString(columnIndex);
        if (jsonSource != null) {
            return fromString(jsonSource);
        }
        return null;
    }

    private TreeNode fromString(String source) {
        return source == null ? null : new TreeNodeLazyWrapper(source);
    }
}
