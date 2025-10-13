# models/load_pyannote.py
from pyannote.audio import Pipeline
from config.settings import HUGGINGFACE_TOKEN, PYANNOTE_MODEL

def load_diarization_pipeline():
    print("Loading PyAnnote pipeline...")
    pipeline = Pipeline.from_pretrained(PYANNOTE_MODEL, use_auth_token=HUGGINGFACE_TOKEN)
    print("PyAnnote pipeline loaded.")
    return pipeline
