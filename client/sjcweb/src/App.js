import React, { Component } from 'react';
import Header from './component/Header';
import Footer from './component/Footer';
import FlowMenu from './component/FlowMenu';
import Converter from './component/steps/Converter';
import Processor from './component/steps/Processor';
import Output from './component/steps/Output';
import LoaderSpinner from './LoaderSpinner';
import { ToastContainer, toast } from 'react-toastify';

import './App.css';
import 'font-awesome/css/font-awesome.min.css';

import 'react-toastify/dist/ReactToastify.min.css';

import httpGatewayFunctions from './service/HttpGateway';

class App extends Component {

  constructor() {
    super();
    this.state = { step: 0, totalInputFiles: 0, uploadedFiles: [], convertedFiles: [], uploadedAfastFile: null, processedFiles: [], isLoading: false };
  }

  getComponentForStep = () => {
    const { step, uploadedFiles, uploadedAfastFile, convertedFiles, processedFiles, totalInputFiles } = this.state;

    switch(step) {
      case 0: 
        return (
          <Converter uploadedFiles={uploadedFiles} convertedFiles={convertedFiles} handleInputFilesUpload={this.handleInputFilesUpload} 
                     resetFiles={this.resetFiles} convertInputFiles={this.convertInputFiles} nextStep={this.nextStep} />
        );
      case 1: 
        return (
          <Processor uploadedAfastFile={uploadedAfastFile} processedFiles={processedFiles} totalInputFiles={totalInputFiles}
                  handleAfastamentosFilesUpload={this.handleAfastamentosFilesUpload} processInputFiles={this.processInputFiles} 
                  deleteAfastamentosFile={this.deleteAfastamentosFile} downloadMessagesFile={this.downloadMessagesFile} getTotalInputFiles={this.getTotalInputFiles}
                  prevStep={this.prevStep} nextStep={this.nextStep} />
        );
      case 2: 
        return (
          <Output totalInputFiles={totalInputFiles} downloadMessagesFile={this.downloadMessagesFile} downloadOutputFile={this.downloadOutputFile} 
                  getTotalInputFiles={this.getTotalInputFiles} prevStep={this.prevStep} resetFiles={this.resetFiles} />
        );
      default: 
        return (
          <p>Erro: Passo de processamento não identificado!</p>
        );

    }
  }

  nextStep = () => {
    const currStep = this.state.step;
    this.setState( { step: currStep+1} );
  }

  prevStep = () => {
    const currStep = this.state.step;
    this.setState( { step: currStep-1, processedFiles: [] } );    
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

    this.setState( { isLoading: true } );
    const promises = newFiles.map(f => httpGatewayFunctions.uploadFileRequest(f));
    Promise.all(promises)
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        const updatedFiles = currFiles.concat(newFiles);
        toast('Arquivos carregados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
        this.setState( {uploadedFiles: updatedFiles, convertedFiles: [], isLoading: false} );
      })
      .catch(err => {
        toast('Erro ao fazer upload de arquivo: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
        this.setState( { isLoading: false } );
      });
  }

  handleAfastamentosFilesUpload = (file) => {
    const filename = file.name.toLowerCase();
    if (!filename.endsWith('.xlsx') && !filename.endsWith('.xls')) {
      toast('Extensão do arquivo inválida: ' + file.name, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
      return;          
    }
    this.setState( { isLoading: true } );
    httpGatewayFunctions.deleteAfastamentoRequest()
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        file.isAfastamento = true;
        return httpGatewayFunctions.uploadFileRequest(file);
        })
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        toast('Arquivo carregado!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
        this.setState( { uploadedAfastFile: file, processedFiles: [], isLoading: false} );
        })
      .catch(err => {
        toast('Erro ao fazer upload de arquivo: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } );
      });
  }

  resetFiles = () => {
    this.setState( { isLoading: true } );
    httpGatewayFunctions.cleanDirsRequest()
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        toast('Arquivos de Entrada apagados!!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
        this.setState( { step: 0, totalInputFiles: 0, uploadedFiles: [], convertedFiles: [], uploadedAfastFile: null, processedFiles: [], isLoading: false } )
      })
      .catch(err => { 
        toast('Erro ao limpar diretórios: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } )
      });    
      
  }
  
  deleteAfastamentosFile = () => {
    this.setState( { isLoading: true } );
    httpGatewayFunctions.deleteAfastamentoRequest()
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        toast('Arquivos de Afastamento apagado!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
        this.setState( { processedFiles: [], uploadedAfastFile: null, isLoading: false } )
      })
      .catch(err => { 
        toast('Erro ao limpar diretórios: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false })
        this.setState( { isLoading: false } );
      });    
  }

  convertInputFiles = () => {
    if (this.state.uploadedFiles.length === 0) {
      toast('Nenhum arquivo feito upload!', { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
      return;
    }
    toast('Aguarde o processamento...', { type: toast.TYPE.INFO, autoClose: true, closeButton: false });
    this.setState( { isLoading: true } );
    httpGatewayFunctions.convertInputFiles()
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        return res.json();
      })
      .then(res => {
        toast('Arquivos convertidos!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false });
        this.setState( { convertedFiles: res, isLoading: false } )
       })
      .catch(err => {
        toast('Erro ao converter arquivos: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } );
      });
  }

  processInputFiles = () => {
    toast('Aguarde o processamento...', { type: toast.TYPE.INFO, autoClose: true, closeButton: false });    
    this.setState( { isLoading: true } );
    httpGatewayFunctions.processInputFiles()
      .then(res => { 
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        return res.json();
      })
      .then(res => {
        toast('Arquivos processados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false });
        this.setState( { processedFiles: res, isLoading: false } )
       })
      .catch(err => {
        toast('Erro ao processar arquivos: ' + err, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } );
      });
  }

  getTotalInputFiles = () => {
    httpGatewayFunctions.totalConvertInputFiles()
      .then(res => { 
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        return res.json();        
      })
      .then(res => this.setState( { totalInputFiles: res, isLoading: false } ))
      .catch(err => { 
        toast('Erro ao processar arquivos: ' + err.message, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } );
    });
  }

  downloadMessagesFile = () => {
    toast('Aguarde o processamento...', { type: toast.TYPE.INFO, autoClose: true, closeButton: false });
    this.setState( { isLoading: true } );
    httpGatewayFunctions.downloadMessagesFile()
      .then(res => {
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        return res.blob();
      })
      .then(blob => {
          let url = window.URL.createObjectURL(blob);
          let a = document.createElement('a');
          a.href = url;
          a.download = 'mensagens.pdf';
          a.click();
          this.setState( { isLoading: false } );
      })
      .catch(err => {
        toast('Erro ao baixar arquivo: ' + err.message, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } );
      });
  }

  downloadOutputFile = () => {
    toast('Aguarde o processamento...', { type: toast.TYPE.INFO, autoClose: true, closeButton: false });
    this.setState( { isLoading: true } );
    httpGatewayFunctions.downloadOutputFile()
      .then(res => { 
        if (res.status === 500) throw new Error("Falha no processamento no servidor!");
        return res.blob();
      })
      .then(blob => {
          let url = window.URL.createObjectURL(blob);
          let a = document.createElement('a');
          a.href = url;
          a.download = 'saida.xls';
          a.click();               
          this.setState( { isLoading: false } );     
      })
      .catch(err => { 
        toast('Erro ao baixar arquivo: ' + err.message, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
        this.setState( { isLoading: false } );
      });
  }


  render() {
    
    return (
      
      <div className="App">

      {/* autenticação ? */}

        <Header />

        <FlowMenu step={this.state.step} />

        <ToastContainer autoClose={5000} />
               
        <main>

          { this.state.isLoading ? (<LoaderSpinner />) : this.getComponentForStep() }

        </main>

        <Footer />

      </div>

    );
  }
}

export default App;
