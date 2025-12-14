import os
import argparse
import google.generativeai as genai
from pathlib import Path

# --- CONFIGURATION ---
# Set default values, but allow them to be overridden by command-line arguments.
# This makes the script more flexible for different use cases.

# The generative model to use for analysis. Using a powerful model like 1.5 Pro
# is recommended for its large context window and strong reasoning capabilities.
MODEL_NAME = "gemini-2.5-flash"

# The root directory of the codebase to be analyzed.
# We'll scan this directory recursively.
ROOT_DIR = ".." # Changed to parent directory to scan the whole project

# The directory where the security analysis report will be saved.
# This helps keep the project's root directory clean.
OUTPUT_DIR = "../temp/analysis"
OUTPUT_FILE_NAME = "SECURITY_REPORT.md"

# File extensions to include in the scan. This helps focus the analysis on
# relevant source code and configuration files.
INCLUDED_EXTENSIONS = {".java", ".gradle", ".xml", ".sh", ".properties"}

# Directories to exclude from the scan. This is crucial for avoiding noise
# from dependencies, build artifacts, and version control metadata.
EXCLUDED_DIRS = {".git", ".gradle", "build", "target", ".idea", "wrapper", "scripts"}


def get_code_context(root_dir):
    """
    Recursively scans the specified root directory, aggregates the content of
    files with allowed extensions, and returns it as a single string.

    This function is the core of the data collection phase. It creates a "context"
    of the entire codebase that can be sent to the generative model for analysis.
    """
    code_content = ""
    file_count = 0
    
    print(f"Starting code scan in: {Path(root_dir).resolve()}")
    
    for root, dirs, files in os.walk(root_dir):
        # Dynamically filter out excluded directories to prune the search space.
        dirs[:] = [d for d in dirs if d not in EXCLUDED_DIRS]
        
        for file in files:
            file_path = Path(root) / file
            if file_path.suffix in INCLUDED_EXTENSIONS:
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        content = f.read()
                        # Add file content with clear separators for the model.
                        code_content += f"\\n--- START FILE: {file_path} ---\\n"
                        code_content += content
                        code_content += f"\\n--- END FILE: {file_path} ---\\n"
                        file_count += 1
                except Exception as e:
                    # Log errors for files that can't be read, but continue the scan.
                    print(f"Skipping file {file_path}: {e}")
                    
    print(f"Scanned {file_count} files.")
    return code_content


def analyze_code(code_context):
    """
    Sends the collected code context to the Gemini model for a security analysis.

    The prompt is engineered to instruct the model to act as a security expert
    and provide a structured, actionable report.
    """
    model = genai.GenerativeModel(MODEL_NAME)
    
    prompt = f"""
    You are an expert Static Application Security Testing (SAST) tool and Senior Software Engineer.
    Your task is to analyze the provided codebase for potential issues.

    Please review the following aspects:
    1.  **Security Vulnerabilities**: Identify critical security flaws such as those listed in the
        OWASP Top 10 (e.g., Injection, Broken Authentication, XSS, Insecure Deserialization).
    2.  **Code Smells & Anti-patterns**: Look for poor programming practices, design flaws,
        or code structures that are hard to maintain.
    3.  **Performance Issues**: Highlight any code that may lead to performance bottlenecks,
        such as inefficient loops, memory leaks, or suboptimal queries.
    4.  **Dependency Checks**: Based on the `build.gradle` file, check for any dependencies
        with known vulnerabilities or that are outdated.

    Structure your findings into a professional Markdown report with these sections:
    -   **Executive Summary**: A high-level overview of the codebase's health and key findings.
    -   **Critical Vulnerabilities**: A list of immediate security threats that must be addressed.
    -   **Code Quality Issues**: Suggestions for improving maintainability, readability, and logic.
    -   **Recommendations**: Specific, actionable advice for fixing the identified issues,
        including code snippets where possible.

    Here is the codebase to analyze:
    {code_context}
    """
    
    print("Sending code to Gemini for analysis. This may take a few moments...")
    
    # Configure safety settings to ensure the model returns a complete analysis,
    # even if it encounters code that might be flagged as "unsafe."
    response = model.generate_content(
        prompt,
        generation_config={"temperature": 0.2}
    )
    
    return response.text


def main():
    """
    Main function to orchestrate the security analysis workflow.
    1. Parses command-line arguments for the API key.
    2. Configures the Gemini client.
    3. Gathers code context.
    4. Triggers the analysis.
    5. Saves the report to a file.
    """
    # Set up argument parser for command-line execution.
    parser = argparse.ArgumentParser(description="Perform a security analysis of a codebase using Gemini.")
    parser.add_argument("--api-key", required=True, help="Your Gemini API key.")
    args = parser.parse_args()

    # Configure the Gemini client with the provided API key.
    try:
        genai.configure(api_key=args.api_key)
    except Exception as e:
        print(f"Error configuring Gemini: {e}")
        return

    print("Gathering code context...")
    code_context = get_code_context(ROOT_DIR)
    
    if not code_context:
        print("No code found to analyze. Please check your configuration.")
        return
        
    report = analyze_code(code_context)
    
    # Ensure the output directory exists.
    output_path = Path(OUTPUT_DIR)
    output_path.mkdir(parents=True, exist_ok=True)
    
    # Write the report to the specified output file.
    report_file = output_path / OUTPUT_FILE_NAME
    with open(report_file, "w", encoding="utf-8") as f:
        f.write(report)
        
    print(f"Security report successfully generated at: {report_file.resolve()}")


if __name__ == "__main__":
    main()
