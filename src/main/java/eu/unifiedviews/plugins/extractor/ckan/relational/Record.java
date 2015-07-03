package eu.unifiedviews.plugins.extractor.ckan.relational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.apache.commons.lang3.StringUtils;

public class Record {
    
    public Map<String, Object> fieldValues;

    public Record(JsonObject record) {
        
        fieldValues = new HashMap<String, Object>();
        String name;
        for (Entry<String, JsonValue> entry : record.entrySet()) {
            name = entry.getKey().replace("\uFEFF", "");
            fieldValues.put(name, getValue(entry.getValue()));
        }
    }

    public static Object getValue(JsonValue value) throws ClassCastException {
        ValueType type = value.getValueType();
        
        switch (type) {
            case NULL:
                return null;
            case STRING:
                // removing quotes at the start and end
                return value.toString().trim().replaceAll("^\"|\"$", "");
            case TRUE:
                return true;
            case FALSE:
                return false;
            case NUMBER:
                return Long.parseLong(value.toString());
            default:
                throw new ClassCastException(type + " is not supported."); 
        }
    }

    @Override
    public String toString() {
        return "Record [fieldValues=" + fieldValues + "]";
    }
    
    public String getSqlInsertValues(List<String> columnNames, boolean columnTypesToString) {
        List<Object> values = new ArrayList<Object>();
        Object value;
        for (String columnName : columnNames) {
            value = fieldValues.get(columnName);
            if (value == null) {
                values.add("NULL");
            } else if (columnTypesToString || value instanceof String) {
                values.add("'" + value + "'");
            } else {
                values.add(value);
            }
        }
        return "( " + StringUtils.join(values, ", ") + " )";
    }
}
