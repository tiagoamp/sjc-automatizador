import React, { Component } from 'react'
import './ConvertedTable.css';

export default class ProcessedTable extends Component {

  getMsgsBy(arr, typeParam) {
    return arr.filter(el => {
        return el.messages.filter(msg => msg.type === typeParam).length > 0;
    });
  }


  render() {
    const { processedFiles, prevStep, nextStep } = this.props;

    return (
      <div className="result-input">
            <h2>Resultado do Processamento</h2>

            <p>
                { processedFiles.length }
                { JSON.stringify(this.getMsgsBy(processedFiles, "INFO")) }
            </p>

            <button onClick={prevStep}><i className="fa fa-angle-double-left fa-1x" aria-hidden="true"></i>VOLTAR</button>
            <button onClick={nextStep}>AVANÇAR<i className="fa fa-angle-double-right fa-1x" aria-hidden="true"></i></button>
        
      </div>
    )
  }

}
