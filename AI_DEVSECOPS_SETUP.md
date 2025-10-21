# 🤖 AI-Enhanced DevSecOps Pipeline Setup Guide

Complete guide for setting up an intelligent DevSecOps pipeline with LLM-powered analysis.

---

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Section 1: Initial Setup & Connectivity](#section-1-initial-setup--connectivity)
3. [Section 2: Jenkins Pipeline Configuration](#section-2-jenkins-pipeline-configuration)
4. [Section 3: n8n Workflow Configuration](#section-3-n8n-workflow-configuration)
5. [Section 4: Testing & Validation](#section-4-testing--validation)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         GITHUB REPOSITORY                       │
│                                                                 │
│  [Code Push] ──────────> [Webhook] ────────────────┐           │
└────────────────────────────────────────────────────┼───────────┘
                                                     │
                                    ┌────────────────▼──────────────┐
                                    │      JENKINS (Local)          │
                                    │                               │
                                    │  1. Checkout Code             │
                                    │  2. Build & Test              │
                                    │  3. SonarQube Scan            │
                                    │  4. Trigger n8n (async) ─────┼──┐
                                    └───────────────────────────────┘  │
                                                                        │
                          ┌─────────────────────────────────────────────┘
                          │
           ┌──────────────▼─────────────┐
           │     n8n (Workflow)         │
           │                            │
           │  1. Receive Webhook        │
           │  2. Fetch Code Diffs       │
           │  3. Send to MCP ──────────┼──┐
           └────────────────────────────┘  │
                                           │
                    ┌──────────────────────▼──────────────────┐
                    │  MCP SERVER (Speculator Bot)            │
                    │                                         │
                    │  • Risk Analysis                        │
                    │  • Test Selection                       │
                    │  • LLM Analysis (Claude) ──────────────┼──┐
                    └─────────────────────────────────────────┘  │
                                                                 │
                                    ┌────────────────────────────▼───┐
                                    │     Claude Desktop / LLM        │
                                    │                                 │
                                    │  • Code Analysis                │
                                    │  • Gap Detection                │
                                    │  • Test Recommendations         │
                                    │  • Security Fixes               │
                                    └────────────────────────────────┬┘
                                                                     │
           ┌─────────────────────────────────────────────────────────┘
           │
           ▼
┌──────────────────────────┐
│  n8n (Continued)         │
│                          │
│  4. Parse LLM Results    │
│  5. Create GitHub Branch │
│  6. Generate PR          │
│  7. Send Notification    │
└──────────────────────────┘
```

---

## Section 1: Initial Setup & Connectivity

### 1.1 Local Environment Verification

#### ✅ Verify Jenkins

Jenkins is already running! Here's how to verify:

```bash
# Check Jenkins service status
brew services list | grep jenkins

# Check if Jenkins is accessible
curl -I http://localhost:8080

# View Jenkins logs
tail -f /opt/homebrew/var/log/jenkins-lts/jenkins-lts.log
```

**Access Jenkins:**
- URL: http://localhost:8080
- Initial Password: `d555d41b83e043e892c23cb7d5950d94`

**First-Time Setup:**
1. Open http://localhost:8080
2. Enter the initial admin password
3. Choose "Install suggested plugins"
4. Create admin user:
   - Username: `admin`
   - Password: (choose secure password)
   - Email: your-email@example.com

#### ✅ Verify n8n

n8n is already running! Verify:

```bash
# Check if n8n is running
ps aux | grep n8n

# Check if accessible
curl -I http://localhost:5678

# Restart if needed
pkill -f n8n
n8n start --tunnel &
```

**Access n8n:**
- URL: http://localhost:5678
- Create account on first access

---

### 1.2 GitHub Connectivity

#### Step 1: Generate GitHub Personal Access Token (PAT)

1. Go to GitHub → Settings → Developer Settings → Personal Access Tokens → Tokens (classic)
2. Click "Generate new token (classic)"
3. Configure:
   - **Name**: `Jenkins-n8n-DevSecOps`
   - **Expiration**: 90 days (or as needed)
   - **Scopes**:
     - ✅ `repo` (Full control of private repositories)
     - ✅ `workflow` (Update GitHub Action workflows)
     - ✅ `write:packages` (if using packages)
     - ✅ `admin:repo_hook` (Full control of repository hooks)
4. Click "Generate token"
5. **Copy and save the token immediately!**

#### Step 2: Configure Jenkins GitHub Credentials

```bash
# In Jenkins Web UI:
```

1. Jenkins → Manage Jenkins → Manage Credentials
2. Click "(global)" domain
3. Add Credentials:
   - **Kind**: Secret text
   - **Secret**: (paste your GitHub PAT)
   - **ID**: `github-pat`
   - **Description**: `GitHub Personal Access Token`

4. Add another credential for SSH (optional):
   - **Kind**: SSH Username with private key
   - **ID**: `github-ssh`
   - **Username**: `git`
   - **Private Key**: (paste your SSH private key)

#### Step 3: Install Jenkins Plugins

In Jenkins → Manage Jenkins → Manage Plugins → Available:

```
✅ GitHub plugin
✅ Git plugin
✅ Pipeline plugin
✅ HTTP Request Plugin
✅ SonarQube Scanner
✅ Email Extension Plugin
```

#### Step 4: Configure GitHub Webhook (Using ngrok)

Since Jenkins is running locally, we need ngrok to expose it:

```bash
# Install ngrok
brew install ngrok

# Start ngrok tunnel to Jenkins
ngrok http 8080
```

**Copy the ngrok URL** (e.g., `https://abc123.ngrok.io`)

**Configure GitHub Webhook:**

1. Go to your GitHub repository
2. Settings → Webhooks → Add webhook
3. Configure:
   - **Payload URL**: `https://abc123.ngrok.io/github-webhook/`
   - **Content type**: `application/json`
   - **Secret**: (optional, create a random string)
   - **Events**: ✅ Just the push event
   - **Active**: ✅ Checked
4. Add webhook

---

### 1.3 Workflow Orchestration Setup (n8n & MCP)

#### Step 1: Configure n8n Credentials

In n8n (http://localhost:5678):

1. **Settings** → **Credentials** → **Add Credential**

2. **GitHub Credentials**:
   - Type: `GitHub`
   - Name: `GitHub DevSecOps`
   - Access Token: (paste your GitHub PAT)

3. **HTTP Auth Credentials** (for MCP):
   - Type: `Header Auth`
   - Name: `MCP Server Auth`
   - Name: `Authorization`
   - Value: `Bearer your-token-here` (optional)

#### Step 2: Configure MCP Server Connection

The MCP server (Speculator Bot) is already configured!

**Verify MCP Server:**
```bash
cd /Users/i079024/ariba/cursor/AIBot
python3 examples/test_mcp_server.py
```

**MCP Server Endpoint:**
- The MCP server runs via Claude Desktop integration
- For direct HTTP access, we'll need to create a wrapper

#### Step 3: Create MCP HTTP Wrapper

```bash
# Create a simple HTTP wrapper for MCP
cd /Users/i079024/ariba/cursor/AIBot
```

I'll create this file next.

---

### 1.4 SonarQube Setup (Optional but Recommended)

#### Quick SonarQube Setup with Docker:

```bash
# Start SonarQube
docker run -d --name sonarqube \
  -p 9000:9000 \
  sonarqube:latest

# Wait for startup (2-3 minutes)
# Access: http://localhost:9000
# Default credentials: admin/admin
```

**Configure in Jenkins:**
1. Manage Jenkins → Configure System
2. SonarQube servers:
   - Name: `SonarQube`
   - Server URL: `http://localhost:9000`
   - Token: (generate in SonarQube: Administration → Security → Users → Tokens)

---

## Section 2: Jenkins Pipeline Configuration

### 2.1 Create Jenkins Pipeline Job

1. Jenkins → New Item
2. Name: `AI-DevSecOps-Pipeline`
3. Type: **Pipeline**
4. Configure:
   - **Pipeline** → **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: Your GitHub repo URL
   - **Credentials**: Select `github-pat`
   - **Branch Specifier**: `*/main` (or your branch)
   - **Script Path**: `Jenkinsfile`

### 2.2 Jenkinsfile (Place in Repository Root)

See separate file: `Jenkinsfile`

---

## Section 3: n8n Workflow Configuration

### 3.1 Import Workflow Template

See separate file: `n8n-ai-devsecops-workflow.json`

### 3.2 Workflow Node Configuration

#### Node 1: Webhook Trigger
- **Type**: Webhook
- **HTTP Method**: POST
- **Path**: `jenkins-trigger`
- **Response**: Immediately
- **Full URL**: `http://localhost:5678/webhook/jenkins-trigger`

#### Node 2: Extract Build Data
- **Type**: Set
- **Extract**:
  - `commitSHA`: `{{$json["body"]["commitSHA"]}}`
  - `branch`: `{{$json["body"]["branch"]}}`
  - `author`: `{{$json["body"]["author"]}}`
  - `sonarURL`: `{{$json["body"]["sonarURL"]}}`
  - `buildNumber`: `{{$json["body"]["buildNumber"]}}`

#### Node 3: Fetch Code Diffs (GitHub)
- **Type**: GitHub
- **Credential**: GitHub DevSecOps
- **Operation**: Get Commit
- **Repository**: `owner/repo`
- **Commit SHA**: `{{$node["Extract Build Data"].json["commitSHA"]}}`

#### Node 4: Analyze with Speculator Bot
- **Type**: HTTP Request
- **Method**: POST
- **URL**: `http://localhost:3000/api/speculator/analyze`
- **Body**: JSON with commit data
- **Headers**: `Content-Type: application/json`

#### Node 5: LLM Analysis via MCP
- **Type**: HTTP Request
- **Method**: POST
- **URL**: `http://localhost:3000/api/mcp/analyze`
- **Body**:
```json
{
  "commitSHA": "{{$node['Extract Build Data'].json['commitSHA']}}",
  "codeDiff": "{{$node['Fetch Code Diffs'].json['files']}}",
  "sonarURL": "{{$node['Extract Build Data'].json['sonarURL']}}",
  "context": {
    "branch": "{{$node['Extract Build Data'].json['branch']}}",
    "author": "{{$node['Extract Build Data'].json['author']}}"
  }
}
```

#### Node 6: Parse LLM Results
- **Type**: Code (JavaScript)
```javascript
const llmResponse = items[0].json;

return {
  changeSummary: llmResponse.changeIntroduced,
  gapAnalysis: llmResponse.gapAnalysis,
  testRecommendations: llmResponse.testCoverage,
  securityFixes: llmResponse.securityFixes,
  newBranchName: `llm-fixes-${items[0].json.commitSHA.substring(0,7)}`
};
```

#### Node 7: Create GitHub Branch
- **Type**: GitHub
- **Operation**: Create Reference
- **Branch Name**: `{{$node["Parse LLM Results"].json["newBranchName"]}}`
- **Base SHA**: `{{$node["Extract Build Data"].json["commitSHA"]}}`

#### Node 8: Commit Fixes (If Any)
- **Type**: GitHub
- **Operation**: Create or Update File
- **Branch**: `{{$node["Parse LLM Results"].json["newBranchName"]}}`
- **Files**: Generated test files and security fixes

#### Node 9: Create Pull Request
- **Type**: GitHub
- **Operation**: Create Pull Request
- **Title**: `AI-Generated Improvements: {{$node["Parse LLM Results"].json["changeSummary"]}}`
- **Body**: Template with analysis results
- **Head Branch**: `{{$node["Parse LLM Results"].json["newBranchName"]}}`
- **Base Branch**: `{{$node["Extract Build Data"].json["branch"]}}`

#### Node 10: Send Notification
- **Type**: Email / Slack
- **To**: Development team
- **Subject**: `AI Analysis Complete: {{$node["Extract Build Data"].json["commitSHA"]}}`
- **Body**: Full report with PR link

---

## Section 4: Testing & Validation

### 4.1 End-to-End Test

```bash
# 1. Make a code change in your repository
echo "// Test change" >> src/main.js
git add .
git commit -m "test: AI DevSecOps pipeline"
git push origin main

# 2. Monitor Jenkins
open http://localhost:8080/job/AI-DevSecOps-Pipeline/

# 3. Monitor n8n
open http://localhost:5678/workflows

# 4. Check GitHub for new PR
```

### 4.2 Verification Checklist

- [ ] Jenkins receives GitHub webhook
- [ ] Jenkins pipeline runs successfully
- [ ] n8n workflow is triggered
- [ ] Code diffs are fetched
- [ ] Speculator Bot analyzes risk
- [ ] LLM generates recommendations
- [ ] GitHub PR is created
- [ ] Notification is sent

---

## 📚 Additional Files

See companion files:
- `Jenkinsfile` - Complete Jenkins pipeline
- `n8n-ai-devsecops-workflow.json` - n8n workflow template
- `mcp-http-wrapper.py` - HTTP wrapper for MCP server
- `sonar-project.properties` - SonarQube configuration

---

## 🔧 Troubleshooting

### Jenkins Issues
```bash
# Check logs
tail -f /opt/homebrew/var/log/jenkins-lts/jenkins-lts.log

# Restart Jenkins
brew services restart jenkins-lts
```

### n8n Issues
```bash
# Check if running
ps aux | grep n8n

# Restart n8n
pkill -f n8n && n8n start --tunnel &
```

### MCP Server Issues
```bash
# Test MCP server
cd /Users/i079024/ariba/cursor/AIBot
python3 examples/test_mcp_server.py
```

### ngrok Issues
```bash
# Check ngrok status
curl http://127.0.0.1:4040/api/tunnels

# Restart ngrok
pkill ngrok && ngrok http 8080
```

---

## 🎯 Success Criteria

Your pipeline is working when:
1. ✅ Push to GitHub triggers Jenkins build
2. ✅ Jenkins runs tests and quality scans
3. ✅ n8n workflow is triggered asynchronously
4. ✅ LLM analyzes code and generates recommendations
5. ✅ GitHub PR is created automatically
6. ✅ Team receives notification with analysis

---

**Setup Complete!** You now have an AI-Enhanced DevSecOps Pipeline! 🚀

