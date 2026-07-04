from pydantic import BaseModel
from typing import List, Optional

# What Spring Boot sends to Python
class ResumeAnalyzeRequest(BaseModel):
    resumeId: int
    fileUrl: str
    fileType: str

# What Python sends back to Spring Boot
class ResumeAnalyzeResponse(BaseModel):
    ats_score: float
    skills_extracted: List[str]
    missing_skills: List[str]
    suggestions: List[str]