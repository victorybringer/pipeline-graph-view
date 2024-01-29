package io.jenkins.plugins.pipelinegraphview.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import io.jenkins.plugins.pipelinegraphview.utils.BlueRun.BlueRunResult;
import io.jenkins.plugins.pipelinegraphview.utils.BlueRun.BlueRunState;
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.graphanalysis.ForkScanner;
import org.jenkinsci.plugins.workflow.graphanalysis.StandardChunkVisitor;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.pipelinegraphanalysis.StageChunkFinder;
import org.jenkinsci.plugins.workflow.pipelinegraphanalysis.TimingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
/**
 * @author Vivek Pandey
 *     <p>Run your Jenkins instance with <code>-DNODE-DUMP-ENABLED</code> to turn on the logging
 *     when diagnosing bugs! You'll also need to have a logging config that enables debug for (at
 *     least) this class. Use <code>-Djava.util.logging.config.file=./logging.properties</code> to
 *     set a custom logging properties file from the command line, or do it from within the admin
 *     UI.
 */
public class PipelineNodeGraphVisitor2 extends StandardChunkVisitor {
  private final WorkflowRun run;



  public final ArrayDeque<FlowNodeWrapper> agenthistnodes = new ArrayDeque<>();

  

  public final Map<String, Stack<FlowNodeWrapper>> stackPerEnd = new HashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(PipelineNodeGraphVisitor.class);

  

  public PipelineNodeGraphVisitor2(WorkflowRun run) {
    this.run = run;
    
    FlowExecution execution = run.getExecution();
    if (execution != null) {
      try {
        ForkScanner.visitSimpleChunks(execution.getCurrentHeads(), this, new StageChunkFinder());
      } catch (final Throwable t) {
        // Log run ID, because the eventual exception handler (probably Stapler) isn't specific
        // enough to do so
        logger.error(
            "Caught a "
                + t.getClass().getSimpleName()
                + " traversing the graph for run "
                + run.getExternalizableId());
        throw t;
      }
    } else {
      logger.debug("Could not find execution for run " + run.getExternalizableId());
    }
  }
  
  public List<FlowNodeWrapper> getPipelineHistoryNodes() {
 
    return new ArrayList<>(agenthistnodes);
  }

  @Override
  public void atomNode(
      @CheckForNull FlowNode before,
      @NonNull FlowNode atomNode,
      @CheckForNull FlowNode after,
      @NonNull ForkScanner scan) {
 

    if (atomNode instanceof StepStartNode && PipelineNodeUtil.isAgentStart(atomNode)) {
       FlowNodeWrapper fn = new FlowNodeWrapper(atomNode, new NodeRunStatus(BlueRunResult.SUCCESS, BlueRunState.FINISHED), new TimingInfo(), run);
      
       agenthistnodes.add(fn);
      
    
    } 


  }


}
