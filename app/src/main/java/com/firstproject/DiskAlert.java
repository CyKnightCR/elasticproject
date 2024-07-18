package com.firstproject;

import java.time.Instant;
import java.util.List;

public class DiskAlert extends Alerts {
    public DiskAlert(){
        report("\ndisk usage alert");
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
            finalUuid.add(q.getUuid());
        }

        //check shard relocation

        AnomalyDetector ad = new AnomalyDetector();
        ad.setRange(rangeStart, rangeEnd);
        List<Passing> shardRelocating = ad.checkRelocatingShard();
        if(!shardRelocating.isEmpty()){
            report("\nShard Relocating");
            for(Passing p: shardRelocating){
                Instant time = p.time;
                long val = p.val;
                String rep = "Time: "+time+"\tRelocating Shard Value: "+val;
                report(rep);
            }
        }


    }
}
