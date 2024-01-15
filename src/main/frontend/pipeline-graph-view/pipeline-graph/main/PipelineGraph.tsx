import * as React from "react";

import startPollingPipelineStatus from "./support/startPollingPipelineStatus";
import {
  CompositeConnection,
  defaultLayout,
  NodeLabelInfo,
  LayoutInfo,
  NodeColumn,
  NodeInfo,
  StageInfo,
  StageType,
} from "./PipelineGraphModel";
import { layoutGraph } from "./PipelineGraphLayout";

import { Node, SelectionHighlight } from "./support/nodes";
import SimpleSelect from "./support/select";
import {
  BigLabel,
  SmallLabel,
  SequentialContainerLabel,
} from "./support/labels";
import { GraphConnections } from "./support/connections";
import { cursorTo } from "readline";
import { resolveAny } from "dns";
import { ContactsOutlined } from "@material-ui/icons";

interface Props {
  stages: Array<StageInfo>;
  layout?: Partial<LayoutInfo>;
  setStages?: (stages: Array<StageInfo>) => void;
  onNodeClick?: (nodeName: string, id: number) => void;
  selectedStage?: StageInfo;
  path?: string;
  collapsed?: boolean;
  
}

interface State {
  stages : Array<StageInfo>;
  nodeColumns: Array<NodeColumn>;
  connections: Array<CompositeConnection>;
  bigLabels: Array<NodeLabelInfo>;
  smallLabels: Array<NodeLabelInfo>;
  branchLabels: Array<NodeLabelInfo>;
  measuredWidth: number;
  measuredHeight: number;
  layout: LayoutInfo;
  selectedStage?: StageInfo;
  agentNodes : Array<StageInfo>;
  agentfilter : string
}

export class PipelineGraph extends React.Component {
  props!: Props;
  state: State;

  constructor(props: Props) {
    super(props);
    this.state = {
      stages : [],
      nodeColumns: [],
      connections: [],
      bigLabels: [],
      smallLabels: [],
      branchLabels: [],
      measuredWidth: 0,
      measuredHeight: 0,
      layout: { ...defaultLayout, ...props.layout },
      selectedStage: props.selectedStage,
      agentNodes:[],
      agentfilter : ""
    };
  }

  recursivesearchNode(stage: StageInfo)  {

    if (stage.children.length == 0) {
      return [stage]
    }
    else {
      
     var res: Array<StageInfo> = []

   

     for (var i = 0;i < stage.children.length;++i){
       
      
       res = res.concat(this.recursivesearchNode(stage.children[i]))

     }

     return res 

    }
    


  }
 
  
  onPipelineDataReceived = (data: { stages: Array<StageInfo> }) => {
      
      var { stages } = data;
     
     
      var allstages:Array<StageInfo> =  stages.map((stage) => this.recursivesearchNode(stage)).reduce((pre,cur) => pre.concat(cur) ,[])
      
      this.setState({ agentNodes: allstages.filter((stage) => stage.type == "AGENT" as StageType)})
      this.stagesUpdated(stages)
     
    };

  onPipelineDataReceived2 = (data: { stages: Array<StageInfo> }) => {
      
      var { stages } = data;
     
     
      var allstages:Array<StageInfo> =  stages.map((stage) => this.recursivesearchNode(stage)).reduce((pre,cur) => pre.concat(cur) ,[])
      
      this.setState({ agentNodes: allstages.filter((stage) => stage.type == "AGENT" as StageType)})
      const stages2 = stages.map((stage) => {
      
        if (stage.children.length > 0 && stage.children[0].type == "PARALLEL"  as StageType) {
          
          stage.children = stage.children.filter((child) => child.children[0].title ==this.state.agentfilter || this.state.agentfilter == "All")
          return stage
        }
  
        else{
          return stage
  
        }
        
        });
      this.stagesUpdated(stages2)
        
     
    };  
    
   
      
    
    
  onPollingError = (err: Error) => {
      console.log("There was an error when polling the pipeline status", err);
    };
  onPipelineComplete = () => undefined;

  componentDidMount() {
    
    
    startPollingPipelineStatus(
      this.onPipelineDataReceived,
      this.onPollingError,
      this.onPipelineComplete,
      this.props.path ?? "tree"
    );
      
    
    
  }

  componentWillReceiveProps(nextProps: Props) {
    let newState: Partial<State> | undefined;
    let needsLayout = false;

    if (nextProps.layout != this.props.layout) {
      newState = {
        ...newState,
        layout: { ...defaultLayout, ...this.props.layout },
      };
      needsLayout = true;
    }

    if (nextProps.selectedStage !== this.props.selectedStage) {
      // If we're just changing selectedStage, we don't need to re-generate the children
      newState = { ...newState, selectedStage: nextProps.selectedStage };
    }

    if (nextProps.stages !== this.props.stages) {
      needsLayout = true;
    }

    const doLayoutIfNeeded = () => {
      if (needsLayout) {
        this.stagesUpdated(nextProps.stages);
      }
    };

    if (newState) {
      // If we need to update the state, then we'll delay any layout changes
      this.setState(newState, doLayoutIfNeeded);
    } else {
      doLayoutIfNeeded();
    }
  }

  /**
   * Main process for laying out the graph. Calls out to PipelineGraphLayout module.
   */
  private stagesUpdated(newStages: Array<StageInfo> = []) {
    if (this.props.setStages != undefined) {
      this.props.setStages(newStages);
    }
    this.setState(
      layoutGraph(newStages, this.state.layout, this.props.collapsed ?? false)
    );
  }

  /**
   * Is this stage currently selected?
   */
  private stageIsSelected = (stage?: StageInfo): boolean => {
    const { selectedStage } = this.state;
    return (selectedStage && stage && selectedStage.id === stage.id) || false;
  };

  private handleNodeClick = (node: NodeInfo) => {
    if (node.isPlaceholder === false && node.stage.state !== "skipped") {
      const stage = node.stage;
      const listener = this.props.onNodeClick;

      if (listener) {
        listener(stage.name, stage.id);
      }

      // Update selection
      this.setState({ selectedStage: stage });
    }
  };
  handleSelect = (agent:string) => {

    this.setState({agentfilter:agent},()=> {      
      
      startPollingPipelineStatus(

        this.onPipelineDataReceived2,
        this.onPollingError,
        this.onPipelineComplete,
        this.props.path ?? "tree"
      )
    
    });
     

  }

  render() {
    const {
      nodeColumns,
      connections,
      bigLabels,
      smallLabels,
      branchLabels,
      measuredWidth,
      measuredHeight
     
    } = this.state;

    // Without these we get fire, so they're hardcoded
    const outerDivStyle = {
      position: "relative", // So we can put the labels where we need them
      overflow: "visible", // So long labels can escape this component in layout
    };

    let nodes = [];
    for (const column of nodeColumns) {
      for (const row of column.rows) {
        for (const node of row) {
          nodes.push(node);
        }
      }
    }

    return (
      <div className="PWGx-PipelineGraph-container">

       <SimpleSelect node_name_list= {this.state.agentNodes.map((node) => node.name).reduce( (pre:string[],cur) =>pre.includes(cur) ? pre : pre.concat([cur]),[])}  onClick={this.handleSelect}/>
        <div style={outerDivStyle as any} className="PWGx-PipelineGraph">
          <svg width={measuredWidth} height={measuredHeight}>
            <GraphConnections
              connections={connections}
              layout={this.state.layout}
            />
          
            {nodes.map((node) => (
              <Node
                key={node.id}
                node={node}
                layout={this.state.layout}
                onClick={this.handleNodeClick}
                isStageSelected={this.stageIsSelected}
              />
            ))}
            <SelectionHighlight
              layout={this.state.layout}
              nodeColumns={this.state.nodeColumns}
              isStageSelected={this.stageIsSelected}
            />
          </svg>

          {bigLabels.map((label) => (
            <BigLabel
              key={label.key}
              details={label}
              layout={this.state.layout}
              measuredHeight={measuredHeight}
              selectedStage={this.state.selectedStage}
              isStageSelected={this.stageIsSelected}
            />
          ))}
          {smallLabels.map((label) => (
            <SmallLabel
              key={label.key}
              details={label}
              layout={this.state.layout}
              isStageSelected={this.stageIsSelected}
            />
          ))}
          {branchLabels.map((label) => (
            <SequentialContainerLabel
              key={label.key}
              details={label}
              layout={this.state.layout}
            />
          ))}
        </div>
      </div>
    );
  }
}
