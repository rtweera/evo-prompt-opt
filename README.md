# Evolutionary Prompt Optimization for Small Language Models

This project implements an evolutionary computing approach to optimize prompts for small language models (SLMs) using Ollama. It evolves multiple parameters including prompt templates, system prompts, temperature, and other SLM parameters to achieve optimal performance on specific tasks.

## Features

- **Comprehensive Parameter Optimization**: Evolves system prompts, prompt templates, temperature, max tokens, top-p, top-k, and repeat penalty
- **Ollama Integration**: Direct integration with Ollama for running small language models
- **Flexible Evaluation**: Supports accuracy, length, and content quality evaluation metrics
- **Custom Evolutionary Operators**: Domain-specific crossover and mutation operators
- **Task-Based Optimization**: Define tasks with test cases and evaluation criteria
- **Parallel Execution**: Optional parallel evaluation of test cases

## Prerequisites

1. **Java 17** or higher
2. **Ollama** installed and running
3. At least one language model installed in Ollama (e.g., `llama3.2:3b`)

### Installing Ollama and Models

1. Install Ollama from [https://ollama.ai](https://ollama.ai)
2. Start Ollama service
3. Install a model (e.g.):
   ```bash
   ollama pull llama3.2:3b
   ```

## Usage

### Basic Usage

Run with default settings (math problems, llama3.2:3b model):
```bash
./gradlew run
```

### Advanced Usage

```bash
./gradlew run --args="<model_name> <task_file> <generations> <population_size>"
```

**Parameters:**
- `model_name`: Ollama model to use (default: `llama3.2:3b`)
- `task_file`: Path to task definition JSON (default: `src/main/java/com/evopromptopt/tasks/sample_tasks.json`)
- `generations`: Number of evolution generations (default: 25)
- `population_size`: Population size (default: 30)

**Examples:**

Math problem solving:
```bash
./gradlew run --args="llama3.2:3b src/main/java/com/evopromptopt/tasks/sample_tasks.json 50 40"
```

Sentiment classification:
```bash
./gradlew run --args="qwen2.5:3b src/main/java/com/evopromptopt/tasks/classification_task.json 30 25"
```

Text summarization:
```bash
./gradlew run --args="mistral:7b src/main/java/com/evopromptopt/tasks/summarization_task.json 40 35"
```

## Task Definition

Tasks are defined in JSON format with the following structure:

```json
{
  "name": "Task Name",
  "description": "Task description",
  "testCases": [
    {
      "input": "Input text or question",
      "expectedOutput": "Expected response (if applicable)",
      "metadata": {
        "key": "value"
      }
    }
  ],
  "evaluation": {
    "type": "accuracy|length|content",
    "caseSensitive": false,
    "trimWhitespace": true,
    "minLength": 50,
    "maxLength": 200,
    "requiredKeywords": ["keyword1", "keyword2"],
    "bonusKeywords": ["bonus1", "bonus2"]
  },
  "configuration": {
    "maxTokens": 512,
    "timeoutMs": 30000
  }
}
```

### Evaluation Types

1. **Accuracy Evaluator**: Exact match evaluation for tasks with definitive answers
   - Perfect for math problems, classification tasks
   - Options: `caseSensitive`, `trimWhitespace`

2. **Length Evaluator**: Evaluates based on response length and basic quality
   - Good for summarization tasks
   - Options: `minLength`, `maxLength`

3. **Content Quality Evaluator**: Keyword-based content evaluation
   - Suitable for open-ended tasks
   - Options: `requiredKeywords`, `bonusKeywords`

## Sample Tasks

The project includes several sample tasks:

1. **Math Problems** (`sample_tasks.json`): Basic arithmetic with accuracy evaluation
2. **Sentiment Classification** (`classification_task.json`): Text classification with accuracy evaluation
3. **Text Summarization** (`summarization_task.json`): Summarization with length and content evaluation

## Evolutionary Parameters

The system optimizes the following parameters:

### Prompt Parameters
- **System Prompt**: Variations of system instructions
- **Prompt Template**: Different formatting approaches
- **Instruction Style**: DIRECT, CONVERSATIONAL, FORMAL, etc.
- **Tool Policy**: NONE, BASIC, ADVANCED
- **Response Format**: text, json, markdown

### Model Parameters
- **Temperature**: 0.1 to 1.5 (creativity vs consistency)
- **Max Tokens**: 64 to 2048 (response length limit)
- **Top P**: 0.1 to 1.0 (nucleus sampling)
- **Top K**: 1 to 100 (top-k sampling)
- **Repeat Penalty**: 0.5 to 2.0 (repetition control)

## Output

The system provides comprehensive results including:

1. **Evolution Statistics**: Best fitness, generation, total evaluations
2. **Best Configuration**: Optimized parameters and prompt templates
3. **Detailed Testing**: Performance on each test case
4. **Success Metrics**: Success rate, execution time, scores

## Architecture

The system is designed with modularity and extensibility in mind:

- **Core Components**: Evolution engine, genome representation, fitness evaluation
- **Execution Layer**: Ollama client, prompt executors
- **Evaluation Framework**: Pluggable metrics for different task types
- **Task Management**: JSON-based task definitions and loading
- **Evolution Operators**: Custom crossover and mutation for prompt optimization

## Extending the System

### Adding New Evaluation Metrics

1. Implement the `EvaluationMetric` interface
2. Register the new metric in `TaskLoader`
3. Use in task definition JSON

### Adding New Execution Backends

1. Implement the `PromptExecutor` interface
2. Create backend-specific request/response classes
3. Update the main runner to use the new executor

### Creating Custom Tasks

1. Create a JSON task definition file
2. Define test cases with inputs and expected outputs
3. Choose appropriate evaluation metrics
4. Run with the new task file

## Configuration

The system can be configured via `application.properties`:

```properties
# Ollama settings
ollama.base_url=http://localhost:11434
ollama.default_model=llama3.2:3b

# Evolution settings
evolution.population_size=30
evolution.max_generations=25
evolution.mutation_rate=0.15
evolution.crossover_rate=0.65

# Fitness weights
fitness.score_weight=0.6
fitness.success_rate_weight=0.3
fitness.execution_time_weight=0.1
```

## Troubleshooting

### Common Issues

1. **"Ollama is not available"**
   - Ensure Ollama is running: `ollama serve`
   - Check if the model is installed: `ollama list`
   - Verify the base URL is correct

2. **Out of memory errors**
   - Reduce population size
   - Use smaller models
   - Reduce max tokens in task configuration

3. **Slow evolution**
   - Reduce timeout values
   - Use smaller models
   - Enable parallel execution (with caution)
   - Reduce number of test cases

4. **Poor convergence**
   - Increase population size
   - Increase number of generations
   - Adjust mutation and crossover rates
   - Review task definition and evaluation metrics

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
