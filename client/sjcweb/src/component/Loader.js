import React, { Component } from 'react'
import Dropzone from 'react-dropzone'
import './Loader.css';

export default class Loader extends Component {

  render() {
    return (

      <div className="entrada-body">

          <h2>Entrada para processamento</h2>

          <div className="dropzone-group">
            <Dropzone onDrop={acceptedFiles => console.log(acceptedFiles)}>
              {({getRootProps, getInputProps}) => (
                <section>
                  <div {...getRootProps()} className="dropzone-div">
                    <input {...getInputProps()} />
                    <p><strong>Relatórios</strong></p>
                    <p>Arraste aqui os arquivos de relatórios em 'pdf' para o quadro abaixo ou click para selecioná-los</p>
                  </div>
                </section>
              )}
            </Dropzone>

            <Dropzone onDrop={acceptedFiles => console.log(acceptedFiles)}>
              {({getRootProps, getInputProps}) => (
                <section>
                  <div {...getRootProps()} className="dropzone-div">
                    <input {...getInputProps()} />
                    <p><strong>Afastamentos</strong></p>
                    <p>Arraste o arquivos de afastamentos em 'xlsx' para o quadro abaixo ou click para selecioná-lo</p>
                  </div>
                </section>
              )}
            </Dropzone>
          </div>

          <button>Carregar</button>
        
      </div>

    )
  }

}
