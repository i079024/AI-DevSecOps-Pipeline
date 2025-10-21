# 🚀 Getting Started with Speculator Bot

Welcome! This guide helps you choose the right way to use Speculator Bot.

## 📚 Quick Navigation

### New to Speculator Bot?
→ Start with **[QUICKSTART.md](QUICKSTART.md)** (5 minutes)

### Want AI Integration (Claude Desktop)?
→ Follow **[MCP_QUICKSTART.md](MCP_QUICKSTART.md)** (5 minutes)

### Need Detailed Documentation?
→ Read **[USAGE_GUIDE.md](USAGE_GUIDE.md)** (comprehensive)

### Setting up MCP?
→ See **[MCP_SETUP.md](MCP_SETUP.md)** (detailed MCP guide)

### Having Issues?
→ Run `./examples/troubleshoot_mcp.sh`

---

## 🎯 Choose Your Path

### Path 1: Command Line Usage (Fastest)

**Best for**: Quick analysis, CI/CD pipelines, scripting

```bash
# 1. Install
pip install -r requirements.txt
pip install -e .

# 2. Initialize
speculator init

# 3. Analyze
speculator analyze \
  --test-catalog examples/test_catalog.json \
  --historical-data examples/historical_failures.json \
  --output report.html \
  --format html
```

**Next**: See [QUICKSTART.md](QUICKSTART.md)

---

### Path 2: Python API (Most Flexible)

**Best for**: Integration with existing tools, custom workflows

```python
from speculator_bot import SpeculatorBot

bot = SpeculatorBot(
    repo_path='.',
    test_catalog_path='examples/test_catalog.json',
    historical_data_path='examples/historical_failures.json'
)

report = bot.speculate()
print(f"Risk: {report.deployment_risk_score}")
```

**Next**: See [examples/example_usage.py](examples/example_usage.py)

---

### Path 3: AI Assistant Integration (Most Powerful)

**Best for**: Natural language analysis, team collaboration

**What you can do:**
- "Analyze deployment risk for my changes"
- "Which tests should I run?"
- "Is this commit safe to deploy?"
- "Compare risk between commits"

**Setup**: 5 minutes
1. `pip install mcp`
2. `python3 examples/test_mcp_server.py`
3. Configure Claude Desktop
4. Restart Claude

**Next**: See [MCP_QUICKSTART.md](MCP_QUICKSTART.md)

---

## 📦 What's Included

### Core Tools
- **speculator_bot/** - Main package
- **config.yaml** - Configuration
- **requirements.txt** - Dependencies

### Documentation
- **README.md** - Full feature overview
- **QUICKSTART.md** - CLI quick start
- **MCP_QUICKSTART.md** - AI integration quick start
- **MCP_SETUP.md** - Detailed MCP setup guide
- **USAGE_GUIDE.md** - Comprehensive usage documentation
- **MCP_SETUP_COMPLETE.md** - Setup completion summary

### Examples
- **examples/test_catalog.json** - Sample test catalog
- **examples/historical_failures.json** - Sample failure data
- **examples/example_usage.py** - Python API examples
- **examples/quick_analysis.py** - Quick analysis script
- **examples/test_mcp_server.py** - MCP server testing
- **examples/workflow_examples.md** - Real-world workflows
- **examples/ci_cd_integration.sh** - CI/CD integration
- **examples/troubleshoot_mcp.sh** - Troubleshooting tool
- **examples/github_actions_workflow.yml** - GitHub Actions example

### Configuration
- **claude_desktop_config.json** - Claude Desktop MCP config
- **mcp_config.json** - Generic MCP configuration

---

## 🎓 Learning Resources

### 5-Minute Quick Starts
1. **CLI**: [QUICKSTART.md](QUICKSTART.md)
2. **MCP**: [MCP_QUICKSTART.md](MCP_QUICKSTART.md)

### In-Depth Guides
1. **Full Usage**: [USAGE_GUIDE.md](USAGE_GUIDE.md)
2. **MCP Setup**: [MCP_SETUP.md](MCP_SETUP.md)
3. **Workflows**: [examples/workflow_examples.md](examples/workflow_examples.md)

### Examples
1. **Python API**: [examples/example_usage.py](examples/example_usage.py)
2. **Quick Analysis**: [examples/quick_analysis.py](examples/quick_analysis.py)
3. **CI/CD**: [examples/ci_cd_integration.sh](examples/ci_cd_integration.sh)

---

## ⚡ Quick Commands

### Test Without Setup
```bash
python3 examples/quick_analysis.py
```

### Test MCP Server
```bash
python3 examples/test_mcp_server.py
```

### Troubleshoot Issues
```bash
./examples/troubleshoot_mcp.sh
```

### Run Analysis
```bash
speculator analyze --help
```

---

## 🎯 Common Use Cases

### Before Committing Code
```bash
speculator analyze --output pre-commit.txt --format text
```

### In CI/CD Pipeline
```bash
speculator analyze --output report.json --format json
# Parse results and make deployment decision
```

### With Claude Desktop
```
Ask Claude: "Analyze deployment risk for my changes"
```

### For Pull Request Review
```bash
speculator analyze --commit <pr-commit-hash> --output pr-review.html
```

---

## 🛠️ Setup Status

After running the MCP setup, you should have:

✅ MCP SDK installed  
✅ All dependencies installed  
✅ MCP server tested and working  
✅ Claude Desktop configured  
⏳ **Restart Claude Desktop to activate**  

To verify setup:
```bash
./examples/troubleshoot_mcp.sh
```

---

## 🤔 Which Path Should I Choose?

| Use Case | Recommended Path |
|----------|-----------------|
| Quick one-time analysis | Path 1: CLI |
| Integrate with Python app | Path 2: Python API |
| Natural language queries | Path 3: MCP/Claude |
| CI/CD automation | Path 1: CLI |
| Team collaboration | Path 3: MCP/Claude |
| Learning/Exploration | Path 3: MCP/Claude |
| Production monitoring | Path 2: Python API |

---

## 📞 Need Help?

### Documentation
- Browse the docs in this directory
- Check [examples/](examples/) for working code

### Troubleshooting
```bash
./examples/troubleshoot_mcp.sh
```

### Common Issues

**"Module not found"**
```bash
pip3 install -r requirements.txt
pip3 install -e .
```

**"MCP server not connecting"**
```bash
python3 examples/test_mcp_server.py
```

**"No git repository"**
- Speculator Bot works best in a git repo
- Some features require git history

---

## 🎉 You're Ready!

Choose your path above and get started. Happy analyzing! 🤖

---

**Quick Links:**
- [5-Min CLI Start](QUICKSTART.md)
- [5-Min MCP Start](MCP_QUICKSTART.md)
- [Full Documentation](USAGE_GUIDE.md)
- [Real Workflows](examples/workflow_examples.md)

