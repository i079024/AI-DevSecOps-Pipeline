# MCP Quick Start - 5 Minutes to AI-Powered Analysis

Get Speculator Bot working with Claude Desktop in 5 minutes!

## Step 1: Install MCP SDK (30 seconds)

```bash
pip install mcp
```

## Step 2: Test the Server (1 minute)

```bash
cd /Users/i079024/ariba/cursor/AIBot
python examples/test_mcp_server.py
```

You should see:
```
✓ MCP Server initialized successfully
Found 4 tools: analyze_risk, select_tests, check_data_drift, export_report
Found 2 resources: speculator://config, speculator://status
✅ All MCP Server tests completed successfully!
```

## Step 3: Configure Claude Desktop (2 minutes)

### macOS

```bash
# Copy configuration
cp claude_desktop_config.json ~/Library/Application\ Support/Claude/claude_desktop_config.json
```

### Manual Configuration

Open: `~/Library/Application Support/Claude/claude_desktop_config.json`

Add:
```json
{
  "mcpServers": {
    "speculator-bot": {
      "command": "python",
      "args": [
        "-m",
        "speculator_bot.mcp_server",
        "--repo",
        "/Users/i079024/ariba/cursor/AIBot",
        "--config",
        "/Users/i079024/ariba/cursor/AIBot/config.yaml"
      ]
    }
  }
}
```

## Step 4: Restart Claude Desktop

Quit and reopen Claude Desktop app.

## Step 5: Try It! (1 minute)

Open Claude and try these commands:

### Example 1: Risk Analysis
```
Analyze the deployment risk for my current changes
```

### Example 2: Test Selection
```
Which tests should I run based on my code changes?
```

### Example 3: Check Configuration
```
Show me the Speculator Bot configuration
```

### Example 4: Get Status
```
What is the status of Speculator Bot?
```

## What Just Happened?

When you ask Claude to analyze code:

1. **Claude** receives your request
2. **MCP Protocol** routes it to Speculator Bot
3. **Speculator Bot** analyzes your git changes
4. **Results** are sent back to Claude
5. **Claude** explains the results in natural language

```
┌──────────────┐
│    Claude    │  "Analyze my changes"
└──────┬───────┘
       │ MCP Protocol
┌──────▼────────────┐
│  Speculator Bot   │  Risk analysis, test selection
└──────┬────────────┘
       │
┌──────▼────────────┐
│  Your Git Repo    │  Analyzes commits, files, history
└───────────────────┘
```

## Available Commands to Try

### Risk Analysis
```
- "Analyze deployment risk for commit abc123"
- "What's the risk of deploying these changes?"
- "Should I be worried about my recent commits?"
```

### Test Selection
```
- "Which tests should I run?"
- "Give me the top 10 most important tests"
- "What tests cover the files I changed?"
```

### Configuration
```
- "Show Speculator Bot configuration"
- "What are the current risk thresholds?"
- "How is Speculator Bot configured?"
```

### Status & Help
```
- "What can Speculator Bot do?"
- "Is Speculator Bot working?"
- "Help me with Speculator Bot"
```

## Troubleshooting

### "I don't see any MCP tools"

1. Check Claude Desktop logs:
   ```bash
   tail -f ~/Library/Logs/Claude/mcp*.log
   ```

2. Verify server works:
   ```bash
   python -m speculator_bot.mcp_server --repo . --config config.yaml
   ```

3. Check config file location:
   ```bash
   cat ~/Library/Application\ Support/Claude/claude_desktop_config.json
   ```

### "Server connection failed"

1. Ensure Python path is correct
2. Check Speculator Bot is installed: `pip list | grep speculator`
3. Verify paths in config are absolute

### "Tool calls fail"

1. Ensure you have test catalog: `examples/test_catalog.json`
2. Check you're in a git repository
3. View logs: `tail -f speculator_bot.log`

## Next Steps

✅ **You're done!** Speculator Bot is now integrated with Claude.

Want more?
- Read [MCP_SETUP.md](MCP_SETUP.md) for advanced configuration
- See [USAGE_GUIDE.md](USAGE_GUIDE.md) for detailed features
- Check [examples/](examples/) for more usage patterns

## Pro Tips

1. **Context Matters**: Claude works better with specific requests
   - ❌ "Check my code"
   - ✅ "Analyze deployment risk for the payment processor changes"

2. **Combine Tools**: Ask Claude to use multiple tools
   - "Analyze risk and then select the most important tests"

3. **Export Reports**: Ask Claude to save analysis
   - "Analyze my changes and export to report.html"

4. **Historical Context**: Provide commit hashes
   - "Compare risk between commit abc123 and def456"

---

**Enjoy AI-powered predictive analysis! 🤖**

