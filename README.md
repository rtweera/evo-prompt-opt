# EvoPromptOpt

**Evolutionary Prompt Optimizer for Small Language Models**

EvoPromptOpt is a practical AI system that automatically discovers high-performing prompts for small language models using **evolutionary optimization**.

Instead of relying on manual prompt tuning or fixed heuristics, EvoPromptOpt treats prompt design as a **search problem**: prompts are iteratively evolved, evaluated, and refined based on task-level performance metrics. This approach is especially effective for small language models, where prompt quality has a disproportionate impact on results and where no single best prompt exists.

The project is designed as an engineering-first system: reproducible, measurable, and extensible.

---

## Problem Motivation

Prompt design for language models suffers from several fundamental issues:

* There is no exact or universal guideline for an optimal prompt
* Effective prompts vary significantly across models and tasks
* Small wording changes can produce non-linear behavior changes
* Manual prompt tuning does not scale and is hard to reproduce

These characteristics make prompt optimization poorly suited for rule-based or deterministic methods.

EvoPromptOpt addresses this by using **evolutionary algorithms**, which excel in search spaces that are:

* Discrete
* Non-convex
* Noisy
* Multi-solution

The goal is not to find *the* perfect prompt, but to reliably discover **one or more high-performing prompts** for a given task and model.

---

## Why Evolutionary Optimization

Evolutionary algorithms are a natural fit for prompt optimization because:

* Prompt quality cannot be expressed as a differentiable objective
* Multiple prompts may achieve similar performance
* Exploration is as important as exploitation
* Evaluation feedback is noisy and task-dependent

By evolving prompts over generations, EvoPromptOpt can efficiently explore the prompt space and converge on effective solutions without requiring gradients or handcrafted rules.

---

## Why Jenetics

Jenetics was chosen as the evolutionary engine because it:

* Is a mature, well-tested Java genetic algorithm library
* Provides strong type safety and custom genome design
* Supports deterministic and reproducible experiments
* Is suitable for production-grade JVM systems

Using Jenetics allows prompt evolution to be expressed explicitly and cleanly, rather than as ad-hoc random search.

---

## System Overview

At a high level, EvoPromptOpt works as follows:

1. A task specification defines the objective and evaluation criteria
2. An initial population of prompt candidates is generated
3. Each prompt is executed against the task
4. Task-level metrics are collected
5. Prompts are evolved using selection, crossover, and mutation
6. The best-performing prompt is returned along with its configuration

---

## Architecture

```text
Task Specification
        │
        ▼
Initial Prompt Population
        │
        ▼
Evolution Engine (Jenetics)
  ├─ Selection
  ├─ Crossover
  └─ Mutation
        │
        ▼
Prompt Execution Layer
        │
        ▼
Metric Evaluation
        │
        ▼
Best Prompt Candidate
```

---

## Prompt Representation

Prompts are represented as **structured configurations**, not raw text blobs. This improves stability and interpretability during evolution.

A prompt genome may include:

* System role definition
* Instruction style (concise, step-by-step, reflective)
* Output constraints
* Tool usage directives
* Decoding parameters (e.g., temperature)

This structure allows meaningful mutations while avoiding destructive changes.

---

## Fitness Evaluation

Each prompt candidate is evaluated using task-specific, measurable metrics.

A typical fitness function may combine:

```
fitness =
  α × task_success_score
- β × token_cost
- γ × verbosity_penalty
- δ × failure_penalty
```

This ensures optimization is objective, reproducible, and aligned with real-world constraints.

---

## Tech Stack

* Java 17+
* Jenetics (evolutionary algorithms)
* Small Language Models (local)
* Gradle
* SLF4J / Logback

---

## Repository Structure

```text
EvoPromptOpt/
├── core/
│   ├── genome/        # Prompt genome definitions
│   ├── evolution/     # Jenetics configuration
│   ├── fitness/       # Evaluation logic
│   └── execution/     # Prompt execution layer
├── app/
│   └── EvoPromptRunner.java
├── tasks/
│   └── sample_tasks.json
├── README.md
└── build.gradle
```

---

## Example Use Cases

* Optimizing prompts for classification tasks
* Discovering task-specific prompts for small language models
* Reducing token usage while maintaining accuracy
* Adapting prompts across different model architectures

---

## Project Goals

* Demonstrate evolutionary optimization applied to language models
* Provide a reproducible alternative to manual prompt tuning
* Emphasize engineering rigor over heuristic prompt design
* Serve as a foundation for future extensions (e.g., agent-based reasoning)

---

## Status

This project is under active development. The current focus is on establishing a robust evolutionary core and reliable evaluation methodology.
