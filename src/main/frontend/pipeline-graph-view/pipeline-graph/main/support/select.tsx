import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

const useStyles = makeStyles((theme) => ({
    formControl: {
        margin: theme.spacing(1),
        minWidth: 120,
    },
    selectEmpty: {
        marginTop: theme.spacing(2),
    },
}));

interface SelectProps {
    node_name_list: Array<string>;
    onClick: (agent:string) => void;
   
  }

export default function SimpleSelect({node_name_list,onClick} : SelectProps) {
    const classes = useStyles();

    const [agent0,setAgent] = React.useState("")

    console.log(node_name_list)
    const handleChange = (event: any) => {
       console.log(event.target.value)
        setAgent(event.target.value)
        onClick(event.target.value);
    };

    return (
        <div>
            <FormControl className={classes.formControl}>
                <InputLabel id="demo-simple-select-label">Agent</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value = {agent0}
                    onChange={handleChange}
                >   

<MenuItem  value="All">All</MenuItem>
                    {
                    node_name_list.map((agent) =>(
                        <MenuItem  value={agent}>{agent}</MenuItem>
                    ))}

                </Select>
            </FormControl>
        </div>
    );
}
