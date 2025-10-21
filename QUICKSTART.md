# 🚀 Quick Start Guide - Speculator Bot

Get up and running with Speculator Bot in 5 minutes!

## Installation

```bash
# Navigate to project directory
cd /Users/i079024/ariba/cursor/AIBot

# Install dependencies
pip install -r requirements.txt

# Install Speculator Bot
pip install -e .
```

## Initialize

```bash
# Create configuration files and directories
speculator init
```

## Run Your First Analysis

```bash
# Analyze current staged changes
speculator analyze \
  --test-catalog examples/test_catalog.json \
  --historical-data examples/historical_failures.json \
  --output my_first_report.html \
  --format html
```

## View Results

Open `my_first_report.html` in your browser to see:
- 📊 Change summary
- ⚠️ Risk analysis
- 🧪 Selected tests
- 🎯 Deployment recommendation

## Next Steps

### 1. Customize Configuration

Edit `config.yaml`:

```yaml
risk_analysis:
  risk_levels:
    high: 0.7      # Adjust thresholds for your team
    medium: 0.4
    low: 0.2

test_selection:
  max_tests: 50    # Adjust based on CI/CD time budget
```

### 2. Create Your Test Catalog

```bash
# Copy example and modify
cp examples/test_catalog.json data/my_tests.json
# Edit data/my_tests.json with your actual tests
```

### 3. Gather Historical Failure Data

```bash
# Copy example and add your failure data
cp examples/historical_failures.json data/my_failures.json
# Edit data/my_failures.json with your historical failures
```

### 4. Run Analysis with Your Data

```bash
speculator analyze \
  --test-catalog data/my_tests.json \
  --historical-data data/my_failures.json \
  --output reports/analysis.html \
  --format html
```

### 5. Integrate with CI/CD

**GitHub Actions:**
```bash
cp examples/github_actions_workflow.yml .github/workflows/speculator.yml
```

**Shell Script:**
```bash
bash examples/ci_cd_integration.sh
```

## Common Use Cases

### Pre-Commit Check
```bash
# Before committing
speculator analyze --output pre-commit-check.txt --format text
```

### Pull Request Analysis
```bash
# Analyze specific commit
speculator analyze --commit <commit-hash> --output pr-analysis.html --format html
```

### Database Migration Check
```bash
# Focus on database changes
speculator analyze --no-drift --output migration-check.json --format json
```

## Python API Quick Example

```python
from speculator_bot import SpeculatorBot

# Initialize
bot = SpeculatorBot(
    repo_path='.',
    test_catalog_path='examples/test_catalog.json',
    historical_data_path='examples/historical_failures.json'
)

# Analyze
report = bot.speculate()

# Check risk
if report.deployment_risk_score > 0.7:
    print("⛔ High risk! Review required.")
else:
    print("✅ Safe to deploy")

# Export
bot.export_report(report, 'report.html', format='html')
```

## Understanding the Output

### Deployment Risk Score (0-1 scale)
- **0.0 - 0.2**: ✅ Minimal risk - safe to deploy
- **0.2 - 0.4**: ✓ Low risk - standard process
- **0.4 - 0.6**: ⚠️ Moderate risk - extra testing
- **0.6 - 0.8**: ⚠️ High risk - thorough review
- **0.8 - 1.0**: ⛔ Critical risk - do not deploy

### Risk Factors
- **Code Complexity**: High complexity = higher risk
- **Historical Failures**: Past failures in modified files
- **Change Magnitude**: Large changes = more risk
- **File Criticality**: Critical files (auth, payment, etc.)

### Test Selection
- Automatically selects tests based on:
  - Risk level of changed files
  - Test criticality
  - Historical failure rates
  - Code coverage

## Troubleshooting

**"No changes detected"**
```bash
# Ensure you have staged changes
git add .
# Or analyze a specific commit
speculator analyze --commit HEAD
```

**"Test catalog not found"**
```bash
# Use example data first
speculator analyze --test-catalog examples/test_catalog.json
```

**"Import errors"**
```bash
# Reinstall
pip install -e .
```

## Getting Help

```bash
# Command help
speculator --help
speculator analyze --help

# View logs
tail -f speculator_bot.log

# Check version
speculator --version
```

## What's Next?

1. ✅ Read [README.md](README.md) for full feature list
2. ✅ Check [USAGE_GUIDE.md](USAGE_GUIDE.md) for detailed documentation
3. ✅ Explore [examples/](examples/) for integration patterns
4. ✅ Customize configuration for your needs
5. ✅ Integrate with your CI/CD pipeline

## Support

- **Issues**: Open a GitHub issue
- **Questions**: Check USAGE_GUIDE.md
- **Examples**: See examples/ directory

---

**Happy Speculating! 🤖**

