package io.jenkins.plugins.pipelinegraphview.utils;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.Jenkins;
import java.util.*;;
public class WorkFlowRunApi {
    

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
}
