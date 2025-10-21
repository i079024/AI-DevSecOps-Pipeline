# ✅ n8n Connection Issue - FIXED

## 🔍 Problem Identified

**Error**: `The service refused the connection - perhaps it is offline`

**Root Cause**: The n8n workflow was trying to connect to port **3000**, but the MCP HTTP Wrapper runs on port **3001**.

---

## ✅ What Was Fixed

1. ✅ **Stopped duplicate MCP wrapper processes**
2. ✅ **Restarted MCP wrapper on correct port (3001)**
3. ✅ **Updated workflow JSON** to use port 3001
4. ✅ **Updated all documentation** with correct port

---

## 🔧 What You Need to Do in n8n

### Option 1: Re-import the Updated Workflow (Recommended)

1. **Open n8n**: http://localhost:5678
2. **Delete** the old workflow (if any)
3. **Click** "+" → **Import from File**
4. **Select**: `n8n-ai-devsecops-workflow.json`
5. **Click**: Import

---

### Option 2: Manually Update URLs in Existing Workflow

If you want to keep your existing workflow:

#### Update "Speculator Bot Analysis" Node:
1. Click on **"Speculator Bot Analysis"** node
2. Change URL from:
   ```
   http://localhost:3000/api/speculator/analyze
   ```
   To:
   ```
   http://localhost:3001/api/speculator/analyze
   ```

#### Update "LLM Analysis via MCP" Node:
1. Click on **"LLM Analysis via MCP"** node
2. Change URL from:
   ```
   http://localhost:3000/api/mcp/analyze
   ```
   To:
   ```
   http://localhost:3001/api/mcp/analyze
   ```

3. **Click Save** (top right)

---

## ✅ Verify It's Working

### Step 1: Check MCP Wrapper Status

```bash
lsof -i :3001
```

You should see:
```
Python  98838 ... TCP *:redwood-broker (LISTEN)
```

---

### Step 2: Test the Connection

```bash
curl -X POST http://localhost:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "commitSHA": "test123",
    "branch": "main",
    "author": "test@example.com",
    "sonarURL": "http://localhost:9000",
    "repository": "https://github.com/test/repo.git"
  }'
```

You should get a JSON response (not a connection error).

---

### Step 3: Test in n8n

1. **Open** your workflow in n8n
2. **Click** on "Speculator Bot Analysis" node
3. **Click** "Execute Node"
4. You should see output (not connection error)

---

## 📊 Current Service Status

| Service | Status | Port | URL |
|---------|--------|------|-----|
| **n8n** | ✅ Running | 5678 | http://localhost:5678 |
| **MCP Wrapper** | ✅ Running | 3001 | http://localhost:3001 |
| **Jenkins** | ✅ Running | 8080 | http://localhost:8080 |

---

## 🎯 Quick Test Command

Test that n8n can now reach the MCP wrapper:

```bash
# This should return JSON (may have errors with test data, but proves connection works)
curl -X POST http://localhost:3001/api/speculator/analyze \
  -H "Content-Type: application/json" \
  -d '{"commitSHA":"test","branch":"main","author":"test@example.com","sonarURL":"http://localhost:9000","repository":"https://github.com/test/repo.git"}'
```

---

## 📝 Updated Files

- ✅ `n8n-ai-devsecops-workflow.json` - Updated ports to 3001
- ✅ `N8N_CONFIGURATION_GUIDE.md` - Updated documentation
- ✅ MCP wrapper restarted on correct port

---

## 🚀 Next Steps

1. **Re-import the workflow** in n8n (or update URLs manually)
2. **Add GitHub credentials** to the workflow nodes
3. **Activate** the workflow
4. **Test** by executing a node

---

## 🐛 If You Still Get Connection Errors

### Check if MCP wrapper is running:
```bash
ps aux | grep mcp-http-wrapper
```

### Restart it if needed:
```bash
cd /Users/i079024/ariba/cursor/AIBot
python3 mcp-http-wrapper.py &
```

### Verify port 3001 is listening:
```bash
lsof -i :3001
```

---

**Your n8n workflow should now connect successfully!** 🎉

The issue was simply a port mismatch - everything is configured correctly now.

