import streamlit as st
import sys, os

# make root dir visible
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from models.load_whisper import load_whisper_model
from models.load_pyannote import load_diarization_pipeline
from modules.diarization import run_diarization
from modules.transcription import run_transcription
from modules.alignment import align_diarization_with_transcription
from models.load_gemini import load_gemini_model, generate_summary
from modules.audio_recorder import AudioRecorder
from modules.audio_utils import convert_to_wav
from modules.email_sender import send_summary_email

UPLOAD_DIR = os.path.join("data", "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)

st.set_page_config(page_title="MediScribe - AI", layout="wide")
st.title("🩺 MediScribe")

# Session state init
if 'audio_path' not in st.session_state:
    st.session_state.audio_path = None
if 'summary' not in st.session_state:
    st.session_state.summary = None

# Load models once and cache
@st.cache_resource
def load_models():
    return load_whisper_model(), load_diarization_pipeline(), load_gemini_model()

whisper_model, diarization_pipeline, gemini_model = load_models()
recorder = AudioRecorder()

# ---------------- Upload audio ----------------
st.subheader("Upload Audio File")
uploaded_file = st.file_uploader("Choose an audio file", type=["wav", "mp3", "m4a"])
if uploaded_file is not None:
    temp_path = os.path.join(UPLOAD_DIR, uploaded_file.name)
    with open(temp_path, "wb") as f:
        f.write(uploaded_file.read())

    # Convert to wav if needed
    if not temp_path.lower().endswith(".wav"):
        wav_path = os.path.join(UPLOAD_DIR, os.path.splitext(uploaded_file.name)[0] + ".wav")
        convert_to_wav(temp_path, wav_path)
        st.session_state.audio_path = wav_path
        st.success(f"Audio converted & uploaded: {wav_path}")
    else:
        st.session_state.audio_path = temp_path
        st.success(f"Audio uploaded: {temp_path}")

# ---------------- Recording (optional) ----------------
st.subheader("Record Audio")
col1, col2 = st.columns(2)
with col1:
    if st.button("🎤 Start Recording"):
        recorder.start_recording()
        st.info("Recording started...")
with col2:
    if st.button("⏹ Stop Recording"):
        path = recorder.stop_recording()
        if path:
            st.session_state.audio_path = path
            st.success(f"Recording saved: {path}")

# ---------------- Run pipeline ----------------
if st.button("Run Pipeline"):
    if not st.session_state.audio_path:
        st.error("Please upload or record audio first.")
    else:
        st.write("### Processing audio…")

        # STEP 1: Raw Diarization Output (print to terminal)
        print("\n--- STEP 1: Raw Diarization Output ---")
        diarization = run_diarization(diarization_pipeline, st.session_state.audio_path)
        print(diarization)

        # STEP 2: Raw Transcription Output (print to terminal)
        print("\n--- STEP 2: Raw Transcription Output ---")
        transcription_result = run_transcription(whisper_model, st.session_state.audio_path)
        print(transcription_result)

        # STEP 3: Raw Aligned Segments (print to terminal)
        print("\n--- STEP 3: Raw Aligned Segments ---")
        aligned_segments = align_diarization_with_transcription(
            diarization, transcription_result['segments']
        )
        for seg in aligned_segments:
            print(seg)

        # STEP 4: Role Classification
        print("\n--- STEP 4: Role Classification ---")
        role_aware_transcript = []
        for seg in aligned_segments:
            entry = {"role": seg['role'], "text": seg['text']}
            role_aware_transcript.append(entry)
            print(f"[{seg['role']}] {seg['start']:.2f}-{seg['end']:.2f}s: {seg['text']}")

        # Show only role-classified transcript in GUI
        st.write("### Role-classified transcript")
        for seg in role_aware_transcript:
            st.write(f"[{seg['role']}] {seg['text']}")

        # STEP 5: Generate Summary
        print("\n--- STEP 5: Generating summary ---")
        summary = generate_summary(gemini_model, role_aware_transcript)
        print("\nSUMMARY:\n", summary)

        st.session_state.summary = summary
        st.success("Summary generated!")
        st.write("### Summary")
        st.write(summary)

# ---------------- Email sending ----------------
if st.session_state.summary:
    st.write("---")
    st.subheader("Send Summary via Email")
    patient_email = st.text_input("Patient Email")
    doctor_name = st.text_input("Doctor Name")
    if st.button("Send Email"):
        success, msg = send_summary_email(patient_email, doctor_name, st.session_state.summary)
        if success:
            st.success(msg)
        else:
            st.error(msg)
