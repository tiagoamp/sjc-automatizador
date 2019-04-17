import React, { Component } from 'react';
import Header from './component/Header';
import Footer from './component/Footer';
import FlowMenu from './component/FlowMenu';
import Converter from './component/input/Converter';
import Processor from './component/input/Processor';
import { ToastContainer, toast } from 'react-toastify';

import './App.css';
import './font-awesome-4.7.0/css/font-awesome.min.css';
import 'react-toastify/dist/ReactToastify.min.css';

import httpGatewayFunctions from './service/HttpGateway';

class App extends Component {

  constructor() {
    super();
    this.state = { step: 0, uploadedFiles: [], convertedFiles: [], uploadedAfastFile: null, processedFiles: [] };
  }

  getComponentForStep = () => {
    const { step, uploadedFiles, uploadedAfastFile, convertedFiles, processedFiles } = this.state;

    switch(step) {
      case 0: 
        return (
          <Converter uploadedFiles={uploadedFiles} convertedFiles={convertedFiles}
                  handleInputFilesUpload={this.handleInputFilesUpload} resetFiles={this.resetFiles} convertInputFiles={this.convertInputFiles} nextStep={this.nextStep} />
        );
      case 1: 
        return (
          <Processor uploadedAfastFile={uploadedAfastFile} processedFiles={processedFiles} 
                  handleAfastamentosFilesUpload={this.handleAfastamentosFilesUpload} processInputFiles={this.processInputFiles}
                  prevStep={this.prevStep} nextStep={this.nextStep} />
        );
      case 2: 
        return (
          <h1>Step 2</h1>
        );
      default: 
        return (
          <Converter />
        );

    }
  }

  nextStep = () => {
    const currStep = this.state.step;
    this.setState( { step: currStep+1 } );
  }

  prevStep = () => {
    const currStep = this.state.step;
    this.setState( { step: currStep-1 } );
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
        this.setState( {uploadedFiles: updatedFiles, convertedFiles: []} );
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
            this.setState( { uploadedAfastFile: file, convertedFiles: []} );
        })
      .catch(err => toast('Erro ao fazer upload de arquivo: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }));
  }

  resetFiles = () => {
    httpGatewayFunctions.cleanDirsRequest()
      .then(res => {
        toast('Arquivos de Entrada apagados!!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
        this.setState( { step: 0, uploadedFiles: [], uploadedAfastFile: null, convertedFiles: [] } )
      })
      .catch(err => toast('Erro ao limpar diretórios: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }));    
  } 

  convertInputFiles = () => {
    if (this.state.uploadedFiles.length === 0) {
      toast('Nenhum arquivo feito upload!', { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
      return;
    }
    httpGatewayFunctions.convertInputFiles()
      .then(res => res.json())
      .then(res => this.setState( { convertedFiles: res } ))
      .catch(err => toast('Erro ao converter arquivos: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }));
  }

  processInputFiles = () => {
    httpGatewayFunctions.processInputFiles()
      .then(res => res.json())
      .then(res => this.setState( { processedFiles: res } ))
      .catch(err => toast('Erro ao processar arquivos: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }));
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
