# Model Context Protocol (MCP) Server Setup

This guide explains how to set up and use the Speculator Bot MCP Server with AI assistants like Claude Desktop.

## What is MCP?

**Model Context Protocol (MCP)** is an open protocol that standardizes how AI applications connect to external data sources and tools. It provides:

- **Tools**: Functions the AI can execute
- **Resources**: Data the AI can read
- **Prompts**: Reusable templates for common tasks

## Architecture

```
┌─────────────────────────────────────────────────┐
│            AI Assistant (Claude)                │
│                                                 │
│  "Analyze deployment risk for my changes"      │
└────────────────┬────────────────────────────────┘
                 │ MCP Protocol
                 │
┌────────────────▼────────────────────────────────┐
│         Speculator Bot MCP Server               │
│                                                 │
│  Tools:                                         │
│  • analyze_risk                                 │
│  • select_tests                                 │
│  • check_data_drift                            │
│  • export_report                               │
│                                                 │
│  Resources:                                     │
│  • speculator://config                         │
│  • speculator://status                         │
│                                                 │
│  Prompts:                                       │
│  • analyze_deployment                          │
│  • review_risk                                 │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│         Speculator Bot Core                     │
│                                                 │
│  • Risk Analyzer                               │
│  • Test Selector                               │
│  • Database Validator                          │
│  • Change Analyzer                             │
└─────────────────────────────────────────────────┘
```

## Installation

### 1. Install MCP SDK

```bash
pip install mcp
```

### 2. Update Requirements

Add to `requirements.txt`:
```
mcp>=0.1.0
```

### 3. Install Speculator Bot

```bash
pip install -e .
```

## Configuration

### For Claude Desktop

1. **Locate Claude Desktop Config**:
   - macOS: `~/Library/Application Support/Claude/claude_desktop_config.json`
   - Windows: `%APPDATA%\Claude\claude_desktop_config.json`

2. **Add Speculator Bot MCP Server**:

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

3. **Restart Claude Desktop**

### For Other AI Applications

Use the configuration from `mcp_config.json` and adapt to your application's MCP configuration format.

## Available Tools

### 1. analyze_risk

Analyzes code changes and predicts deployment risk.

**Parameters:**
- `commit_hash` (optional): Specific commit to analyze
- `test_catalog` (optional): Path to test catalog JSON
- `historical_data` (optional): Path to historical failure data
- `analyze_db` (default: true): Whether to analyze database changes

**Example Usage in Claude:**
```
Analyze the deployment risk for commit abc123 using test catalog at examples/test_catalog.json
```

**Returns:**
```json
{
  "deployment_risk_score": 0.45,
  "risk_level": "MEDIUM",
  "recommendation": "✓ PROCEED WITH CARE - Moderate risk detected.",
  "change_summary": { ... },
  "risk_analysis": { ... },
  "test_selection": { ... }
}
```

### 2. select_tests

Intelligently selects tests based on code changes.

**Parameters:**
- `test_catalog` (required): Path to test catalog
- `commit_hash` (optional): Commit to analyze
- `max_tests` (default: 50): Maximum tests to select

**Example Usage:**
```
Select the most important tests to run based on my changes
```

**Returns:**
```json
{
  "selected_tests": [
    {
      "test_id": "test_001",
      "test_name": "User Authentication Test",
      "test_path": "tests/auth/test_login.py",
      "criticality": "critical",
      "execution_time_seconds": 12.5
    }
  ],
  "total_tests": 15,
  "estimated_time_minutes": 8.5,
  "coverage_score": 0.85
}
```

### 3. check_data_drift

Detects data drift in database tables.

**Parameters:**
- `table_name` (required): Database table to check
- `columns` (optional): Specific columns to check

**Example Usage:**
```
Check for data drift in the users table
```

### 4. export_report

Exports analysis report to file.

**Parameters:**
- `output_path` (required): Where to save the report
- `format` (default: "html"): Format (json, html, text)

**Example Usage:**
```
Export the analysis report to reports/deployment_analysis.html
```

## Available Resources

### speculator://config

Access current Speculator Bot configuration.

**Example:**
```
Show me the current Speculator Bot configuration
```

### speculator://status

Get bot status and capabilities.

**Example:**
```
What is the status of Speculator Bot?
```

## Available Prompts

### analyze_deployment

Template for comprehensive deployment analysis.

**Arguments:**
- `commit` (optional): Commit to analyze

**Usage:**
```
Use the analyze_deployment prompt
```

### review_risk

Template for reviewing risk factors.

**Arguments:**
- `file_path` (optional): Specific file to review

**Usage:**
```
Use the review_risk prompt for src/payment/processor.py
```

## Testing the MCP Server

### Manual Testing

```bash
# Run the MCP server directly
python -m speculator_bot.mcp_server --repo . --config config.yaml

# The server communicates via stdio (standard input/output)
# Type MCP protocol messages or use an MCP client for testing
```

### With MCP Inspector

```bash
# Install MCP Inspector
npm install -g @modelcontextprotocol/inspector

# Run inspector with Speculator Bot
mcp-inspector python -m speculator_bot.mcp_server --repo . --config config.yaml
```

## Example Conversations with Claude

### Example 1: Pre-Deployment Check

**You:** "I'm about to deploy changes to the payment processor. Can you analyze the risk?"

**Claude (using MCP):**
```
I'll analyze the deployment risk for your changes.

[Uses analyze_risk tool]

Analysis Results:
- Deployment Risk Score: 0.68 (HIGH)
- Risk Level: HIGH
- Files Changed: 5
- High Risk Files:
  • src/payment/processor.py (risk: 0.82)
  • src/payment/gateway.py (risk: 0.65)

Recommendations:
⚠️ HIGH CODE RISK
⚠️ This file has a history of failures. Extra scrutiny recommended.
⚠️ Large change detected. Consider breaking into smaller commits.

I recommend:
1. Running comprehensive testing
2. Deploying to staging first
3. Having rollback plan ready
4. Monitoring closely post-deployment
```

### Example 2: Test Selection

**You:** "Which tests should I run for these changes?"

**Claude (using MCP):**
```
[Uses select_tests tool]

I've selected 18 critical tests to run:

High Priority Tests (8):
1. Payment Processing - Credit Card (25.7s)
2. Security - SQL Injection Prevention (15.8s)
3. User Authentication - Login (12.5s)
...

Total Estimated Time: 6.5 minutes
Coverage Score: 92%

These tests cover all your changed files and historically problematic areas.
```

### Example 3: Configuration Review

**You:** "What are the current risk thresholds?"

**Claude (using MCP):**
```
[Reads speculator://config resource]

Current Risk Thresholds:
- High: 0.7
- Medium: 0.4
- Low: 0.2

Feature Weights:
- Code Complexity: 25%
- Historical Failures: 35%
- Change Magnitude: 20%
- File Criticality: 20%

Would you like me to explain or adjust any of these settings?
```

## Troubleshooting

### Server Not Starting

```bash
# Check if MCP SDK is installed
pip list | grep mcp

# Install if missing
pip install mcp

# Test server directly
python -m speculator_bot.mcp_server --repo . --config config.yaml
```

### Claude Desktop Not Finding Server

1. Check config file location
2. Verify Python path in configuration
3. Check Claude Desktop logs:
   - macOS: `~/Library/Logs/Claude/`
   - Windows: `%APPDATA%\Claude\logs\`

### Tool Calls Failing

1. Ensure Speculator Bot is properly installed: `pip install -e .`
2. Verify test catalog and historical data paths exist
3. Check logs in `speculator_bot.log`

## Advanced Usage

### Custom MCP Server Configuration

Create a custom configuration file:

```json
{
  "mcpServers": {
    "speculator-bot-staging": {
      "command": "python",
      "args": [
        "-m",
        "speculator_bot.mcp_server",
        "--repo",
        "/path/to/staging/repo",
        "--config",
        "/path/to/staging/config.yaml"
      ],
      "env": {
        "LOG_LEVEL": "DEBUG"
      }
    }
  }
}
```

### Extending the MCP Server

Add custom tools by editing `speculator_bot/mcp_server.py`:

```python
@self.server.list_tools()
async def list_tools() -> list[Tool]:
    return [
        # ... existing tools ...
        Tool(
            name="custom_tool",
            description="Your custom tool",
            inputSchema={ ... }
        )
    ]
```

## Security Considerations

1. **Access Control**: MCP server runs with your user permissions
2. **Data Privacy**: Sensitive data may be sent to AI assistant
3. **Code Execution**: AI can trigger tool execution
4. **Rate Limiting**: Consider implementing rate limits for production

## Best Practices

1. **Version Control**: Keep MCP config in version control
2. **Documentation**: Document custom tools and their usage
3. **Testing**: Test MCP tools before production use
4. **Monitoring**: Log MCP tool usage for audit trails
5. **Updates**: Keep MCP SDK updated for security patches

## Resources

- **MCP Specification**: https://spec.modelcontextprotocol.io/
- **MCP SDK Docs**: https://github.com/modelcontextprotocol
- **Speculator Bot Docs**: See README.md and USAGE_GUIDE.md

## Support

For issues with:
- **MCP Protocol**: Refer to MCP specification
- **Speculator Bot**: See main project documentation
- **Integration**: Open an issue on GitHub

---

**Ready to use Speculator Bot with AI assistants via MCP! 🤖**

