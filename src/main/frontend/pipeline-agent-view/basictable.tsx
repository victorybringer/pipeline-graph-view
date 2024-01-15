import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

const useStyles = makeStyles({
  table: {
    minWidth: 500,
  },
});

export interface Data {
  jobName: string;
  buildNumber: string;
  agentname: string;
  buildtime: string;
  
}

export function createData(
    jobName: string,
    buildNumber: string,
    agentname: string,
    buildtime: string,
): Data {
  return { jobName,buildNumber,agentname,buildtime };
}

interface TablePropsRows {
  rows : Array<Data>
  
}


export default function BasicTable(props: TablePropsRows) {
  const classes = useStyles();
  const {rows} = props
  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="simple table">
        <TableHead>
          <TableRow>
          <TableCell align="center">agentname</TableCell>
                      <TableCell align="center">jobName</TableCell>
                      <TableCell align="center">buildNumber</TableCell>
                      <TableCell align="center">buildtime</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row:Data) => (
            <TableRow key={row.agentname}>
              <TableCell align="center">
                        {row.agentname}
                      </TableCell>
                      <TableCell align="center">{row.jobName}</TableCell>
                      <TableCell align="center">{row.buildNumber}</TableCell>
                      <TableCell align="center">{row.buildtime}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}