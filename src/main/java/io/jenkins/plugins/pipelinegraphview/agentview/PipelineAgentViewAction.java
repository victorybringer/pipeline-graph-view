package io.jenkins.plugins.pipelinegraphview.agentview;



import io.jenkins.plugins.pipelinegraphview.utils.AbstractPipelineViewAction;

import org.jenkinsci.plugins.workflow.job.WorkflowRun;


public class PipelineAgentViewAction extends AbstractPipelineViewAction {
  public static final long LOG_THRESHOLD = 150 * 1024; // 150KB


  public PipelineAgentViewAction(WorkflowRun target) {
    super(target);
 
   
  }

  @Override
  public String getDisplayName() {
    return "Pipeline Agent";
  }

  @Override
  public String getUrlName() {
    return "pipeline-agent-dashboard";
  }

  @Override
  public String getIconClassName() {
    return "symbol-rocket-outline plugin-ionicons-api";
  }

 
}
