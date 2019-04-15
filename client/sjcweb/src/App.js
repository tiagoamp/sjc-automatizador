import React, { Component } from 'react';
import Header from './component/Header';
import Footer from './component/Footer';
import FlowMenu from './component/FlowMenu';
import Loader from './component/input/Loader';
import { ToastContainer, toast } from 'react-toastify';

import './App.css';
import './font-awesome-4.7.0/css/font-awesome.min.css';
import 'react-toastify/dist/ReactToastify.min.css';

import httpGatewayFunctions from './service/HttpGateway';

class App extends Component {

  constructor() {
    super();
    this.state = { step: 0, uploadedFiles: [], uploadedAfastFile: null, resultFiles: [] };
  }

  getComponentForStep = () => {
    const { step, uploadedFiles, uploadedAfastFile, resultFiles } = this.state;

    switch(step) {
      case 0: 
        return (
          <Loader uploadedFiles={uploadedFiles} uploadedAfastFile={uploadedAfastFile} resultFiles={resultFiles}
                  handleInputFilesUpload={this.handleInputFilesUpload} handleAfastamentosFilesUpload={this.handleAfastamentosFilesUpload} 
                  resetFiles={this.resetFiles} loadInputFiles={this.loadInputFiles} />
        );
      case 1: 
        return (
          <h1>Step 1</h1>
        );
      case 2: 
        return (
          <h1>Step 2</h1>
        );
      default: 
        return (
          <Loader />
        );

    }
  }

  handleInputFilesUpload = (files) => {
    const validFiles = files.filter(f => f.name.toLowerCase().endsWith('.pdf'));
    const invalidFiles = files.filter(f => !f.name.toLowerCase().endsWith('.pdf'));
    const currFiles = [...this.state.uploadedFiles];
    const existingFiles = validFiles.filter(f => currFiles.map(c => c.name).includes(f.name));
    const newFiles = validFiles.filter(f => !currFiles.map(c => c.name).includes(f.name));

    if (invalidFiles.length > 0) {
      const filesNames = invalidFiles.map(f => ' ' + f.name);
      toast('Os arquivos com extensão inválidas nao foram carregados: ' + filesNames, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
    }
    if (existingFiles.length > 0) {
      const filesNames = existingFiles.map(f => ' ' + f.name);
      toast('Arquivos já carregados anteriormente: ' + filesNames, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
    }
    if (newFiles.length === 0) return;

    const promises = newFiles.map(f => httpGatewayFunctions.uploadFileRequest(f));
    Promise.all(promises)
      .then(res => {
        const updatedFiles = currFiles.concat(newFiles);
        toast('Arquivos carregados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
        this.setState( {uploadedFiles: updatedFiles, resultFiles: []} );
      })
      .catch(err => {
        toast('Erro ao fazer upload de arquivo: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
      });
  }

  handleAfastamentosFilesUpload = (file) => {
    const filename = file.name.toLowerCase();
    if (!filename.endsWith('.xlsx') && !filename.endsWith('.xls')) {
      toast('Extensão do arquivo inválida: ' + file.name, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
      return;          
    }
    httpGatewayFunctions.deleteAfastamentoRequest()
      .then(res => {
        file.isAfastamento = true;
        httpGatewayFunctions.uploadFileRequest(file);
        })
      .then(res => {
            toast('Arquivo carregado!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
            this.setState( { uploadedAfastFile: file, resultFiles: []} );
        })
      .catch(err => toast('Erro ao fazer upload de arquivo: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }));
  }

  resetFiles = () => {
    httpGatewayFunctions.cleanDirsRequest()
      .then(res => {
        this.setState( { step: 0, uploadedFiles: [], uploadedAfastFile: null, resultFiles: [] } )
      })
      .catch(err => toast('Erro ao limpar diretórios: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }));
    
  } 

  loadInputFiles = () => {
    if (this.state.uploadedFiles.length === 0) {
      toast('Nenhum arquivo feito upload!', { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
      return;
    }


    // TODO: transformar arquivos pdfs e setar no state !!!

    
    let currFiles = [...this.state.uploadedFiles];
    let result = currFiles.map(f => {
      const r = Object.assign({}, f);
      r.name = f.name.replace(/pdf/i,"xls");
      return r;
    });
    this.setState( {resultFiles: result} );
  }


  render() {

    return (
      
      <div className="App">

      {/* autenticação ? */}

        <Header />

        <FlowMenu step={this.state.step} />

        <ToastContainer autoClose={5000} />
               
        <main>
          {
            this.getComponentForStep()
          }
        </main>

        <Footer />

      </div>

    );
  }
}

export default App;
