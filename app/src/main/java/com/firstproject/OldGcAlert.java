package com.firstproject;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

//filter in ttm. heap dump???

public class OldGcAlert extends Alerts{
    public OldGcAlert(){
        report("Old Gc Alert");
    }
    @Override
    public void filter() {

        QueryFilter fil = new QueryFilter();
        QueryFilter.IndFilter indFilter = fil.new IndFilter();

        List<QueryLog> result1 =  indFilter.ttm(querySources);
        List<QueryLog> result2 =  indFilter.hitsCount(querySources);
        List<QueryLog> result3 =  indFilter.totalResponsesCount(querySources);
        List<QueryLog> result4 =  indFilter.totalMatchCount(querySources);


        //aggregation filter
        QueryFilter.AggFilter aggFilter = fil.new AggFilter();

//        aggFilter.uploadData(querySources,"hitsCount");

        List<String> aggHitsList = new ArrayList<>();
        aggHitsList =  aggFilter.hitsCount(startTime,endTime,1000);


        for(String r: aggHitsList) report(r);

        for(QueryLog q: result1){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\tTime taken by Query in millis: "+q.getAttributes().getTtm();
            report(rep);
        }

        for(QueryLog q: result2){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\tTotal hits Count: "+q.getAttributes().getHitsCount();
            report(rep);
        }

        for(QueryLog q: result3){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\tTotal Response Count: "+q.getAttributes().getTotalResponsesCount();
            report(rep);
        }

        for(QueryLog q: result4){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\t Total Match Count: "+q.getAttributes().getTotalMatchCount();
            report(rep);
        }

        // aggregate fetch size
    }
}
