from fastapi import FastAPI
from app.api import analyze

app = FastAPI(
    title="Resume AI Engine",
    description="Python backend for parsing resumes and calculating ATS scores.",
    version="1.0.0"
)

# Register the routes
app.include_router(analyze.router, prefix="/api/ai", tags=["AI Analysis"])

@app.get("/")
def health_check():
    return {"status": "healthy", "service": "Resume AI Engine"}