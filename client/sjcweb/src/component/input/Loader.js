import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import { ToastContainer, toast } from 'react-toastify';
import Result from './Result';
import 'react-toastify/dist/ReactToastify.min.css';
import './Loader.css';

export default class Loader extends Component {

  constructor() {
    super();
    this.state = { uploadedFiles: [], uploadedAfastFile: null, resultFiles: [] };
  }


  handleInputFilesUpload = (files) => {
    const newFiles = files.filter(f => f.name.toLowerCase().endsWith('.pdf'));
    if (newFiles.length !== files.length) {
      toast('Os arquivos com extensão inválidas nao foram carregados!', { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
    }
    let prevFiles = this.state.uploadedFiles;
    let updatedFiles = prevFiles.concat(newFiles); 
    if (newFiles.length > 0) {
      toast('Arquivos carregados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
    }    
    this.setState( {uploadedFiles: updatedFiles} );    
  }

  handleAfastamentosFilesUpload = (file) => {
    const filename = file.name.toLowerCase();
    console.log(filename);
    if (!filename.endsWith('.xlsx') && !filename.endsWith('.xls')) {
      toast('Extensão do arquivo inválida: ' + file.name, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
      return;      
    }
    toast('Arquivos carregados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
    this.setState( { uploadedAfastFile: file} );      
  }

  limparAction = () => {

  }

  carregarAction = () => {
    
  }

  render() {
    const uploadedFiles = this.state.uploadedFiles;
    const uploadedAfastFile = this.state.uploadedAfastFile;
    const resultFiles = this.state.resultFiles;

    return (
    <section>
      <div className="entrada-body">

        <ToastContainer autoClose={5000} />

        <h2>Entrada para processamento</h2>

        <div className="dropzone-group">
          <Dropzone onDrop={acceptedFiles => this.handleInputFilesUpload(acceptedFiles)}>
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
          <Dropzone onDrop={acceptedFiles => this.handleAfastamentosFilesUpload(acceptedFiles[0])}>
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
          <button>LIMPAR</button>
          <button>CARREGAR</button>
        </div>
     
      </div>

      {
        resultFiles.length > 0 ? (<Result uploadedFiles={uploadedFiles} resultFiles={resultFiles} />) : null
      }
      

    </section>
    )
  }

}
