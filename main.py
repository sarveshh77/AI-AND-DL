# main.py
import os
import torchaudio
import librosa

from models.load_whisper import load_whisper_model
from models.load_pyannote import load_diarization_pipeline
from modules.diarization import run_diarization
from modules.transcription import run_transcription
from modules.alignment import align_diarization_with_transcription


# main.py
from gui.main_gui import start_gui

if __name__ == "__main__":
    start_gui()

    
# ---- STEP 2: Load Models ----
whisper_model = load_whisper_model()
diarization_pipeline = load_diarization_pipeline()

# ---- STEP 3: Show Audio Info ----
info = torchaudio.info(AUDIO_PATH)
print("Audio metadata:", info)

y, sr = librosa.load(AUDIO_PATH, sr=None)
print(f"Audio Duration: {len(y) / sr:.2f} s at {sr} Hz")

# ---- STEP 4: Run Diarization ----
diarization = run_diarization(diarization_pipeline, AUDIO_PATH)

# ---- STEP 5: Transcription ----
transcription_result = run_transcription(whisper_model, AUDIO_PATH)

# ---- STEP 6: Alignment ----
aligned_segments = align_diarization_with_transcription(diarization, transcription_result['segments'])

# ---- STEP 7: Print Aligned Output ----
for seg in aligned_segments:
    print(f"[{seg['speaker']}] {seg['start']:.2f}–{seg['end']:.2f}s: {seg['text']}")
