package org.opendatanode.plugins.extractor.ckan.relational;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class DatastoreSearchResult {
    
    public List<Field> fields;
    
    public List<Record> records;
    
    public int total;
    
    public int limit;

    public int offset; 

    /**
     * CKAN default value, see ckan datastore API
     */
    public static final int CKAN_DATASTORE_DEFAULT_LIMIT = 100;
    
    public DatastoreSearchResult(JsonObject datastoreSearchResult) {
        this.total = datastoreSearchResult.getInt("total", -1);
        this.limit = datastoreSearchResult.getInt("limit", CKAN_DATASTORE_DEFAULT_LIMIT);
        this.offset = datastoreSearchResult.getInt("offset", 0);
        
        this.fields = new ArrayList<Field>();
        this.records = new ArrayList<Record>();
        
        loadFields(datastoreSearchResult.getJsonArray("fields"));
        loadRecords(datastoreSearchResult.getJsonArray("records"));
    }

    private void loadRecords(JsonArray records) {
        if (records != null) {
            this.records.clear();
            for (JsonObject record : records.getValuesAs(JsonObject.class)) {
                this.records.add(new Record(record));
            }
        }
    }

    private void loadFields(JsonArray fields) {
        if (fields != null) {
            this.fields.clear();
            Field f = null;
            for (JsonObject field : fields.getValuesAs(JsonObject.class)) {
                f = new Field(field);
                // _id is CKAN internal primary key
                if (!f.id.equalsIgnoreCase("_id")) {
                    this.fields.add(f);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "DatastoreSearchResult [fields=" + fields + ", records=" + records + ", total=" + total + ", limit=" + limit + ", offset=" + offset + "]";
    }
    
    public List<String> getFieldList() {
        List<String> fieldList = new ArrayList<String>(fields.size());
        for (Field f : this.fields) {
            fieldList.add(f.id);
        }
        return fieldList;
    }
}
