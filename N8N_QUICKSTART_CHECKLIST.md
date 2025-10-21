# ✅ n8n Quick Setup Checklist

## 🎯 Pre-requisites

- [ ] n8n is running on http://localhost:5678
- [ ] You have a GitHub account with a repository
- [ ] (Optional) Email account for notifications

---

## 📝 Setup Checklist

### 1️⃣ Access n8n (2 minutes)

- [ ] Open http://localhost:5678 in browser
- [ ] Create n8n account (first time only)

---

### 2️⃣ Import Workflow (1 minute)

- [ ] Click **"+"** → **Import from File**
- [ ] Select `n8n-ai-devsecops-workflow.json`
- [ ] Click **Import**

---

### 3️⃣ Configure GitHub Credentials (3 minutes)

- [ ] Go to https://github.com/settings/tokens
- [ ] Click **"Generate new token (classic)"**
- [ ] Select scopes: `repo`, `workflow`, `write:packages`
- [ ] Generate and **copy the token**
- [ ] In n8n: Settings → Credentials → Add Credential → **GitHub API**
- [ ] Name: `GitHub DevSecOps`
- [ ] Paste your token
- [ ] Click **Save**

---

### 4️⃣ Configure SMTP (Optional - 3 minutes)

- [ ] In n8n: Settings → Credentials → Add Credential → **SMTP**
- [ ] Name: `SMTP Account`
- [ ] Enter your email settings:
  - Gmail users: Use App Password
  - Host: `smtp.gmail.com`, Port: `587`
- [ ] Click **Save**

**Skip this if you don't need email notifications**

---

### 5️⃣ Activate Workflow (1 minute)

- [ ] Toggle workflow to **Active** (top right)
- [ ] Click on **"Jenkins Webhook Trigger"** node
- [ ] **Copy the Webhook URL**: 
  ```
  http://localhost:5678/webhook/jenkins-trigger
  ```

---

### 6️⃣ Update GitHub Credentials in Nodes (2 minutes)

- [ ] Click on **"Fetch Git Commit Details"** node
- [ ] Select credential: `GitHub DevSecOps`
- [ ] Click on **"Create GitHub Branch"** node
- [ ] Select credential: `GitHub DevSecOps`
- [ ] Click on **"Create Pull Request"** node
- [ ] Select credential: `GitHub DevSecOps`
- [ ] Click **Save** (top right)

---

### 7️⃣ Start MCP HTTP Wrapper (1 minute)

Open a new terminal in your AIBot directory:

```bash
cd /Users/i079024/ariba/cursor/AIBot
python3 mcp-http-wrapper.py
```

Should see: `Running on http://127.0.0.1:3000`

---

### 8️⃣ Test the Webhook (2 minutes)

Open another terminal and run:

```bash
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "test123",
    "branch": "main",
    "author": "test@example.com",
    "sonarURL": "http://localhost:9000",
    "buildNumber": "1",
    "jenkinsURL": "http://localhost:8080",
    "repository": "https://github.com/yourusername/yourrepo.git"
  }'
```

- [ ] Check n8n **Executions** tab
- [ ] Verify execution appears (may show errors if GitHub repo doesn't exist - that's OK for now)

---

## 🎉 You're Done!

### ✅ What You've Configured:

- ✅ n8n workflow imported
- ✅ GitHub credentials set up
- ✅ Webhook endpoint active
- ✅ MCP wrapper running
- ✅ Test execution completed

---

## 🔗 Next: Connect to Jenkins

Copy your n8n webhook URL and add it to your Jenkinsfile:

```groovy
stage('Trigger AI Analysis') {
    steps {
        script {
            httpRequest(
                url: 'http://localhost:5678/webhook/jenkins-trigger',
                httpMode: 'POST',
                contentType: 'APPLICATION_JSON',
                requestBody: """{ ... }"""
            )
        }
    }
}
```

---

## 📚 Documentation

- **Full Guide**: `N8N_CONFIGURATION_GUIDE.md`
- **Pipeline Guide**: `AI_DEVSECOPS_QUICKSTART.md`
- **MCP Setup**: `MCP_QUICKSTART.md`

---

## 🆘 Troubleshooting

| Issue | Fix |
|-------|-----|
| Webhook not working | Make sure workflow is **Active** |
| GitHub auth failed | Verify token has `repo` scope |
| MCP not responding | Run `python3 mcp-http-wrapper.py` |
| Email not sending | Use App Password for Gmail |

---

**Total Setup Time: ~15 minutes** ⏱️

**Need help?** See `N8N_CONFIGURATION_GUIDE.md` for detailed instructions.

