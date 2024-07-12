package com.firstproject;

import java.util.ArrayList;
import java.util.List;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.jetbrains.annotations.NotNull;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;

import java.time.Instant;

class QueryFilter {


    // now we have all the querySources of hits,.... how to process these????

    //1. find documents being fetched per second??
    // if lots of document being fetched -> pressure on memory -> memory limit -> cluster crash
    //upload fetchsize-timestamp to influx db from here.
    //another class which performs aggregation (queries influx) and gives result

    //filter based on some field value...

    private int thresholdTtm = 100000;
    private int thresholdTimeTakenMillis = 100000;
    private int thresholdQuerySize = 10000;
    private int thresholdFetchSize = 19999;
    private int thresholdTotalMatchCount = 10000;
    private int thresholdResponsesCount = 10000;
    private int thresholdHitsCount = 999;

    //individual queries which can be cause
    class IndFilter{
        public List<QueryLog> execTime(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTtm() > thresholdTtm)
                    .toList();

            return result;
        }
        public List<QueryLog> timeTakenMillis(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTimeTakenMillis() > thresholdTimeTakenMillis)
                    .toList();

            return result;
        }
        public List<QueryLog> ttm(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTtm()!= null && query.getAttributes().getTtm() > thresholdTtm)
                    .toList();

            return result;
        }

        public List<QueryLog> isWrite(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();

            result = sources.stream()
                    .filter(query ->  query.getAttributes().getOp()!=null && query.getAttributes().getOp().equals("bulkIndex"))
                    .toList();

            return result;
        }
        public List<QueryLog> isRefreshInline(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();

            result = sources.stream()
                    .filter(query ->  query.getAttributes().isRefreshInline()!=null && query.getAttributes().isRefreshInline())
                    .toList();

            return result;
        }
        public List<QueryLog> querySize(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getQuerySize() > thresholdQuerySize)
                    .toList();

            return result;
        }

        //not using fetch size. using hits count-> actual no of docs returned.

        public List<QueryLog> totalMatchCount(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTotalMatchCount() != null && query.getAttributes().getTotalMatchCount() > thresholdTotalMatchCount)
                    .toList();

            return result;
        }
        public List<QueryLog> totalResponsesCount(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getTotalResponsesCount()!=null && query.getAttributes().getTotalResponsesCount() > thresholdResponsesCount)
                    .toList();

            return result;
        }

        public List<QueryLog> hitsCount(@NotNull List<QueryLog> sources){
            List<QueryLog> result = new ArrayList<QueryLog>();
            result = sources.stream()
                    .filter(query -> query.getAttributes().getHitsCount() != null && query.getAttributes().getHitsCount() > thresholdHitsCount)
                    .toList();

            return result;
        }
    }

    //queries collectively putting pressure
    class AggFilter extends InfluxConnection{

        public void uploadData(@NotNull List<QueryLog> sources, String measurement){
            InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);

            try {
                WriteApiBlocking writeApi = client.getWriteApiBlocking();
                for (QueryLog log : sources) {
                    if(log.getAttributes().getHitsCount()!= null)
                    {
                        Point point = Point.measurement(measurement)
                                .addField("val", log.getAttributes().getHitsCount())
                                .time(Instant.ofEpochMilli(log.getTimestamp()), WritePrecision.MS);

                        writeApi.writePoint(point);
                    }
                }
            } finally {
                client.close();
            }

//
        }

        public void hitsCount(Instant startTime , Instant endTime, long aggHits) {
            InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);

            String flux = "from(bucket: \"" + bucket + "\")\n" +
                    "  |> range(start: "+ startTime +", stop: "+ endTime +")\n" +
                    "  |> filter(fn: (r) => r._measurement == \"hitsCount\")\n" +
                    "  |> aggregateWindow(every: 2s, fn: sum, createEmpty: false)\n" +
                    "  |> filter(fn: (r) => r._value > " + aggHits + ")\n" +
                    "  |> keep(columns: [\"_time\", \"_value\"])\n" +
                    "  |> yield(name: \"exceeding_values\")";

            QueryApi queryApi = client.getQueryApi();
            List<FluxTable> tables = queryApi.query(flux);

//            System.out.println("Time Window(1 sec) where hitsCount exceeds threshold value");
            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    System.out.println("Time: " + record.getTime() + ", Aggregate hitsCount exceeding: " + record.getValueByKey("_value"));
                }
            }

            client.close();
        }


    }


}
