import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BarChart3, Play, CheckCircle, AlertCircle, TrendingUp, Database } from 'lucide-react';
import './Evaluation.css';

function Evaluation() {
  const [evaluationResults, setEvaluationResults] = useState(null);
  const [baselineResults, setBaselineResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const runEvaluation = async () => {
    setLoading(true);
    setError(null);
    setEvaluationResults(null);

    try {
      const res = await axios.get('http://localhost:8080/api/evaluation/golden-set');
      setEvaluationResults(res.data);
    } catch (err) {
      setError('Failed to run evaluation. Please ensure the golden set is populated.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const runBaselineComparison = async () => {
    setLoading(true);
    setError(null);
    setBaselineResults(null);

    try {
      const res = await axios.get('http://localhost:8080/api/evaluation/baselines');
      setBaselineResults(res.data);
    } catch (err) {
      setError('Failed to run baseline comparison.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const loadGoldenSet = async () => {
    setLoading(true);
    setError(null);

    try {
      const res = await axios.post('http://localhost:8080/api/golden-set/load', {
        jsonFilePath: 'data/sample_golden_set.json'
      });
      alert(res.data.message);
    } catch (err) {
      setError('Failed to load golden set. Make sure the JSON file exists.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    runBaselineComparison();
  }, []);

  return (
    <div className="evaluation">
      <div className="dashboard-header">
        <h1>Model Evaluation</h1>
        <p>Evaluate AI agent performance against golden set and baseline models</p>
      </div>

      <div className="evaluation-actions">
        <button 
          className="btn btn-primary" 
          onClick={runEvaluation}
          disabled={loading}
        >
          <Play size={18} />
          {loading ? 'Running...' : 'Run Golden Set Evaluation'}
        </button>
        <button 
          className="btn btn-secondary" 
          onClick={runBaselineComparison}
          disabled={loading}
        >
          <TrendingUp size={18} />
          Compare Baselines
        </button>
        <button 
          className="btn btn-secondary" 
          onClick={loadGoldenSet}
          disabled={loading}
        >
          <Database size={18} />
          Load Golden Set
        </button>
      </div>

      {error && (
        <div className="card error">
          <AlertCircle size={20} />
          <span>{error}</span>
        </div>
      )}

      {baselineResults && !baselineResults.error && (
        <div className="card">
          <div className="card-header">
            <TrendingUp size={24} />
            <span>Baseline Comparison</span>
          </div>
          <div className="baseline-results">
            <div className="baseline-item">
              <h4>LLM-based System (Ours)</h4>
              {baselineResults.llm_system && (
                <div className="metrics">
                  <div className="metric">
                    <span className="metric-label">Intent Accuracy:</span>
                    <span className="metric-value">{(baselineResults.llm_system.intentAccuracy * 100).toFixed(1)}%</span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">Escalation Accuracy:</span>
                    <span className="metric-value">{(baselineResults.llm_system.escalationAccuracy * 100).toFixed(1)}%</span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">Reply Quality:</span>
                    <span className="metric-value">{(baselineResults.llm_system.averageReplyQuality * 100).toFixed(1)}%</span>
                  </div>
                </div>
              )}
            </div>
            <div className="baseline-item">
              <h4>Random Baseline</h4>
              <div className="metrics">
                <div className="metric">
                  <span className="metric-label">Intent Accuracy:</span>
                  <span className="metric-value">{(baselineResults.random_baseline.intentAccuracy * 100).toFixed(1)}%</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Escalation Accuracy:</span>
                  <span className="metric-value">{(baselineResults.random_baseline.escalationAccuracy * 100).toFixed(1)}%</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Reply Quality:</span>
                  <span className="metric-value">{(baselineResults.random_baseline.averageReplyQuality * 100).toFixed(1)}%</span>
                </div>
              </div>
            </div>
            <div className="baseline-item">
              <h4>Keyword Baseline</h4>
              <div className="metrics">
                <div className="metric">
                  <span className="metric-label">Intent Accuracy:</span>
                  <span className="metric-value">{(baselineResults.keyword_baseline.intentAccuracy * 100).toFixed(1)}%</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Escalation Accuracy:</span>
                  <span className="metric-value">{(baselineResults.keyword_baseline.escalationAccuracy * 100).toFixed(1)}%</span>
                </div>
                <div className="metric">
                  <span className="metric-label">Reply Quality:</span>
                  <span className="metric-value">{(baselineResults.keyword_baseline.averageReplyQuality * 100).toFixed(1)}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {evaluationResults && !evaluationResults.error && (
        <div className="card">
          <div className="card-header">
            <BarChart3 size={24} />
            <span>Golden Set Evaluation Results</span>
          </div>
          <div className="evaluation-summary">
            <div className="summary-item">
              <span className="summary-label">Total Evaluated:</span>
              <span className="summary-value">{evaluationResults.totalEvaluated}</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Intent Accuracy:</span>
              <span className="summary-value success">{(evaluationResults.intentAccuracy * 100).toFixed(1)}%</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Escalation Accuracy:</span>
              <span className="summary-value success">{(evaluationResults.escalationAccuracy * 100).toFixed(1)}%</span>
            </div>
            <div className="summary-item">
              <span className="summary-label">Average Reply Quality:</span>
              <span className="summary-value success">{(evaluationResults.averageReplyQuality * 100).toFixed(1)}%</span>
            </div>
          </div>

          {evaluationResults.detailedResults && evaluationResults.detailedResults.length > 0 && (
            <div className="detailed-results">
              <h3>Detailed Results</h3>
              <div className="results-table">
                <table>
                  <thead>
                    <tr>
                      <th>Message</th>
                      <th>Intent</th>
                      <th>Escalation</th>
                      <th>Reply Quality</th>
                    </tr>
                  </thead>
                  <tbody>
                    {evaluationResults.detailedResults.slice(0, 10).map((result, index) => (
                      <tr key={index}>
                        <td className="message-cell">{result.customerMessage.substring(0, 50)}...</td>
                        <td>
                          <span className={`match-badge ${result.intentMatch ? 'match' : 'mismatch'}`}>
                            {result.predictedIntent}
                          </span>
                        </td>
                        <td>
                          <span className={`match-badge ${result.escalationMatch ? 'match' : 'mismatch'}`}>
                            {result.predictedEscalation}
                          </span>
                        </td>
                        <td>{(result.replyQuality * 100).toFixed(0)}%</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}

      <div className="card">
        <div className="card-header">
          <CheckCircle size={24} />
          <span>Evaluation Instructions</span>
        </div>
        <div className="instructions">
          <h3>Golden Set Requirements:</h3>
          <ul>
            <li>Create 150-250 hand-labelled examples for evaluation</li>
            <li>Each example should include: customer message, true intent, escalation decision, and expected response quality</li>
            <li>Mark examples as golden set in the database (is_golden_set = true)</li>
            <li>Include diverse cases covering all intent categories</li>
            <li>Sample edge cases and ambiguous messages for robustness testing</li>
          </ul>
          <h3>Metrics Explained:</h3>
          <ul>
            <li><strong>Intent Accuracy:</strong> Percentage of correctly classified intents</li>
            <li><strong>Escalation Accuracy:</strong> Percentage of correct auto-handle vs escalate decisions</li>
            <li><strong>Reply Quality:</strong> LLM-as-judge rating (0.0-1.0) on response helpfulness and appropriateness</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default Evaluation;