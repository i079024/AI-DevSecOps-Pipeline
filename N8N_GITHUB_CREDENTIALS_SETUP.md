# 🔑 Setting Up GitHub Credentials in n8n

## ✅ Current Progress

You're making great progress! Here's where we are:

| Node | Status |
|------|--------|
| ✅ Jenkins Webhook Trigger | Working |
| ✅ Extract Build Data | Working |
| ✅ **Speculator Bot Analysis** | **NOW WORKING!** 🎉 |
| ❌ Fetch Git Commit Details | **Needs GitHub credentials** |
| 🔄 Remaining nodes | Waiting for credentials |

---

## 🔑 Add GitHub Credentials

### Step 1: Create GitHub Personal Access Token

1. **Go to GitHub**: https://github.com/settings/tokens
2. **Click**: **"Generate new token"** → **"Generate new token (classic)"**
3. **Fill in**:
   - **Note**: `n8n AI DevSecOps Pipeline`
   - **Expiration**: Choose your preferred expiration (90 days or custom)
   
4. **Select scopes** (permissions):
   ```
   ✅ repo (Full control of private repositories)
      ✅ repo:status
      ✅ repo_deployment
      ✅ public_repo
      ✅ repo:invite
   ✅ workflow (Update GitHub Action workflows)
   ✅ write:packages (Upload packages to GitHub Package Registry)
   ```

5. **Click**: **"Generate token"**
6. **Copy the token** immediately (you won't see it again!)
   - It looks like: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

---

### Step 2: Add Credentials to n8n

1. **Open n8n**: http://localhost:5678

2. **Click** on **Settings** (gear icon, bottom left sidebar)

3. **Click**: **"Credentials"**

4. **Click**: **"Add Credential"** (top right)

5. **Search for**: `GitHub`

6. **Select**: **"GitHub API"**

7. **Fill in the form**:
   ```
   Credential Name: GitHub DevSecOps
   Authentication Method: Access Token
   Access Token: [paste your token here]
   ```

8. **Click**: **"Save"**

---

### Step 3: Assign Credentials to Workflow Nodes

1. **Open your workflow** (AI-Enhanced DevSecOps Pipeline)

2. **Click** on the **"Fetch Git Commit Details"** node

3. **In the "Credential to connect with" section**:
   - Select: **"GitHub DevSecOps"** (the credential you just created)

4. **Repeat for other GitHub nodes**:
   - Click on **"Create GitHub Branch"** node → Select **"GitHub DevSecOps"**
   - Click on **"Create Pull Request"** node → Select **"GitHub DevSecOps"**

5. **Click "Save"** (top right)

---

## 🧪 Test After Adding Credentials

Send the webhook again:

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

Then check **Executions** in n8n - the GitHub nodes should now work!

---

## 📊 Expected Results After Fix

Once credentials are added, the workflow will:

1. ✅ Receive webhook
2. ✅ Extract data
3. ✅ **Analyze with Speculator Bot** (returns mock data)
4. ✅ **Fetch commit from GitHub** (will work with credentials)
5. 🔄 Continue to remaining nodes

---

## ⚠️ Troubleshooting

### Issue: "Invalid credentials"

**Cause**: Token doesn't have the right scopes

**Fix**: 
- Recreate the token with `repo`, `workflow`, and `write:packages` scopes
- Update the credential in n8n

---

### Issue: "Rate limit exceeded"

**Cause**: Too many API calls to GitHub

**Fix**: Wait 1 hour or use a different token

---

### Issue: "404 Not Found" on GitHub nodes

**Cause**: Repository name or commit SHA is incorrect

**Fix**: Verify:
- Repository: `https://github.com/i079024/AI-DevSecOps-Pipeline.git`
- Commit SHA: Use a real commit from your repository

---

## 🎯 Quick Summary

**What you need to do**:

1. ✅ Create GitHub token at: https://github.com/settings/tokens
   - Scopes: `repo`, `workflow`, `write:packages`

2. ✅ Add to n8n:
   - Settings → Credentials → Add Credential → GitHub API
   - Name: `GitHub DevSecOps`
   - Paste token

3. ✅ Assign to nodes:
   - "Fetch Git Commit Details"
   - "Create GitHub Branch"
   - "Create Pull Request"

4. ✅ Test workflow again

---

## 📝 Security Note

**Important**: Keep your GitHub token secure!
- Don't share it
- Don't commit it to repositories
- Use expiration dates
- Revoke if compromised

---

## 🎉 Progress

**You've successfully fixed**:
- ✅ Connection issues (IPv4/IPv6)
- ✅ HTTP method issues
- ✅ MCP wrapper error handling
- ✅ Speculator Bot Analysis node

**Next**: Add GitHub credentials and the workflow will be complete!

---

**Total setup time remaining: ~5 minutes** ⏱️

