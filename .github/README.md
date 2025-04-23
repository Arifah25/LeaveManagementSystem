# CI/CD Pipeline for Leave Management System

This directory contains GitHub Actions workflows for continuous integration and deployment of the Leave Management System.

## Workflow Overview

The `ci-cd.yml` workflow automates the following processes:

### When Triggered

- On push to `main` or `master` branches
- On pull requests to `main` or `master` branches
- Manually via workflow dispatch

### Build Job

1. **Environment Setup**:

   - Spins up PostgreSQL and Redis services with health checks
   - Uses the same configuration as the application

2. **Build Process**:

   - Checks out the code
   - Sets up JDK 17
   - Builds the application with Maven
   - Runs tests
   - Analyzes the code

3. **Artifact Handling**:
   - Caches Maven packages for faster builds
   - Creates a JAR file
   - Uploads the JAR as an artifact

### Deployment (Commented Out)

A deployment section is included but commented out. To enable deployment:

1. Uncomment the `deploy` job section
2. Configure your deployment strategy based on your hosting platform
3. Add any necessary secrets in your GitHub repository settings

## Adding Secrets

For secure deployment, add necessary secrets to your GitHub repository:

1. Go to repository Settings
2. Navigate to Secrets and Variables > Actions
3. Add your deployment credentials as needed

## Customizing the Workflow

Modify the workflow to match your specific needs:

- Add code quality tools like SonarQube
- Implement Docker containerization
- Configure specific deployment targets
