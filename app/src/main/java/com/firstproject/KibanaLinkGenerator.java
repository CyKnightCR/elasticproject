package com.firstproject;

import java.util.List;
import java.util.StringJoiner;

public class KibanaLinkGenerator {


    public static String generateKibanaLink( List<String> uuids, String timeFrom, String timeTo) {
        String timeRange = createTimeRange(timeFrom, timeTo);
        final String KIBANA_BASE_URL = Config.getProperty("es.kibanaBaseURL");
        final String CLUSTER_NAME = Config.getProperty("es.clusterName");
        String indexPattern = "monitoring_*";
        String query = createQuery(indexPattern, uuids, CLUSTER_NAME);

        return String.format("%s?_g=%s&_a=%s", KIBANA_BASE_URL, timeRange, query);
    }

    private static String createQuery(String indexPattern, List<String> uuids, String clusterName) {
        StringJoiner uuidJoiner = new StringJoiner(" OR ");
        for (String uuid : uuids) {
            uuidJoiner.add(String.format("uuid:\"%s\"", uuid));
        }

        String query = String.format("attributes.clusterName:\"%s\" AND (%s)", clusterName, uuidJoiner.toString());

        String kquery = String.format(
                "(columns:!(),filters:!(),index:'%s',interval:auto,query:(language:kuery,query:'%s'),sort:!())",
                indexPattern,
                query
        );

        return kquery;
    }

    private static String createTimeRange(String timeFrom, String timeTo) {
        String timeRange = String.format("(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:'%s',to:'%s'))", timeFrom, timeTo);
        return timeRange;
    }

//    public static void main(String[] args) {
//        String indexPattern = "monitoring_*";
//        List<String> uuids = List.of("8c1b4e60-c3df-4c3d-9690-fc0819d4b691", "7e64f83c-8667-400a-822f-e4e3dff4261a");
//        String timeFrom = "2024-07-15T12:39:00.000Z";
//        String timeTo = "2024-07-16T16:15:05.000Z";
//        String clusterName = "case1-es7";
//
//        String kibanaLink = generateKibanaLink(indexPattern, uuids, timeFrom, timeTo, clusterName);
//        System.out.println("Generated Kibana Link: " + kibanaLink);
//    }
}
