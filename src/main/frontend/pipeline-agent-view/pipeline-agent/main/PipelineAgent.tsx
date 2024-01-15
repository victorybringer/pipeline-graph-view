import React from "react";


import { StageInfo } from "../../../pipeline-graph-view/pipeline-graph/main";

import {Data,createData} from "../../dategrid";
import EnhancedTable from "../../dategrid";
import BasicTable  from "../../basictable";
import "./pipeline-agent.scss";
import { resolveAny } from "dns";
import { StageType } from "../../../pipeline-graph-view/pipeline-graph/main/PipelineGraphModel";


interface PipelineAgentProps {}
interface PipelineAgentState {

  stages: Array<Data>;
 
}




export class PipelineAgent extends React.Component<
  PipelineAgentProps,
  PipelineAgentState
> {
  constructor(props: PipelineAgentProps) {
    super(props);
   
    // set default values of state
    this.state = {
       
      stages:[]
      
    };
  
    this.setAgents();
  }

  extractAgentStages(stages:Array<StageInfo>):Array<StageInfo>{
    let agentStages:Array<StageInfo> = [];
    for (let stage of stages) {
      if (stage.type === 'AGENT' as StageType) {
        agentStages.push(stage);
      }
      if (stage.children && stage.children.length > 0) {
        agentStages = agentStages.concat(this.extractAgentStages(stage.children));
      }
    }
    return agentStages;
  }

  
  async setAgents() {
    // Sets stages state.
    const res = await fetch("alljobs");
    const result_1 = await res.json();
    console.log(result_1.data)
    const combine:Array<Data> = []
    for (let jsonres of result_1.data){

      for (let ag of this.extractAgentStages(jsonres.stages)){
           combine.push(createData(jsonres.jobName,jsonres.buildNumber,ag.name,jsonres.buildtime))
      }

    }
   
    this.setState({
      stages: combine,
    });
  }


  render() {
    return (
      <React.Fragment>
        <div className="App">
          <EnhancedTable rows={this.state.stages} />
        </div>
      </React.Fragment>
    );
  }
}
