package eu.unifiedviews.plugins.extractor.ckan.relational;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Organization implements CkanTreeItem {
    
    public String id;
    public String name;
    public String title;
    public String description;
    public List<Dataset> datasets;
    
    public Organization(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Organization(JsonObject org) {
        this.id = org.getString("id");
        this.name = org.getString("name", "");
        this.title = org.getString("title", "");
        this.description = org.getString("description", "");
        
        this.datasets = new ArrayList<Dataset>();
        
        loadDatasets(org.getJsonArray("packages"));
    }
    
    public void loadDatasets(JsonArray datasets) {
        this.datasets.clear();
        if (datasets != null) {
            for (JsonObject dataset : datasets.getValuesAs(JsonObject.class)) {
                this.datasets.add(new Dataset(dataset));
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title != null && !title.isEmpty() ? title : name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Organization other = (Organization) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
