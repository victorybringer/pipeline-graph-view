package io.jenkins.plugins.pipelinegraphview.utils;

import java.util.List;

public class PipelineGraphWithJob {

  private String jobName;
  private String buildNumber;
  private String buildtime;
  private List<PipelineStage> stages;
  private boolean complete = false;

  public PipelineGraphWithJob(List<PipelineStage> stages, boolean complete,String jobName,String buildNumber,String buildtime) {
    this.stages = stages;
    this.complete = complete;
    this.jobName = jobName;
    this.buildNumber = buildNumber;
    this.buildtime = buildtime;
  }

  public boolean isComplete() {
    return complete;
  }

  public List<PipelineStage> getStages() {
    return stages;
  }
  public String getjobName(){
    return jobName;

  }

  public String getbuildtime(){
    return buildtime;

  }
  public String getBuildNumber(){
    return buildNumber;
    
  }

}
