import React, { Component } from 'react'
import './Result.css';

export default class Result extends Component {

  render() {
    const { uploadedFiles, resultFiles, uploadedAfastFile } = this.props;
    
    return (
      <div className="result-input">
            <h2>Resultado do Carregamento</h2>

            <p className="result-afast">Arquivo de afastamentos: <span>{ uploadedAfastFile !== null ? uploadedAfastFile.name : ' nenhum ' }</span></p>

            <table className="table-entrada">
                <thead>
                    <tr>
                    <th>Arquivos de Entrada (diretório 'ENTRADA')</th>
                    <th>Planilhas Geradas (diretório 'CARREGADO')</th>
                    </tr>
                </thead>
                <tbody>

                    {
                    resultFiles.map( (resultFile, index) => {
                        const inputFile = uploadedFiles[index];
                        return (
                        <tr key={index}>
                            <td>{inputFile.name}</td>
                            <td>{resultFile.name} <small>(xx serv. op | yy serv. adm)</small></td> 
                        </tr>
                        )
                    })
                    }
                    
                </tbody>
            </table>
        
      </div>
    )
  }

}
