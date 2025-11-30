# GitHub Push Script for VanishVault

# Step 1: Initialize Git (if not already done)
cd d:\Projects\Spring\VanishVault
git init

# Step 2: Configure Git (replace with your details)
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Step 3: Add all files
git add .

# Step 4: Create initial commit
git commit -m "Initial commit: VanishVault - Self-Destructing Secret Sharing App"

# Step 5: Create GitHub repository using GitHub CLI (gh) or manually
# If you have GitHub CLI installed:
# gh repo create VanishVault --public --source=. --remote=origin --push

# OR manually create the repo on GitHub, then:
# Replace YOUR_USERNAME with your GitHub username
git remote add origin https://github.com/YOUR_USERNAME/VanishVault.git

# Step 6: Push to GitHub
git branch -M main
git push -u origin main

# If prompted for credentials:
# Username: your_github_username
# Password: paste_your_personal_access_token_here
