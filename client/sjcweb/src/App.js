import React, { Component } from 'react';
import Header from './component/Header';
import FlowMenu from './component/FlowMenu';
import Loader from './component/Loader';

import './App.css';
import './font-awesome-4.7.0/css/font-awesome.min.css';

class App extends Component {
  render() {
    return (
      
      <div className="App">

        <Header />

        <FlowMenu />

        {/* autenticação ? */}

        <main>

          <Loader />

        </main>

      </div>

    );
  }
}

export default App;
