package eu.unifiedviews.plugins.extractor.ckan.relational;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUContext.MessageType;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.resource.Resource;
import eu.unifiedviews.helpers.dataunit.resource.ResourceHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;


@DPU.AsExtractor
public class RelationalFromCkan extends AbstractDpu<RelationalFromCkanConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(RelationalFromCkan.class);

    /**
     * @deprecated Global configuration should be used {@link CONFIGURATION_SECRET_TOKEN}
     */
    @Deprecated
    public static final String CONFIGURATION_DPU_SECRET_TOKEN = "dpu.uv-e-filesFromCKAN.secret.token";

    /**
     * @deprecated Global configuration should be used {@link CONFIGURATION_CATALOG_API_LOCATION}
     */
    @Deprecated
    public static final String CONFIGURATION_DPU_CATALOG_API_LOCATION = "dpu.uv-e-filesFromCKAN.catalog.api.url";

    /**
     * @deprecated Global configuration should be used {@link CONFIGURATION_HTTP_HEADER}
     */
    @Deprecated
    public static final String CONFIGURATION_DPU_HTTP_HEADER = "dpu.uv-l-filesToCkan.http.header.";

    public static final String CONFIGURATION_SECRET_TOKEN = "org.opendatanode.CKAN.secret.token";

    public static final String CONFIGURATION_CATALOG_API_LOCATION = "org.opendatanode.CKAN.api.url";

    public static final String CONFIGURATION_HTTP_HEADER = "org.opendatanode.CKAN.http.header.";

    /**
     * Requesting rows in chunks
     */
    public static final int REQUEST_RECORD_LIMIT = 1000;

    @DataUnit.AsOutput(name = "output")
    public WritableRelationalDataUnit relationalOutput;

    private DPUContext context;
    
    public RelationalFromCkan() {
        super(RelationalFromCkanVaadinDialog.class, ConfigHistory.noHistory(RelationalFromCkanConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {
        this.context = this.ctx.getExecMasterContext().getDpuContext();
        String shortMessage = this.ctx.tr("dpu.ckan.starting", this.getClass().getSimpleName());
        String longMessage = String.valueOf(this.config);
        this.context.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        
        Map<String, String> environment = this.context.getEnvironment();
        final long pipelineId = this.context.getPipelineId();
        final String userId = this.context.getPipelineExecutionOwnerExternalId();
        String token = environment.get(CONFIGURATION_SECRET_TOKEN);
        if (StringUtils.isEmpty(token)) {
            LOG.debug("Missing global configuration for CKAN secret token, trying to use DPU specific configuration");
            token = environment.get(CONFIGURATION_DPU_SECRET_TOKEN);
            if (StringUtils.isEmpty(token)) {
                throw ContextUtils.dpuException(this.ctx, "errors.token.missing");
            }
        }
        
        String catalogApiLocation = environment.get(CONFIGURATION_CATALOG_API_LOCATION);
        if (StringUtils.isEmpty(catalogApiLocation)) {
            LOG.debug("Missing global configuration for CKAN API location, trying to use DPU specific configuration");
            catalogApiLocation = environment.get(CONFIGURATION_DPU_CATALOG_API_LOCATION);
            if (StringUtils.isEmpty(catalogApiLocation)) {
                throw ContextUtils.dpuException(this.ctx, "errors.api.missing");
            }
        }

        Map<String, String> additionalHttpHeaders = new HashMap<>();
        for (Map.Entry<String, String> configEntry : environment.entrySet()) {
            if (configEntry.getKey().startsWith(CONFIGURATION_HTTP_HEADER)) {
                String headerName = configEntry.getKey().replace(CONFIGURATION_HTTP_HEADER, "");
                String headerValue = configEntry.getValue();
                additionalHttpHeaders.put(headerName, headerValue);
            }
        }
        if (additionalHttpHeaders.isEmpty()) {
            LOG.debug("Missing global configuration for additional HTTP headers, trying to use DPU specific configuration");
            for (Map.Entry<String, String> configEntry : environment.entrySet()) {
                if (configEntry.getKey().startsWith(CONFIGURATION_DPU_HTTP_HEADER)) {
                    String headerName = configEntry.getKey().replace(CONFIGURATION_DPU_HTTP_HEADER, "");
                    String headerValue = configEntry.getValue();
                    additionalHttpHeaders.put(headerName, headerValue);
                }
            }
        }
        
        CatalogApiConfig apiConfig = new CatalogApiConfig(catalogApiLocation, pipelineId, userId, token, additionalHttpHeaders);
        
        if (ctx.canceled()) {
            throw ContextUtils.dpuExceptionCancelled(ctx);
        }
        
        final String packageId = config.getPackageId();
        final String resourceId = config.getResourceId();
        
        try {
            LOG.debug("Processing CKAN resource with id {} from package / dataset with id {}", resourceId, packageId);
            // create table from user config
            DatastoreSearchResult tableMetadata = RelationalFromCkanHelper.getDatastoreSearchResult(apiConfig, resourceId, 0, 0);
            
            String createTableQuery = prepareCreateTableQuery(config, tableMetadata);
            LOG.debug("Table create query: {}", createTableQuery);
            ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG, ctx.tr("dpu.create.new.table"), createTableQuery);
            DatabaseHelper.executeUpdate(createTableQuery, relationalOutput);
            ContextUtils.sendMessage(ctx, DPUContext.MessageType.DEBUG,
                    ctx.tr("dpu.created.new.table"),
                    DatabaseHelper.getTableMetadataInfo(config.getTableName(), relationalOutput));

            // add metadata
            final String tableName = config.getTableName().toUpperCase();
            relationalOutput.addExistingDatabaseTable(tableName, tableName);

            Resource resource = ResourceHelpers.getResource(relationalOutput, tableName);
            Date now = new Date();
            resource.setCreated(now);
            resource.setLast_modified(now);
            ResourceHelpers.setResource(relationalOutput, tableName, resource);

            // get the data from CKAN in chunks
            boolean recordsRemain = true;
            int offset = 0;
            while (!ctx.canceled() && recordsRemain) {
                LOG.debug("requesting record from {0} to {1}", offset, (offset + REQUEST_RECORD_LIMIT - 1));
                DatastoreSearchResult result = RelationalFromCkanHelper.getDatastoreSearchResult(apiConfig, resourceId, REQUEST_RECORD_LIMIT, offset);
                String insertIntoQuery = prepareInsertIntoQuery(config, result);
                LOG.debug("Insert into query: {}", insertIntoQuery);
                DatabaseHelper.executeUpdate(insertIntoQuery, relationalOutput);
                recordsRemain = offset + REQUEST_RECORD_LIMIT < result.total;
                offset += result.records.size();
            }
        } catch (DataUnitException e) {
            ContextUtils.dpuException(ctx, e, "Failed to process datastore resource.");
        } catch (Exception e) {
            String errMsg = "Failed to retrieve datastore resource data.";
            LOG.error(errMsg, e);
            this.context.sendMessage(MessageType.ERROR, errMsg, e.getMessage());
        }
    }
    
    protected static String prepareInsertIntoQuery(RelationalFromCkanConfig_V1 config, DatastoreSearchResult result) {
        StringBuilder sb = new StringBuilder();
        
        List<String> columnNames = result.getFieldList();
        List<String> values = new ArrayList<String>(result.records.size());
        for (Record r : result.records) {
            values.add(r.getSqlInsertValues(columnNames, config.isColumnsAsString()));
        }
        
        sb.append(String.format("INSERT INTO %s VALUES\n%s",
                config.getTableName(),
                StringUtils.join(values, ",\n")));
        
        sb.append(";");
        
        return sb.toString();
    }

    protected static String prepareCreateTableQuery(RelationalFromCkanConfig_V1 config, DatastoreSearchResult tableMetadata) {
        // inspired by TabularToRelational

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CREATE TABLE %s (", config.getTableName()));
        for (Iterator<Field> it = tableMetadata.fields.iterator(); it.hasNext();) {
            sb.append(it.next().getStringForCreateTable(config.isColumnsAsString()));
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(");");
        return sb.toString();
    }
}