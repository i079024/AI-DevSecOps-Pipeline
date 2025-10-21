# 🔑 n8n GitHub Credentials - Visual Step-by-Step Guide

## Part 1: Create the Credential First

### Step 1: Open n8n Settings

1. **Open n8n**: http://localhost:5678
2. Look at the **left sidebar** (vertical menu)
3. **Click** on the **⚙️ Settings** icon (at the bottom of the sidebar)

---

### Step 2: Go to Credentials

1. In the Settings page, look for tabs at the top
2. **Click** on **"Credentials"** tab

---

### Step 3: Create New Credential

1. **Click** the **"+ Add Credential"** button (top right corner)
2. A modal/dialog will open asking you to select a credential type

---

### Step 4: Select GitHub API

1. In the search box, type: `GitHub`
2. **Click** on **"GitHub API"** from the list
3. A form will appear

---

### Step 5: Fill in the Form

You'll see these fields:

```
┌─────────────────────────────────────────────┐
│ Credential Name                             │
│ [GitHub DevSecOps                  ]        │
│                                             │
│ Authentication Method                       │
│ [Access Token                      ▼]       │
│                                             │
│ Access Token                                │
│ [ghp_your_token_here               ]        │
│                                             │
│ [Test Credential]  [Cancel]  [Save]        │
└─────────────────────────────────────────────┘
```

**Fill in**:
- **Credential Name**: `GitHub DevSecOps`
- **Authentication Method**: `Access Token` (should be default)
- **Access Token**: Paste your GitHub token here

---

### Step 6: Get Your GitHub Token

If you don't have a token yet:

1. **Open new tab**: https://github.com/settings/tokens
2. **Click**: **"Generate new token"** → **"Generate new token (classic)"**
3. **Fill in**:
   - **Note**: `n8n AI DevSecOps`
   - **Expiration**: `90 days` (or your preference)
4. **Check these boxes**:
   - ✅ **repo** (all sub-items)
   - ✅ **workflow**
   - ✅ **write:packages**
5. **Scroll down** and click **"Generate token"**
6. **Copy the token** (starts with `ghp_`)
7. **Go back to n8n** and paste it in the "Access Token" field

---

### Step 7: Save the Credential

1. **Click**: **"Save"** button
2. You should see a success message
3. The credential is now available to use!

---

## Part 2: Assign Credential to the Node

Now that the credential exists, let's add it to the node.

### Step 1: Open Your Workflow

1. **Click** the **"<"** back button to exit Settings
2. **Click** on **"Workflows"** in the left sidebar
3. **Click** on your workflow: **"AI-Enhanced DevSecOps Pipeline"**

---

### Step 2: Click on the Node

1. **Click** on the **"Fetch Git Commit Details"** node (in the canvas)
2. The node configuration panel will open on the right side

---

### Step 3: Find the Credential Field

The GitHub node has several sections. Look for:

```
┌─────────────────────────────────────────────┐
│ Fetch Git Commit Details                    │
├─────────────────────────────────────────────┤
│                                             │
│ Credential to connect with                  │
│ [Select Credential...              ▼]       │  ← HERE!
│                                             │
│ Authentication                              │
│ [Predefined Credential Type        ▼]       │
│                                             │
│ Resource                                    │
│ [Repository                        ▼]       │
│                                             │
│ Operation                                   │
│ [Get Commit                        ▼]       │
│                                             │
│ Owner                                       │
│ [Expression input...               ]        │
│                                             │
│ Repository                                  │
│ [Expression input...               ]        │
│                                             │
│ SHA                                         │
│ [Expression input...               ]        │
│                                             │
└─────────────────────────────────────────────┘
```

---

### Step 4: Select the Credential

1. **Look for**: **"Credential to connect with"** (near the top of the form)
2. **Click** on the dropdown that says **"Select Credential..."**
3. You should see: **"GitHub DevSecOps"** in the list
4. **Click** on **"GitHub DevSecOps"**

---

### If You Don't See "Credential to connect with"

The field might be hidden if "Authentication" is set to something else:

1. **Look for**: **"Authentication"** field
2. Make sure it's set to: **"Predefined Credential Type"**
3. Once you set that, the **"Credential to connect with"** field will appear above it

---

### Step 5: Verify Other Settings

Make sure these are correct:

- **Resource**: `Repository`
- **Operation**: `Get Commit` (or similar)
- **Owner**: Should have an expression like `={{$node["Extract Build Data"].json["repository"].split("/")[3]}}`
- **Repository**: Should have an expression
- **SHA**: Should have an expression like `={{$node["Extract Build Data"].json["commitSHA"]}}`

---

### Step 6: Repeat for Other GitHub Nodes

Do the same for:

1. **"Create GitHub Branch"** node
   - Click the node → Select **"GitHub DevSecOps"** credential

2. **"Create Pull Request"** node
   - Click the node → Select **"GitHub DevSecOps"** credential

---

### Step 7: Save the Workflow

1. **Click** the **"Save"** button (top right corner)
2. Wait for "Workflow saved" confirmation

---

## 🧪 Test the Workflow

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

---

## ❓ Troubleshooting

### Issue: "Credential to connect with" field not visible

**Solution**:
1. Check that **"Authentication"** is set to **"Predefined Credential Type"**
2. If it says "OAuth2", change it to "Predefined Credential Type"

---

### Issue: Dropdown is empty (no credentials to select)

**Solution**:
1. Go back to **Settings** → **Credentials**
2. Make sure you created the **"GitHub DevSecOps"** credential
3. If not, create it (see Part 1 above)

---

### Issue: "GitHub DevSecOps" doesn't appear in dropdown

**Solution**:
1. **Refresh** the n8n page (Cmd + R / Ctrl + R)
2. Open the node again
3. The credential should now appear

---

### Issue: Credential test fails

**Solution**:
1. Go to **Settings** → **Credentials**
2. **Click** on **"GitHub DevSecOps"**
3. **Click** "Test" button
4. If it fails, verify:
   - Token is copied correctly (no extra spaces)
   - Token has the right scopes (`repo`, `workflow`)
   - Token hasn't expired

---

## 📝 Quick Checklist

Before testing:

- [ ] Created GitHub token at https://github.com/settings/tokens
- [ ] Added credential in n8n: Settings → Credentials → Add Credential
- [ ] Named it: "GitHub DevSecOps"
- [ ] Pasted the token
- [ ] Saved the credential
- [ ] Opened the workflow
- [ ] Clicked on "Fetch Git Commit Details" node
- [ ] Found "Credential to connect with" field
- [ ] Selected "GitHub DevSecOps"
- [ ] Repeated for other GitHub nodes
- [ ] Saved the workflow
- [ ] Ready to test!

---

## 🎯 Visual Summary

```
Settings → Credentials → Add Credential → GitHub API
  ↓
Name: GitHub DevSecOps
Token: ghp_xxxxx
  ↓
Save
  ↓
Workflows → Your Workflow → Click Node
  ↓
"Credential to connect with" → Select "GitHub DevSecOps"
  ↓
Save Workflow
  ↓
Test!
```

---

**Need help?** Check if:
1. The credential is created in Settings → Credentials
2. You're looking at the right field ("Credential to connect with" near the top)
3. "Authentication" is set to "Predefined Credential Type"

