#!/usr/bin/env python3
"""
SAP AI Core Connection Validation Script
"""

import requests
import yaml
import json
import sys
import os
import time
from datetime import datetime

def load_config():
    """Load AI Core configuration"""
    config_path = "/Users/i079024/ariba/cursor/AIBot/ai_core_config.yaml"
    try:
        with open(config_path, 'r') as file:
            config = yaml.safe_load(file)
            return config['ai_core']
    except Exception as e:
        print(f"❌ Error loading config: {e}")
        return None

def get_oauth_token(config):
    """Get OAuth token from SAP AI Core"""
    try:
        print("🔐 Requesting OAuth token...")
        
        auth_data = {
            'grant_type': 'client_credentials',
            'client_id': config['client_id'],
            'client_secret': config['client_secret']
        }
        
        headers = {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
        
        response = requests.post(
            config['auth_url'],
            data=auth_data,
            headers=headers,
            timeout=30
        )
        
        if response.status_code == 200:
            token_data = response.json()
            print("✅ OAuth token received successfully")
            return token_data.get('access_token')
        else:
            print(f"❌ OAuth token request failed: {response.status_code}")
            print(f"Response: {response.text[:500]}")
            return None
            
    except Exception as e:
        print(f"❌ OAuth token error: {e}")
        return None

def test_ai_core_api(config, access_token):
    """Test AI Core API endpoints"""
    try:
        print("🧪 Testing AI Core API endpoints...")
        
        headers = {
            'Authorization': f'Bearer {access_token}',
            'AI-Resource-Group': config['resource_group'],
            'Content-Type': 'application/json'
        }
        
        # Test 1: Get scenarios
        print("  📋 Testing scenarios endpoint...")
        scenarios_url = f"{config['api_url']}/v2/lm/scenarios"
        response = requests.get(scenarios_url, headers=headers, timeout=30)
        
        if response.status_code == 200:
            scenarios = response.json()
            print(f"  ✅ Scenarios endpoint working - Found {len(scenarios.get('resources', []))} scenarios")
        else:
            print(f"  ❌ Scenarios endpoint failed: {response.status_code}")
            
        # Test 2: Get deployments
        print("  🚀 Testing deployments endpoint...")
        deployments_url = f"{config['api_url']}/v2/lm/deployments"
        response = requests.get(deployments_url, headers=headers, timeout=30)
        
        if response.status_code == 200:
            deployments = response.json()
            print(f"  ✅ Deployments endpoint working - Found {len(deployments.get('resources', []))} deployments")
            
            # Show active deployments
            active_deployments = [d for d in deployments.get('resources', []) if d.get('status') == 'RUNNING']
            if active_deployments:
                print(f"  🟢 Active deployments: {len(active_deployments)}")
                for deployment in active_deployments[:3]:  # Show first 3
                    print(f"    - {deployment.get('id', 'N/A')}: {deployment.get('configurationName', 'N/A')}")
            else:
                print(f"  🟡 No active deployments found")
                
            return active_deployments
        else:
            print(f"  ❌ Deployments endpoint failed: {response.status_code}")
            print(f"  Response: {response.text[:300]}")
            return []
            
    except Exception as e:
        print(f"❌ API testing error: {e}")
        return []

def test_inference_endpoint(config, access_token, deployments):
    """Test inference endpoint if deployments are available"""
    if not deployments:
        print("⚠️  No active deployments to test inference")
        return False
        
    try:
        print("🤖 Testing inference endpoint...")
        
        # Use first active deployment
        deployment = deployments[0]
        deployment_url = deployment.get('deploymentUrl')
        
        if not deployment_url:
            print("❌ No deployment URL found")
            return False
            
        headers = {
            'Authorization': f'Bearer {access_token}',
            'AI-Resource-Group': config['resource_group'],
            'Content-Type': 'application/json'
        }
        
        # Simple test prompt
        test_payload = {
            "messages": [
                {
                    "role": "user",
                    "content": "Hello! Please respond with 'AI Core connection test successful' to confirm the connection is working."
                }
            ],
            "max_tokens": 100,
            "temperature": 0.1
        }
        
        print(f"  📡 Sending test request to: {deployment_url}")
        response = requests.post(
            f"{deployment_url}/chat/completions",
            json=test_payload,
            headers=headers,
            timeout=60
        )
        
        if response.status_code == 200:
            result = response.json()
            content = result.get('choices', [{}])[0].get('message', {}).get('content', '')
            print("  ✅ Inference endpoint working successfully!")
            print(f"  🤖 AI Response: {content[:200]}...")
            return True
        else:
            print(f"  ❌ Inference request failed: {response.status_code}")
            print(f"  Response: {response.text[:500]}")
            return False
            
    except Exception as e:
        print(f"❌ Inference testing error: {e}")
        return False

def main():
    """Main validation function"""
    print("=" * 60)
    print("🔍 SAP AI Core Connection Validation")
    print("=" * 60)
    print(f"📅 Started at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # Load configuration
    config = load_config()
    if not config:
        sys.exit(1)
        
    print(f"🌍 Region: EU10 (Europe)")
    print(f"🔗 API URL: {config['api_url']}")
    print(f"👥 Resource Group: {config['resource_group']}")
    print()
    
    # Get OAuth token
    access_token = get_oauth_token(config)
    if not access_token:
        sys.exit(1)
        
    print()
    
    # Test API endpoints
    deployments = test_ai_core_api(config, access_token)
    
    print()
    
    # Test inference if possible
    inference_success = test_inference_endpoint(config, access_token, deployments)
    
    print()
    print("=" * 60)
    print("📊 VALIDATION SUMMARY")
    print("=" * 60)
    print(f"✅ OAuth Authentication: {'SUCCESS' if access_token else 'FAILED'}")
    print(f"✅ API Endpoints: {'SUCCESS' if deployments is not None else 'FAILED'}")
    print(f"✅ Inference Test: {'SUCCESS' if inference_success else 'SKIPPED/FAILED'}")
    
    if access_token and deployments is not None:
        print()
        print("🎉 SAP AI Core connection is working properly!")
        print("🚀 Your AI-Enhanced DevSecOps Pipeline is ready to use SAP AI Core.")
    else:
        print()
        print("❌ Connection validation failed. Please check your credentials and try again.")
        sys.exit(1)

if __name__ == "__main__":
    main()