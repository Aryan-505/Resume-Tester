import google.generativeai as genai
import os
import json
from dotenv import load_dotenv

load_dotenv()

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

if not GEMINI_API_KEY:
    raise Exception("GEMINI_API_KEY not found in .env")

genai.configure(api_key=GEMINI_API_KEY)


def analyze_resume_with_ai(resume_text: str) -> dict:

    print("\n" + "=" * 80)
    print("RESUME TEXT SENT TO AI")
    print("=" * 80)
    print(resume_text[:5000])
    print("=" * 80 + "\n")

    prompt = f"""
You are an expert Applicant Tracking System (ATS) and Senior Tech Recruiter.

Analyze the following resume.

RESUME:
{resume_text}

TASKS:
1. Calculate ATS score (0-100)
2. Extract technical skills
3. Find missing important skills
4. Give 2-3 actionable suggestions

Return ONLY valid JSON.

Example:
{{
    "ats_score": 85.5,
    "skills_extracted": ["Java", "Spring Boot", "SQL"],
    "missing_skills": ["Docker", "AWS"],
    "suggestions": [
        "Add more quantifiable metrics.",
        "Include a project summary."
    ]
}}
"""

    print("\n" + "=" * 80)
    print("PROMPT SENT TO GEMINI")
    print("=" * 80)
    print(prompt[:10000])
    print("=" * 80 + "\n")

    try:

        model = genai.GenerativeModel("gemini-2.5-flash")

        response = model.generate_content(prompt)

        print("\n" + "=" * 80)
        print("RAW GEMINI RESPONSE")
        print("=" * 80)

        print(response.text)

        print("=" * 80 + "\n")

        result_text = response.text.strip()

        if result_text.startswith("```json"):
            result_text = result_text[7:]
            result_text = result_text.replace("```", "").strip()

        elif result_text.startswith("```"):
            result_text = result_text[3:]
            result_text = result_text.replace("```", "").strip()

        parsed_json = json.loads(result_text)

        print("\n" + "=" * 80)
        print("PARSED JSON")
        print("=" * 80)

        print(json.dumps(parsed_json, indent=4))

        print("=" * 80 + "\n")

        return parsed_json

    except Exception as e:

        print("\n" + "=" * 80)
        print("GEMINI ERROR")
        print("=" * 80)

        import traceback
        traceback.print_exc()

        print("=" * 80 + "\n")

        raise Exception(f"Failed to analyze resume with AI: {str(e)}")