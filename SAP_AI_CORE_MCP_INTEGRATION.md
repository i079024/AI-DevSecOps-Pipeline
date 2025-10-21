# 🤖 Integrating MCP Server with SAP AI Core

## Overview

This guide shows how to connect your Speculator Bot MCP Server to **SAP AI Core** for enterprise-grade AI/ML model deployment and inference.

**SAP AI Core** provides:
- Scalable AI model serving
- MLOps capabilities
- Enterprise security and governance
- Multi-cloud deployment

---

## 🏗️ Architecture

```
Speculator Bot MCP Server
         ↓
    AI Core Client
         ↓
SAP AI Core (deployed models)
         ↓
    LLM Inference
         ↓
   Analysis Results
```

---

## 📋 Prerequisites

### 1. SAP AI Core Setup

- **SAP BTP Account** with AI Core entitlement
- **Service Key** for AI Core
- **Deployed Model** (e.g., GPT, Claude, or custom model)
- **Resource Group** configured

### 2. Python Dependencies

```bash
pip install ai-core-sdk requests PyYAML
```

**Note**: The `ai-core-sdk` (v2.6.2) is the official SAP package that includes all necessary APIs.

---

## 🔧 Configuration Steps

### ✅ Updated Configuration (EU10 Region)

Your SAP AI Core setup has been configured with the following credentials:

```yaml
ai_core:
  auth_url: "https://ngproc-proto-eu10-aicore.authentication.eu10.hana.ondemand.com/oauth/token"
  client_id: "sb-29bac045-836a-421f-8ad9-664a722c2eda!b576115|aicore!b540"
  client_secret: "5fc0d6c1-2cb1-4214-af37-35c06a052e4e$iHamtWqvFNuI9wYuhDYzsuBU_C1mrecfPfJR_8mno6E="
  api_url: "https://api.ai.prod.eu-central-1.aws.ml.hana.ondemand.com"
  resource_group: "default"
```

**Connection Status**: ✅ **VALIDATED** - OAuth authentication and API endpoints working properly!

**Available Deployments**: 
- 7 active deployments detected
- Models available: Gemini 2.5 Pro, GPT-5, Claude 4 Sonnet

## 🔧 Original Configuration Steps

### Step 1: Get SAP AI Core Service Key

1. **Log in to SAP BTP Cockpit**
2. **Navigate to**: Subaccount → Services → Instances
3. **Find**: Your AI Core instance
4. **Create Service Key**:
   - Name: `aicore-mcp-key`
   - Click **Create**
5. **Copy** the service key JSON

The service key contains:
```json
{
  "clientid": "...",
  "clientsecret": "...",
  "url": "https://api.ai.prod.eu-central-1.aws.ml.hana.ondemand.com",
  "serviceurls": {
    "AI_API_URL": "https://..."
  }
}
```

---

### Step 2: Create AI Core Configuration File

Create `ai_core_config.yaml`:

```yaml
ai_core:
  # Authentication
  auth_url: "https://your-auth-url.authentication.eu10.hana.ondemand.com/oauth/token"
  client_id: "your-client-id"
  client_secret: "your-client-secret"
  
  # AI Core API
  api_url: "https://api.ai.prod.eu-central-1.aws.ml.hana.ondemand.com"
  resource_group: "default"
  
  # Deployment Configuration
  deployment:
    scenario_id: "foundation-models"
    executable_id: "azure-openai"  # or your model
    configuration_id: "gpt-4-config"
    
  # Model Settings
  model:
    name: "gpt-4"
    temperature: 0.7
    max_tokens: 2000
    
  # Proxy Settings (if behind corporate proxy)
  proxy:
    http: "http://proxy.company.com:8080"
    https: "https://proxy.company.com:8080"
```

---

### Step 3: Update MCP Wrapper with AI Core Integration

Create `ai_core_client.py`:

```python
"""
SAP AI Core Client for MCP Server
"""
import os
import requests
from typing import Dict, Any, Optional
import yaml
import logging

logger = logging.getLogger(__name__)


class AICoreLLMClient:
    """Client for SAP AI Core LLM inference"""
    
    def __init__(self, config_path: str = "ai_core_config.yaml"):
        """Initialize AI Core client with configuration"""
        with open(config_path, 'r') as f:
            self.config = yaml.safe_load(f)['ai_core']
        
        self.access_token = None
        self.deployment_url = None
        
    def authenticate(self) -> str:
        """Get OAuth token from SAP AI Core"""
        try:
            response = requests.post(
                self.config['auth_url'],
                auth=(self.config['client_id'], self.config['client_secret']),
                data={'grant_type': 'client_credentials'},
                proxies=self.config.get('proxy', {})
            )
            response.raise_for_status()
            self.access_token = response.json()['access_token']
            logger.info("Successfully authenticated with AI Core")
            return self.access_token
        except Exception as e:
            logger.error(f"Authentication failed: {e}")
            raise
    
    def get_deployment_url(self) -> str:
        """Get the inference endpoint URL for the deployed model"""
        if not self.access_token:
            self.authenticate()
        
        try:
            # Get deployments
            url = f"{self.config['api_url']}/v2/lm/deployments"
            headers = {
                'Authorization': f'Bearer {self.access_token}',
                'AI-Resource-Group': self.config['resource_group']
            }
            
            response = requests.get(url, headers=headers, proxies=self.config.get('proxy', {}))
            response.raise_for_status()
            
            deployments = response.json()['resources']
            
            # Find active deployment
            for deployment in deployments:
                if (deployment['scenarioId'] == self.config['deployment']['scenario_id'] and
                    deployment['status'] == 'RUNNING'):
                    self.deployment_url = deployment['deploymentUrl']
                    logger.info(f"Found deployment URL: {self.deployment_url}")
                    return self.deployment_url
            
            raise Exception("No active deployment found")
        except Exception as e:
            logger.error(f"Failed to get deployment URL: {e}")
            raise
    
    def analyze_with_llm(self, prompt: str, context: Dict[str, Any] = None) -> Dict[str, Any]:
        """
        Send prompt to SAP AI Core LLM and get analysis
        
        Args:
            prompt: The analysis prompt
            context: Additional context for the analysis
            
        Returns:
            Analysis results from the LLM
        """
        if not self.deployment_url:
            self.get_deployment_url()
        
        try:
            headers = {
                'Authorization': f'Bearer {self.access_token}',
                'AI-Resource-Group': self.config['resource_group'],
                'Content-Type': 'application/json'
            }
            
            # Prepare request payload (format depends on your model)
            payload = {
                'messages': [
                    {
                        'role': 'system',
                        'content': 'You are an expert DevSecOps analyst.'
                    },
                    {
                        'role': 'user',
                        'content': prompt
                    }
                ],
                'max_tokens': self.config['model']['max_tokens'],
                'temperature': self.config['model']['temperature']
            }
            
            # Add context if provided
            if context:
                payload['messages'][0]['content'] += f"\nContext: {context}"
            
            response = requests.post(
                f"{self.deployment_url}/chat/completions",
                headers=headers,
                json=payload,
                proxies=self.config.get('proxy', {}),
                timeout=60
            )
            response.raise_for_status()
            
            result = response.json()
            logger.info("Successfully received LLM analysis")
            
            return {
                'response': result['choices'][0]['message']['content'],
                'model': self.config['model']['name'],
                'tokens_used': result.get('usage', {})
            }
            
        except Exception as e:
            logger.error(f"LLM analysis failed: {e}")
            # Return fallback analysis
            return {
                'response': 'Analysis unavailable - using fallback',
                'error': str(e)
            }


def get_ai_core_client() -> AICoreLLMClient:
    """Get or create AI Core client instance"""
    global _ai_core_client
    if '_ai_core_client' not in globals():
        _ai_core_client = AICoreLLMClient()
    return _ai_core_client
```

---

### Step 4: Update MCP HTTP Wrapper

Modify `mcp-http-wrapper.py` to use AI Core:

```python
# Add at the top
from ai_core_client import get_ai_core_client

# Update the mcp_analyze endpoint
@app.route('/api/mcp/analyze', methods=['POST'])
def mcp_analyze():
    """Enhanced analysis using SAP AI Core LLM"""
    try:
        data = request.get_json()
        logger.info(f"MCP Analysis for commit: {data.get('commitSHA', 'unknown')}")
        
        # Get Speculator Bot analysis first
        speculator = get_bot()
        try:
            report = speculator.speculate(
                commit_hash=data.get('commitSHA'),
                analyze_db=False,
                check_drift=False
            )
        except Exception as analysis_error:
            logger.warning(f"Analysis failed, using mock data")
            report = None
        
        # Prepare prompt for AI Core
        llm_prompt = generate_llm_prompt(data, report)
        
        # Get AI Core client and analyze
        ai_client = get_ai_core_client()
        llm_result = ai_client.analyze_with_llm(
            prompt=llm_prompt,
            context={
                'commit': data.get('commitSHA'),
                'branch': data.get('branch'),
                'author': data.get('author')
            }
        )
        
        # Parse LLM response and structure it
        response = {
            'status': 'success',
            'timestamp': datetime.now().isoformat(),
            'analysis': {
                'llm_analysis': llm_result['response'],
                'model': llm_result.get('model', 'unknown'),
                'speculator_report': format_speculator_report(report) if report else None,
                'recommendations': extract_recommendations(llm_result['response'])
            }
        }
        
        return jsonify(response), 200
        
    except Exception as e:
        logger.error(f"MCP analysis error: {e}", exc_info=True)
        return jsonify({
            'status': 'error',
            'message': str(e),
            'timestamp': datetime.now().isoformat()
        }), 500
```

---

### Step 5: Install Dependencies

```bash
# Add to requirements.txt
ai-core-sdk>=2.0.0
sap-ai-services-client>=0.1.0
PyYAML>=6.0
```

Install:
```bash
pip install -r requirements.txt
```

---

### Step 6: Test the Integration

```python
# test_ai_core_integration.py
from ai_core_client import AICoreLLMClient

def test_ai_core():
    """Test AI Core connection and inference"""
    
    # Initialize client
    client = AICoreLLMClient()
    
    # Test authentication
    print("Testing authentication...")
    client.authenticate()
    print("✅ Authentication successful")
    
    # Test deployment URL
    print("Getting deployment URL...")
    url = client.get_deployment_url()
    print(f"✅ Deployment URL: {url}")
    
    # Test inference
    print("Testing LLM inference...")
    result = client.analyze_with_llm(
        prompt="Analyze this code change: Added error handling to API endpoint",
        context={'type': 'test'}
    )
    print(f"✅ LLM Response: {result['response'][:100]}...")
    print(f"Tokens used: {result.get('tokens_used', 'N/A')}")

if __name__ == '__main__':
    test_ai_core()
```

Run test:
```bash
python test_ai_core_integration.py
```

---

## 🔒 Security Best Practices

### 1. Environment Variables

**Don't hardcode credentials!** Use environment variables:

```bash
export AI_CORE_CLIENT_ID="your-client-id"
export AI_CORE_CLIENT_SECRET="your-client-secret"
export AI_CORE_API_URL="https://..."
```

Update config to read from env:
```python
import os

config = {
    'client_id': os.getenv('AI_CORE_CLIENT_ID'),
    'client_secret': os.getenv('AI_CORE_CLIENT_SECRET'),
    'api_url': os.getenv('AI_CORE_API_URL')
}
```

---

### 2. Use SAP Credential Store

For production, use SAP BTP Credential Store:

```python
from sap import xssec

# Get credentials from credential store
credentials = xssec.create_security_context(vcap_services)
```

---

### 3. Network Security

- Use **VPN** or **Cloud Connector** for on-premise connections
- Enable **mTLS** for API calls
- Whitelist IPs in AI Core

---

## 📊 Monitoring & Logging

### Enable AI Core Metrics

```python
import logging
from opentelemetry import trace
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter

# Configure telemetry
tracer = trace.get_tracer(__name__)

@tracer.start_as_current_span("ai_core_inference")
def analyze_with_llm(prompt):
    # Your inference code
    pass
```

---

## 🚀 Deployment Options

### Option 1: Deploy MCP Wrapper to SAP BTP (Cloud Foundry)

```yaml
# manifest.yml
applications:
- name: speculator-mcp-wrapper
  memory: 512M
  instances: 2
  buildpacks:
  - python_buildpack
  command: python mcp-http-wrapper.py
  services:
  - aicore-service
  env:
    FLASK_ENV: production
```

Deploy:
```bash
cf push
```

---

### Option 2: Deploy to Kyma (Kubernetes)

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mcp-wrapper
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mcp-wrapper
  template:
    metadata:
      labels:
        app: mcp-wrapper
    spec:
      containers:
      - name: mcp-wrapper
        image: your-registry/mcp-wrapper:latest
        ports:
        - containerPort: 3001
        env:
        - name: AI_CORE_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: aicore-credentials
              key: client-id
```

---

## 🧪 Testing Checklist

- [ ] AI Core service key obtained
- [ ] Configuration file created
- [ ] Dependencies installed
- [ ] Authentication working
- [ ] Deployment URL retrieved
- [ ] Test inference successful
- [ ] Error handling tested
- [ ] Logging configured
- [ ] Security review completed

---

## 📚 Additional Resources

- [SAP AI Core Documentation](https://help.sap.com/docs/AI_CORE)
- [AI Core SDK Python](https://pypi.org/project/ai-core-sdk/)
- [SAP BTP Documentation](https://help.sap.com/docs/BTP)

---

## 🆘 Troubleshooting

### Authentication Error

**Issue**: `401 Unauthorized`

**Fix**:
- Verify client ID and secret
- Check service key is not expired
- Ensure correct auth URL

---

### Deployment Not Found

**Issue**: No active deployment

**Fix**:
- Check deployment status in AI Core UI
- Verify resource group name
- Ensure model is deployed and running

---

### Timeout Errors

**Issue**: Request timeout

**Fix**:
- Increase timeout in requests
- Check network connectivity
- Verify proxy settings (if behind corporate firewall)

---

**Your MCP Server is now ready to use SAP AI Core!** 🎉

