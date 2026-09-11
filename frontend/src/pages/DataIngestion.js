import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Upload, Database, CheckCircle, AlertCircle } from 'lucide-react';
import './DataIngestion.css';

function DataIngestion() {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState(null);
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/data/stats');
      setStats(res.data);
    } catch (err) {
      console.error('Error fetching stats:', err);
    }
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile.name.endsWith('.csv')) {
      setFile(selectedFile);
      setError(null);
    } else {
      setError('Please select a CSV file');
      setFile(null);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file first');
      return;
    }

    setUploading(true);
    setError(null);
    setUploadResult(null);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await axios.post('http://localhost:8080/api/data/ingest', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      setUploadResult(res.data);
      fetchStats();
    } catch (err) {
      setError('Failed to upload file. Please ensure the backend is running and the file format is correct.');
      console.error('Error:', err);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="data-ingestion">
      <div className="dashboard-header">
        <h1>Data Ingestion</h1>
        <p>Upload Twitter customer support data to train and evaluate the AI agent</p>
      </div>

      <div className="stats-grid">
        {stats && (
          <>
            <div className="stat-card">
              <Database size={32} />
              <div className="stat-content">
                <div className="stat-value">{stats.totalConversations}</div>
                <div className="stat-label">Total Conversations</div>
              </div>
            </div>
            <div className="stat-card">
              <CheckCircle size={32} />
              <div className="stat-content">
                <div className="stat-value">{stats.brandConversations}</div>
                <div className="stat-label">Brand Conversations</div>
              </div>
            </div>
          </>
        )}
      </div>

      <div className="card">
        <div className="card-header">
          <Upload size={24} />
          <span>Upload CSV Data</span>
        </div>
        <form onSubmit={handleUpload}>
          <div className="form-group">
            <label>Select CSV File</label>
            <input
              type="file"
              accept=".csv"
              onChange={handleFileChange}
              disabled={uploading}
            />
            <p className="file-hint">
              Expected columns: tweet_id, author_id, author_name, text, brand, inbound_or_outbound, response_tweet_id, response_text
            </p>
          </div>
          {file && (
            <div className="selected-file">
              <CheckCircle size={16} />
              <span>Selected: {file.name}</span>
            </div>
          )}
          <button type="submit" className="btn btn-primary" disabled={uploading || !file}>
            <Upload size={18} />
            {uploading ? 'Uploading...' : 'Upload Data'}
          </button>
        </form>
      </div>

      {error && (
        <div className="card error">
          <AlertCircle size={20} />
          <span>{error}</span>
        </div>
      )}

      {uploadResult && (
        <div className="card success">
          <CheckCircle size={20} />
          <div>
            <strong>Upload Successful!</strong>
            <p>{uploadResult.message}</p>
          </div>
        </div>
      )}

      <div className="card">
        <div className="card-header">
          <Database size={24} />
          <span>Data Format Instructions</span>
        </div>
        <div className="instructions">
          <h3>CSV File Requirements:</h3>
          <ul>
            <li>File must be in CSV format with headers</li>
            <li>Required columns: tweet_id, author_id, author_name, text, brand, inbound_or_outbound, response_tweet_id, response_text</li>
            <li>The system will filter conversations for the configured brand (default: AmazonSupport)</li>
            <li>Maximum file size: 10MB</li>
            <li>Recommended sample size: 1,000-10,000 conversations for training</li>
          </ul>
          <h3>Data Source:</h3>
          <p>
            Download the Customer Support on Twitter dataset from{' '}
            <a href="https://www.kaggle.com/datasets/thoughtvector/customer-support-on-twitter" target="_blank" rel="noopener noreferrer">
              Kaggle
            </a>
          </p>
        </div>
      </div>
    </div>
  );
}

export default DataIngestion;