import React from 'react';
import { Table, Pagination } from 'element-react';

import {Data,createData} from "../../dategrid";
interface TableProps{
    data: Array<Data>
}
interface TableState {

    columns: Array<any>;
    data:Array<Data>;
    currentPage: number;
    pageSize:number
   
  }
  

  
class PaginatedTable extends React.Component<TableProps,TableState> {
  constructor(props:TableProps) {
    super(props);
    
    this.state = {
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
      <div>
        <Table
          style={{width: '100%'}}
          columns={columns}
          data={displayData}
          border={true}
        
        />
        <Pagination
          layout="total, prev, pager, next"
          total={total}
          pageSize = {pageSize}
          currentPage={currentPage}
          onCurrentChange={this.handlePageChange}
          onSizeChange={this.handleSizeChange}
        />
      </div>
    )
  }
}

export default PaginatedTable;
