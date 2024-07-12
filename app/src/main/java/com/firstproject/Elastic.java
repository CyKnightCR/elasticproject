package com.firstproject;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
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
import java.util.Date;
import java.util.List;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramAggregation;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
//import co.elastic.clients.elasticsearch.core.search.aggregations.Bucket;
//import co.elastic.clients.elasticsearch.core.search.aggregations.DateHistogramBucket;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;



public class Elastic {

    public static List<QueryLog> getLogs(Instant start, Instant end, String ClusterName) {
        long startTime = start.toEpochMilli();
        long endTime = end.toEpochMilli();

        String hostName = Config.getProperty("es.hostName");
        int port = Integer.parseInt(Config.getProperty("es.port"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", port, "http"));
        RestClient restClient = builder.build();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {

            BoolQuery boolQuery = QueryBuilders.bool()
                    .must(QueryBuilders.range().field("timestamp")
                            .gte(JsonData.of(startTime))
                            .lte(JsonData.of(endTime))
                            .build()._toQuery())
                    .must(QueryBuilders.match().field("attributes.clusterName").query(ClusterName).build()._toQuery())
                    .build();

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("monitoring")
                    .query(boolQuery._toQuery())
                    .size(1000)
                    .build();

            SearchResponse<QueryLog> searchResponse = client.search(searchRequest, QueryLog.class);

            // Process search results
            List<Hit<QueryLog>> hits = searchResponse.hits().hits();
            if(hits.isEmpty()) System.out.println("empty hits received from elasticsearch");
            else System.out.println("hits received");

            List<QueryLog> querySources = new ArrayList<>();
            for (Hit<QueryLog> hit : hits) {

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



//    public static void agg(Instant start , Instant end){
//
//        long startTime = (long) start.toEpochMilli();
//        long endTime = (long) end.toEpochMilli();
//
//        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
//
//        // Create the transport with the Jackson mapper
//        RestClientTransport transport = new RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());
//
//        // Create the API client
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        try {
//            // Define the time range for the query
//              // Adjust end time as needed
//
//            // Execute a search query with a sum aggregation on the "hitsCount" field
//            SearchResponse<JsonData> searchResponse = client.search(s -> s
//                    .index("monitoring") // Replace with your index name
//                    .query(q -> q
//                            .range(r -> r
//                                    .field("timestamp") // Adjust this field name to your timestamp field
//                                    .gte(JsonData.of(startTime))
//                                    .lte(JsonData.of(endTime))
//                            )
//                    )
//                    .size(0)
//                    .aggregations("fetchSizeSum", a -> a
//                            .sum(sum -> sum
//                                    .field("hitsCount") // Adjust if your field name is different
//                            )
//                    ), JsonData.class);
//
//            // Extract and print the sum from the response
//            JsonData sumResult = (JsonData) searchResponse.aggregations().get("fetchSizeSum");
//            System.out.println("Sum of hitsCount: " + sumResult.toJson());
//
//        }
//        catch (Exception e){
//            System.out.println("aggregation error in elastic call");
//        }
//        finally {
//            try{
//                restClient.close();
//            }
//            catch(Exception e){
//                System.out.println("elastic client close error");
//            }
//
//        }
//    }

//    public static void a(){
//        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
//
//        RestClientTransport transport = new RestClientTransport(restClient, new co.elastic.clients.json.jackson.JacksonJsonpMapper());
//
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        try {
//            String startTime = "2023-01-01T00:00:00Z";
//            String endTime = "2023-01-02T00:00:00Z";
//
//            SearchResponse<JsonData> searchResponse = client.search(s -> s
//                    .index("monitoring")
//                    .size(0)
//                    .aggregations("time_buckets", a -> a
//                            .dateHistogram(dh -> dh
//                                    .field("timestamp") // Adjust this field name to your timestamp field
//                                    .fixedInterval(DateHistogramAggregation.fixedInterval.of("5s")) // 5-second intervals
//                                    .subAggregations("fetchSizeSum", sa -> sa
//                                            .sum(sum -> sum.field("hitsCount")) // Sum aggregation on hitsCount
//                                    )
//                            )
//                    ), JsonData.class);
//
//            List<DateHistogramBucket> buckets = searchResponse.aggregations().get("time_buckets").dateHistogram().buckets().array();
//            for (DateHistogramBucket bucket : buckets) {
//                String keyAsString = bucket.keyAsString();
//                double fetchSizeSum = bucket.aggregations().get("fetchSizeSum").sum().value();
//                System.out.println("Time: " + keyAsString + ", Sum of hitsCount: " + fetchSizeSum);
//            }
//
//        } catch (IOException e) {
//            System.err.println("An IOException occurred while performing the search operation: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            try {
//                restClient.close();
//            } catch (IOException e) {
//                System.err.println("Failed to close the Elasticsearch client: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }


}
