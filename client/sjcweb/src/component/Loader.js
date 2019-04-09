import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import './Loader.css';

export default class Loader extends Component {

  constructor() {
    super();
    this.state = { uploadedFiles: [], uploadedAfastFile: null };
  }

  render() {
    const uploadedFiles = this.state.uploadedFiles;
    const uploadedAfastFile = this.state.uploadedFiles;

    return (

      <div className="entrada-body">

          <h2>Entrada para processamento</h2>

          <div className="dropzone-group">
            <Dropzone onDrop={acceptedFiles => console.log(acceptedFiles)}>
              {({getRootProps, getInputProps}) => (
                <section>
                  <div {...getRootProps()} className="dropzone-div">
                    <input {...getInputProps()} />
                    <span><strong>Relatórios</strong></span>
                    <p>Arraste aqui os arquivos de relatórios em 'pdf' para o quadro abaixo ou click para selecioná-los</p>
                    <div className="arquivos">Arquivos carregados: <span>{ uploadedFiles.length > 0 ? uploadedFiles.map( f => f + ' ') : 'nenhum' }</span></div>
                  </div>
                </section>
              )}
            </Dropzone>

            <Dropzone onDrop={acceptedFiles => console.log(acceptedFiles)}>
              {({getRootProps, getInputProps}) => (
                <section>
                  <div {...getRootProps()} className="dropzone-div">
                    <input {...getInputProps()} />
                    <span><strong>Afastamentos</strong></span>
                    <p>Arraste o arquivos de afastamentos em 'xlsx' para o quadro abaixo ou click para selecioná-lo</p>
                    <div className="arquivos">Arquivos carregados: <span>{ uploadedAfastFile === null ? uploadedAfastFile : 'nenhum' }</span></div>
                  </div>
                </section>
              )}
            </Dropzone>
          </div>

          <div>
            <button>LIMPAR</button>
            <button>CARREGAR</button>
          </div>
        
          <h2>Resultado do Carregamento</h2>

          <table className="table-entrada">
            <thead>
              <tr>
                <th>Arquivos de Entrada (pdf)</th>
                <th>Planilhas Geradas</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>UPA Blablabla NORTE.PDF <a className="link-formatted" href="/test.pdf">abrir</a></td>
                <td>UPA Blablabla NORTE.xlsx <small>(xx serv. op | yy serv. adm)</small> <a className="link-formatted" href="/test.xlsx">abrir</a></td> 
              </tr>
              <tr>
                <td>UPA Blablabla NORTE.PDF <a className="link-formatted" href="/test.pdf">abrir</a></td>
                <td>UPA Blablabla NORTE.xlsx <small>(xx serv. op | yy serv. adm)</small> <a className="link-formatted" href="/test.xlsx">abrir</a></td> 
              </tr>
              <tr>
                <td>UPA Blablabla NORTE.PDF <a className="link-formatted" href="/test.pdf">abrir</a></td>
                <td>UPA Blablabla NORTE.xlsx <small>(xx serv. op | yy serv. adm)</small> <a className="link-formatted" href="/test.xlsx">abrir</a></td> 
              </tr>
              <tr>
                <td>UPA Blablabla NORTE.PDF <a className="link-formatted" href="/test.pdf">abrir</a></td>
                <td>UPA Blablabla NORTE.xlsx <small>(xx serv. op | yy serv. adm)</small> <a className="link-formatted" href="/test.xlsx">abrir</a></td> 
              </tr>
            </tbody>
          </table>

      </div>

    )
  }

}
