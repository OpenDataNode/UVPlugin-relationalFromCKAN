package org.opendatanode.plugins.extractor.ckan.relational;

import javax.json.JsonObject;

public class Field {
    
    public String id;
    
    public String type;

    public Field(JsonObject field) {
        this.id = field.getString("id").replace("\uFEFF", "");
        this.type = field.getString("type").replace("\uFEFF", "");
    }

    @Override
    public String toString() {
        return "Field [id=" + id + ", type=" + type + "]";
    }
    
    public String getStringForCreateTable(boolean ignoreType) {
        String name = id.contains(" ") ? "\"" + id + "\"" : id;

        if (ignoreType) {
            return name + " VARCHAR";
        } else {
            return name + " " + type;
        }
    }
}
