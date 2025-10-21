# 🚀 Start SonarQube

## Quick Start (Docker)

### Step 1: Start Docker (Colima)

```bash
colima start
```

Wait 30-60 seconds for Docker to start.

---

### Step 2: Start SonarQube Container

```bash
docker run -d --name sonarqube \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  sonarqube:community
```

Wait 2-3 minutes for SonarQube to fully start.

---

### Step 3: Access SonarQube

**URL**: http://localhost:9000

**Default credentials**:
- Username: `admin`
- Password: `admin`

(You'll be prompted to change the password on first login)

---

## Check Status

```bash
# Check if container is running
docker ps | grep sonarqube

# View logs
docker logs sonarqube -f

# Check if SonarQube is ready
curl http://localhost:9000/api/system/status
```

---

## Stop/Start Commands

```bash
# Stop
docker stop sonarqube

# Start
docker start sonarqube

# Remove (if you want to start fresh)
docker rm -f sonarqube
```

---

## Alternative: Skip SonarQube for Testing

If you don't need SonarQube right now, you can test the n8n workflow without it. The workflow will work fine - it just passes the SonarQube URL as metadata.

Just use any URL like:
```
"sonarURL": "http://localhost:9000/dashboard"
```

The workflow doesn't actually call SonarQube, it just includes the URL in the analysis report.

---

## 📝 For This Project

SonarQube is optional for testing. The main components that are working:

- ✅ Jenkins (http://localhost:8080)
- ✅ n8n (http://localhost:5678)  
- ✅ MCP Wrapper (http://127.0.0.1:3001)
- 🔄 SonarQube (http://localhost:9000) - Optional

---

**You can test the workflow without SonarQube!** Just add GitHub credentials and you're ready to go! 🚀

