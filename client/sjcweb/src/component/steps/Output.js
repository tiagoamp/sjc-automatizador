import React, { Component } from 'react'
import './Steps.css';

export default class Output extends Component {
  
  componentDidMount() {
    this.props.getTotalInputFiles();    
  }

  render() {
    const { totalInputFiles, downloadMessagesFile, downloadOutputFile, prevStep, resetFiles } = this.props;
    
    return (
    <section>
      <div className="entrada-body">

        <h2>Arquivos de saída</h2>

        <p>Total de arquivos convertidos identificados: { totalInputFiles }</p>
        
        <div>
          <button onClick={prevStep}><i className="fa fa-angle-double-left fa-1x" aria-hidden="true"></i>VOLTAR</button>
          <button onClick={downloadMessagesFile}><i className="fa fa-download fa-1x" aria-hidden="true"></i>MENSAGENS</button>
          <button onClick={downloadOutputFile}><i className="fa fa-download fa-1x" aria-hidden="true"></i>PLANILHA</button>
          <button onClick={resetFiles}><i className="fa fa-repeat fa-1x" aria-hidden="true"></i>REINICIAR</button>
        </div>
     
      </div>
     
    </section>
    )
  }

}
