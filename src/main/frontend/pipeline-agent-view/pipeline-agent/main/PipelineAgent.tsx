import React from "react";


import { StageInfo } from "../../../pipeline-graph-view/pipeline-graph/main";

import {Data,createData} from "../../dategrid";

import PaginatedTable from "./elementtable";
import "./pipeline-agent.scss";
import { Loading } from 'element-react';
import { StageType } from "../../../pipeline-graph-view/pipeline-graph/main/PipelineGraphModel";


interface PipelineAgentProps {



}
interface PipelineAgentState {

  stages: Array<Data>;
  fullscreen : boolean;
  jobname : string;
  buildnumber : string;
  fromJob : boolean
 
}

interface agentInfo {
      
  info: StageInfo,
  parent:string,
  parentid:number

}

export class PipelineAgent extends React.Component<
  PipelineAgentProps,
  PipelineAgentState
> {
  constructor(props: PipelineAgentProps) {
    super(props);
   
    // set default values of state
    this.state = {
       
      stages:[],
      fullscreen:true,
      jobname:"",
      buildnumber:"",
      fromJob: false
      
    };
  
    this.setAgents();
  }

  handlePaginationChange(e:any){
     console.log(e)
  }



  extractAgentStages(parent:string,parentid:number,stages:Array<StageInfo>):Array<agentInfo>{
    let agentStages:Array<agentInfo> = [];
    for (let stage of stages) {
      if (stage.type === 'AGENT' as StageType) {
        agentStages.push({info:stage,parent:parent,parentid:parentid});
      }
      if (stage.children && stage.children.length > 0) {
        agentStages = agentStages.concat(this.extractAgentStages(stage.name,stage.id,stage.children));
      }
    }
    return agentStages;
  }
  
  
  async setAgents() {
    // Sets stages state.
    const res = await fetch("allhistory");
    const result_1 = await res.json();
    console.log(result_1.data)

    var combine:Array<Data> = []
    for (let jsonres of result_1.data){

      for (let ag of this.extractAgentStages("",-1,jsonres.stages).filter((ag) => ag.info.name != "")){
           combine.push(createData(jsonres.jobName,jsonres.buildNumber,ag.info.state.split(" ")[1],ag.info.name,ag.info.title.split(";")[0],Number.parseInt(ag.info.title.split(";")[1])))
      }

    }

    
    combine = combine.reduce((acc:Data[], current) => {
      const x = acc.find(item => item.jobName === current.jobName && item.buildNumber === current.buildNumber && item.agentname === current.agentname && item.buildtime === current.buildtime);
      if (!x) {
        return acc.concat([current]);
      } else {
        return acc;
      }
    }, []);
 
      console.debug(`In handleUrlParams.`);
      let params = new URLSearchParams(document.location.search.substring(1));
     
      let jn = params.get("jobname") || "";
      if (jn) {
       
        this.setState({
          stages: combine.reduce( (pre:Data[],cur) => cur.jobName == jn  ? pre : pre.concat([cur]) ,[]) ,
          fullscreen:false,
          fromJob : true
        });
      } else {
        this.setState({
          stages: combine,
          fullscreen:false
        });
      }
    
  }

  render() {
    return (
      <React.Fragment>
        <div className="App">
        <Loading fullscreen={this.state.fullscreen} loading ={this.state.fullscreen} />
          <PaginatedTable data={this.state.stages} fromJob={this.state.fromJob}  />
          
        </div>
      </React.Fragment>
    );
  }
}
