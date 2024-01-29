import React from 'react';
import { Table, Pagination } from 'element-react';

import {Data,createData} from "../../dategrid";
interface TableProps{
    data: Array<Data>;
    fromJob:boolean
}
interface TableState {

    columns: Array<any>;
    data:Array<Data>;
    
    currentPage: number;
    pageSize:number;
    fullscreen:boolean;
   
  }
  

  
class PaginatedTable extends React.Component<TableProps,TableState> {
  constructor(props:TableProps) {
    super(props);
    
    this.state = {
      fullscreen :true,
      columns: [
        {
          label: "AgentName",
          prop: "agentname",
          width: 240,
          sortable: true
        },
        {
          label: "jobName",
          prop: "jobName",
          width: 240,
          sortable: true
        },
        {
          label: "caseName",
          prop: "casename",
          render: function(data:any){
            if (props.fromJob == false) {
               return (
              <span>
              <a href={`../job/${data.jobName}/${data.buildNumber.split('#')[1]}/pipeline-console?selected-node=${data.nodeid}`} target="_blank" >
          {data.casename}
        </a>
              </span>
            )
            }
            else{
              return (
                <span>
                <a href={`../../../job/${data.jobName}/${data.buildNumber.split('#')[1]}/pipeline-console?selected-node=${data.nodeid}`} target="_blank" >
            {data.casename}
          </a>
                </span>
              ) 
            }
           
          },
          width: 240,
          sortable: true
        },
        {
          label: "BuildNumber",
          prop: "buildNumber",
          sortable: true
        }
        ,
        {
          label: "BuildTime",
          prop: "buildtime",
          sortable: true
        }
      ],
      data: props.data,
      currentPage: 1,
      pageSize: 100
    }
  }

  handleSizeChange = (pageSize:any) => {
    this.setState({ pageSize });
  }

  handlePageChange = (currentPage:any) => {
    this.setState({ currentPage });
  }

  render() {
    const { columns, currentPage, pageSize } = this.state;
    const data = this.props.data
    const total = data.length;
    const start = (currentPage - 1) * pageSize;
    const end = currentPage * pageSize;
    const displayData = data.slice(start, end);
   
    

    return (
      <div style= {{marginTop:"20px"}}>
         
        <Table
          style={{width: '100%'}}
          columns={columns}
          data={data}
          border={true}
        
        />
        
        <Pagination
          layout="total"
          total={total}
         
        />
        
      </div>
    )
  }
}

export default PaginatedTable;
