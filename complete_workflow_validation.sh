#!/bin/bash

# Complete Workflow Validation for sagar_2 Branch
# This script validates the entire pipeline from commit to final analysis

echo "🚀 COMPLETE WORKFLOW VALIDATION"
echo "================================"
echo "Repository: i079024/auto-pipeline"
echo "Branch: sagar_2"
echo "Validation Time: $(date)"
echo ""

# Step 1: Pre-validation checks
echo "📋 STEP 1: PRE-VALIDATION CHECKS"
echo "================================="

# Check all required services
echo "🔍 Checking required services..."

# Jenkins
JENKINS_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080)
echo "   Jenkins (port 8080): $([ "$JENKINS_STATUS" = "403" ] && echo "✅ Running" || echo "❌ Not accessible ($JENKINS_STATUS)")"

# n8n
N8N_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:5678)
echo "   n8n (port 5678): $([ "$N8N_STATUS" = "200" ] && echo "✅ Running" || echo "❌ Not accessible ($N8N_STATUS)")"

# MCP HTTP Wrapper
MCP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3001/health)
if [ "$MCP_STATUS" = "200" ]; then
    echo "   MCP Wrapper (port 3001): ✅ Running"
else
    echo "   MCP Wrapper (port 3001): ❌ Not accessible ($MCP_STATUS)"
    echo "   💡 Starting MCP HTTP Wrapper..."
    python3 mcp-http-wrapper.py &
    MCP_PID=$!
    sleep 3
    echo "   🔄 MCP Wrapper started with PID: $MCP_PID"
fi

# SonarQube (optional)
SONAR_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9000)
echo "   SonarQube (port 9000): $([ "$SONAR_STATUS" = "200" ] && echo "✅ Running" || echo "⚠️ Not running ($SONAR_STATUS) - Optional")"

echo ""

# Step 2: Create test commit
echo "📝 STEP 2: CREATE TEST COMMIT"
echo "=============================="

# Generate unique test content
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
TEST_FILE="workflow_test_${TIMESTAMP}.md"

cat > "$TEST_FILE" << EOF
# Workflow Test - $TIMESTAMP

This is a test commit to validate the complete CI/CD pipeline workflow.

## Test Details
- **Branch**: sagar_2
- **Timestamp**: $(date)
- **Test ID**: $TIMESTAMP
- **Purpose**: Validate Jenkins webhook trigger and full pipeline execution

## Expected Workflow
1. Git commit pushed to sagar_2
2. GitHub webhook triggers Jenkins
3. Jenkins executes Jenkinsfile pipeline
4. Jenkins triggers n8n workflow
5. n8n calls MCP HTTP Wrapper
6. Speculator Bot analyzes changes
7. SAP AI Core provides LLM analysis
8. Results stored and notifications sent

## Files Changed
- $TEST_FILE (new file)
- workflow_validation.log (updated)

## Risk Assessment Expected
- Risk Level: LOW (documentation only)
- Files Changed: 1-2
- Security Impact: None
- Test Coverage: Documentation tests
EOF

echo "📄 Created test file: $TEST_FILE"
echo "📋 Content preview:"
head -5 "$TEST_FILE" | sed 's/^/   │ /'

# Add to git
git add "$TEST_FILE"
COMMIT_MSG="Workflow validation test - $TIMESTAMP"
git commit -m "$COMMIT_MSG"

COMMIT_SHA=$(git rev-parse HEAD)
SHORT_SHA=$(git rev-parse --short HEAD)

echo "✅ Test commit created:"
echo "   SHA: $COMMIT_SHA"
echo "   Short: $SHORT_SHA"
echo "   Message: $COMMIT_MSG"

echo ""

# Step 3: Push and monitor webhook
echo "🚀 STEP 3: PUSH AND MONITOR WEBHOOK"
echo "==================================="

echo "📤 Pushing commit to GitHub..."
git push origin sagar_2

if [ $? -eq 0 ]; then
    echo "✅ Commit successfully pushed to sagar_2"
else
    echo "❌ Failed to push commit"
    exit 1
fi

echo ""

# Step 4: Monitor Jenkins response
echo "🏗️  STEP 4: MONITOR JENKINS RESPONSE"
echo "====================================="

echo "⏱️  Waiting for Jenkins webhook (30 seconds)..."
sleep 5

# Check Jenkins webhook endpoint
for i in {1..6}; do
    echo "   🔍 Check $i/6: Testing Jenkins webhook response..."
    
    # Simulate GitHub webhook payload
    WEBHOOK_PAYLOAD=$(cat <<EOF
{
  "ref": "refs/heads/sagar_2",
  "after": "$COMMIT_SHA",
  "repository": {
    "name": "auto-pipeline",
    "full_name": "i079024/auto-pipeline"
  },
  "head_commit": {
    "id": "$COMMIT_SHA",
    "message": "$COMMIT_MSG"
  }
}
EOF
)
    
    WEBHOOK_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" \
        -X POST \
        -H "Content-Type: application/json" \
        -H "X-GitHub-Event: push" \
        -d "$WEBHOOK_PAYLOAD" \
        http://localhost:8080/github-webhook/)
    
    echo "      Jenkins webhook: HTTP $WEBHOOK_RESPONSE $([ "$WEBHOOK_RESPONSE" = "200" ] && echo "✅" || echo "⚠️")"
    
    sleep 5
done

echo ""

# Step 5: Test n8n workflow trigger
echo "🔄 STEP 5: TEST N8N WORKFLOW TRIGGER"
echo "===================================="

echo "🧪 Testing n8n webhook (simulating Jenkins trigger)..."
N8N_PAYLOAD=$(cat <<EOF
{
  "commitSHA": "$COMMIT_SHA",
  "branch": "sagar_2",
  "author": "$(git log --format='%an' -n 1)",
  "buildNumber": "validation-test-$TIMESTAMP"
}
EOF
)

N8N_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d "$N8N_PAYLOAD" \
    http://localhost:5678/webhook/jenkins-trigger)

echo "📋 n8n Response: $N8N_RESPONSE"

if echo "$N8N_RESPONSE" | grep -q "started\|success"; then
    echo "✅ n8n workflow triggered successfully"
else
    echo "⚠️  n8n workflow response unclear"
fi

echo ""

# Step 6: Test MCP Analysis
echo "🤖 STEP 6: TEST MCP ANALYSIS"
echo "============================"

echo "🔍 Testing Speculator Bot analysis..."
MCP_PAYLOAD=$(cat <<EOF
{
  "commitSHA": "$COMMIT_SHA",
  "branch": "sagar_2",
  "author": "$(git log --format='%an' -n 1)"
}
EOF
)

MCP_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d "$MCP_PAYLOAD" \
    http://localhost:3001/api/speculator/analyze)

if echo "$MCP_RESPONSE" | grep -q "success"; then
    echo "✅ MCP Analysis successful"
    
    # Extract key metrics
    RISK_SCORE=$(echo "$MCP_RESPONSE" | grep -o '"deploymentRiskScore":[0-9.]*' | cut -d':' -f2)
    FILES_CHANGED=$(echo "$MCP_RESPONSE" | grep -o '"filesChanged":[0-9]*' | cut -d':' -f2)
    
    echo "📊 Analysis Results:"
    echo "   Risk Score: ${RISK_SCORE:-"N/A"}"
    echo "   Files Changed: ${FILES_CHANGED:-"N/A"}"
else
    echo "⚠️  MCP Analysis response unclear"
    echo "📋 Response preview: $(echo "$MCP_RESPONSE" | head -c 200)..."
fi

echo ""

# Step 7: Validation Summary
echo "📊 STEP 7: VALIDATION SUMMARY"
echo "=============================="

echo "🎯 WORKFLOW VALIDATION RESULTS:"
echo "   ✅ Test commit created and pushed"
echo "   $([ "$JENKINS_STATUS" = "403" ] && echo "✅" || echo "❌") Jenkins service running"
echo "   $([ "$WEBHOOK_RESPONSE" = "200" ] && echo "✅" || echo "⚠️") Jenkins webhook responsive"
echo "   $([ "$N8N_STATUS" = "200" ] && echo "✅" || echo "❌") n8n service running"
echo "   $(echo "$N8N_RESPONSE" | grep -q "started" && echo "✅" || echo "⚠️") n8n workflow triggered"
echo "   $(echo "$MCP_RESPONSE" | grep -q "success" && echo "✅" || echo "⚠️") MCP analysis executed"

echo ""

echo "🔍 MANUAL VERIFICATION CHECKLIST:"
echo "   1. 🌐 Open Jenkins: http://localhost:8080"
echo "   2. 📁 Look for build triggered by commit $SHORT_SHA"
echo "   3. 📊 Check build logs for pipeline execution"
echo "   4. 🔄 Open n8n: http://localhost:5678"
echo "   5. 📈 Check workflow execution history"
echo "   6. 🤖 Verify AI analysis results"

echo ""

echo "✅ COMPLETE WORKFLOW VALIDATION FINISHED"
echo "Commit: $SHORT_SHA | Time: $(date)"
echo "Expected: Jenkins build + n8n workflow + AI analysis within 5-10 minutes"

# Log the validation
cat >> workflow_validation.log << EOF
[$(date)] Workflow validation test completed
Commit: $COMMIT_SHA
Status: Jenkins($JENKINS_STATUS) n8n($N8N_STATUS) MCP($(echo "$MCP_RESPONSE" | grep -q "success" && echo "OK" || echo "WARN"))
EOF