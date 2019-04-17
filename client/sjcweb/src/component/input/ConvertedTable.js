import React, { Component } from 'react'
import './ConvertedTable.css';

export default class ConvertedTable extends Component {

  render() {
    const { convertedFiles, nextStep } = this.props;
    
    return (
      <div className="result-input">
            <h2>Resultado do Carregamento</h2>

            <p>Diretório: "Entrada"</p>

            <table className="table-entrada">
                <thead>
                    <tr>
                    <th>Arquivos de Entrada</th>
                    <th>Planilhas Geradas</th>
                    </tr>
                </thead>
                <tbody>

                    {
                    convertedFiles.map( (convFile, index) => {
                        return (
                        <tr key={index}>
                            <td>{convFile.originalFileName}</td>
                            <td>{convFile.convertedFileName} <small>({convFile.operacionalRowsCount} serv. op | {convFile.administrativoRowsCount} serv. adm)</small></td> 
                        </tr>
                        )
                    })
                    }
                    
                </tbody>
            </table>

            <button onClick={nextStep}>AVANÇAR<i className="fa fa-angle-double-right fa-1x" aria-hidden="true"></i></button>
        
      </div>
    )
  }

}
