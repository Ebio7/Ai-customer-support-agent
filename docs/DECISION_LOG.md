# Decision Log

This document records the main non-obvious engineering and product decisions made while building the Hiver AI Support Agent.

## 1. Selected a single brand from the Twitter customer-support dataset

- The assignment asks for an agent focused on one brand rather than a generic multi-brand support system.
- Focusing on one brand makes the historical-response grounding more consistent and keeps the intent taxonomy specific to that support context.

## 2. Defined a small intent taxonomy from the support data

- Instead of using a large generic intent taxonomy, the system uses a limited set of intents relevant to the selected brand.
- This reduces unnecessary classification ambiguity and makes the evaluation easier to interpret.

## 3. Used the Customer Support on Twitter dataset as the primary source

- The dataset contains real-world customer/brand interactions and therefore better represents noisy support language than clean synthetic examples.
- The system is designed around a subsample because the assignment explicitly allows and encourages subsampling.

## 4. Used a hand-labelled golden set for evaluation

- Model-generated labels are not treated as ground truth.
- A separate manually labelled evaluation set provides a fixed reference for measuring intent and escalation performance.

## 5. Targeted 150–250 examples for the final golden set

- This follows the assignment requirement while keeping manual labelling feasible.
- The final set should cover all intents and include difficult and ambiguous examples rather than only easy cases.

## 6. Included ambiguous and edge cases in evaluation

- Real support messages can be short, poorly written, emotional, multi-intent, or unclear.
- Including such cases makes the evaluation more representative of the conditions in which an automated support agent could fail.

## 7. Used historical support responses for reply grounding

- The reply generator receives relevant historical responses as references.
- This is intended to make responses more consistent with the brand's previous support style instead of relying entirely on generic LLM knowledge.

## 8. Kept retrieval constrained by intent

- Historical responses are first restricted to the relevant support intent.
- This reduces the chance that a response from an unrelated support category is used as grounding.

## 9. Used AUTO_HANDLE vs. ESCALATE as the operational decision

- The support workflow needs a clear action rather than only a probability or free-form recommendation.
- A binary decision makes the result directly usable by a support workflow while still allowing the system to provide a reason and confidence.

## 10. Escalated high-risk cases conservatively

- Threats, legal issues, severe harassment, complex technical problems, management requests, and safety concerns are treated as escalation candidates.
- The goal is to avoid automating cases where an incorrect response could have a higher cost.

## 11. Added a reason and confidence to escalation decisions

- A bare AUTO_HANDLE/ESCALATE label is difficult for a human reviewer to audit.
- Providing a reason makes the decision more interpretable, while confidence communicates uncertainty.

## 12. Compared the AI agent against simple baselines

- A strong-looking model score is less meaningful without a reference point.
- A trivial baseline and a simple heuristic/keyword baseline provide inexpensive comparisons and help establish whether the LLM-based approach adds value.

## 13. Used an LLM-as-judge for reply quality

- Reply quality is difficult to reduce to a single exact-match metric because multiple responses can be correct.
- An LLM judge provides a scalable way to evaluate helpfulness and appropriateness using a defined rubric.

## 14. Added a human agreement check for the LLM judge

- LLM-as-judge scores can themselves be unreliable.
- A manually rated subset is used to compare human and judge assessments, making the reported reply-quality metric more transparent.

## 15. Switched inference from OpenAI to Gemini

- The initial implementation used OpenAI's API, but the available account did not have usable API credits.
- Gemini was selected so the project could continue using an external LLM while keeping the application architecture largely unchanged.
- The API key is supplied through an environment variable rather than committed to the repository.

## Engineering principle

The overall design prioritizes a reproducible evaluation process over maximizing a single headline metric. Results should be reported from an actual evaluation run, and limitations or failure modes should be stated explicitly rather than hidden.
