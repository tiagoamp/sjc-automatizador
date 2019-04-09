import React, { Component } from 'react'
import './FlowMenu.css';

export default class FlowMenu extends Component {
  
  render() {
    const activePhaseNr = this.props.phaseNumber || 0;
        
    return (

      <nav>
        
          <div className={activePhaseNr === 0 ? 'box' : 'box inactive'}>
            <div>
                <i className="fa fa-files-o fa-2x" aria-hidden="true"></i>
                <span className="phaseName">Carregar Dados</span>
            </div>
            <p>Extrai dados dos arquivos no formato 'pdf' e gera as planilhas de entrada</p>            
          </div>

          <i className="fa fa-angle-double-right fa-3x" aria-hidden="true"></i>

          <div className={activePhaseNr === 1 ? 'box' : 'box inactive'}>
            <div>
                <i className="fa fa-cogs fa-2x" aria-hidden="true"></i>
                <span className="phaseName">Processar Dados</span>
            </div>
            <p>Processa dados das planilhas de entrada gerando realtório de processamento com erros e alertas</p>            
          </div>

          <i className="fa fa-angle-double-right fa-3x" aria-hidden="true"></i>

          <div className={activePhaseNr === 2 ? 'box' : 'box inactive'}>
            <div>
                <i className="fa fa-table fa-2x" aria-hidden="true"></i>
                <span className="phaseName">Gerar planilha de saída</span>
            </div>
            <p>Gera planilha de saída a partir dos dados de entrada carregados</p>            
          </div>

      </nav>

    )
  }

}
