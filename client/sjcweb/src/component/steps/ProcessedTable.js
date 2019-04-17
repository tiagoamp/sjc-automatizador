import React, { Component } from 'react'
import './ProcessedTable.css';

export default class ProcessedTable extends Component {

  getMsgsFrom(arr, typeParam) {
    return arr.filter(el => {
        return el.messages.filter(msg => msg.type === typeParam).length > 0;
    });
  }


  render() {
    const { processedFiles, downloadMessagesFile, prevStep, nextStep } = this.props;

    return (
      <div className="result-input">
            <h2>Resultado do Processamento</h2>

            <div className='msgs msgs-info'>
                <h3 className='msgs-header'>Mensagens de Informação</h3>
                <div className='msgs-body'>            
                    {   
                        this.getMsgsFrom(processedFiles, "INFO").length === 0 ? <span className='msg-filename'>Sem mensagens deste tipo!</span> :
                        this.getMsgsFrom(processedFiles, "INFO").map( file => {
                            return (
                                <div key={ file.fileName }>
                                    <span className='msg-filename'> { file.fileName }</span>
                                    <ul>
                                        { file.messages.map( (el, index) => <li key={index}> {el.text} </li>) }
                                    </ul>
                                </div>
                            )
                        }) 
                    }
                </div>
            </div>

            <div className='msgs msgs-error'>
                <h3 className='msgs-header'>Mensagens de Erro</h3>
                <div className='msgs-body'>            
                    {   
                        this.getMsgsFrom(processedFiles, "ERROR").length === 0 ? <span className='msg-filename'>Sem mensagens deste tipo!</span> :
                        this.getMsgsFrom(processedFiles, "ERROR").map( file => {
                            return (
                                <div key={ file.fileName }>
                                    <span className='msg-filename'> { file.fileName }</span>
                                    <ul>
                                        { file.messages.map( (el, index) => <li key={index}> {el.text} </li>) }
                                    </ul>
                                </div>
                            )
                        }) 
                    }
                </div>
            </div>

            <div className='msgs msgs-alert'>
                <h3 className='msgs-header'>Mensagens de Alerta</h3>
                <div className='msgs-body'>            
                    { 
                        this.getMsgsFrom(processedFiles, "ALERT").length === 0 ? <span className='msg-filename'>Sem mensagens deste tipo!</span> :
                        this.getMsgsFrom(processedFiles, "ALERT").map( file => {
                            return (
                                <div key={ file.fileName }>
                                    <span className='msg-filename'> { file.fileName }</span>
                                    <ul>
                                        { file.messages.map( (el, index) => <li key={index}> {el.text} </li>) }
                                    </ul>
                                </div>
                            )
                        }) 
                    }
                </div>
            </div>
            
            <div className='buttons-group'>
                <button onClick={prevStep}><i className="fa fa-angle-double-left fa-1x" aria-hidden="true"></i>VOLTAR</button>
                <button onClick={downloadMessagesFile}><i className="fa fa-download fa-1x" aria-hidden="true"></i>DOWNLOAD</button>
                <button onClick={nextStep}>AVANÇAR<i className="fa fa-angle-double-right fa-1x" aria-hidden="true"></i></button>
            </div>
        
      </div>
    )
  }

}
