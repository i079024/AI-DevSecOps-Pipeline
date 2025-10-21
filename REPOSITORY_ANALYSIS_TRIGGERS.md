# 🔍 Repository Analysis Triggers & Solutions

## When "Repository Not Available for Detailed Analysis" Gets Triggered

The "repository not available for detailed analysis" message in the AI-Enhanced DevSecOps Pipeline occurs in several specific scenarios. Here's a comprehensive breakdown:

---

## 🚨 Trigger Scenarios

### 1. **No Git Repository** (MOST COMMON)
**Trigger**: When the MCP server is running in a directory without `.git`

**Error Location**: `speculator_bot/core/change_analyzer.py:59`
```python
try:
    self.repo = Repo(repo_path)
except Exception as e:
    logger.warning(f"Failed to initialize git repo: {e}")
```

**Log Message**: `"Failed to initialize git repo: <path>"`

**Status**: ✅ **FIXED** - Git repository initialized

---

### 2. **Empty Change Analysis**
**Trigger**: When git analysis returns no changed files

**Error Location**: `speculator_bot/core/change_analyzer.py:72`
```python
if not self.repo:
    logger.error("Git repository not initialized")
    return []
```

**Log Message**: `"Git repository not initialized"`

**Status**: ✅ **FIXED** - Repository now working

---

### 3. **Risk Analysis Failure**
**Trigger**: When ML models fail to analyze risk (missing dependencies)

**Error Location**: `mcp-http-wrapper.py:182-186`
```python
except (KeyError, AttributeError, Exception) as analysis_error:
    logger.warning(f"Analysis failed ({analysis_error}), returning mock data")
    report = None
```

**Current Error**: `'GradientBoostingClassifier' object has no attribute 'estimators_'`

**Status**: 🔄 **IN PROGRESS** - ML dependencies installed

---

### 4. **Empty Risk Summary**
**Trigger**: When risk analysis returns no results

**Error Location**: `speculator_bot/bot.py:104`
```python
logger.info(f"Risk analysis complete. Average risk: {risk_summary['average_risk']:.2f}")
# KeyError if risk_summary = {'error': 'No results to summarize'}
```

**Status**: 🔄 **RELATED** - Dependent on risk analysis fix

---

### 5. **Invalid Commit SHA**
**Trigger**: When the provided commit SHA doesn't exist in the repository

**Error Location**: Git operations fail when commit is not found

**Status**: ✅ **RESOLVED** - Using valid commit SHAs

---

### 6. **Corrupted Git Repository**
**Trigger**: When `.git` directory exists but is corrupted or incomplete

**Symptoms**: 
- Git operations fail intermittently
- Partial analysis results
- Inconsistent behavior

**Status**: ✅ **NOT APPLICABLE** - Fresh repository created

---

## 🛠️ Resolution Status

### ✅ **RESOLVED Issues**

1. **Git Repository Initialization**
   - **Action**: `git init` executed
   - **Result**: Repository properly initialized
   - **Verification**: `.git` directory created

2. **Initial Commit Created**
   - **Action**: `git add . && git commit -m "Initial commit"`
   - **Result**: 67 files committed
   - **Commit SHA**: `23694eb`

3. **Test Commit with Changes**
   - **Action**: Created `TEST_FILE.md` and committed
   - **Result**: Real file changes detected
   - **Commit SHA**: `f1a8581`
   - **Changes**: 1 file, 16 insertions

### 🔄 **IN PROGRESS Issues**

4. **Machine Learning Dependencies**
   - **Issue**: `GradientBoostingClassifier` attribute error
   - **Action**: Installed `scikit-learn`, `numpy`, `pandas`
   - **Status**: Server restarted with new dependencies
   - **Next**: Test analysis with ML libraries

---

## 📊 Current Analysis Results

### Before Git Repository:
```json
{
  "status": "success",
  "analysis": {
    "changeSummary": {
      "filesChanged": 0,
      "linesAdded": 0,
      "linesRemoved": 0
    }
  },
  "note": "Mock data returned - repository not available for analysis"
}
```

### After Git Repository + Valid Commit:
```
Log: "Analyzed 1 changed files" ✅
Log: "Performing risk analysis..." ✅
Error: ML model attribute error ❌
Result: Still returns mock data, but git analysis works
```

---

## 🎯 Expected Final Result

Once ML dependencies are fully resolved, the analysis should return:

```json
{
  "status": "success",
  "analysis": {
    "deploymentRiskScore": 0.25,
    "riskLevel": "LOW",
    "changeSummary": {
      "filesChanged": 1,
      "linesAdded": 16,
      "linesRemoved": 0,
      "criticalFiles": 0
    },
    "riskAnalysis": {
      "averageRisk": 0.25,
      "maxRisk": 0.30,
      "highRiskFiles": []
    },
    "testSelection": {
      "totalTests": 5,
      "estimatedTimeMinutes": 2.5,
      "coverageScore": 0.85
    }
  }
}
```

---

## 🔧 For Production Use

### To Avoid "Repository Not Available" Messages:

1. **Ensure Git Repository Exists**
   ```bash
   # In your project directory:
   git init
   git add .
   git commit -m "Initial commit"
   ```

2. **Use Valid Commit SHAs**
   ```bash
   # Get recent commits:
   git log --oneline -5
   
   # Use the full or short SHA in API calls:
   curl -X POST .../api/speculator/analyze \
     -d '{"commitSHA": "f1a8581", ...}'
   ```

3. **Install Required Dependencies**
   ```bash
   pip install scikit-learn numpy pandas GitPython
   ```

4. **Point to Correct Repository**
   - Ensure MCP server runs in git repository root
   - Or modify `repo_path` in server configuration

---

## 🧪 Testing Commands

### Verify Git Repository:
```bash
ls -la .git/          # Should exist
git status            # Should work
git log --oneline -1  # Should show commits
```

### Test Analysis:
```bash
# Get latest commit
COMMIT=$(git log --format="%H" -n 1)

# Test analysis
curl -X POST http://127.0.0.1:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d "{\"commitSHA\": \"$COMMIT\", \"branch\": \"main\"}" | jq .
```

---

**Status**: 🟡 **PARTIALLY RESOLVED** 
- ✅ Git repository issues fixed
- 🔄 ML model issues being resolved
- 🎯 Target: Full real-time analysis operational

*Last Updated: October 21, 2025*