package com.github.javaplugs.mybatis;

import com.fasterxml.jackson.core.TreeNode;
import java.io.Serializable;

public class JsonEntity implements Serializable {

    private static final long serialVersionUID = 2361613838967425855L;

    private long id;

    private TreeNode jsonArray;

    private TreeNode jsonObject;

    public JsonEntity() {
    }

    public JsonEntity(long id, TreeNode jsonArray, TreeNode jsonObject) {
        this.id = id;
        this.jsonArray = jsonArray;
        this.jsonObject = jsonObject;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TreeNode getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(TreeNode jsonArray) {
        this.jsonArray = jsonArray;
    }

    public TreeNode getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(TreeNode jsonObject) {
        this.jsonObject = jsonObject;
    }
}
