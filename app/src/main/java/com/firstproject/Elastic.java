package com.firstproject;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;



public class Elastic {

    public static List<QueryLog> getLogs(Instant start, Instant end, String ClusterName) {
        long startTime = start.toEpochMilli();
        long endTime = end.toEpochMilli();
        // Initialize RestClient
        String hostName = Config.getProperty("es.hostName");
//        int port = (Integer) Config.getProperty("es.port");
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        RestClient restClient = builder.build();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {
            // Create query
            BoolQuery boolQuery = QueryBuilders.bool()
                    .must(QueryBuilders.range().field("timestamp")
                            .gte(JsonData.of(startTime))
                            .lte(JsonData.of(endTime))
                            .build()._toQuery())
                    .must(QueryBuilders.match().field("attributes.clusterName").query(ClusterName).build()._toQuery())
                    .build();

            // Create search request
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("monitoring")
                    .query(boolQuery._toQuery())
                    .size(1000)
                    .build();

            // Execute search
            SearchResponse<QueryLog> searchResponse = client.search(searchRequest, QueryLog.class);

            // Process search results
            List<Hit<QueryLog>> hits = searchResponse.hits().hits();
            if(hits.isEmpty()) System.out.println("empty hits received from elasticsearch");
            else System.out.println("hits received");

            List<QueryLog> querySources = new ArrayList<>();
            for (Hit<QueryLog> hit : hits) {
//                System.out.println("worjing");

                QueryLog log = hit.source();
                querySources.add(log);
            }
            return querySources;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                transport.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

//    public static void agg(List<QueryLog> querySources){
//
//
//    }
}
