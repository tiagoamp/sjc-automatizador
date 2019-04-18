import React, { Component } from 'react'
import './Steps.css';

export default class Output extends Component {
  
  componentDidMount() {
    this.props.getTotalInputFiles();    
  }

  render() {
    const { totalInputFiles, downloadMessagesFile, prevStep } = this.props;
    
    return (
    <section>
      <div className="entrada-body">

        <h2>Arquivos de saída</h2>

        <p>Total de arquivos convertidos identificados: { totalInputFiles }</p>
        
        <div>
          <button onClick={prevStep}><i className="fa fa-angle-double-left fa-1x" aria-hidden="true"></i>VOLTAR</button>
          <button onClick={downloadMessagesFile}><i className="fa fa-download fa-1x" aria-hidden="true"></i>MENSAGENS</button>
          <button onClick={() => alert('Implementar!!!')}><i className="fa fa-download fa-1x" aria-hidden="true"></i>PLANILHA</button>
        </div>
     
      </div>
     
    </section>
    )
  }

}
