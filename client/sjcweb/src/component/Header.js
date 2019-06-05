import React, { Component } from 'react'
import './Header.css';

export default class Header extends Component {

  render() {
    return (
      <header>
          <span className='brand'>SJC Automatizador</span>
          <span className='by-tag'>by @tiagoamp</span>
      </header>
    )
  }

}
