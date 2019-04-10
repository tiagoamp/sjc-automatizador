import React, { Component } from 'react';
import Header from './component/Header';
import Footer from './component/Footer';
import FlowMenu from './component/FlowMenu';
import Loader from './component/input/Loader';
import { ToastContainer, toast } from 'react-toastify';

import './App.css';
import './font-awesome-4.7.0/css/font-awesome.min.css';
import 'react-toastify/dist/ReactToastify.min.css';

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
    const newFiles = files.filter(f => f.name.toLowerCase().endsWith('.pdf'));
    const removedFiles = files.filter(f => !f.name.toLowerCase().endsWith('.pdf'));
    if (newFiles.length !== files.length) {
      const removedFilesNames = removedFiles.map(f => ' ' + f.name);
      toast('Os arquivos com extensão inválidas nao foram carregados: ' + removedFilesNames, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
    }
    const prevFiles = [...this.state.uploadedFiles];
    const updatedFiles = prevFiles.concat(newFiles); 
    if (newFiles.length > 0) toast('Arquivos carregados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
    this.setState( {uploadedFiles: updatedFiles, resultFiles: []} );    
  }

  handleAfastamentosFilesUpload = (file) => {
    const filename = file.name.toLowerCase();
    if (!filename.endsWith('.xlsx') && !filename.endsWith('.xls')) {
      toast('Extensão do arquivo inválida: ' + file.name, { type: toast.TYPE.ERROR, autoClose: true, closeButton: false });
      return;      
    }
    toast('Arquivos carregados!', { type: toast.TYPE.SUCCESS, autoClose: true, closeButton: false }); 
    this.setState( { uploadedAfastFile: file, resultFiles: []} );      
  }

  resetFiles = () => {
    this.setState( { step: 0, uploadedFiles: [], uploadedAfastFile: null, resultFiles: [] } );
  } 

  loadInputFiles = () => {
    if (this.state.uploadedFiles.length === 0) {
      toast('Nenhum arquivo feito upload!', { type: toast.TYPE.ERROR, autoClose: true, closeButton: false }); 
      return;
    }
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

        <ToastContainer autoClose={4000} />
               
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
