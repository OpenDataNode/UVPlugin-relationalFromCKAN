package org.opendatanode.plugins.extractor.ckan.relational;

import javax.json.JsonObject;

public class Resource implements CkanTreeItem {
    
    public String packageId;
    public String id;
    public String name;
    public String description;
    public String url;
    public boolean isDatastore;
    
    public Resource(JsonObject resource, String packageId) {
        this.packageId = packageId;
        this.id = resource.getString("id");
        this.name = resource.getString("name", "");
        this.description = resource.getString("description", "");
        this.url = resource.getString("url", "");
        this.isDatastore = resource.getString("url_type", "").equals("datastore");
        // information datastore_active isn't given by used api calls
        // so csv's pushed to datastore by extension can't be caounted in
    }
    
    /**
     * 
     * @param id
     * @param packageId
     */
    public Resource(String id, String packageId) {
        // these parameters are needed for config
        this.id = id;
        this.packageId = packageId;
    }

    @Override
    public String toString() {
        if (name != null && !name.isEmpty()) {
            return name;
        } else {
            return "Unnamed resource";
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
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
		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (packageId == null) {
			if (other.packageId != null)
				return false;
		} else if (!packageId.equals(other.packageId))
			return false;
		return true;
	}
}