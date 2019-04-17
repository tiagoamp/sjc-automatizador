import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import ProcessedTable from './ProcessedTable'
import './Steps.css';

export default class Processor extends Component {
  
  render() {
    const { uploadedAfastFile, processedFiles, handleAfastamentosFilesUpload, processInputFiles,  prevStep, nextStep } = this.props;
    
    return (
    <section>
      <div className="entrada-body">

        <h2>Processamento dos Arquivos</h2>
        
        <Dropzone onDrop={acceptedFiles => handleAfastamentosFilesUpload(acceptedFiles[0])}>
          {({getRootProps, getInputProps}) => (
            <section>
              <div {...getRootProps()} className="dropzone-div">
                <input {...getInputProps()} accept=".xlsx, xls"/>
                <span><strong>Afastamentos</strong></span>
                <p>Arraste aqui o arquivo de afastamentos em 'xlsx' ou click para selecioná-lo</p>
                <div className="arquivos">Arquivo de afastamentos: <span>{ uploadedAfastFile !== null ? uploadedAfastFile.name : ' nenhum ' }</span></div>
              </div>
            </section>
          )}
        </Dropzone>         
        
        <div>
          <button onClick={prevStep}><i className="fa fa-angle-double-left fa-1x" aria-hidden="true"></i>VOLTAR</button>
          <button onClick={processInputFiles}><i className="fa fa-cogs fa-1x" aria-hidden="true"></i>PROCESSAR</button>
        </div>
     
      </div>

      {
        processedFiles.length > 0 ? (<ProcessedTable processedFiles={processedFiles} nextStep={nextStep} />) : null
      }

    </section>
    )
  }

}
