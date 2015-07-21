package org.opendatanode.plugins.extractor.ckan.relational;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.VersionedConfig;

public class RelationalFromCkanConfig_V2 implements VersionedConfig<RelationalFromCkanConfig_V2> {
    
    private String tableName;
    
    private String packageId;
    
    private String resourceId;
    
    private boolean columnsAsString;
    
    private boolean showOnlyMyOrgDatasets = true;

    public RelationalFromCkanConfig_V2() {
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
    
    public boolean isShowOnlyMyOrgDatasets() {
        return showOnlyMyOrgDatasets;
    }

    public void setShowOnlyMyOrgDatasets(boolean showOnlyMyOrgDatasets) {
        this.showOnlyMyOrgDatasets = showOnlyMyOrgDatasets;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

    @Override
    public RelationalFromCkanConfig_V2 toNextVersion() throws DPUConfigException {
        return this;
    }
}
