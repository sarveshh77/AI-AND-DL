# config/settings.py

import os
from dotenv import load_dotenv

# Load variables from .env file
load_dotenv()

# Hugging Face token (loaded securely)
HUGGINGFACE_TOKEN = os.getenv("HUGGINGFACE_TOKEN")

# Model sizes
WHISPER_MODEL_SIZE = "base"
PYANNOTE_MODEL = "pyannote/speaker-diarization@2.1"
