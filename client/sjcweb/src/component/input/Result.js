import React, { Component } from 'react'
import './Result.css';

export default class Result extends Component {

  render() {
    const uploadedFiles = this.props.uploadedFiles;
    const resultFiles = this.props.resultFiles;

    return (
      <div className="result-input">
            <h2>Resultado do Carregamento</h2>

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
                        <tr key="{resultFile.name}">
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
