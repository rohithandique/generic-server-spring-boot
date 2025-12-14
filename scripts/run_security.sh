#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Check for API Key
if [ -z "$1" ]; then
    echo "ERROR: No Gemini API key provided."
    echo "Usage: ./run.sh YOUR_GEMINI_API_KEY"
    exit 1
fi

GEMINI_API_KEY=$1

# Check for uv
if ! command -v uv &> /dev/null; then
    echo "uv not found. Attempting to install via pip..."

    if command -v pip &> /dev/null; then
        pip install uv

        # Check if uv is available after install
        if ! command -v uv &> /dev/null; then
            echo "----------------------------------------------------------------"
            echo "WARNING: 'uv' was installed via pip but is not in your PATH."
            echo "It is likely located in your Python Scripts directory."
            echo ""
            echo "To fix this, you can either:"
            echo "1. Add the Python Scripts directory to your PATH."
            echo "2. Or install uv globally using PowerShell (recommended for Windows):"
            echo "   powershell -c \"irm https://astral.sh/uv/install.ps1 | iex\""
            echo "----------------------------------------------------------------"
            exit 1
        else
            echo "uv successfully installed."
        fi
    else
        echo "ERROR: neither uv nor pip found."
        echo "Please install uv manually using PowerShell:"
        echo "powershell -c \"irm https://astral.sh/uv/install.ps1 | iex\""
        exit 1
    fi
fi

# Create virtual environment if it doesn't exist
if [ ! -d ".venv" ]; then
    echo "Creating virtual environment..."
    uv venv
fi

# Install dependencies
echo "Installing dependencies..."
uv pip install -r requirements.txt

# Run the script
echo "Starting security analysis..."
# uv run automatically picks up the .venv in the directory
uv run python security.py --api-key "$GEMINI_API_KEY"

echo "Done."
