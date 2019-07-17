import React, { Component } from 'react';
import Loader from 'react-loader-spinner';

export default class LoaderSpinner extends Component {
    render() {
        return (
            <div className="spinner">
                <Loader 
                    type="Watch"
                    color="#00BFFF"
                    height="80"	
                    width="80"
                />
                <span> &nbsp; Processando... </span>
            </div>
        )
    }
}