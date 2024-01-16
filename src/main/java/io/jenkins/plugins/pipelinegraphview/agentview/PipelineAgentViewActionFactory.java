package io.jenkins.plugins.pipelinegraphview.agentview;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Action;
import java.util.Collection;
import java.util.Collections;
import jenkins.model.TransientActionFactory;
import hudson.model.*;

@Extension
public class PipelineAgentViewActionFactory extends TransientActionFactory<Job> {
  
  @Override
  public Class<Job> type() {
    return Job.class;
  }

  @NonNull
  @Override
  public Collection<? extends Action> createFor(@NonNull Job target) {
    PipelineAgentViewAction a = new PipelineAgentViewAction();
    return Collections.singleton(a);
  }
}
