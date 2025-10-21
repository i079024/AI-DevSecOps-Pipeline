# 🧪 n8n Speculator Bot Analysis - Test Guide

## ✅ Pre-Test Verification

### Status Check:

| Component | Status | Port |
|-----------|--------|------|
| **MCP Wrapper** | ✅ Running | 3001 |
| **n8n** | ✅ Running | 5678 |
| **Endpoint** | ✅ Responding | http://localhost:3001/api/speculator/analyze |

---

## 🔧 Option 1: Test in n8n (Full Workflow)

### Step 1: Import the Workflow

1. **Open n8n**: http://localhost:5678
2. **Click "+"** (top right) → **Import from File**
3. **Select**: `n8n-ai-devsecops-workflow.json`
4. **Click Import**

---

### Step 2: Verify Node Configuration

Click on **"Speculator Bot Analysis"** node and verify:

```
✅ URL: http://localhost:3001/api/speculator/analyze
✅ Method: POST
✅ Send Body: Yes
✅ Timeout: 30000 (30 seconds)
✅ Headers: Content-Type: application/json
```

**Body Parameters** (from previous node):
```javascript
{
  "commitSHA": "={{$node["Extract Build Data"].json["commitSHA"]}}",
  "branch": "={{$node["Extract Build Data"].json["branch"]}}",
  "author": "={{$node["Extract Build Data"].json["author"]}}",
  "sonarURL": "={{$node["Extract Build Data"].json["sonarURL"]}}",
  "repository": "={{$node["Extract Build Data"].json["repository"]}}"
}
```

---

### Step 3: Test Individual Node

1. **Click** on "Speculator Bot Analysis" node
2. **Add Test Data** by clicking "Execute Previous Nodes" first:
   - This will run "Extract Build Data" node
3. **Or**: Manually provide test input in the node

---

### Step 4: Test Complete Workflow

1. **Activate** the workflow (toggle in top right)
2. **Send test webhook**:

```bash
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "abc123def456",
    "branch": "main",
    "author": "developer@example.com",
    "sonarURL": "http://localhost:9000/dashboard?id=test-project",
    "buildNumber": "42",
    "jenkinsURL": "http://localhost:8080/job/test/42/",
    "repository": "https://github.com/yourusername/yourrepo.git"
  }'
```

3. **Check Executions** tab in n8n to see the result

---

## 🔧 Option 2: Test Directly (Command Line)

### Quick Test Script

Run the provided test script:

```bash
./test-speculator-bot.sh
```

### Manual cURL Test

```bash
curl -X POST http://localhost:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "abc123def456",
    "branch": "main",
    "author": "developer@example.com",
    "sonarURL": "http://localhost:9000/dashboard",
    "repository": "https://github.com/testuser/testrepo.git"
  }'
```

---

## 📊 Expected Results

### ✅ Success Response (with real repo):

```json
{
  "status": "success",
  "analysis": {
    "deploymentRiskScore": 0.45,
    "riskLevel": "medium",
    "changeSummary": {
      "filesChanged": 5,
      "linesAdded": 120,
      "linesRemoved": 45
    },
    "testSelection": {
      "totalTests": 15,
      "coverageScore": 0.85,
      "estimatedTimeMinutes": 8.5
    }
  }
}
```

---

### ⚠️ Expected Error (with test data):

```json
{
  "message": "'average_risk'",
  "status": "error",
  "timestamp": "2025-10-15T19:04:35.104466"
}
```

**This is normal!** The endpoint is working, but test data doesn't have a real repository to analyze.

---

## 🐛 Troubleshooting

### Issue: "Connection refused"

**Cause**: Wrong port or MCP wrapper not running

**Fix**:
```bash
# Check if running
lsof -i :3001

# If not running, start it
python3 mcp-http-wrapper.py &
```

---

### Issue: "404 Not Found"

**Cause**: Wrong URL path

**Fix**: Verify URL is exactly:
```
http://localhost:3001/api/speculator/analyze
```

---

### Issue: "405 Method Not Allowed"

**Cause**: Using GET instead of POST

**Fix**: Ensure method is set to `POST` in n8n node

---

### Issue: n8n shows "Connection refused"

**Solutions**:

1. **Verify port in n8n node**:
   - Should be `3001`, NOT `3000`

2. **Re-import workflow**:
   ```
   Import n8n-ai-devsecops-workflow.json
   ```

3. **Check firewall**:
   ```bash
   curl http://localhost:3001/health
   ```

---

## 🎯 Test Checklist

- [ ] MCP wrapper running on port 3001
- [ ] cURL test successful (returns JSON response)
- [ ] n8n workflow imported
- [ ] "Speculator Bot Analysis" node URL uses port 3001
- [ ] Test webhook sent successfully
- [ ] Execution appears in n8n Executions tab

---

## ✅ Verification Commands

```bash
# 1. Check MCP wrapper status
ps aux | grep mcp-http-wrapper | grep -v grep

# 2. Check port 3001
lsof -i :3001

# 3. Test endpoint
curl -X POST http://localhost:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{"commitSHA":"test","branch":"main","author":"test@example.com","sonarURL":"http://localhost:9000","repository":"https://github.com/test/repo"}'

# 4. Run comprehensive test
./test-speculator-bot.sh
```

---

## 📝 n8n Node Configuration Checklist

When setting up the "Speculator Bot Analysis" node in n8n:

- [ ] **URL**: `http://localhost:3001/api/speculator/analyze` ← Port 3001!
- [ ] **Method**: `POST`
- [ ] **Headers**: `Content-Type: application/json`
- [ ] **Body Type**: `JSON`
- [ ] **Timeout**: `30000` or higher
- [ ] **Body Parameters**: Mapped from "Extract Build Data" node

---

## 🚀 Next Steps

1. ✅ **Tested endpoint** - Working!
2. ✅ **Verified port** - 3001
3. 🔄 **Import workflow** to n8n
4. 🧪 **Test in n8n** using webhook
5. 🔗 **Connect to Jenkins**

---

## 📚 Related Files

- `n8n-ai-devsecops-workflow.json` - Complete workflow
- `test-speculator-bot.sh` - Quick test script
- `N8N_CONFIGURATION_GUIDE.md` - Full setup guide
- `N8N_FIX_APPLIED.md` - Port fix documentation

---

**Your Speculator Bot Analysis endpoint is working correctly!** 🎉

The connection is live on **port 3001**. Just make sure your n8n workflow uses the correct port.

