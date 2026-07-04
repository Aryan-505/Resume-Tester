import requests
import fitz  # PyMuPDF
import re    # Built-in Python library for text cleaning

def clean_extracted_text(raw_text: str) -> str:
    """
    Cleans messy PDF text by removing icons, extra spaces, and weird characters.
    """
    # 1. Replace weird unicode icons (like LinkedIn/GitHub symbols) with spaces
    # This keeps standard text, numbers, punctuation, and basic math symbols
    cleaned = re.sub(r'[^\x00-\x7F]+', ' ', raw_text)
    
    # 2. Replace multiple newlines with a single newline
    cleaned = re.sub(r'\n+', '\n', cleaned)
    
    # 3. Replace multiple spaces/tabs with a single space
    cleaned = re.sub(r'[ \t]+', ' ', cleaned)
    
    # 4. Strip leading/trailing whitespace
    return cleaned.strip()

def extract_text_from_url(pdf_url: str) -> str:
    """
    Downloads a PDF from a given URL (S3) and extracts its text.
    """
    try:
        response = requests.get(pdf_url, stream=True)
        response.raise_for_status()
        
        pdf_document = fitz.open(stream=response.content, filetype="pdf")
        
        full_text = ""
        for page_num in range(pdf_document.page_count):
            page = pdf_document.load_page(page_num)
            
            # Using 'blocks' extraction is slightly better for columns than raw 'text'
            blocks = page.get_text("blocks")
            
            # Sort blocks primarily by vertical position (Y), then horizontal (X)
            blocks.sort(key=lambda b: (b[1], b[0]))
            
            for block in blocks:
                if block[6] == 0:  # block[6] == 0 means it's a text block (not an image)
                    full_text += block[4] + "\n"
            
        pdf_document.close()
        
        # Clean the text before returning it
        return clean_extracted_text(full_text)

    except requests.exceptions.RequestException as e:
        raise Exception(f"Failed to download PDF from URL: {str(e)}")
    except Exception as e:
        raise Exception(f"Failed to parse PDF: {str(e)}")