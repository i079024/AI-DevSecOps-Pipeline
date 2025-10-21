# Speculator Bot - Comprehensive Usage Guide

## Table of Contents
1. [Installation](#installation)
2. [Configuration](#configuration)
3. [Command-Line Usage](#command-line-usage)
4. [Python API Usage](#python-api-usage)
5. [CI/CD Integration](#cicd-integration)
6. [Data Preparation](#data-preparation)
7. [Advanced Features](#advanced-features)
8. [Troubleshooting](#troubleshooting)

---

## Installation

### Prerequisites
- Python 3.8 or higher
- Git repository
- pip package manager

### Basic Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/speculator-bot.git
cd speculator-bot

# Install dependencies
pip install -r requirements.txt

# Install as a package
pip install -e .
```

### Verify Installation

```bash
speculator --version
```

---

## Configuration

### Initialize Configuration

```bash
speculator init
```

This creates:
- `config.yaml` - Main configuration file
- `models/` - Directory for ML models
- `logs/` - Directory for log files  
- `data/` - Directory for data files

### Configuration Options

Edit `config.yaml`:

```yaml
# Risk Analysis Settings
risk_analysis:
  enabled: true                      # Enable/disable risk analysis
  historical_window_days: 90         # Days of history to consider
  min_confidence_threshold: 0.6      # Minimum confidence for predictions
  risk_levels:
    high: 0.7                        # High risk threshold
    medium: 0.4                      # Medium risk threshold
    low: 0.2                         # Low risk threshold

# Test Selection Settings
test_selection:
  enabled: true                      # Enable/disable test selection
  max_tests: 50                      # Maximum tests to select
  prioritize_by_risk: true           # Prioritize tests by risk
  include_critical_tests: true       # Always include critical tests

# Database Validation Settings
database_validation:
  enabled: true                      # Enable/disable DB validation
  schema_analysis: true              # Analyze schema changes
  data_drift_detection: true         # Detect data drift
  drift_threshold: 0.15              # Drift detection threshold
  sample_size: 1000                  # Sample size for drift detection

# Feature Weights for Risk Calculation
feature_weights:
  code_complexity: 0.25              # Weight for code complexity
  historical_failures: 0.35          # Weight for historical failures
  change_magnitude: 0.20             # Weight for change size
  file_criticality: 0.20             # Weight for critical files
```

---

## Command-Line Usage

### Basic Commands

#### Initialize
```bash
speculator init
```

#### Analyze Current Changes
```bash
# Analyze staged changes
speculator analyze

# Verbose output
speculator analyze -v
```

#### Analyze Specific Commit
```bash
speculator analyze --commit abc123
```

#### With Custom Config
```bash
speculator analyze --config my_config.yaml
```

#### With Test Catalog
```bash
speculator analyze --test-catalog data/tests.json
```

#### With Historical Data
```bash
speculator analyze --historical-data data/failures.json
```

#### Export Reports
```bash
# JSON format
speculator analyze --output report.json --format json

# Text format
speculator analyze --output report.txt --format text

# HTML format
speculator analyze --output report.html --format html
```

#### Skip Database Analysis
```bash
speculator analyze --no-db
```

#### Skip Drift Detection
```bash
speculator analyze --no-drift
```

### Complete Example
```bash
speculator analyze \
  --repo /path/to/repo \
  --config config.yaml \
  --commit HEAD \
  --test-catalog data/tests.json \
  --historical-data data/failures.json \
  --output reports/analysis_$(date +%Y%m%d).html \
  --format html \
  --verbose
```

---

## Python API Usage

### Basic Usage

```python
from speculator_bot import SpeculatorBot

# Initialize bot
bot = SpeculatorBot(
    repo_path='.',
    config=None,  # Uses default or pass custom dict
    test_catalog_path='data/tests.json',
    historical_data_path='data/failures.json'
)

# Run analysis
report = bot.speculate()

# Access results
print(f"Risk Score: {report.deployment_risk_score}")
print(f"Recommendation: {report.overall_recommendation}")
```

### Custom Configuration

```python
custom_config = {
    'risk_analysis': {
        'risk_levels': {
            'high': 0.8,
            'medium': 0.5,
            'low': 0.3
        }
    },
    'test_selection': {
        'max_tests': 30
    }
}

bot = SpeculatorBot(
    repo_path='.',
    config=custom_config
)
```

### Analyze Specific Commit

```python
# Analyze specific commit
report = bot.speculate(commit_hash='abc123')

# Analyze with options
report = bot.speculate(
    commit_hash='HEAD~1',
    analyze_db=True,
    check_drift=False
)
```

### Export Reports

```python
# Export as JSON
bot.export_report(report, 'report.json', format='json')

# Export as HTML
bot.export_report(report, 'report.html', format='html')

# Export as text
bot.export_report(report, 'report.txt', format='text')
```

### Access Detailed Results

```python
# Change summary
print(f"Files changed: {report.change_summary['total_files_changed']}")
print(f"Lines added: {report.change_summary['total_lines_added']}")

# Risk analysis
print(f"Average risk: {report.risk_analysis['average_risk']}")
print(f"High risk files: {report.risk_analysis['high_risk_files']}")

# Test selection
print(f"Tests selected: {report.test_selection['total_tests_selected']}")
print(f"Coverage: {report.test_selection['coverage_score']}")

# Database analysis
if report.schema_analysis:
    print(f"Schema changes: {report.schema_analysis['total_changes']}")

# Data drift
if report.drift_analysis:
    print(f"Drift detected: {report.drift_analysis['drift_detected_columns']}")
```

---

## CI/CD Integration

### GitHub Actions

See `examples/github_actions_workflow.yml` for a complete example.

Key steps:
1. Checkout code with full history
2. Install Speculator Bot
3. Run analysis
4. Comment results on PR
5. Block deployment if risk too high

### GitLab CI

```yaml
speculator_analysis:
  stage: test
  script:
    - pip install -e .
    - speculator analyze --output report.json --format json
    - RISK_SCORE=$(jq -r '.deployment_risk_score' report.json)
    - |
      if (( $(echo "$RISK_SCORE >= 0.8" | bc -l) )); then
        echo "Risk too high!"
        exit 1
      fi
  artifacts:
    paths:
      - report.json
    when: always
```

### Jenkins

```groovy
pipeline {
    agent any
    
    stages {
        stage('Speculator Analysis') {
            steps {
                sh 'pip install -e .'
                sh 'speculator analyze --output report.json --format json'
                
                script {
                    def report = readJSON file: 'report.json'
                    def riskScore = report.deployment_risk_score
                    
                    if (riskScore >= 0.8) {
                        error("Risk score too high: ${riskScore}")
                    }
                }
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'report.json'
        }
    }
}
```

### Shell Script Integration

See `examples/ci_cd_integration.sh` for a complete bash script example.

---

## Data Preparation

### Test Catalog Format

Create `test_catalog.json`:

```json
[
  {
    "test_id": "test_001",
    "test_name": "User Authentication Test",
    "test_path": "tests/auth/test_login.py::test_valid_credentials",
    "test_type": "integration",
    "execution_time_seconds": 12.5,
    "covered_files": [
      "src/auth/login.py",
      "src/auth/session.py"
    ],
    "criticality": "critical",
    "last_failure_date": null,
    "failure_count": 0,
    "dependencies": []
  }
]
```

**Fields:**
- `test_id`: Unique identifier
- `test_name`: Human-readable name
- `test_path`: Path to test (pytest format)
- `test_type`: `unit`, `integration`, `e2e`, or `performance`
- `execution_time_seconds`: Average execution time
- `covered_files`: List of files this test covers
- `criticality`: `critical`, `high`, `medium`, or `low`
- `last_failure_date`: ISO date of last failure (optional)
- `failure_count`: Number of historical failures
- `dependencies`: List of test IDs this test depends on

### Historical Failure Data Format

Create `historical_failures.json`:

```json
[
  {
    "timestamp": "2024-10-01T10:30:00",
    "file_path": "src/payment/processor.py",
    "failure_type": "runtime_error",
    "severity": "high",
    "resolution_time_hours": 4.5,
    "related_commits": ["abc123"],
    "metadata": {
      "error_message": "Payment failed",
      "affected_users": 150,
      "environment": "production"
    }
  }
]
```

**Fields:**
- `timestamp`: ISO timestamp of failure
- `file_path`: File that caused failure
- `failure_type`: Type of failure
- `severity`: `critical`, `high`, `medium`, or `low`
- `resolution_time_hours`: Time to fix
- `related_commits`: List of related commit hashes
- `metadata`: Additional context (flexible)

### Generating Test Catalogs

From pytest coverage:
```python
import json
import pytest_cov

# Extract test information
tests = []
for test in collect_tests():
    tests.append({
        'test_id': test.nodeid,
        'test_name': test.name,
        'test_path': test.fspath,
        'covered_files': get_covered_files(test),
        # ... other fields
    })

with open('test_catalog.json', 'w') as f:
    json.dump(tests, f, indent=2)
```

---

## Advanced Features

### Training Custom Models

```python
import pandas as pd
from speculator_bot import RiskAnalyzer

# Prepare training data
# Features: lines_added, complexity, is_critical, etc.
X = pd.DataFrame([...])
y = [0, 1, 0, 1, ...]  # 0 = no failure, 1 = failure

analyzer = RiskAnalyzer(config=config)
analyzer.train_model(X, y)
```

### Database Baseline Capture

```python
from speculator_bot.core.db_validator import DatabaseValidator
import sqlalchemy

# Create DB connection
engine = sqlalchemy.create_engine('postgresql://...')

validator = DatabaseValidator(db_connection=engine)

# Capture baseline for tables
validator.capture_baseline_stats('users')
validator.capture_baseline_stats('payments')
```

### Data Drift Detection

```python
# After capturing baseline, detect drift
drift_reports = validator.detect_data_drift('users')

for report in drift_reports:
    if report.drift_detected:
        print(f"Drift in {report.column_name}: {report.recommendation}")
```

### Custom Risk Scoring

```python
from speculator_bot.core.risk_analyzer import RiskAnalyzer

# Custom feature weights
config = {
    'feature_weights': {
        'code_complexity': 0.1,
        'historical_failures': 0.6,  # Emphasize history
        'change_magnitude': 0.1,
        'file_criticality': 0.2
    }
}

analyzer = RiskAnalyzer(config=config)
```

---

## Troubleshooting

### Common Issues

#### "No git repository found"
- Ensure you're running in a git repository
- Use `--repo` to specify path

#### "Test catalog not found"
- Check file path is correct
- Use absolute paths if relative paths fail

#### "No changes detected"
- Ensure there are staged changes or specify `--commit`
- Check git status

#### "Module not found"
- Reinstall: `pip install -e .`
- Check Python version >= 3.8

### Debug Mode

```bash
# Enable verbose logging
speculator analyze --verbose

# Check logs
tail -f speculator_bot.log
```

### Python API Debugging

```python
import logging

# Enable debug logging
logging.basicConfig(level=logging.DEBUG)

# Run analysis
bot.speculate()
```

### Getting Help

```bash
# General help
speculator --help

# Command-specific help
speculator analyze --help
speculator train --help
```

---

## Best Practices

1. **Keep Historical Data Updated**: Regularly update failure data for better predictions
2. **Maintain Test Catalog**: Keep test metadata current
3. **Set Appropriate Thresholds**: Adjust risk thresholds for your team's risk tolerance
4. **Integrate Early**: Add to CI/CD pipeline early in development
5. **Review Reports**: Don't just rely on scores - read recommendations
6. **Capture Baselines Regularly**: Update data baselines weekly or after major changes
7. **Start Conservative**: Begin with stricter thresholds, relax as you gain confidence

---

## Support

For issues or questions:
- GitHub Issues: https://github.com/yourusername/speculator-bot/issues
- Documentation: https://github.com/yourusername/speculator-bot/wiki
- Email: speculator-bot@example.com

