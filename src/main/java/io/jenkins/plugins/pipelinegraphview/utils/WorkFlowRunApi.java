package io.jenkins.plugins.pipelinegraphview.utils;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.Jenkins;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class WorkFlowRunApi {
    private static final Logger logger = LoggerFactory.getLogger(PipelineStepVisitor.class);

    public static Map<String,WorkflowRun> getAllWorkFlowRun(){

        Jenkins jenkins = Jenkins.get();
        Map<String,WorkflowRun> res = new HashMap<>();
        for (Job<?, ?> job : jenkins.getAllItems(Job.class)) {
            if (job instanceof WorkflowJob) {
                WorkflowJob workflowJob = (WorkflowJob) job;
                for (Run<?, ?> run : workflowJob.getBuilds()) {
                    if (run instanceof WorkflowRun) {
                        WorkflowRun workflowRun = (WorkflowRun) run;
                        res.put( workflowJob.getName()+ ";" + workflowRun.getDisplayName(),workflowRun);
                    }
                }
            } 
    }

    return res;
     
}

public static Map<String,WorkflowRun> getAllWorkFlowRunhistory(long starttime , long endtime){

    Jenkins jenkins = Jenkins.get();
    Map<String,WorkflowRun> res = new HashMap<>();
    for (Job<?, ?> job : jenkins.getAllItems(Job.class)) {
        if (job instanceof WorkflowJob) {
            WorkflowJob workflowJob = (WorkflowJob) job;
            for (Run<?, ?> run : workflowJob.getBuilds()) {
                if (run instanceof WorkflowRun) {
                    WorkflowRun workflowRun = (WorkflowRun) run;
                   
                        if (workflowRun.getTimeInMillis() > endtime  ){
                          continue;
                        }

                        if (workflowRun.getTimeInMillis() < starttime ){
                          break;
                          }
                    
                    res.put( workflowJob.getName()+ ";" + workflowRun.getDisplayName(),workflowRun);
                    logger.info( workflowJob.getName()+ ";" + workflowRun.getDisplayName());
                }
            }
        } 
}

return res;
 
}

}
