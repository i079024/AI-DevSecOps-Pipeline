#!/usr/bin/env python3
"""
Comprehensive SAP AI Core Integration Test Script
"""

import requests
import json
import time
from datetime import datetime

def test_endpoint(url, method='GET', data=None, expected_status=200):
    """Test an endpoint and return result"""
    try:
        if method == 'GET':
            response = requests.get(url, timeout=30)
        elif method == 'POST':
            response = requests.post(url, json=data, headers={'Content-Type': 'application/json'}, timeout=30)
        
        success = response.status_code == expected_status
        result = {
            'url': url,
            'method': method,
            'status_code': response.status_code,
            'success': success,
            'response': response.json() if response.headers.get('content-type', '').startswith('application/json') else response.text[:200]
        }
        return result
    except Exception as e:
        return {
            'url': url,
            'method': method,
            'success': False,
            'error': str(e)
        }

def main():
    print("=" * 80)
    print("🧪 COMPREHENSIVE SAP AI CORE INTEGRATION TEST")
    print("=" * 80)
    print(f"⏰ Started: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    base_url = "http://127.0.0.1:3001"
    
    tests = [
        {
            'name': '🏥 Health Check',
            'test': lambda: test_endpoint(f"{base_url}/health")
        },
        {
            'name': '🤖 SAP AI Core Connection Test',
            'test': lambda: test_endpoint(f"{base_url}/api/ai-core/test")
        },
        {
            'name': '📊 Speculator Analysis (Mock Data)',
            'test': lambda: test_endpoint(
                f"{base_url}/api/speculator/analyze",
                method='POST',
                data={
                    "commitSHA": "test123abc",
                    "branch": "main",
                    "author": "test@example.com",
                    "repository": "https://github.com/test/repo.git"
                }
            )
        },
        {
            'name': '🧠 MCP Analysis (Mock Data)',
            'test': lambda: test_endpoint(
                f"{base_url}/api/mcp/analyze",
                method='POST',
                data={
                    "commitSHA": "test123abc",
                    "codeDiff": [],
                    "sonarURL": "http://localhost:9000/dashboard?id=test",
                    "context": {
                        "branch": "main",
                        "author": "test@example.com"
                    }
                }
            )
        }
    ]
    
    results = []
    
    for i, test_case in enumerate(tests, 1):
        print(f"{i}. {test_case['name']}...")
        result = test_case['test']()
        results.append(result)
        
        if result['success']:
            print(f"   ✅ SUCCESS ({result['status_code']})")
            if 'response' in result and isinstance(result['response'], dict):
                if 'status' in result['response']:
                    print(f"   📋 Status: {result['response']['status']}")
                if 'message' in result['response']:
                    print(f"   💬 Message: {result['response']['message']}")
                if 'details' in result['response']:
                    details = result['response']['details']
                    if isinstance(details, dict):
                        for key, value in details.items():
                            if key == 'deployment_models' and isinstance(value, list):
                                print(f"   🎯 Models: {', '.join(value[:3])}{'...' if len(value) > 3 else ''}")
                            elif not isinstance(value, (list, dict)):
                                print(f"   📈 {key.title().replace('_', ' ')}: {value}")
        else:
            print(f"   ❌ FAILED ({result.get('status_code', 'N/A')})")
            if 'error' in result:
                print(f"   🔴 Error: {result['error']}")
        print()
    
    # Summary
    print("=" * 80)
    print("📊 TEST SUMMARY")
    print("=" * 80)
    
    successful_tests = [r for r in results if r['success']]
    failed_tests = [r for r in results if not r['success']]
    
    print(f"✅ Successful Tests: {len(successful_tests)}/{len(results)}")
    print(f"❌ Failed Tests: {len(failed_tests)}/{len(results)}")
    print()
    
    if len(successful_tests) == len(results):
        print("🎉 ALL TESTS PASSED!")
        print("🚀 Your SAP AI Core integration is fully operational!")
        print()
        print("🔗 Available Endpoints:")
        print("   • Health Check: GET http://127.0.0.1:3001/health")
        print("   • AI Core Test: GET http://127.0.0.1:3001/api/ai-core/test")
        print("   • Speculator Analysis: POST http://127.0.0.1:3001/api/speculator/analyze")
        print("   • MCP Analysis: POST http://127.0.0.1:3001/api/mcp/analyze")
        print()
        print("✨ Integration Status: COMPLETE ✅")
    else:
        print("⚠️  Some tests failed. Please check the errors above.")
        
        if failed_tests:
            print("\n🔧 Failed Tests:")
            for result in failed_tests:
                print(f"   • {result['url']}: {result.get('error', 'HTTP ' + str(result.get('status_code', 'Unknown')))}")
    
    print("\n" + "=" * 80)

if __name__ == "__main__":
    main()