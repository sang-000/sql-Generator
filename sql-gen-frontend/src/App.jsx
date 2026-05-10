import { useState } from "react";
import "./App.css";

function App() {
  const [prompt, setPrompt] = useState("");
  const [query, setQuery] = useState("");
  const [explanation, setExplanation] = useState("");
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState("");

  const generateSql = async () => {
    if (!prompt.trim()) return;
    setLoading(true);
    setQuery("");
    setExplanation("");
    setError("");

    try {
      const response = await fetch("http://localhost:8081/api/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt }),
      });
      const data = await response.json();
      setQuery(data.query);
      setExplanation(data.explanation);
    } catch (err) {
      setError("Failed to connect to backend. Make sure Spring Boot is running.");
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(query);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && e.ctrlKey) generateSql();
  };

  return (
    <div className="container">
      <div className="card">

        <div className="header">
          {/* <div className="logo">⚡</div> */}
          <h1 className="title">SQL Generator</h1>
          <p className="subtitle">
            Type your requirement in plain English — get MySQL query instantly
          </p>
        </div>

        <div className="input-section">
          <label className="label">Describe what you need</label>
          <textarea
            className="textarea"
            rows={4}
            placeholder="e.g. show all employees in the HR department with salary above 50000"
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <span className="hint">Tip: Press Ctrl + Enter to generate</span>
          <button
            className="generate-btn"
            onClick={generateSql}
            disabled={loading || !prompt.trim()}
          >
            {loading ? (
              <span className="loading-text">
                <span className="spinner"></span> Generating...
              </span>
            ) : (
              "Generate SQL"
            )}
          </button>
        </div>

        {error && <div className="error-box">{error}</div>}

        {query && (
          <div className="result-section">
            <div className="result-header">
              <h3>Generated Query</h3>
              <button className="copy-btn" onClick={copyToClipboard}>
                {copied ? "✅ Copied!" : "📋 Copy"}
              </button>
            </div>
            <div className="code-box">
              <pre><code>{query}</code></pre>
            </div>

            {explanation && (
              <div className="explanation">
                <h3>💡 Explanation</h3>
                <p>{explanation}</p>
              </div>
            )}
          </div>
        )}

      </div>
    </div>
  );
}

export default App;