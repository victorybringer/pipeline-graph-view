package io.jenkins.plugins.pipelinegraphview.utils;

import static java.util.Collections.emptyList;

import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Item;
import hudson.model.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.jenkins.plugins.pipelinegraphview.utils.WorkFlowRunApi;
import org.jenkinsci.plugins.workflow.actions.LogAction;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineGraphApi {
  private static final Logger logger = LoggerFactory.getLogger(PipelineStepApi.class);
  private final transient WorkflowRun run;

  public PipelineGraphApi(WorkflowRun run) {
    this.run = run;
  }

  public Integer replay() throws ExecutionException, InterruptedException, TimeoutException {
    run.checkPermission(Item.BUILD);

    CauseAction causeAction = new CauseAction(new Cause.UserIdCause());

    if (!run.getParent().isBuildable()) {
      return null;
    }

    Queue.Item item = Queue.getInstance().schedule2(run.getParent(), 0, causeAction).getItem();
    if (item == null) {
      return null;
    }

    return run.getParent().getNextBuildNumber();
  }


  private static List<PipelineStageInternal> convertNodes(List<FlowNodeWrapper> l){

            return l.stream()
            .map(
                flowNodeWrapper -> {
                  String state = flowNodeWrapper.getStatus().getResult().name();
                  if (! state.equals("AGENT") && flowNodeWrapper.getStatus().getState() != BlueRun.BlueRunState.FINISHED) {
                    state = flowNodeWrapper.getStatus().getState().name().toLowerCase(Locale.ROOT);
                  }
    
                  String name_id = flowNodeWrapper.getDisplayName();
                  if (PipelineNodeUtil.isAgentStart(flowNodeWrapper.getNode())){
                      
                  
                      name_id = PipelineNodeUtil.getAgentName(flowNodeWrapper.getNode());
                    
            
                  }
                  return new PipelineStageInternal(
                      flowNodeWrapper
                          .getId(), // TODO no need to parse it BO returns a string even though the
                      // datatype is number on the frontend
                      name_id,
                      flowNodeWrapper.getParents().stream()
                          .map(FlowNodeWrapper::getId)
                          .collect(Collectors.toList()),
                      state,
                      50, // TODO how ???
                     flowNodeWrapper.getType().name(),
                     name_id, // TODO blue ocean uses timing information: "Passed in 0s"
                      flowNodeWrapper.isSynthetic());
                })
            .collect(Collectors.toList());
  }
  
  private static List<PipelineStageInternal> convertNodes2(List<FlowNodeWrapper> l, WorkflowRun run2){

    return l.stream()
    .map(
        flowNodeWrapper -> {
          String state = flowNodeWrapper.getStatus().getResult().name();
          if (! state.equals("AGENT") && flowNodeWrapper.getStatus().getState() != BlueRun.BlueRunState.FINISHED) {
            state = flowNodeWrapper.getStatus().getState().name().toLowerCase(Locale.ROOT);
          }

          String name_id = flowNodeWrapper.getDisplayName();
          if (PipelineNodeUtil.isAgentStart(flowNodeWrapper.getNode())){
              
          
              name_id = PipelineNodeUtil.getAgentName(flowNodeWrapper.getNode());
            
    
          }
          PipelineStageInternal res = null;
          String parentid = flowNodeWrapper.getNode().getAllEnclosingIds().get(0);
          try {
            res = new PipelineStageInternal(
              flowNodeWrapper
                  .getId(), // TODO no need to parse it BO returns a string even though the
              // datatype is number on the frontend
              name_id,
              flowNodeWrapper.getParents().stream()
                  .map(FlowNodeWrapper::getId)
                  .collect(Collectors.toList()),
                  run2.getExecution().getNode(parentid).getDisplayName(),
              50, // TODO how ???
             flowNodeWrapper.getType().name(),
             PipelineNodeUtil.getAgentStartTime(flowNodeWrapper.getNode()) +";" + parentid,
              flowNodeWrapper.isSynthetic());
          }
          catch (Exception e)  {
             res = null;
          }
          return  res;
        }).collect(Collectors.toList());
    
}

  private List<PipelineStageInternal> getPipelineNodes() {
    
    PipelineNodeGraphVisitor builder = new PipelineNodeGraphVisitor(run);
    return convertNodes(builder.getPipelineNodes());
  }

  private List<PipelineStageInternal> getPipelineHistoryNodes() {
    
    PipelineNodeGraphVisitor2 builder = new PipelineNodeGraphVisitor2(run);
    return convertNodes2(builder.getPipelineHistoryNodes(),run);
  }


  private static List<PipelineStageInternal> getPipelineNodesWithRun(WorkflowRun run0) {
    
    PipelineNodeGraphVisitor builder = new PipelineNodeGraphVisitor(run0);
    return convertNodes(builder.getPipelineNodes());
  }
  
  private static List<PipelineStageInternal> getPipelineHistoryNodesWithRun(WorkflowRun run0) {
    
    PipelineNodeGraphVisitor2 builder = new PipelineNodeGraphVisitor2(run0);
    return convertNodes2(builder.getPipelineHistoryNodes(),run0);
  }
  
  public static List <PipelineGraphWithJob> getallJobshistory(String starttime,String endtime){
    
    List <PipelineGraphWithJob> res = new ArrayList<>();
    
    Map<String,WorkflowRun> allrun = WorkFlowRunApi.getAllWorkFlowRun();
     
    for (Map.Entry<String,WorkflowRun> e: allrun.entrySet()){
    
     if(starttime != null && endtime != null){
      long start = Long.parseLong(starttime);
    long end = Long.parseLong(endtime);
          logger.info(e.getValue().getTimeInMillis() + "" );
          logger.info(e.getValue().getFullDisplayName() );
          if (e.getValue().getTimeInMillis() > end || e.getValue().getTimeInMillis() < start ){
            continue;
          }
         
     }
      PipelineGraph pg = createHistoryTreeWithRun(e.getValue());
     res.add(new PipelineGraphWithJob(pg.getStages(),pg.isComplete(),e.getKey().split(";")[0],e.getKey().split(";")[1],e.getValue().getTimestampString()));
    }
    
    return res;

 }
 public static List <PipelineGraphWithJob> getallJobs(){
    
  List <PipelineGraphWithJob> res = new ArrayList<>();
  
  Map<String,WorkflowRun> allrun = WorkFlowRunApi.getAllWorkFlowRun();
  
  for (Map.Entry<String,WorkflowRun> e: allrun.entrySet()){
   PipelineGraph pg = createTreeWithRun(e.getValue());
   res.add(new PipelineGraphWithJob(pg.getStages(),pg.isComplete(),e.getKey().split(";")[0],e.getKey().split(";")[1],e.getValue().getTimestampString2()));
  }
  
  return res;

}
  public PipelineGraph createGraph() {
    List<PipelineStageInternal> stages = getPipelineNodes();

    // id => stage
    Map<String, PipelineStageInternal> stageMap =
        stages.stream()
            .collect(
                Collectors.toMap(
                    PipelineStageInternal::getId, stage -> stage, (u, v) -> u, LinkedHashMap::new));

    Map<String, List<String>> stageToChildrenMap = new HashMap<>();

    List<String> stagesThatAreNested = new ArrayList<>();

    Map<String, String> nextSiblingToOlderSibling = new HashMap<>();

    List<String> stagesThatAreChildrenOrNestedStages = new ArrayList<>();
    stages.forEach(
        stage -> {
         
          if (stage.getParents().isEmpty()) {
            stageToChildrenMap.put(stage.getId(), new ArrayList<>());
          } else if (stage.getType().equals("PARALLEL")) {
            String parentId = stage.getParents().get(0); // assume one parent for now
            List<String> childrenOfParent =
                stageToChildrenMap.getOrDefault(parentId, new ArrayList<>());
            childrenOfParent.add(stage.getId());
            stageToChildrenMap.put(parentId, childrenOfParent);
            stagesThatAreChildrenOrNestedStages.add(stage.getId());
          } else if (stageMap.get(stage.getParents().get(0)).getType().equals("PARALLEL")) {
            String parentId = stage.getParents().get(0);
            PipelineStageInternal parent = stageMap.get(parentId);
            parent.setSeqContainerName(parent.getName());
            parent.setName(stage.getName());
            parent.setSequential(true);
            parent.setType(stage.getType());
            parent.setTitle(stage.getTitle());
            parent.setCompletePercent(stage.getCompletePercent());
            stage.setSequential(true);

            nextSiblingToOlderSibling.put(stage.getId(), parentId);
            stagesThatAreNested.add(stage.getId());
            stagesThatAreChildrenOrNestedStages.add(stage.getId());
            // nested stage of nested stage
          } else if (stagesThatAreNested.contains(
              stageMap.get(stage.getParents().get(0)).getId())) {
            PipelineStageInternal parent =
                stageMap.get(nextSiblingToOlderSibling.get(stage.getParents().get(0)));
            // shouldn't happen but found it after restarting a matrix build
            // this breaks the layout badly but prevents a null pointer
            if (parent != null) {
              stage.setSequential(true);
              parent.setNextSibling(stage);
              stagesThatAreNested.add(stage.getId());
              stagesThatAreChildrenOrNestedStages.add(stage.getId());
            }
          }
        });

    List<PipelineStage> stageResults =
        stageMap.values().stream()
            .map(
                pipelineStageInternal -> {
                  List<PipelineStage> children =
                      stageToChildrenMap.getOrDefault(pipelineStageInternal.getId(), emptyList())
                          .stream()
                          .map(mapper(stageMap, stageToChildrenMap))
                          .collect(Collectors.toList());

                  return pipelineStageInternal.toPipelineStage(children);
                })
            .filter(stage -> !stagesThatAreChildrenOrNestedStages.contains(stage.getId()))
            .collect(Collectors.toList());

    FlowExecution execution = run.getExecution();
    return new PipelineGraph(stageResults, execution != null && execution.isComplete());
  }

  private static Function<String, PipelineStage> mapper(
      Map<String, PipelineStageInternal> stageMap, Map<String, List<String>> stageToChildrenMap) {

    return id -> {
      List<String> orDefault = stageToChildrenMap.getOrDefault(id, emptyList());
      List<PipelineStage> children =
          orDefault.stream().map(mapper(stageMap, stageToChildrenMap)).collect(Collectors.toList());
      return stageMap.get(id).toPipelineStage(children);
    };
  }

  /*
   * Create a Tree from the GraphVisitor.
   * Original source: https://github.com/jenkinsci/workflow-support-plugin/blob/master/src/main/java/org/jenkinsci/plugins/workflow/support/visualization/table/FlowGraphTable.java#L126
   */
  public static PipelineGraph createHistoryTreeWithRun(WorkflowRun run0) {
    List<PipelineStageInternal> stages = getPipelineHistoryNodesWithRun(run0);

    FlowExecution execution = run0.getExecution();
    if (execution == null) {
      // If we don't have an execution - e.g. if the Pipeline has a syntax error - then return an
      // empty graph.
      return new PipelineGraph(new ArrayList<>(), false);
    }
   

    List<PipelineStage> stageResults =
        stages.stream()
            .map(
                pipelineStageInternal -> {
                  
                  return pipelineStageInternal.toPipelineStage(new ArrayList<>());
                })
            
            .collect(Collectors.toList());
    return new PipelineGraph(stageResults, execution.isComplete());
  }

  public static PipelineGraph createTreeWithRun(WorkflowRun run0) {
    List<PipelineStageInternal> stages = getPipelineNodesWithRun(run0);
    
    List<String> topLevelStageIds = new ArrayList<>();

    // id => stage
    Map<String, PipelineStageInternal> stageMap =
        stages.stream()
            .collect(
                Collectors.toMap(
                    PipelineStageInternal::getId, stage -> stage, (u, v) -> u, LinkedHashMap::new));

    Map<String, List<String>> stageToChildrenMap = new HashMap<>();

    FlowExecution execution = run0.getExecution();
    if (execution == null) {
      // If we don't have an execution - e.g. if the Pipeline has a syntax error - then return an
      // empty graph.
      return new PipelineGraph(new ArrayList<>(), false);
    }
    stages.forEach(
        stage -> {
          try {

            
            FlowNode stageNode = execution.getNode(stage.getId());
            if (stageNode == null) {
              return;
            }
            List<String> ancestors = getAncestors(stage, stageMap);
            String treeParentId = null;
            // Compare the list of GraphVistor ancestors to the IDs of the enclosing node in the
            // execution.
            // If a node encloses another node, it means it's a tree parent, so the first ancestor
            // ID we find
            // which matches an enclosing node then it's the stages tree parent.
            List<String> enclosingIds = stageNode.getAllEnclosingIds();
           
               
            for (String ancestorId : ancestors) {
              if (enclosingIds.contains(ancestorId)) {
                treeParentId = ancestorId;
                break;
              }
            }

            if (PipelineNodeUtil.isAgentStart(stageNode)){
              treeParentId = enclosingIds.get(0);
            }
            if (treeParentId != null) {
              List<String> childrenOfParent =
                  stageToChildrenMap.getOrDefault(treeParentId, new ArrayList<>());
              childrenOfParent.add(stage.getId());
              stageToChildrenMap.put(treeParentId, childrenOfParent);
            } else {
              // If we can't find a matching parent in the execution and GraphVistor then this is a
              // top level node.
              stageToChildrenMap.put(stage.getId(), new ArrayList<>());
              topLevelStageIds.add(stage.getId());
            }
          } catch (java.io.IOException ex) {
            logger.error(
                "Caught a "
                    + ex.getClass().getSimpleName()
                    + " when trying to find parent of stage '"
                    + stage.getName()
                    + "'");
          }
        });

    List<PipelineStage> stageResults =
        stageMap.values().stream()
            .map(
                pipelineStageInternal -> {
                  List<PipelineStage> children =
                      stageToChildrenMap.getOrDefault(pipelineStageInternal.getId(), emptyList())
                          .stream()
                          .map(mapper(stageMap, stageToChildrenMap))
                          .collect(Collectors.toList());

                  return pipelineStageInternal.toPipelineStage(children);
                })
            .filter(stage -> topLevelStageIds.contains(stage.getId()))
            .collect(Collectors.toList());
    return new PipelineGraph(stageResults, execution.isComplete());
  }

  public PipelineGraph createTree() {
    List<PipelineStageInternal> stages = getPipelineNodes();
    
    List<String> topLevelStageIds = new ArrayList<>();

    // id => stage
    Map<String, PipelineStageInternal> stageMap =
        stages.stream()
            .collect(
                Collectors.toMap(
                    PipelineStageInternal::getId, stage -> stage, (u, v) -> u, LinkedHashMap::new));

    Map<String, List<String>> stageToChildrenMap = new HashMap<>();

    FlowExecution execution = run.getExecution();
    if (execution == null) {
      // If we don't have an execution - e.g. if the Pipeline has a syntax error - then return an
      // empty graph.
      return new PipelineGraph(new ArrayList<>(), false);
    }
    stages.forEach(
        stage -> {
          try {

            
            FlowNode stageNode = execution.getNode(stage.getId());
            if (stageNode == null) {
              return;
            }
            List<String> ancestors = getAncestors(stage, stageMap);
            String treeParentId = null;
            // Compare the list of GraphVistor ancestors to the IDs of the enclosing node in the
            // execution.
            // If a node encloses another node, it means it's a tree parent, so the first ancestor
            // ID we find
            // which matches an enclosing node then it's the stages tree parent.
            List<String> enclosingIds = stageNode.getAllEnclosingIds();
           
               
            for (String ancestorId : ancestors) {
              if (enclosingIds.contains(ancestorId)) {
                treeParentId = ancestorId;
                break;
              }
            }

            if (PipelineNodeUtil.isAgentStart(stageNode)){
              treeParentId = enclosingIds.get(0);
            }
            if (treeParentId != null) {
              List<String> childrenOfParent =
                  stageToChildrenMap.getOrDefault(treeParentId, new ArrayList<>());
              childrenOfParent.add(stage.getId());
              stageToChildrenMap.put(treeParentId, childrenOfParent);
            } else {
              // If we can't find a matching parent in the execution and GraphVistor then this is a
              // top level node.
              stageToChildrenMap.put(stage.getId(), new ArrayList<>());
              topLevelStageIds.add(stage.getId());
            }
          } catch (java.io.IOException ex) {
            logger.error(
                "Caught a "
                    + ex.getClass().getSimpleName()
                    + " when trying to find parent of stage '"
                    + stage.getName()
                    + "'");
          }
        });

    List<PipelineStage> stageResults =
        stageMap.values().stream()
            .map(
                pipelineStageInternal -> {
                  List<PipelineStage> children =
                      stageToChildrenMap.getOrDefault(pipelineStageInternal.getId(), emptyList())
                          .stream()
                          .map(mapper(stageMap, stageToChildrenMap))
                          .collect(Collectors.toList());

                  return pipelineStageInternal.toPipelineStage(children);
                })
            .filter(stage -> topLevelStageIds.contains(stage.getId()))
            .collect(Collectors.toList());
    return new PipelineGraph(stageResults, execution.isComplete());
  }

  private static List<String> getAncestors(
      PipelineStageInternal stage, Map<String, PipelineStageInternal> stageMap) {
    List<String> ancestors = new ArrayList<>();
    if (!stage.getParents().isEmpty()) {
      String parentId = stage.getParents().get(0); // Assume one parent.
      ancestors.add(parentId);
      if (stageMap.containsKey(parentId)) {
        PipelineStageInternal parent = stageMap.get(parentId);
        ancestors.addAll(getAncestors(parent, stageMap));
      }
    }
    return ancestors;
  }
}
