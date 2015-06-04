package eu.unifiedviews.plugins.extractor.ckan.relational;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RelationalFromCkanConfig_V1 {
    
    private String tableName;
    
    private String packageId;
    
    private String resourceId;
    
    private boolean columnsAsString;

    public RelationalFromCkanConfig_V1() {
    }
    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isColumnsAsString() {
        return columnsAsString;
    }

    public void setColumnsAsString(boolean columnsAsString) {
        this.columnsAsString = columnsAsString;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
