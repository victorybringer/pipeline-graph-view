package io.jenkins.plugins.pipelinegraphview.agentview;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Action;
import java.util.Collection;
import java.util.Collections;
import jenkins.model.TransientActionFactory;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

@Extension
public class PipelineAgentViewActionFactory extends TransientActionFactory<WorkflowRun> {

  @Override
  public Class<WorkflowRun> type() {
    return WorkflowRun.class;
  }

  @NonNull
  @Override
  public Collection<? extends Action> createFor(@NonNull WorkflowRun target) {
    
    PipelineAgentViewAction a = new PipelineAgentViewAction(target);
    return Collections.singleton(a);
  }
}
