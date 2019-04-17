import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import ProcessedTable from './ProcessedTable'
import './Steps.css';

export default class Processor extends Component {
  
  componentDidMount() {
    this.props.getTotalInputFiles();
  }

  render() {
    const { uploadedAfastFile, processedFiles, totalInputFiles, handleAfastamentosFilesUpload, processInputFiles, deleteAfastamentosFile, downloadMessagesFile,
      prevStep, nextStep } = this.props;
    
    return (
    <section>
      <div className="entrada-body">

        <h2>Processamento dos Arquivos</h2>

        <p>
          Total de arquivos convertidos identificados: { totalInputFiles }
        </p>
        
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
          <button onClick={deleteAfastamentosFile}><i className="fa fa-trash-o fa-1x" aria-hidden="true"></i>RETIRAR</button>
          <button onClick={processInputFiles}><i className="fa fa-cogs fa-1x" aria-hidden="true"></i>PROCESSAR</button>
        </div>
     
      </div>

      {
        processedFiles.length > 0 ? (<ProcessedTable processedFiles={processedFiles} downloadMessagesFile={downloadMessagesFile} prevStep={prevStep} nextStep={nextStep} />) : null
      }

    </section>
    )
  }

}
