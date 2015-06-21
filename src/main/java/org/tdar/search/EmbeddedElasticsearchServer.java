package org.tdar.search;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.search.index.LookupSource;

public class EmbeddedElasticsearchServer {
    // http://cupofjava.de/blog/2012/11/27/embedded-elasticsearch-server-for-tests/
    private static final String DEFAULT_DATA_DIRECTORY = "elasticsearch-data";

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticsearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticsearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.data", "target/" + dataDirectory);

        node = NodeBuilder.nodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
        node.close();
        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }

    public void setupIndexes() {

        for (LookupSource source : LookupSource.values()) {
            String indexName = source.getIndexName();
            createIndex(indexName);
        }

    }

    public void createIndex(String indexName) {
        Settings indexSettings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", 1)
                .put("number_of_replicas", 1)
                .build();
        try {
            CreateIndexRequest indexRequest = new CreateIndexRequest(indexName, indexSettings);
            CreateIndexResponse response = getClient().admin().indices().create(indexRequest).actionGet();
            getClient().admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
            logger.debug("{} : response.isAcknowledged(): {}",indexName, response.isAcknowledged());
        } catch (IndexAlreadyExistsException ime) {
            logger.debug("index already exists... {}", indexName);
        }
    }
}