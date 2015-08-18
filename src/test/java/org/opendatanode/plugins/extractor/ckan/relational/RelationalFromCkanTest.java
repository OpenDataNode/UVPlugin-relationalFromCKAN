package org.opendatanode.plugins.extractor.ckan.relational;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendatanode.plugins.extractor.ckan.relational.DatabaseHelper;
import org.opendatanode.plugins.extractor.ckan.relational.DatastoreSearchResult;
import org.opendatanode.plugins.extractor.ckan.relational.RelationalFromCkan;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.relational.WritableRelationalDataUnit;

public class RelationalFromCkanTest {
    
    private DatastoreSearchResult result;
    
    private TestEnvironment env;
    private WritableRelationalDataUnit output;
    private RelationalFromCkanConfig_V2 config;
    
    @Before
    public void before() throws Exception {
        
        config = new RelationalFromCkanConfig_V2();
        config.setTableName("test");
        
        env = new TestEnvironment();
        output = env.createRelationalOutput("output");
        
        result = new DatastoreSearchResult(getDatastoreSearchResponseJson());
        Assert.assertEquals(0, result.offset);
        Assert.assertEquals(10, result.limit);
        Assert.assertEquals(2, result.total);
        Assert.assertEquals(4, result.fields.size());
        Assert.assertEquals(2, result.records.size());
    }
    
    @After
    public void after() throws Exception {
        env.release();
    }

    private JsonObject getDatastoreSearchResponseJson() {
        String ckanResponseJson =
        "{"
        + "\"fields\":["
                    + "{\"id\":\"id\", \"type\":\"int4\"},"
                    + "{\"id\":\"name\", \"type\":\"varchar\"},"
                    + "{\"id\":\"date\", \"type\":\"timestamp\"},"
                    + "{\"id\":\"aaa bbb\", \"type\":\"varchar\"}"
                    + "],"
        + "\"offset\":0,"
        + "\"limit\":10,"
        + "\"total\":2,"
        + "\"records\":["
                + "{\"id\":1, \"name\":\"aaa\", \"date\":\"2015-01-01 00:00:00.0\", \"aaa bbb\":null},"
                + "{\"id\":2, \"name\":null, \"date\":null, \"aaa bbb\":\"cccc\"}"
                + "]"
        + "}";
        
        JsonReaderFactory readerFactory = Json.createReaderFactory(Collections.<String, Object> emptyMap());
        JsonReader reader = readerFactory.createReader(new StringReader(ckanResponseJson));
        return reader.readObject();
    }
    
    @Test
    public void testCreateAndInsertQuery() throws Exception {
        String createQuery = RelationalFromCkan.prepareCreateTableQuery(config, result);
        String insertQuery = RelationalFromCkan.prepareInsertIntoQuery(config, result);
        
        DatabaseHelper.executeUpdate(createQuery, output);
        DatabaseHelper.executeUpdate(insertQuery, output);
        
        Connection conn = null;
        try {
            conn = output.getDatabaseConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM test WHERE id = ?");
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertEquals(1, rs.getInt("id"));
            Assert.assertEquals("aaa", rs.getString("name"));
            Assert.assertEquals("2015-01-01 00:00:00.0", rs.getString("date"));
            
            // test null
            ps = conn.prepareStatement("SELECT * FROM test WHERE id = ?");
            ps.setInt(1, 2);
            rs = ps.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertEquals(2, rs.getInt("id"));
            Assert.assertNull(rs.getString("name"));
            Assert.assertNull(rs.getString("date"));
            
            ps = conn.prepareStatement("SELECT t.id, t.\"aaa bbb\" FROM test t WHERE t.\"aaa bbb\" = 'cccc'");
            rs = ps.executeQuery();
            Assert.assertTrue(rs.next());
            Assert.assertEquals(2, rs.getInt("id"));
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}
