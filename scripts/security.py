import os
import google.generativeai as genai
from pathlib import Path

# Configure Gemini
API_KEY = os.getenv("GEMINI_API_KEY")
if not API_KEY:
    raise ValueError("GEMINI_API_KEY environment variable not set")

genai.configure(api_key=API_KEY)

# Configuration
MODEL_NAME = "gemini-1.5-pro-latest" # Using 1.5 Pro for large context window
ROOT_DIR = "."
OUTPUT_FILE = "SECURITY_REPORT.md"

# Extensions to scan
INCLUDED_EXTENSIONS = {".java", ".gradle", ".xml", ".sh", ".properties"}
EXCLUDED_DIRS = {".git", ".gradle", "build", "target", ".idea", "wrapper"}

def get_code_context(root_dir):
    code_content = ""
    file_count = 0
    
    for root, dirs, files in os.walk(root_dir):
        # Filter directories
        dirs[:] = [d for d in dirs if d not in EXCLUDED_DIRS]
        
        for file in files:
            file_path = Path(root) / file
            if file_path.suffix in INCLUDED_EXTENSIONS:
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        content = f.read()
                        code_content += f"\n--- START FILE: {file_path} ---\n"
                        code_content += content
                        code_content += f"\n--- END FILE: {file_path} ---\n"
                        file_count += 1
                except Exception as e:
                    print(f"Skipping file {file_path}: {e}")
                    
    print(f"Scanned {file_count} files.")
    return code_content

def analyze_code(code_context):
    model = genai.GenerativeModel(MODEL_NAME)
    
    prompt = f"""
    You are an expert Static Application Security Testing (SAST) tool and Senior Software Engineer.
    Analyze the following codebase for:
    1. Security Vulnerabilities (OWASP Top 10, Injection, XSS, etc.)
    2. Code Smells & Anti-patterns
    3. Performance Issues
    4. Dependency checks (based on build.gradle)
    
    Format your response as a professional Markdown report with the following sections:
    - **Executive Summary**: High-level overview of health.
    - **Critical Vulnerabilities**: Immediate security threats.
    - **Code Quality Issues**: Maintainability and logic suggestions.
    - **Recommendations**: Specific code fixes.
    
    Here is the codebase:
    {code_context}
    """
    
    # Generate content
    # Set safety settings to block none to ensure we get the full critique even if code is "unsafe"
    response = model.generate_content(
        prompt,
        generation_config={"temperature": 0.2}
    )
    
    return response.text

def main():
    print("Gathering code...")
    code_context = get_code_context(ROOT_DIR)
    
    print("Sending to Gemini for analysis...")
    report = analyze_code(code_context)
    
    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write(report)
    print(f"Report generated: {OUTPUT_FILE}")

if __name__ == "__main__":
    main()