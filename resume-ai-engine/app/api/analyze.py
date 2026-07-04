from fastapi import APIRouter, HTTPException
from app.models.resume_schema import ResumeAnalyzeRequest, ResumeAnalyzeResponse
from app.services.pdf_parser import extract_text_from_url
from app.services.llm_service import analyze_resume_with_ai # <-- Import the new service

router = APIRouter()

@router.post("/analyze", response_model=ResumeAnalyzeResponse)
async def analyze_resume_endpoint(request: ResumeAnalyzeRequest):

    try:

        print("\n")
        print("=" * 80)
        print("NEW ANALYSIS REQUEST")
        print("=" * 80)

        print(f"Resume ID : {request.resumeId}")
        print(f"File URL  : {request.fileUrl}")
        print(f"File Type : {request.fileType}")

        resume_text = extract_text_from_url(request.fileUrl)

        print(f"Extracted Text Length : {len(resume_text)}")

        ai_result = analyze_resume_with_ai(resume_text)

        print("\nAI RESULT RETURNED TO SPRING BOOT")
        print(ai_result)

        return ResumeAnalyzeResponse(
            ats_score=ai_result["ats_score"],
            skills_extracted=ai_result["skills_extracted"],
            missing_skills=ai_result["missing_skills"],
            suggestions=ai_result["suggestions"]
        )

    except Exception as e:

        import traceback
        traceback.print_exc()

        raise HTTPException(
            status_code=500,
            detail=str(e)
        )