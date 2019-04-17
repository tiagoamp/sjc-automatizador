import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import ConvertedTable from './ConvertedTable';
import './Steps.css';

export default class Converter extends Component {
  
  render() {
    const { uploadedFiles, convertedFiles, handleInputFilesUpload, resetFiles, convertInputFiles, nextStep } = this.props;
    
    return (
    <section>
      <div className="entrada-body">

        <h2>Entrada para processamento</h2>
        
        <Dropzone onDrop={acceptedFiles => handleInputFilesUpload(acceptedFiles)}>
          {({getRootProps, getInputProps}) => (
            <section>
              <div {...getRootProps()} className="dropzone-div">
                <input {...getInputProps()} accept=".pdf" />
                <span><strong>Relatórios</strong></span>
                <p>Arraste aqui os arquivos de relatórios em 'pdf' ou click para selecioná-los</p>
                <div className="arquivos">Arquivos para carregar [{uploadedFiles.length}]:  
                  <span>{ uploadedFiles.length > 0 ? uploadedFiles.map( f => ' [' + f.name + ']') : ' nenhum ' }</span></div>
              </div>
            </section>
          )}
        </Dropzone>          
        
        <div>
          <button onClick={resetFiles}><i className="fa fa-trash-o fa-1x" aria-hidden="true"></i>LIMPAR</button>
          <button onClick={convertInputFiles}><i className="fa fa-file-pdf-o fa-1x" aria-hidden="true"></i>CONVERTER</button>
        </div>
     
      </div>

      {
        convertedFiles.length > 0 ? (<ConvertedTable convertedFiles={convertedFiles} nextStep={nextStep} />) : null
      }
      
    </section>
    )
  }

}
