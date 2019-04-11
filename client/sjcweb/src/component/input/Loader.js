import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import Result from './Result';
import './Loader.css';

export default class Loader extends Component {
  
  render() {
    const { uploadedFiles, uploadedAfastFile, resultFiles, 
            handleInputFilesUpload, handleAfastamentosFilesUpload, resetFiles, loadInputFiles } = this.props;
    
    return (
    <section>
      <div className="entrada-body">

        <h2>Entrada para processamento</h2>

        <div className="dropzone-group">
          <Dropzone onDrop={acceptedFiles => handleInputFilesUpload(acceptedFiles)}>
            {({getRootProps, getInputProps}) => (
              <section className="dropzone-section">
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
        </div>

        <div>
          <button onClick={resetFiles}><i className="fa fa-trash-o fa-1x" aria-hidden="true"></i>LIMPAR</button>
          <button onClick={loadInputFiles}><i className="fa fa-file-pdf-o fa-1x" aria-hidden="true"></i>CONVERTER</button>
        </div>
     
      </div>

      {
        resultFiles.length > 0 ? (<Result uploadedFiles={uploadedFiles} resultFiles={resultFiles} uploadedAfastFile={uploadedAfastFile} />) : null
      }
      

    </section>
    )
  }

}
