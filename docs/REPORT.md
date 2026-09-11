# Hiver SDE Intern — AI Support Agent Technical Report

> **Submission note:** This report is written to match the current project structure while keeping evaluation claims reproducible. Before submission, replace every `[FINAL ...]` placeholder with values from the final evaluation run. Do not report the old placeholder results from earlier versions of the project.

---

# Page 1 — Problem & Approach

## Problem Framing

The goal is to build a focused AI support agent for one brand from the *Customer Support on Twitter* dataset. The agent has three responsibilities:

1. Classify an incoming customer message into a small, data-driven intent taxonomy.
2. Draft a response grounded in how the selected brand has historically responded to similar issues.
3. Decide whether the case can be auto-handled or should be escalated to a human, with a reason and confidence.

For this prototype, "good" means:

- The predicted intent matches the manually assigned intent.
- The drafted response directly addresses the customer's issue and stays consistent with available historical support behaviour.
- High-risk or ambiguous cases are routed to a human rather than being aggressively automated.
- The evaluation is reproducible on a fixed hand-labelled golden set.

## Brand Selected

**AmazonSupport**

The project focuses on one brand so that intent definitions, historical responses, and response style remain internally consistent.

## Intent Taxonomy

The current application defines seven intents:

1. `technical_issue`
2. `billing_question`
3. `shipping_delay`
4. `product_inquiry`
5. `complaint`
6. `general_inquiry`
7. `account_issue`

These categories are intentionally broad. A smaller taxonomy reduces sparsity and makes the initial classifier easier to evaluate.

## System Architecture

```text
Customer message
       |
       v
Intent classification (LLM)
       |
       v
Historical response retrieval
       |
       v
Reply drafting (LLM)
       |
       v
Escalation decision (LLM)
       |
       +----> AUTO_HANDLE
       |
       +----> ESCALATE -> human review
       |
       v
Persist conversation + decision
```

The backend is implemented with Java/Spring Boot and MySQL. A React frontend provides an interactive dashboard for testing, data ingestion, and evaluation.

## What I Intentionally Did Not Build

This is a take-home prototype, not a production support platform. I intentionally did not build:

- Full multi-brand support.
- Model fine-tuning.
- A large-scale vector database.
- Autonomous execution of refunds, cancellations, or account changes.
- A full multi-turn conversation memory system.
- Production authentication, authorization, rate limiting, and observability.
- A claim that the model is safe to deploy without human oversight.

These exclusions keep the implementation focused on the assignment's core question: whether an AI support workflow can be built and evaluated credibly.

---

# Page 2 — Evaluation Methodology

## Golden Evaluation Set

The final evaluation set is intended to contain **150–250 hand-labelled examples**, as required by the assignment.

Each example is labelled for:

- Customer intent.
- Escalation decision.
- Expected response approach/quality.
- Complexity where useful.

The final checked-in golden set should be the source of truth for all reported metrics.

> **Current project status:** the repository version inspected during development contained only 10 actual examples while its metadata claimed 250. This must be corrected before submission so the reported evaluation size matches the checked-in data.

## Sampling Strategy

The intended sampling strategy is stratified sampling across:

- All seven intents.
- Different conversation/message complexity levels.
- Routine support questions.
- Ambiguous messages.
- Emotional/complaint messages.
- Potentially high-risk escalation cases.
- Noisy Twitter-style language.

The purpose is to avoid an evaluation set consisting only of easy, clearly worded examples.

## Labelling Process

Each example is assigned a human ground-truth label before model evaluation.

The label should be based on the message itself and the defined intent/escalation guidelines, rather than on the model's prediction.

For ambiguous examples, the labelling guideline should prefer the most operationally useful intent and escalate cases where incorrect automation has a materially higher cost.

## Metrics

### Intent Accuracy

```text
correct intent predictions / total golden examples
```

### Escalation Accuracy

```text
correct AUTO_HANDLE / ESCALATE decisions / total golden examples
```

For escalation, the final report should also consider the cost of false negatives: incorrectly auto-handling a high-risk case is more serious than unnecessarily escalating a routine case.

### Reply Quality

Reply quality is evaluated with an LLM-as-judge rubric covering:

- Relevance to the customer's problem.
- Helpfulness/actionability.
- Professional and empathetic tone.
- Consistency with historical support behaviour.
- Unsupported or hallucinated claims.

The final judge score should be reported only after the judge has been checked against a manually rated subset.

## Human Agreement With the LLM Judge

A manually rated subset of **[FINAL HUMAN-JUDGE SAMPLE SIZE]** generated replies should be independently scored by a human using the same rubric.

Report:

- Exact agreement: **[FINAL EXACT AGREEMENT]**
- Within-one-point agreement: **[FINAL WITHIN-ONE AGREEMENT]**
- Correlation/agreement statistic: **[FINAL AGREEMENT STATISTIC]**

This check is important because an LLM judge is itself an imperfect evaluator.

---

# Page 3 — Results

## Final Results

> Replace the placeholders below with results from the final reproducible evaluation run.

| System | Intent Accuracy | Escalation Accuracy | Reply Quality |
|---|---:|---:|---:|
| Trivial baseline | [FINAL %] | [FINAL %] | — |
| Keyword baseline | [FINAL %] | [FINAL %] | [FINAL SCORE / N/A] |
| AI Support Agent | **[FINAL %]** | **[FINAL %]** | **[FINAL SCORE]** |

## Baseline 1 — Trivial Baseline

The trivial baseline provides a deliberately simple reference point.

For intent, the final implementation should use either:

- majority-class prediction, or
- a clearly defined random classifier with a fixed seed.

For escalation, the baseline should use a simple constant prediction or fixed 50/50 reference, depending on the implementation.

The important property is that the baseline requires no semantic understanding.

## Baseline 2 — Keyword Baseline

The simple baseline uses manually defined keywords associated with the seven intents and straightforward escalation keywords/rules.

Example signals include terms associated with:

- shipping/tracking,
- billing/charges,
- account/login,
- technical failures,
- product questions,
- complaints,
- general information.

This baseline represents a conventional rule-based support system without an LLM.

## AI Support Agent

The AI agent uses an LLM for:

1. Intent classification.
2. Response drafting.
3. Escalation reasoning.

The response-generation step can additionally receive historical support responses retrieved from the selected brand's data.

## Results Interpretation

The key comparison is not only the absolute AI score, but the improvement over simple baselines.

Final interpretation:

> **[INSERT 2–4 SENTENCE INTERPRETATION AFTER FINAL RUN]**

For example, the analysis should state where the AI agent improves most, where it remains weak, and whether the gains justify the added complexity and API cost.

---

# Page 4 — Failure Analysis

Failure analysis should use examples from the **final golden-set evaluation run**. The following categories are the hypotheses to investigate; the final report should replace the placeholders with the actual observed failures.

## 1. Ambiguous Intent

**Example from final evaluation:** `[INSERT REAL EXAMPLE]`

**Observed behaviour:** `[INSERT MODEL PREDICTION]`

**Expected:** `[INSERT HUMAN LABEL]`

**Hypothesis:** The message contains signals for more than one intent, while the current classifier is explicitly required to choose one.

**Potential improvement:** Add multi-intent detection or a dedicated ambiguous/multi-intent route.

## 2. Multi-intent Messages

**Example from final evaluation:** `[INSERT REAL EXAMPLE]`

**Observed behaviour:** `[INSERT MODEL PREDICTION]`

**Expected:** `[INSERT HUMAN LABEL]`

**Hypothesis:** A single-label taxonomy loses information when a customer combines issues such as shipping and billing.

**Potential improvement:** Detect multiple intents and route the case according to the highest-risk component.

## 3. Noisy or Unusual Language

**Example from final evaluation:** `[INSERT REAL EXAMPLE]`

**Observed behaviour:** `[INSERT MODEL PREDICTION]`

**Expected:** `[INSERT HUMAN LABEL]`

**Hypothesis:** Twitter messages contain abbreviations, typos, sarcasm, missing context, and very short messages.

**Potential improvement:** Add preprocessing and few-shot examples containing realistic noisy language.

## 4. Incorrect Escalation

**Example from final evaluation:** `[INSERT REAL EXAMPLE]`

**Observed behaviour:** `[INSERT AUTO_HANDLE / ESCALATE]`

**Expected:** `[INSERT HUMAN LABEL]`

**Hypothesis:** Escalation criteria are easier to describe than to apply consistently across unusual financial, legal, safety, or management-related language.

**Potential improvement:** Use calibrated confidence thresholds and explicitly prioritise high-cost false negatives.

## 5. Unsupported Details in Replies

**Example from final evaluation:** `[INSERT REAL EXAMPLE]`

**Observed behaviour:** `[INSERT UNSUPPORTED CLAIM]`

**Expected:** The reply should remain within information supported by the customer message and retrieved historical evidence.

**Hypothesis:** An LLM may generate plausible details even when the retrieved support history does not justify them.

**Potential improvement:** Add a grounded-answer constraint and a post-generation factual/evidence check.

---

# Page 5 — What Is Misleading About My Headline Number?

A single aggregate accuracy number is useful, but it can be misleading.

## 1. Aggregate accuracy hides per-intent weaknesses

A model can achieve a strong overall score while performing poorly on minority or difficult intents.

The final report should therefore include per-intent performance:

| Intent | Number of Examples | Accuracy |
|---|---:|---:|
| technical_issue | [N] | [X%] |
| billing_question | [N] | [X%] |
| shipping_delay | [N] | [X%] |
| product_inquiry | [N] | [X%] |
| complaint | [N] | [X%] |
| general_inquiry | [N] | [X%] |
| account_issue | [N] | [X%] |

## 2. The taxonomy itself affects the number

Seven broad intents are easier to classify than a much more granular support taxonomy. High accuracy therefore does not mean that the system can solve every routing problem a production support team may have.

## 3. A curated golden set is not the production distribution

Hand-labelled evaluation examples are intentionally structured to cover important cases. Real Twitter traffic can be noisier and can contain distributions that differ from the evaluation set.

## 4. Errors have different costs

Intent accuracy treats errors similarly, but operationally they are not equally harmful.

For example:

```text
shipping_delay -> general_inquiry
```

may be inconvenient, while:

```text
legal/safety concern -> AUTO_HANDLE
```

could be a serious operational failure.

Escalation should therefore be judged using both accuracy and the cost of unsafe false negatives.

## 5. Reply-quality scores are not objective ground truth

LLM-as-judge scores can be useful, but the judge can also be biased or inconsistent. This is why the human-agreement study is part of the evaluation.

## Honest headline

The final headline should be phrased narrowly, for example:

> **"[FINAL INTENT ACCURACY]% intent accuracy on a [N]-example hand-labelled AmazonSupport evaluation set across seven broad intents."**

This is more informative and defensible than presenting the percentage as a general production accuracy claim.

---

# Page 6 — What I Would Do With One More Week

## 1. Improve Historical Retrieval

The current prototype should move from simple intent-filtered historical responses toward semantic retrieval using embeddings or TF-IDF/cosine similarity.

Goal:

```text
Customer message
      ↓
Intent filter
      ↓
Semantic similarity
      ↓
Top-k historical resolutions
      ↓
Grounded response
```

This would make "similar issue" retrieval closer to the assignment's intended behaviour.

## 2. Add Better Escalation Calibration

Evaluate the escalation model using a confusion matrix and tune the decision threshold based on the cost of false auto-handling versus unnecessary escalation.

## 3. Add Conversation-Level Context

The current prototype focuses on individual customer messages. A production-oriented version should use the surrounding conversation thread when available.

## 4. Expand Human Labels

Increase the golden set over time and periodically refresh it with new difficult cases discovered through production-like evaluation.

## 5. Improve Multi-intent Handling

Introduce multi-label classification or an explicit `multi_intent`/`ambiguous` state so the model does not have to force every message into one category.

## 6. Add Evidence-Constrained Generation

Require the response generator to distinguish between:

- facts explicitly present in the customer message,
- facts supported by retrieved historical responses,
- information that should not be invented.

## 7. Add Production Monitoring

Track:

- Intent distribution drift.
- Escalation rate.
- Confidence calibration.
- Response quality.
- API latency and cost.
- Human correction rates.

## 8. Security and Reliability Hardening

Before production use, add authentication/authorization, rate limiting, secret management, PII handling, structured logging, retries, timeouts, and explicit failure states.

---

# Conclusion

The project demonstrates an end-to-end AI support workflow rather than treating the LLM as an isolated text-generation component. It combines intent classification, historical-response grounding, escalation reasoning, persistence, a web interface, and an evaluation harness.

The most important engineering principle is that the agent should be evaluated against a human-labelled reference set and compared with simple baselines. The final numbers should be interpreted with their limitations, especially the effects of the intent taxonomy, curated evaluation distribution, LLM variability, and judge reliability.

The next iteration should focus primarily on **better retrieval, calibrated escalation, stronger human evaluation, and conversation context** rather than simply using a larger model.

---

## Final Submission Data Checklist

Before committing this report:

- [ ] Final golden set contains 150–250 actual hand-labelled examples.
- [ ] Golden-set metadata matches the actual number of examples.
- [ ] Final evaluation results have been run from the checked-in code.
- [ ] Baseline results are generated from the same golden set.
- [ ] LLM-as-judge results are generated from the final system.
- [ ] Human-vs-LLM judge agreement is measured and reported.
- [ ] Failure-analysis examples are actual failures from the final run.
- [ ] Per-intent metrics are included.
- [ ] No unsupported headline numbers remain.
- [ ] Gemini configuration does not expose an API key.
- [ ] README reproduces the final results in under 15 minutes.
