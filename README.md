# Hiver AI Support Agent

An AI-powered customer support agent built for the Hiver SDE Intern take-home assignment.

The system:
- Classifies incoming customer messages into support intents.
- Drafts replies grounded in historically observed support responses.
- Decides whether a case should be auto-handled or escalated to a human.
- Provides an evaluation harness for measuring agent performance against a hand-labelled golden set.

## Tech Stack

- Java 17
- Spring Boot
- React
- MySQL
- Gemini API
- Maven
- Node.js / npm

## Project Structure

```text
hiver-sde-intern/
├── backend/                 # Spring Boot API and AI agent
├── frontend/                # React dashboard
├── evaluation/              # Golden set and evaluation resources
├── docs/                    # Report and decision log
└── README.md
```

## Prerequisites

Install:

- Java 17+
- Maven
- Node.js and npm
- MySQL
- A Gemini API key with available API quota

Check the installations:

```bash
java -version
mvn -version
node -v
npm -v
mysql --version
```

## 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd hiver-sde-intern
```

Replace `<YOUR_GITHUB_REPOSITORY_URL>` with the actual GitHub repository URL before submitting.

## 2. Configure MySQL

Create the database used by the backend.

For example:

```sql
CREATE DATABASE hiver_support;
```

Update the database username/password in:

```text
backend/src/main/resources/application.properties
```

Do not commit production credentials or API keys to GitHub.

## 3. Configure Gemini

Set the Gemini API key as an environment variable.

### Windows PowerShell

```powershell
$env:GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

### macOS / Linux

```bash
export GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

The application should reference the environment variable rather than storing the secret directly in source code.

Example:

```properties
llm.api.key=${GEMINI_API_KEY}
```

If the project uses a specific Gemini model/URL, keep those values in `application.properties`.

## 4. Start the Backend

Open a terminal:

```bash
cd backend
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

Wait until Spring Boot reports that the application has started successfully.

## 5. Start the Frontend

Open a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the local URL printed by Vite/React in the terminal.

The dashboard provides access to:

- Support Agent
- Data Ingestion
- Model Evaluation

## 6. Load / Prepare the Data

The primary dataset for this assignment is the Kaggle:

**Customer Support on Twitter** dataset.

The project uses a subsample rather than requiring the full dataset.

Follow the data-ingestion instructions implemented in the application to load the selected brand's support conversations.

The selected brand and intent taxonomy are documented in the report.

## 7. Load the Golden Evaluation Set

From the frontend:

1. Open **Evaluation**.
2. Click **Load Golden Set**.
3. Confirm that the examples are loaded successfully.

The golden set contains hand-labelled examples covering the project's intent categories, escalation decisions, and expected response quality.

## 8. Run the Evaluation

From the **Evaluation** page:

1. Click **Run Golden Set Evaluation**.
2. Wait for the evaluation to finish.
3. Review the reported intent accuracy, escalation accuracy, and reply-quality results.
4. Click **Compare Baselines** to compare the AI agent with the baseline approaches.

The evaluation uses the checked-in golden set and produces the headline metrics reported in the project report.

## 9. Reproduce the Headline Results

The intended reproduction flow is:

```text
Clone repository
      ↓
Configure MySQL
      ↓
Set GEMINI_API_KEY
      ↓
Start backend
      ↓
Start frontend
      ↓
Load golden set
      ↓
Run Golden Set Evaluation
      ↓
Compare Baselines
```

### Expected Output

The exact values below should be filled with the results from the final evaluation run before submission:

```text
Intent Accuracy: XX%
Escalation Accuracy: XX%
Reply Quality: XX
```

Do not manually edit these values to match a target. They should correspond to an actual reproducible evaluation run.

## 10. Running an Individual Support Query

Use the dashboard to submit a customer-support message.

The agent performs:

```text
Customer Message
       ↓
Intent Classification
       ↓
Historical Response Retrieval
       ↓
Reply Drafting
       ↓
Escalation Decision
       ↓
Human Review / Auto-Handle
```

For escalated cases, the system provides a reason and confidence.

## Evaluation Methodology

The evaluation is based on a hand-labelled golden set rather than relying only on model-generated scores.

Metrics include:

- **Intent Accuracy** — percentage of examples assigned the correct intent.
- **Escalation Accuracy** — percentage of examples where AUTO_HANDLE vs. ESCALATE matches the human label.
- **Reply Quality** — LLM-as-judge assessment of response helpfulness and appropriateness.
- **Baseline comparison** — comparison against a trivial baseline and a simple baseline.
- **Human agreement check** — comparison between LLM judge ratings and human ratings on a manually reviewed subset.

See:

```text
docs/REPORT.md
docs/DECISION_LOG.md
```

for the detailed methodology, results, failure analysis, limitations, and engineering decisions.

## Important Limitations

This is a take-home assignment prototype rather than a production support platform.

Known limitations and trade-offs are documented in the report, including:

- Dataset noise and incomplete conversation context.
- Ambiguous or multi-intent customer messages.
- Limitations of historical-response retrieval.
- LLM variability.
- LLM-as-judge limitations.
- The difference between aggregate metrics and performance on difficult/high-risk cases.

## Security

Never commit:

- Gemini API keys
- Database passwords
- `.env` files
- Other credentials or secrets

Use environment variables for secrets.

## Submission Checklist

Before submitting to Hiver, verify:

- [ ] Repository is accessible to Hiver.
- [ ] README contains complete setup instructions.
- [ ] Gemini API key is not committed.
- [ ] MySQL setup is documented.
- [ ] Backend starts successfully.
- [ ] Frontend starts successfully.
- [ ] Golden set loads successfully.
- [ ] Golden-set evaluation runs successfully.
- [ ] Baseline comparison runs successfully.
- [ ] Headline metrics in this README match an actual evaluation run.
- [ ] `docs/REPORT.md` is included and within the 6-page-equivalent limit.
- [ ] `docs/DECISION_LOG.md` contains 10–15 non-obvious decisions.
- [ ] Failure analysis includes real evaluation examples.
- [ ] The mandatory “What is misleading about my headline number?” section is included.
- [ ] The GitHub repository URL is the same valid URL submitted to Hiver.

## License

This repository was created for the Hiver SDE Intern take-home assignment.
