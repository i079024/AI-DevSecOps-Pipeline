# 🔧 n8n Configuration Guide for AI-Enhanced DevSecOps Pipeline

## 📋 Overview

This guide walks you through configuring n8n for the AI-Enhanced DevSecOps Pipeline.

**n8n URL**: http://localhost:5678

---

## 🚀 Quick Setup Steps

### Step 1: Access n8n

1. Open your browser: **http://localhost:5678**
2. Create your n8n account (first time only)
   - Email: Your email
   - Password: Your password

---

### Step 2: Import the Workflow

1. **Click** the **"+"** button (top right) → **Import from File**
2. **Select**: `n8n-ai-devsecops-workflow.json`
3. **Click**: **Import**

The workflow will load with all nodes pre-configured.

---

## 🔑 Step 3: Configure Credentials

You need to set up 2 credentials:

### 3.1 GitHub API Credentials

1. **In n8n**: Click on **Settings** (gear icon, bottom left)
2. **Click**: **Credentials**
3. **Click**: **Add Credential** → Select **GitHub API**
4. **Fill in**:
   - **Name**: `GitHub DevSecOps`
   - **Authentication Method**: `Access Token`
   - **Access Token**: Your GitHub Personal Access Token
   
   **To create a GitHub token**:
   - Go to: https://github.com/settings/tokens
   - Click: **Generate new token (classic)**
   - Select scopes:
     - ✅ `repo` (Full control of private repositories)
     - ✅ `workflow` (Update GitHub Action workflows)
     - ✅ `write:packages` (Upload packages)
   - Click: **Generate token**
   - **Copy the token** and paste it in n8n

5. **Click**: **Save**

---

### 3.2 SMTP Credentials (Optional - for Email Notifications)

1. **In n8n Credentials**: **Add Credential** → Select **SMTP**
2. **Fill in**:
   - **Name**: `SMTP Account`
   - **User**: Your email address
   - **Password**: Your email password or app-specific password
   - **Host**: Your SMTP server
     - Gmail: `smtp.gmail.com`
     - Outlook: `smtp-mail.outlook.com`
     - Yahoo: `smtp.mail.yahoo.com`
   - **Port**: `587` (TLS) or `465` (SSL)
   - **Security**: `TLS`

3. **Click**: **Save**

**Note**: If using Gmail, you need to:
- Enable 2FA
- Create an App Password: https://myaccount.google.com/apppasswords

---

## 🔧 Step 4: Configure Workflow Nodes

### 4.1 Webhook Trigger Node

**Purpose**: Receives triggers from Jenkins

1. **Click** on the **"Jenkins Webhook Trigger"** node
2. **Note the Webhook URL** (you'll use this in Jenkins):
   ```
   http://localhost:5678/webhook/jenkins-trigger
   ```
3. **Path**: `jenkins-trigger`
4. **HTTP Method**: `POST`

**No changes needed** - this is pre-configured!

---

### 4.2 Speculator Bot Analysis Node

**Purpose**: Calls Speculator Bot for risk analysis

1. **Click** on **"Speculator Bot Analysis"** node
2. **Verify URL**: `http://localhost:3001/api/speculator/analyze`
3. **Method**: `POST`
4. **Timeout**: `30000` (30 seconds)

**This node will work once you start the MCP HTTP Wrapper** (see below).

---

### 4.3 GitHub Nodes (Fetch Commit, Create Branch, Create PR)

**Purpose**: Interact with GitHub

1. **Click** on each GitHub node:
   - **Fetch Git Commit Details**
   - **Create GitHub Branch**
   - **Create Pull Request**

2. **Select Credential**: `GitHub DevSecOps` (the one you created)

**All expressions are pre-configured** - no changes needed!

---

### 4.4 LLM Analysis via MCP Node

**Purpose**: Sends data to MCP server for AI analysis

1. **Click** on **"LLM Analysis via MCP"** node
2. **Verify URL**: `http://localhost:3001/api/mcp/analyze`
3. **Method**: `POST`
4. **Timeout**: `60000` (60 seconds)

---

### 4.5 Email Notification Node (Optional)

**Purpose**: Sends email notifications

1. **Click** on **"Send Email Notification"** node
2. **Select Credential**: `SMTP Account`
3. **Update**:
   - **From Email**: `your-email@example.com`
   - **To Email**: Keep the expression (uses author email from commit)
   - **Subject**: Pre-configured

**If you don't want email notifications**:
- You can skip SMTP setup
- Delete this node or disconnect it

---

## 🧪 Step 5: Test the Webhook

### 5.1 Enable the Workflow

1. **Toggle** the workflow to **Active** (top right switch)

---

### 5.2 Get the Webhook URL

1. **Click** on **"Jenkins Webhook Trigger"** node
2. **Copy** the **Test URL** or **Production URL**:
   ```
   http://localhost:5678/webhook/jenkins-trigger
   ```

---

### 5.3 Test with cURL

Open a terminal and run:

```bash
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "abc123def456",
    "branch": "main",
    "author": "developer@example.com",
    "sonarURL": "http://localhost:9000/dashboard?id=my-project",
    "buildNumber": "42",
    "jenkinsURL": "http://localhost:8080/job/my-pipeline/42/",
    "repository": "https://github.com/yourusername/yourrepo.git"
  }'
```

---

### 5.4 Verify Execution

1. **In n8n**: Click on **Executions** (left sidebar)
2. You should see a new execution
3. **Click** on it to see the flow and debug any issues

---

## 🔄 Step 6: Start the MCP HTTP Wrapper

The workflow needs the MCP HTTP Wrapper running:

```bash
# In your AIBot directory
python3 mcp-http-wrapper.py
```

This starts the API server on **http://localhost:3001**

---

## 🔗 Step 7: Connect Jenkins to n8n

### 7.1 Get n8n Webhook URL

From the **"Jenkins Webhook Trigger"** node:
```
http://localhost:5678/webhook/jenkins-trigger
```

---

### 7.2 Update Jenkinsfile

Make sure your `Jenkinsfile` has the n8n trigger step:

```groovy
stage('Trigger AI Analysis') {
    steps {
        script {
            try {
                httpRequest(
                    url: 'http://localhost:5678/webhook/jenkins-trigger',
                    httpMode: 'POST',
                    contentType: 'APPLICATION_JSON',
                    requestBody: """{
                        "commitSHA": "${env.GIT_COMMIT}",
                        "branch": "${env.GIT_BRANCH}",
                        "author": "${env.GIT_AUTHOR}",
                        "sonarURL": "${env.SONAR_URL}",
                        "buildNumber": "${env.BUILD_NUMBER}",
                        "jenkinsURL": "${env.BUILD_URL}",
                        "repository": "${env.GIT_URL}"
                    }"""
                )
            } catch (Exception e) {
                echo "n8n trigger failed (non-blocking): ${e.message}"
            }
        }
    }
}
```

---

## 📊 Understanding the Workflow

### Workflow Flow:

```
1. Jenkins Webhook Trigger (receives POST from Jenkins)
   ↓
2. Extract Build Data (parses JSON payload)
   ↓
3. Speculator Bot Analysis (risk assessment)
   ↓
4. Fetch Git Commit Details (gets code changes from GitHub)
   ↓
5. LLM Analysis via MCP (AI-driven analysis)
   ↓
6. Parse & Format Results (formats PR body)
   ↓
7. Has Improvements? (conditional check)
   ↓
8a. [YES] Create GitHub Branch → Create Pull Request → Send Email
8b. [NO] Send Email only
```

---

## 🎨 Customization Options

### Change Email Template

Edit the **"Parse & Format Results"** node → Modify the `prBody` template

---

### Add More Checks

1. **Click** between two nodes
2. **Add** new nodes like:
   - **HTTP Request** for custom APIs
   - **Code** for custom JavaScript logic
   - **IF** for conditional branches

---

### Modify Risk Thresholds

Edit the **"Has Improvements?"** node to change conditions

---

## 🐛 Troubleshooting

### Issue: "No webhook registered"

**Fix**: 
- Make sure the workflow is **Active** (toggle in top right)
- Click **"Jenkins Webhook Trigger"** node → Click **"Execute Node"**

---

### Issue: "GitHub authentication failed"

**Fix**:
- Verify your GitHub token has the correct scopes
- Test the credential: Settings → Credentials → GitHub DevSecOps → Test

---

### Issue: "MCP server not responding"

**Fix**:
```bash
# Start the MCP HTTP wrapper
python3 mcp-http-wrapper.py
```

Verify it's running on http://localhost:3001

---

### Issue: "Email not sending"

**Fix**:
- Verify SMTP credentials
- Check your email provider allows SMTP
- Gmail users: Use App Password, not regular password
- Test: Settings → Credentials → SMTP Account → Test

---

## 🔍 Monitoring Executions

### View All Executions

1. **Click**: **Executions** (left sidebar)
2. See all workflow runs with status (Success/Error)

---

### Debug Failed Executions

1. **Click** on a failed execution
2. See which node failed
3. **Click** on the failed node to see error details
4. Check:
   - Input data
   - Output data
   - Error message

---

## 🎯 Next Steps

✅ **Workflow imported**
✅ **Credentials configured**
✅ **Webhook tested**

Now you can:

1. **Start the MCP wrapper**: `python3 mcp-http-wrapper.py`
2. **Configure Jenkins** to use the webhook URL
3. **Push a commit** to trigger the full pipeline
4. **Monitor** in n8n Executions

---

## 📚 Additional Resources

- **n8n Documentation**: https://docs.n8n.io
- **GitHub API**: https://docs.github.com/en/rest
- **n8n Community**: https://community.n8n.io

---

## 🆘 Need Help?

1. **Check Executions** tab in n8n for detailed error logs
2. **Check MCP wrapper logs** in the terminal
3. **Check Jenkins console** output
4. **Review** this guide and `AI_DEVSECOPS_QUICKSTART.md`

---

**Your n8n workflow is ready to power your AI-Enhanced DevSecOps Pipeline!** 🚀

