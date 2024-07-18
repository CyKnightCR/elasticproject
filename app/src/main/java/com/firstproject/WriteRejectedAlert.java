package com.firstproject;

import java.util.List;

public class WriteRejectedAlert extends Alerts {
    public WriteRejectedAlert(){
        report("Write rejected Alert");
    }

    public void filter() {

        QueryFilter filter = new QueryFilter();
        QueryFilter.IndFilter indFilter = filter.new IndFilter();

        List<QueryLog> result =  indFilter.isWrite(querySources);

        result = indFilter.timeTakenMillis(result);

        for(QueryLog q: result){
//            System.out.println(q.getDistributedTraceId()+" "+q.getAttributes().getTimeTakenMillis());
            String rep = "Query uuid : "+q.getUuid()+"\tTime taken by Query in millis: "+q.getAttributes().getTimeTakenMillis();
            report(rep);
            finalUuid.add(q.getUuid());
        }


    }

}
