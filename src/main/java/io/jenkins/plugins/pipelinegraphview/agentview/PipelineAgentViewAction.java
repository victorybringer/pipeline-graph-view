package io.jenkins.plugins.pipelinegraphview.agentview;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.WebMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.util.HttpResponses;
import io.jenkins.plugins.pipelinegraphview.utils.PipelineGraph;
import io.jenkins.plugins.pipelinegraphview.utils.PipelineGraphApi;
import io.jenkins.plugins.pipelinegraphview.utils.PipelineGraphWithJob;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hudson.model.*;
import hudson.Extension;

@Extension
public class PipelineAgentViewAction implements RootAction {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected JSONObject createJson(PipelineGraph pipelineGraph) throws JsonProcessingException {
    String graph = OBJECT_MAPPER.writeValueAsString(pipelineGraph);
    return JSONObject.fromObject(graph);
  }

  @WebMethod(name = "allhistory")
  public HttpResponse gethistory() throws JsonProcessingException {
    // TODO: This need to be updated to return a tree representation of the graph, not the graph.
    // Here is how FlowGraphTree does it:
    // https://github.com/jenkinsci/workflow-support-plugin/blob/master/src/main/java/org/jenkinsci/plugins/workflow/support/visualization/table/FlowGraphTable.java#L126
    
    JSONArray graphArray = new JSONArray();

    for (PipelineGraphWithJob g : PipelineGraphApi.getallJobshistory()) {
        JSONObject graph = JSONObject.fromObject(g);
        graphArray.add(graph);
    }
    
    return HttpResponses.okJSON(graphArray);
  }
  @WebMethod(name = "alljobs")
  public HttpResponse getAllJobs() throws JsonProcessingException {
    // TODO: This need to be updated to return a tree representation of the graph, not the graph.
    // Here is how FlowGraphTree does it:
    // https://github.com/jenkinsci/workflow-support-plugin/blob/master/src/main/java/org/jenkinsci/plugins/workflow/support/visualization/table/FlowGraphTable.java#L126
    
    JSONArray graphArray = new JSONArray();

    for (PipelineGraphWithJob g : PipelineGraphApi.getallJobs()) {
        JSONObject graph = JSONObject.fromObject(g);
        graphArray.add(graph);
    }
    
    return HttpResponses.okJSON(graphArray);
  }


  @Override
  public String getIconFileName() {
    return "/plugin/pipeline-graph-view-icore-test/images/rocket-outline.svg";
  }

  
  @Override
  public String getDisplayName() {
    return "Enhanced Build History";
  }

  @Override
  public String getUrlName() {
    return "pipeline-agent-dashboard";
  }

  

 
}
