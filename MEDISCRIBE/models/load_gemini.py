# models/load_gemini.py
import os
import google.generativeai as genai

def load_gemini_model():
    api_key = os.getenv("GEMINI_API")
    genai.configure(api_key=api_key)
    # use one of the officially available models
    return genai.GenerativeModel("models/gemini-1.5-flash")


def generate_summary(gemini, transcript):
    """
    transcript: list of dicts with 'role' and 'text'
    """
    # Format transcript properly for LLM
    conversation_text = "\n".join(
        [f"[{seg['role']}] {seg['text']}" for seg in transcript]
    )

    prompt = f"""
    You are a medical scribe. Your task is to generate a structured clinical summary from a dialogue between a Doctor and a Patient.

### Instructions:
1. Focus only on clinically relevant information.
2. Do not include small talk, filler words, or repeated phrases.
3. Preserve important medical entities (symptoms, medications, tests, diagnoses, lifestyle factors).
4. Handle negations carefully (e.g., “no fever” should NOT become “fever”).
5. Present the summary in a clear, structured format.

### Output Format:
- **Chief Complaint:** [Main issue patient came with]
- **History of Present Illness:** [Details of symptoms, onset, duration, severity, progression]
- **Past Medical History:** [Relevant previous conditions, surgeries, allergies]
- **Medications:** [Current medications if mentioned]
- **Examination/Findings:** [If doctor mentions findings during dialogue]
- **Assessment/Diagnosis:** [Doctor’s possible conclusions]
- **Plan:** [Tests ordered, treatments suggested, follow-up]

### Dialogue Transcript:
{conversation_text}

### Clinical Summary:
    """

    response = gemini.generate_content(prompt)
    return response.text
