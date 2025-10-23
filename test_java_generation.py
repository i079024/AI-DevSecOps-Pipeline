#!/usr/bin/env python3
"""
Test script to validate Java code generation fixes
"""

import json
import requests
import time

def test_java_code_generation():
    """Test the Java code generation with the fixes"""
    
    # Simulate a test payload with problematic filenames
    test_payload = {
        "ref": "refs/heads/sagar_2",
        "repository": {
            "full_name": "i079024/auto-pipeline",
            "clone_url": "https://github.com/i079024/auto-pipeline.git"
        },
        "head_commit": {
            "id": "a91c07fdcb0a15cb2344b9e19df969e22667133f",
            "message": "Java code generation test",
            "author": {
                "name": "i079024"
            },
            "added": ["pom.xml", "src/main/java/com/example/Test-File.java"],
            "modified": ["config.yaml", "app.properties"],
            "removed": []
        }
    }
    
    # Test n8n workflow trigger (local instance)
    n8n_webhook_url = "http://localhost:5678/webhook/jenkins-trigger"
    
    print("🧪 Testing Java Code Generation Fixes")
    print("=" * 50)
    print(f"Repository: {test_payload['repository']['full_name']}")
    print(f"Branch: {test_payload['ref'].split('/')[-1]}")
    print(f"Commit: {test_payload['head_commit']['id'][:7]}")
    print(f"Files that should generate proper Java classes:")
    
    for file in test_payload['head_commit']['added'] + test_payload['head_commit']['modified']:
        print(f"  - {file}")
    
    print(f"\n🔗 Triggering n8n workflow...")
    
    try:
        response = requests.post(
            n8n_webhook_url,
            json=test_payload,
            headers={'Content-Type': 'application/json'},
            timeout=30
        )
        
        print(f"✅ Webhook Response: HTTP {response.status_code}")
        print(f"📋 Response Body: {response.text}")
        
        if response.status_code == 200:
            print(f"\n🎯 Expected Java Classes to be Generated:")
            print(f"  - PomXmlTest.java (from pom.xml)")
            print(f"  - TestFileTest.java (from Test-File.java)")
            print(f"  - ConfigYamlTest.java (from config.yaml)")
            print(f"  - AppPropertiesTest.java (from app.properties)")
            
            print(f"\n⏰ Monitor n8n execution for next 2-3 minutes")
            print(f"🔍 Check GitHub for new branch and PR creation")
            
        return response.status_code == 200
        
    except requests.exceptions.RequestException as e:
        print(f"❌ Webhook request failed: {e}")
        return False

if __name__ == "__main__":
    success = test_java_code_generation()
    
    if success:
        print(f"\n✅ Java code generation test initiated successfully!")
        print(f"📊 Next steps:")
        print(f"  1. Monitor n8n execution dashboard")
        print(f"  2. Check for new branch creation in GitHub")
        print(f"  3. Verify generated Java files have proper class names")
        print(f"  4. Confirm no compilation errors in generated code")
    else:
        print(f"\n❌ Test failed - check n8n webhook configuration")