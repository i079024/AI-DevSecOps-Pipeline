# AI Coding Agent Instructions for AI-Enhanced DevSecOps Pipeline

## 🎯 Project Overview

This is an **AI-Enhanced DevSecOps Pipeline** that automates code quality analysis, security scanning, unit test generation, and pull request creation using AI/ML models. The system integrates multiple technologies to provide intelligent code review and improvement suggestions.

## 🏗️ Architecture Components

### Core Technologies
- **Jenkins**: CI/CD pipeline orchestration and build automation
- **n8n**: Workflow automation and API orchestration 
- **GitHub**: Version control and PR management via REST API
- **Speculator Bot**: Python-based risk analysis and predictive testing
- **Model Context Protocol (MCP)**: AI assistant integration layer
- **SAP AI Core**: LLM hosting for Claude and other models
- **SonarQube**: Static code analysis and quality gates

### Data Flow
```
GitHub Push → Jenkins Pipeline → n8n Workflow → AI Analysis → GitHub PR Creation
```

## 📁 Project Structure

```
├── .github/                    # GitHub configuration
├── speculator_bot/             # Python risk analysis engine
├── examples/                   # Example configurations and scripts
├── tests/                      # Unit and integration tests
├── logs/                       # Runtime logs and debug output
├── Jenkinsfile                 # Jenkins pipeline definition
├── testflow_3.json            # n8n workflow configuration
├── mcp-http-wrapper.py        # MCP server HTTP wrapper
├── ai_core_config.yaml        # SAP AI Core configuration
└── setup.py                   # Python package setup
```

## 🔧 Key Configuration Files

### Jenkins Pipeline (`Jenkinsfile`)
- **Language**: Groovy (Declarative Pipeline)
- **Purpose**: Main CI/CD orchestration with 6 stages
- **Key Features**: Multi-language build support, SonarQube integration, n8n webhook triggering
- **Modification Guidelines**: 
  - Always test changes in a feature branch first
  - Maintain backward compatibility with existing webhook payload format
  - Preserve error handling and notification mechanisms

### n8n Workflow (`testflow_3.json`)
- **Language**: JSON configuration with embedded JavaScript
- **Purpose**: Automated workflow for AI analysis and PR creation
- **Node Types**: Webhooks, HTTP requests, JavaScript code execution, GitHub API calls
- **Critical Sections**:
  - JavaScript sandbox code in "Prepare Test File Commits" node
  - GitHub API authentication and parameter passing
  - Error handling for API rate limits and failures

### MCP HTTP Wrapper (`mcp-http-wrapper.py`)
- **Language**: Python 3.8+ with Flask
- **Purpose**: Exposes Speculator Bot functionality via REST API
- **Key Endpoints**: `/api/speculator/analyze`, `/api/mcp/analyze`, `/api/generate/unit-tests`
- **Dependencies**: Flask, requests, json, logging

## 🛡️ Security & Authentication

### GitHub API Integration
- **Authentication**: Personal Access Token (PAT) via n8n credentials
- **Required Scopes**: `repo`, `workflow`, `write:packages`, `admin:repo_hook`
- **Rate Limiting**: Implemented with retry logic and exponential backoff
- **Error Handling**: Comprehensive HTTP status code handling

### SAP AI Core Integration
- **Authentication**: OAuth 2.0 with client credentials flow
- **Configuration**: `ai_core_config.yaml` with encrypted credentials
- **Model Access**: Claude 3.5 Sonnet via deployment endpoints
- **Cost Management**: Request throttling and usage monitoring

## 🎯 Development Guidelines

### Code Style & Standards
- **Python**: Follow PEP 8, use type hints, comprehensive docstrings
- **JavaScript (n8n)**: ES6+ syntax, explicit return statements, error handling
- **Groovy (Jenkins)**: Declarative pipeline syntax, proper stage organization
- **JSON**: Properly formatted, validated schema compliance

### Testing Requirements
- **Unit Tests**: Required for all Python modules in `speculator_bot/`
- **Integration Tests**: Full workflow testing via `comprehensive_webhook_test.sh`
- **API Testing**: Validate all MCP wrapper endpoints
- **Pipeline Testing**: Jenkins pipeline validation in test environment

### Error Handling Patterns
```python
# Python MCP Wrapper Pattern
try:
    result = risky_operation()
    logger.info(f"Operation successful: {result}")
    return {"success": True, "data": result}
except SpecificException as e:
    logger.error(f"Known error occurred: {e}")
    return {"success": False, "error": str(e)}
except Exception as e:
    logger.exception("Unexpected error occurred")
    return {"success": False, "error": "Internal server error"}
```

```javascript
// n8n JavaScript Node Pattern
try {
  const result = processData($input.all());
  console.log(`[DEBUG] Processing completed: ${result.length} items`);
  return result.map(item => ({ json: item }));
} catch (error) {
  console.error(`[ERROR] Processing failed: ${error.message}`);
  throw new Error(`Processing failed: ${error.message}`);
}
```

### API Integration Guidelines
- **GitHub API**: Use v3 REST API, handle rate limits, validate payloads
- **MCP Protocol**: Follow standard request/response patterns
- **n8n Webhooks**: Preserve payload structure, maintain backward compatibility
- **SAP AI Core**: Implement proper token refresh, handle model limitations

## 🚀 Common Development Tasks

### Adding New AI Analysis Features
1. **Define Analysis Logic**: Implement in `speculator_bot/analyzers/`
2. **Add MCP Endpoint**: Extend `mcp-http-wrapper.py` with new route
3. **Update n8n Workflow**: Add new HTTP request node or modify existing
4. **Test Integration**: Validate end-to-end via webhook trigger
5. **Update Documentation**: Add to relevant `.md` files

### Modifying GitHub Integration
1. **Test API Changes**: Use GitHub API documentation and Postman
2. **Update n8n Nodes**: Modify HTTP request parameters carefully
3. **Validate Credentials**: Ensure PAT has required scopes
4. **Test Error Scenarios**: Handle 422, 403, 404 responses appropriately
5. **Monitor Rate Limits**: Implement backoff strategies

### Debugging Workflow Issues
1. **Check Jenkins Logs**: Monitor build console output
2. **Examine n8n Executions**: Use built-in execution viewer
3. **Review MCP Wrapper Logs**: Check `mcp-wrapper.log` for API errors
4. **Validate Webhooks**: Test endpoint connectivity and payload format
5. **Verify Credentials**: Ensure all authentication tokens are valid

## 📊 Monitoring & Observability

### Log Locations
- **Jenkins**: Build console output and system logs
- **n8n**: Execution history with node-level details
- **MCP Wrapper**: `mcp-wrapper.log` with structured logging
- **Speculator Bot**: `logs/` directory with rotating files

### Key Metrics to Monitor
- **Pipeline Success Rate**: Jenkins build success percentage
- **AI Analysis Response Time**: MCP wrapper endpoint latency  
- **GitHub API Usage**: Rate limit consumption and errors
- **PR Creation Success**: End-to-end workflow completion rate

### Health Check Endpoints
- **Jenkins**: `http://localhost:8080/login`
- **n8n**: `http://localhost:5678/healthz`
- **MCP Wrapper**: `http://localhost:3001/health`
- **Speculator Bot**: Programmatic health check via MCP

## 🔄 Deployment & Maintenance

### Local Development Setup
```bash
# Start all services
./start-devsecops-pipeline.sh

# Validate setup  
./comprehensive_webhook_test.sh

# Stop services
./stop-devsecops-pipeline.sh
```

### Production Considerations
- **Service Discovery**: Use proper DNS/service mesh
- **Secret Management**: External secret stores for credentials
- **High Availability**: Load balancing and redundancy
- **Backup Strategy**: Configuration and workflow backup procedures

## ⚠️ Critical Safety Guidelines

### Data Handling
- **Never log sensitive data**: Credentials, tokens, personal information
- **Sanitize inputs**: Validate all external API inputs
- **Rate limit protection**: Implement backoff for all external services
- **Error message security**: Don't expose internal system details

### Code Modification Safety
- **Test in isolation**: Always test changes in non-production environment
- **Preserve backward compatibility**: Don't break existing integrations
- **Document breaking changes**: Update all relevant documentation
- **Rollback procedures**: Maintain ability to revert changes quickly

### GitHub Security
- **Token scope minimization**: Use least privilege principle
- **Repository access**: Validate permissions before operations
- **Branch protection**: Respect repository branch protection rules
- **Audit logging**: Maintain comprehensive action logs

## 🎯 AI Agent Optimization

### Prompt Engineering
- **Context-aware analysis**: Use repository structure and commit history
- **Focused recommendations**: Target specific code quality issues
- **Actionable suggestions**: Provide concrete implementation steps
- **Risk-based prioritization**: Highlight critical security and performance issues

### Model Selection Guidelines
- **Code Analysis**: Use Claude 3.5 Sonnet for complex reasoning
- **Unit Test Generation**: Leverage coding-specialized models
- **Security Analysis**: Employ security-focused model variants
- **Performance Optimization**: Use models trained on performance patterns

### Quality Assurance
- **Validation Logic**: Implement checks for AI-generated code quality
- **Human Review Integration**: Preserve human oversight in critical decisions
- **Feedback Loops**: Capture and learn from human corrections
- **Continuous Improvement**: Monitor and optimize AI accuracy over time

---

## 📚 Additional Resources

- **Setup Guide**: `AI_DEVSECOPS_SETUP.md`
- **Quick Reference**: `QUICK_REFERENCE.md`
- **Architecture Details**: `ARCHITECTURE_DIAGRAMS.md`
- **Troubleshooting**: `COMPREHENSIVE_TOOL_REPORT.md`
- **Jenkins Guide**: `Jenkinsfile` (well-commented)
- **n8n Configuration**: `N8N_CONFIGURATION_GUIDE.md`
- **MCP Documentation**: `MCP_QUICKSTART.md`

> **Remember**: This system processes real code and creates actual pull requests. Always validate changes thoroughly before deployment and maintain proper backup procedures.