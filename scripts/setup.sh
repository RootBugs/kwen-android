#!/usr/bin/env bash
# Project setup script
set -euo pipefail

echo "Setting up project..."

# Check prerequisites
command -v node >/dev/null 2>&1 || { echo "Node.js is required"; exit 1; }

# Install dependencies
if [ -f "package.json" ]; then
    echo "Installing dependencies..."
    npm install || yarn install
fi

# Set up environment
if [ ! -f ".env" ] && [ -f ".env.example" ]; then
    cp .env.example .env
    echo "Created .env from .env.example"
fi

echo "Setup complete!"
