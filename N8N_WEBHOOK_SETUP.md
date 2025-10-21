# 🪝 n8n Webhook Setup Guide

## 🔍 Understanding n8n Webhook URLs

n8n provides **two types** of webhook URLs:

### 1. **Test URL** (Temporary)
```
http://localhost:5678/webhook-test/jenkins-trigger
```
- ⚠️ Only works **once** after clicking "Execute Workflow"
- Used for testing during workflow development
- Resets after each test

### 2. **Production URL** (Permanent)
```
http://localhost:5678/webhook/jenkins-trigger
```
- ✅ Always available when workflow is **Active**
- Used for real integrations (Jenkins, GitHub, etc.)
- Persists until workflow is deactivated

---

## ✅ Setup Steps

### Step 1: Activate Your Workflow

1. **Open n8n**: http://localhost:5678
2. **Open** your workflow (AI-Enhanced DevSecOps Pipeline)
3. **Toggle** the workflow to **Active** (top right switch)
   - When active, the toggle should be **blue/green**

---

### Step 2: Get the Production Webhook URL

1. **Click** on the **"Jenkins Webhook Trigger"** node
2. Look for the **Production URL** section
3. **Copy** the URL:
   ```
   http://localhost:5678/webhook/jenkins-trigger
   ```

---

### Step 3: Test with Production URL

```bash
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "cc2619c1ffae990f183bcfadf5dd70e142e36657",
    "branch": "main",
    "author": "sagar.maddali@sap.com",
    "sonarURL": "http://localhost:9000/dashboard?id=ai-devsecops-pipeline",
    "buildNumber": "1",
    "jenkinsURL": "http://localhost:8080/job/ai-devsecops-pipeline/1/",
    "repository": "https://github.com/i079024/AI-DevSecOps-Pipeline.git"
  }'
```

---

## 🧪 Testing Methods

### Method 1: Test Mode (One-Time)

**Use when**: Developing/debugging the workflow

1. **Open** the workflow in n8n
2. **Click** "Execute Workflow" button
3. **Immediately send** webhook to test URL:
   ```bash
   curl -X POST http://localhost:5678/webhook-test/jenkins-trigger \
     -H "Content-Type: application/json" \
     -d '{ ... }'
   ```
4. **Check** execution in the workflow canvas

**Note**: Test URL expires after one use or 120 seconds!

---

### Method 2: Production Mode (Permanent)

**Use when**: Ready for real integration

1. **Activate** the workflow (toggle to Active)
2. **Send** webhook to production URL:
   ```bash
   curl -X POST http://localhost:5678/webhook/jenkins-trigger \
     -H "Content-Type: application/json" \
     -d '{ ... }'
   ```
3. **Check** executions in the "Executions" tab

---

## 🔧 Quick Test Script

I've created a script for you:

```bash
# Test with production URL
./test-n8n-webhook-production.sh
```

---

## 📊 Troubleshooting

### Issue: "Webhook not registered" (404)

**Causes**:
1. Using test URL without clicking "Execute Workflow" first
2. Test URL expired (120 seconds timeout)
3. Workflow is not Active

**Fixes**:
- ✅ **Use production URL** instead: `/webhook/jenkins-trigger`
- ✅ **Activate** the workflow (toggle to Active)
- ✅ For test URL: Click "Execute Workflow" before sending

---

### Issue: "Workflow not found"

**Cause**: Workflow is not Active

**Fix**: 
1. Open workflow in n8n
2. Toggle to **Active** (top right)

---

### Issue: "Connection refused" on nodes

**Cause**: MCP wrapper not running or wrong port

**Fix**:
```bash
# Check if MCP wrapper is running
lsof -i :3001

# If not, start it
python3 mcp-http-wrapper.py &
```

---

## 🎯 Recommended Approach

### For Development/Testing:
```bash
# Use production URL with Active workflow
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{ ... your test data ... }'
```

### For Jenkins Integration:
```groovy
// In Jenkinsfile
httpRequest(
    url: 'http://localhost:5678/webhook/jenkins-trigger',
    httpMode: 'POST',
    contentType: 'APPLICATION_JSON',
    requestBody: """{ ... }"""
)
```

---

## ✅ Complete Test

Here's a complete test with your real repository:

```bash
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "cc2619c1ffae990f183bcfadf5dd70e142e36657",
    "branch": "main",
    "author": "sagar.maddali@sap.com",
    "sonarURL": "http://localhost:9000/dashboard",
    "buildNumber": "1",
    "jenkinsURL": "http://localhost:8080/job/test/1/",
    "repository": "https://github.com/i079024/AI-DevSecOps-Pipeline.git"
  }'
```

**Expected response**:
```json
{"message":"Workflow was started"}
```

---

## 📝 Checklist

Before testing, ensure:

- [ ] n8n is running (http://localhost:5678)
- [ ] MCP wrapper is running (port 3001)
- [ ] Workflow is **Active** in n8n
- [ ] Using production URL: `/webhook/jenkins-trigger`
- [ ] GitHub credentials configured in n8n
- [ ] Real commit SHA from your repository

---

## 🔗 URLs Summary

| Type | URL | When to Use |
|------|-----|-------------|
| **n8n UI** | http://localhost:5678 | Access workflows |
| **Test Webhook** | http://localhost:5678/webhook-test/jenkins-trigger | One-time testing |
| **Production Webhook** | http://localhost:5678/webhook/jenkins-trigger | Real integration |
| **MCP Wrapper** | http://localhost:3001 | Backend API |
| **Jenkins** | http://localhost:8080 | CI/CD server |

---

**Use the production webhook URL for reliable testing!** 🚀

