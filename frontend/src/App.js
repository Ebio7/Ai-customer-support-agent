import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import './App.css';
import SupportDashboard from './pages/SupportDashboard';
import DataIngestion from './pages/DataIngestion';
import Evaluation from './pages/Evaluation';
import { MessageSquare, Database, BarChart3, Home } from 'lucide-react';

function App() {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <div className="nav-brand">
            <MessageSquare size={24} />
            <span>Hiver Support Agent</span>
          </div>
          <div className="nav-links">
            <Link to="/" className="nav-link">
              <Home size={18} />
              Dashboard
            </Link>
            <Link to="/data" className="nav-link">
              <Database size={18} />
              Data Ingestion
            </Link>
            <Link to="/evaluation" className="nav-link">
              <BarChart3 size={18} />
              Evaluation
            </Link>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<SupportDashboard />} />
            <Route path="/data" element={<DataIngestion />} />
            <Route path="/evaluation" element={<Evaluation />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;