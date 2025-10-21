# 🔧 n8n Webhook "Not Registered" Error - Troubleshooting

## 🔍 Problem

**Error**: `The requested webhook "POST jenkins-trigger" is not registered`

**Even though**: The workflow appears active in the UI

---

## ✅ **Solutions** (Try in order)

### Solution 1: Deactivate and Reactivate

1. **Open n8n**: http://localhost:5678
2. **Open** your workflow
3. **Toggle OFF** the workflow (top right switch)
4. **Wait 5 seconds**
5. **Toggle ON** the workflow again
6. **Wait for** the green "Active" indicator
7. **Test** the webhook

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

### Solution 2: Check Webhook Node Configuration

1. **Click** on the **"Jenkins Webhook Trigger"** node
2. **Verify** these settings:
   ```
   HTTP Method: POST
   Path: jenkins-trigger
   Response Mode: On Received
   ```
3. **Important**: The path should be **exactly** `jenkins-trigger` (no leading slash)
4. **Save** the workflow
5. **Deactivate** and **Reactivate**

---

### Solution 3: Restart n8n

Sometimes n8n needs a restart to register webhooks properly:

```bash
# Stop n8n
pkill -f "n8n start"

# Start n8n again
n8n start &

# Wait 10 seconds
sleep 10

# Check if n8n is running
curl http://localhost:5678/healthz
```

---

### Solution 4: Check the Webhook Path

The webhook path should be WITHOUT the leading slash:

**Correct in node**:
```json
{
  "path": "jenkins-trigger"  ← No leading slash
}
```

**Correct URL to call**:
```
http://localhost:5678/webhook/jenkins-trigger  ← Has /webhook/ prefix
```

---

### Solution 5: Verify Workflow is Saved

1. **Make a small change** to the workflow (move a node slightly)
2. **Click Save** (top right)
3. **Wait** for "Workflow saved" message
4. **Deactivate** and **Reactivate**

---

## 🔍 **Debugging Steps**

### Check Active Workflows

In n8n:
1. **Click** "Workflows" (left sidebar)
2. **Look for** green "Active" badge on your workflow
3. **If not active**: Open it and toggle to Active

---

### Check Webhook Registration

Look at the webhook node:
1. **Click** on "Jenkins Webhook Trigger" node
2. **Scroll down** to see the **Production URL**
3. **Copy** that exact URL
4. **Use that URL** in your curl command

---

### Check for Errors

1. **Open** the workflow
2. **Look** at the bottom of the screen for any error messages
3. **Check** that all nodes are properly connected

---

## 🧪 **Test with Simple Workflow**

Create a minimal test workflow:

1. **Create new workflow**
2. **Add** a Webhook node
3. **Configure**:
   - HTTP Method: POST
   - Path: test-webhook
4. **Add** a "Set" node to return data
5. **Connect** them
6. **Save** and **Activate**
7. **Test**:

```bash
curl -X POST http://localhost:5678/webhook/test-webhook \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}'
```

If this works, the issue is with your main workflow configuration.

---

## 🔧 **Common Issues**

### Issue: Path has leading slash

**Wrong**: `/jenkins-trigger`  
**Correct**: `jenkins-trigger`

---

### Issue: Wrong URL format

**Wrong**: `http://localhost:5678/jenkins-trigger`  
**Correct**: `http://localhost:5678/webhook/jenkins-trigger`

---

### Issue: Workflow not saved

- Always click **Save** after making changes
- Wait for confirmation message
- Then toggle Active

---

### Issue: Multiple workflows with same webhook path

- Each webhook path must be unique
- Check if you have multiple workflows using `jenkins-trigger`
- Delete or rename duplicates

---

## 🎯 **Step-by-Step Fix**

1. **Deactivate** workflow
2. **Click** on "Jenkins Webhook Trigger" node
3. **Verify** path is: `jenkins-trigger` (no leading slash)
4. **Click Save**
5. **Wait** 5 seconds
6. **Toggle Active**
7. **Wait** for green indicator
8. **Test** with curl

---

## 📝 **Webhook Node Settings**

Make sure your webhook node has these EXACT settings:

```json
{
  "httpMethod": "POST",
  "path": "jenkins-trigger",
  "responseMode": "onReceived",
  "options": {}
}
```

---

## ✅ **Expected Behavior**

When the workflow is active and properly registered:

**Success Response**:
```json
{"message":"Workflow was started"}
```

**Not** a 404 error!

---

## 🆘 **If Nothing Works**

1. **Export** your workflow (Download as JSON)
2. **Delete** the workflow in n8n
3. **Restart** n8n
4. **Import** the workflow again
5. **Configure** credentials
6. **Activate** and test

---

## 📊 **Quick Checklist**

- [ ] Workflow shows "Active" badge
- [ ] Webhook path is `jenkins-trigger` (no slash)
- [ ] Workflow is saved (saw confirmation)
- [ ] Waited 5+ seconds after activating
- [ ] Using correct URL: `/webhook/jenkins-trigger`
- [ ] No duplicate workflows with same path
- [ ] All nodes properly connected
- [ ] n8n is running (check http://localhost:5678)

---

**Most common fix**: Deactivate → Wait 5 seconds → Activate → Test

