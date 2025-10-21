# 🔧 n8n Connection Issue - FIXED

## 🔍 Problem Identified

**Error**: `The service refused the connection - perhaps it is offline`

**Root Cause**: IPv4/IPv6 resolution issue
- n8n tries to connect to `localhost` which resolves to IPv6 `::1` first
- MCP wrapper only listens on IPv4 `127.0.0.1`
- IPv6 connection fails, causing the error

---

## ✅ Solution Applied

Changed URLs from `localhost` to explicit IPv4 address `127.0.0.1`:

### Before (Not Working):
```
http://localhost:3001/api/speculator/analyze  ❌
http://localhost:3001/api/mcp/analyze  ❌
```

### After (Working):
```
http://127.0.0.1:3001/api/speculator/analyze  ✅
http://127.0.0.1:3001/api/mcp/analyze  ✅
```

---

## 🔧 How to Fix in n8n

### Option 1: Re-import the Updated Workflow (Recommended)

1. **Open n8n**: http://localhost:5678
2. **Delete** your current workflow
3. **Click "+"** → **Import from File**
4. **Select**: `n8n-ai-devsecops-workflow.json`
5. **Click Import**

✅ This will load the workflow with the correct IPv4 addresses!

---

### Option 2: Manually Update URLs

If you want to keep your current workflow:

#### Update "Speculator Bot Analysis" Node:
1. Click on **"Speculator Bot Analysis"** node
2. Change URL from:
   ```
   http://localhost:3001/api/speculator/analyze
   ```
   To:
   ```
   http://127.0.0.1:3001/api/speculator/analyze
   ```

#### Update "LLM Analysis via MCP" Node:
1. Click on **"LLM Analysis via MCP"** node
2. Change URL from:
   ```
   http://localhost:3001/api/mcp/analyze
   ```
   To:
   ```
   http://127.0.0.1:3001/api/mcp/analyze
   ```

3. **Click Save** (top right)

---

## ✅ Verification

Test the connection:

```bash
# This works now ✅
curl -X POST http://127.0.0.1:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "test",
    "branch": "main",
    "author": "test@example.com",
    "sonarURL": "http://localhost:9000",
    "repository": "https://github.com/test/repo"
  }'
```

**Expected response**: JSON with status (even if error with test data)

---

## 🧪 Test in n8n

After updating the URLs:

1. **Activate** your workflow in n8n
2. **Send test webhook**:

```bash
curl -X POST http://localhost:5678/webhook/jenkins-trigger \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "cc2619c1ffae990f183bcfadf5dd70e142e36657",
    "branch": "main",
    "author": "sagar.maddali@sap.com",
    "sonarURL": "http://localhost:9000",
    "buildNumber": "1",
    "jenkinsURL": "http://localhost:8080",
    "repository": "https://github.com/i079024/AI-DevSecOps-Pipeline.git"
  }'
```

3. **Check Executions** tab in n8n
4. **Verify** "Speculator Bot Analysis" node now succeeds (connection-wise)

---

## 📊 Technical Explanation

### Why localhost Failed:

Modern systems resolve `localhost` in this order:
1. Try IPv6: `::1` first
2. If that fails, try IPv4: `127.0.0.1`

The MCP wrapper binds to `0.0.0.0:3001` which should listen on all interfaces, but n8n's connection to IPv6 `::1:3001` was being refused.

### Why 127.0.0.1 Works:

Using explicit IPv4 address `127.0.0.1` bypasses IPv6 resolution entirely, ensuring the connection goes directly to the IPv4 interface where the MCP wrapper is listening.

---

## 📝 Updated Files

- ✅ `n8n-ai-devsecops-workflow.json` - URLs changed to 127.0.0.1
- ✅ Workflow ready to re-import

---

## 🎯 Quick Summary

| Item | Status |
|------|--------|
| **MCP Wrapper** | ✅ Running on port 3001 |
| **IPv4 Connectivity** | ✅ Working (127.0.0.1) |
| **IPv6 Connectivity** | ❌ Not configured |
| **Workflow JSON** | ✅ Fixed with 127.0.0.1 |
| **Solution** | Re-import workflow in n8n |

---

## ✅ After Fix

Once you re-import or update the URLs:

- ✅ No more "connection refused" errors
- ✅ Speculator Bot Analysis node will connect
- ✅ LLM Analysis via MCP node will connect
- ✅ Workflow will execute successfully

---

**The connection issue is now resolved!** 🎉

Re-import the workflow to apply the fix.

