# models/load_whisper.py
import whisper
from config.settings import WHISPER_MODEL_SIZE

def load_whisper_model():
    print("Loading Whisper model...")
    model = whisper.load_model(WHISPER_MODEL_SIZE)
    print("Whisper model loaded.")
    return model
