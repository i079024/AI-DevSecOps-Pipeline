# 🚀 AI-Enhanced DevSecOps Pipeline - Quick Start

Get your AI-powered DevSecOps pipeline running in 15 minutes!

---

## ✅ Prerequisites Check

You already have:
- ✅ Jenkins installed and running (localhost:8080)
- ✅ n8n installed (localhost:5678)
- ✅ Speculator Bot with MCP server
- ✅ Python 3.13 with all dependencies
- ✅ Homebrew and basic dev tools

---

## 🎯 Quick Start (15 Minutes)

### Step 1: Start All Services (2 minutes)

```bash
cd /Users/i079024/ariba/cursor/AIBot

# Start everything with one command
./start-devsecops-pipeline.sh
```

**This will start:**
1. Jenkins (http://localhost:8080)
2. n8n (http://localhost:5678)
3. MCP HTTP Wrapper (http://localhost:3000)
4. ngrok tunnel (for GitHub webhooks)

### Step 2: Configure Jenkins (5 minutes)

#### A. First-Time Jenkins Setup
1. Open http://localhost:8080
2. Enter password: `d555d41b83e043e892c23cb7d5950d94`
3. Install suggested plugins
4. Create admin user

#### B. Add GitHub Credentials
1. Jenkins → Manage Jenkins → Manage Credentials
2. Add Credentials:
   - **Kind**: Secret text
   - **Secret**: (your GitHub Personal Access Token)
   - **ID**: `github-pat`

#### C. Create Pipeline Job
1. Jenkins → New Item
2. Name: `AI-DevSecOps-Pipeline`
3. Type: **Pipeline**
4. Configuration:
   - **Pipeline** → **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: `https://github.com/your-org/your-repo.git`
   - **Credentials**: `github-pat`
   - **Script Path**: `Jenkinsfile`
5. Save

### Step 3: Configure GitHub Webhook (3 minutes)

```bash
# Get your ngrok URL
curl -s http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url'
```

**In GitHub:**
1. Go to your repository → Settings → Webhooks
2. Add webhook:
   - **Payload URL**: `https://YOUR-NGROK-URL.ngrok.io/github-webhook/`
   - **Content type**: `application/json`
   - **Events**: Just the push event
3. Add webhook

### Step 4: Configure n8n Workflow (3 minutes)

1. Open http://localhost:5678
2. Create account (first time)
3. Add Credentials:
   - **GitHub**: Your PAT token
   - **SMTP** (optional): For email notifications
4. Import workflow:
   - Click **+** → **Import from File**
   - Select `n8n-ai-devsecops-workflow.json`
5. Activate workflow (toggle on)

### Step 5: Copy Files to Your Repository (2 minutes)

```bash
# Copy pipeline files to your project
cp Jenkinsfile /path/to/your/project/
cp sonar-project.properties /path/to/your/project/
cd /path/to/your/project/

# Commit and push
git add Jenkinsfile sonar-project.properties
git commit -m "Add AI-Enhanced DevSecOps Pipeline"
git push origin main
```

---

## 🧪 Test the Pipeline

### Make a Test Commit

```bash
cd /path/to/your/project

# Make a small change
echo "// Test AI Pipeline" >> src/main.js

# Commit and push
git add .
git commit -m "test: AI DevSecOps pipeline"
git push origin main
```

### Watch the Magic Happen!

**Monitor the flow:**

1. **GitHub → Jenkins** (within seconds)
   - http://localhost:8080/job/AI-DevSecOps-Pipeline/

2. **Jenkins → n8n** (non-blocking trigger)
   - Jenkins continues with build
   - n8n workflow starts in parallel

3. **n8n Execution** (http://localhost:5678/executions)
   - Fetches code diffs
   - Runs Speculator Bot analysis
   - Sends to LLM for review
   - Creates GitHub PR with recommendations

4. **GitHub PR Created**
   - Check your repository for new PR
   - Review AI-generated analysis and suggestions

---

## 📊 What You'll Get

### In Jenkins Build:
- ✅ Code checkout and build
- ✅ Unit tests executed
- ✅ SonarQube quality scan (if configured)
- ✅ Build artifacts
- ✅ Deployment (if configured)

### In GitHub PR (Created by AI):
- 📊 **Risk Assessment**: Deployment risk score and level
- 🔍 **Change Analysis**: What changed and why
- ⚠️ **Gap Analysis**: Potential issues and missing edge cases
- 🧪 **Test Recommendations**: Suggested test cases
- 🔒 **Security Fixes**: Security improvements needed
- 📈 **Metrics**: Coverage, complexity, risk factors

---

## 🎯 Example Output

When you push code, you'll get a PR like this:

```markdown
## 🤖 AI-Generated Code Analysis & Improvements

### Summary
**Commit**: abc123...
**Risk Score**: 0.45 (MEDIUM)
**Files Changed**: 3

### 🔍 Change Analysis
This commit adds payment processing functionality...

### ⚠️ Gap Analysis
**Test Coverage**: Test coverage is 65%, below recommended 70%
*Recommendation*: Add unit and integration tests

### 🧪 Test Coverage Recommendations
- **HIGH**: Add comprehensive integration tests for payment/processor.py
- **MEDIUM**: Add unit tests to cover all new code paths

### 🔒 Security Fixes
- **SonarQube Issues** [HIGH]: Review and fix security vulnerabilities
- **Configuration Security** [HIGH]: Verify no secrets hardcoded

### 📈 Metrics
- Test Coverage: 65.0%
- Recommended Tests: 18
- Est. Test Time: 6.5 minutes
```

---

## 🔧 Configuration

### Customize Risk Thresholds

Edit `config.yaml`:
```yaml
risk_analysis:
  risk_levels:
    high: 0.7      # Adjust based on your tolerance
    medium: 0.4
    low: 0.2
```

### Customize n8n Workflow

1. Open workflow in n8n
2. Modify nodes as needed:
   - Change notification channels (Email/Slack/Teams)
   - Adjust LLM prompts
   - Add additional checks

### Customize Jenkinsfile

Edit the `Jenkinsfile` in your repository:
- Add deployment stages
- Modify test commands
- Add security scans
- Configure notifications

---

## 🛠️ Troubleshooting

### Services Not Starting

```bash
# Check what's running
brew services list
ps aux | grep -E 'jenkins|n8n'

# Restart individual services
brew services restart jenkins-lts
pkill -f n8n && n8n start &
python3 mcp-http-wrapper.py &
```

### Jenkins Can't Access GitHub

```bash
# Test GitHub connectivity
curl -H "Authorization: token YOUR_PAT" https://api.github.com/user

# Verify Jenkins credentials
# Go to: Jenkins → Manage Jenkins → Manage Credentials
```

### n8n Workflow Fails

```bash
# Check MCP wrapper is running
curl http://localhost:3000/health

# View MCP wrapper logs
tail -f logs/mcp-wrapper.log

# Test MCP API manually
curl -X POST http://localhost:3000/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{"commitSHA":"test","branch":"main","author":"test"}'
```

### ngrok Tunnel Expires

```bash
# Restart ngrok
pkill ngrok
ngrok http 8080

# Update GitHub webhook with new URL
# Get URL: curl http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url'
```

---

## 📚 Advanced Features

### Add SonarQube

```bash
# Start SonarQube
docker run -d --name sonarqube -p 9000:9000 sonarqube:latest

# Access: http://localhost:9000 (admin/admin)
# Configure in Jenkins: Manage Jenkins → Configure System → SonarQube
```

### Add Slack Notifications

In n8n workflow:
1. Add Slack node after "Parse & Format Results"
2. Configure Slack webhook URL
3. Format message with analysis results

### Add More Security Scans

Edit `Jenkinsfile` to add:
- Dependency vulnerability scanning (OWASP)
- Container security scanning
- Infrastructure as Code scanning

---

## 🎓 Understanding the Flow

```
Developer Pushes Code
       ↓
GitHub Webhook → Jenkins
       ↓
Jenkins Pipeline:
  1. Build & Test
  2. SonarQube Scan
  3. Trigger n8n (async) ────┐
  4. Package                 │
  5. Deploy                  │
       ↓                     │
   Build Complete            │
                             │
                ┌────────────┘
                ↓
           n8n Workflow:
             1. Fetch Code Diffs
             2. Speculator Bot Analysis
             3. LLM Analysis
             4. Create GitHub Branch
             5. Generate PR
             6. Send Notification
                ↓
        Developer Reviews PR
                ↓
          Merge Improvements
```

---

## ✅ Success Checklist

Your pipeline is working when:

- [  ] Push to GitHub triggers Jenkins build
- [  ] Jenkins build completes successfully
- [  ] n8n workflow executes
- [  ] MCP analysis completes
- [  ] GitHub PR is created
- [  ] You receive notification

---

## 🎯 Next Steps

1. **Customize Analysis**:
   - Adjust risk thresholds in `config.yaml`
   - Modify LLM prompts in MCP wrapper
   - Add custom rules

2. **Add More Tools**:
   - Security scanners (Snyk, Trivy)
   - Performance testing
   - Load testing

3. **Integrate with Team Tools**:
   - Slack/Teams notifications
   - Jira ticket creation
   - Confluence documentation

4. **Scale Up**:
   - Add more repositories
   - Create repository templates
   - Set up multi-branch pipelines

---

## 🆘 Getting Help

- **Setup Guide**: `AI_DEVSECOPS_SETUP.md`
- **Jenkinsfile**: Well-commented pipeline script
- **n8n Workflow**: Visual workflow in n8n UI
- **MCP Wrapper**: `mcp-http-wrapper.py` with detailed logs
- **Troubleshooting**: Run `./examples/troubleshoot_mcp.sh`

---

## 🛑 Stopping Services

```bash
# Stop all services
./stop-devsecops-pipeline.sh

# Or stop individually
brew services stop jenkins-lts
pkill -f n8n
pkill -f mcp-http-wrapper
pkill -f ngrok
```

---

**You're all set!** 🎉

Your AI-Enhanced DevSecOps Pipeline is ready to revolutionize your development workflow!

Push some code and watch the AI analyze it for you! 🚀

