import React, { useState } from 'react';
import axios from 'axios';
import { Send, Bot, AlertTriangle, CheckCircle } from 'lucide-react';
import './SupportDashboard.css';

function SupportDashboard() {
  const [customerMessage, setCustomerMessage] = useState('');
  const [customerName, setCustomerName] = useState('');
  const [customerId, setCustomerId] = useState('');
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResponse(null);

    try {
      const requestData = {
        customerMessage,
        customerName,
        customerId,
        tweetId: `tweet_${Date.now()}`
      };

      const res = await axios.post('http://localhost:8080/api/support/process', requestData);
      setResponse(res.data);
    } catch (err) {
      setError('Failed to process message. Please ensure the backend is running.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Support Agent Dashboard</h1>
        <p>Process customer messages with AI-powered intent classification and response drafting</p>
      </div>

      <div className="dashboard-grid">
        <div className="card">
          <div className="card-header">
            <Bot size={24} />
            <span>New Customer Message</span>
          </div>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Customer Name</label>
              <input
                type="text"
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
                placeholder="Enter customer name"
                required
              />
            </div>
            <div className="form-group">
              <label>Customer ID</label>
              <input
                type="text"
                value={customerId}
                onChange={(e) => setCustomerId(e.target.value)}
                placeholder="Enter customer ID"
                required
              />
            </div>
            <div className="form-group">
              <label>Customer Message</label>
              <textarea
                value={customerMessage}
                onChange={(e) => setCustomerMessage(e.target.value)}
                placeholder="Enter the customer's message..."
                rows={4}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              <Send size={18} />
              {loading ? 'Processing...' : 'Process Message'}
            </button>
          </form>
        </div>

        {error && (
          <div className="card error">
            <AlertTriangle size={20} />
            <span>{error}</span>
          </div>
        )}

        {response && (
          <div className="card">
            <div className="card-header">
              <CheckCircle size={24} />
              <span>AI Response</span>
            </div>
            <div className="response-details">
              <div className="response-item">
                <label>Intent:</label>
                <span className="intent-badge">{response.intent}</span>
              </div>
              <div className="response-item">
                <label>Escalation Decision:</label>
                <span className={`badge ${response.escalationDecision === 'AUTO_HANDLE' ? 'badge-auto' : 'badge-escalate'}`}>
                  {response.escalationDecision}
                </span>
              </div>
              <div className="response-item">
                <label>Confidence Score:</label>
                <span>{(response.confidenceScore * 100).toFixed(1)}%</span>
              </div>
              <div className="response-item">
                <label>Escalation Reason:</label>
                <span>{response.escalationReason}</span>
              </div>
              <div className="response-item">
                <label>Drafted Reply:</label>
                <div className="drafted-reply">{response.draftedReply}</div>
              </div>
              {response.similarResponses && response.similarResponses.length > 0 && (
                <div className="response-item">
                  <label>Similar Historical Responses:</label>
                  <ul className="similar-responses">
                    {response.similarResponses.slice(0, 3).map((similar, index) => (
                      <li key={index}>{similar}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default SupportDashboard;