package com.firstproject;

import java.sql.Time;
import java.time.Instant;
import java.util.List;

public class DiskAlert extends Alerts {
    public DiskAlert(){
        report("disk usage alert");
    }

    @Override
    void filter() {
        //finding query with refreshInline true
        QueryFilter filter = new QueryFilter();
        QueryFilter.IndFilter indFilter = filter.new IndFilter();

        List<QueryLog> result =  indFilter.isRefreshInline(querySources);
        result = indFilter.ttm(result);

        if(!result.isEmpty()) report("\nQueries with refreshInline=true");

        for(QueryLog q: result){
            String rep = "time: "+q.getDate()+"\tquery uuid: "+q.getUuid()+"\tTime ttm: "+q.getAttributes().getTtm();
            report(rep);
        }

        //check shard relocation

        AnomalyDetector ad = new AnomalyDetector();
        ad.setRange(startTime,endTime);
        List<Passing> shardRelocating = ad.checkRelocatingShard();
        report("\nShard Relocating");
        if(!shardRelocating.isEmpty()){
            for(Passing p: shardRelocating){
                Instant time = p.time;
                long val = p.val;
                String rep = "Time: "+time+"\tRelocating Shard Value: "+val;
                report(rep);
            }
        }


    }
}
