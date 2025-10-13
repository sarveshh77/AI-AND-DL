import tkinter as tk
from tkinter import messagebox, scrolledtext, simpledialog
import os

# Import your custom modules
from gui.upload_window import select_audio_file
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


class MedicalTranscriptionApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Medical Transcription App")
        self.root.geometry("700x500")

        self.audio_path = None
        self.whisper_model = None
        self.diarization_pipeline = None
        self.recorder = AudioRecorder()
        self.last_summary = None
        self.send_btn = None   # Keep reference for later

        self.setup_ui()

    def setup_ui(self):
        self.upload_btn = tk.Button(self.root, text="Upload Audio", command=self.upload_audio)
        self.upload_btn.pack(pady=10)

        self.start_btn = tk.Button(self.root, text="Start Recording", command=self.start_recording)
        self.start_btn.pack(pady=5)

        self.stop_btn = tk.Button(self.root, text="Stop Recording", command=self.stop_recording)
        self.stop_btn.pack(pady=5)

        self.run_btn = tk.Button(self.root, text="Run Pipeline", command=self.run_pipeline)
        self.run_btn.pack(pady=10)

        self.output_text = scrolledtext.ScrolledText(self.root, wrap=tk.WORD, width=80, height=20)
        self.output_text.pack(padx=10, pady=10)

        # Pre-create Send button (but hide initially)
        self.send_btn = tk.Button(self.root, text="Send Summary via Email", command=self.send_email)
        self.send_btn.pack(pady=5)
        self.send_btn.pack_forget()   # hide initially

    # 📂 Upload Audio
    def upload_audio(self):
        filepath = select_audio_file()
        if not filepath:
            messagebox.showwarning("No File", "No audio file selected.")
            return

        filename = os.path.basename(filepath)
        dest_path = os.path.join(UPLOAD_DIR, filename)

        # If file already wav → just copy
        if filepath.lower().endswith(".wav"):
            if filepath != dest_path:
                os.replace(filepath, dest_path)
            self.audio_path = dest_path
            messagebox.showinfo("Success", f"Audio uploaded:\n{self.audio_path}")
        else:
            # Convert to wav and save in uploads
            dest_wav = os.path.join(UPLOAD_DIR, os.path.splitext(filename)[0] + ".wav")
            convert_to_wav(filepath, dest_wav)
            self.audio_path = dest_wav
            messagebox.showinfo("Success", f"Audio converted & uploaded:\n{self.audio_path}")

    # 🎤 Recording
    def start_recording(self):
        filepath = self.recorder.start_recording()
        if filepath:
            messagebox.showinfo("Recording", "🎤 Recording started...")

    def stop_recording(self):
        filepath = self.recorder.stop_recording()
        if filepath:
            self.audio_path = filepath
            messagebox.showinfo("Recording Saved", f"Recording saved:\n{self.audio_path}")

    # 🔄 Run Pipeline
    def run_pipeline(self):
        if not self.audio_path:
            messagebox.showerror("Error", "Please upload or record audio first.")
            return

        self.output_text.delete(1.0, tk.END)
        self.output_text.insert(tk.END, "Loading models...\n")

        if not self.whisper_model:
            self.whisper_model = load_whisper_model()
        if not self.diarization_pipeline:
            self.diarization_pipeline = load_diarization_pipeline()

        print("\n--- STEP 1: Raw Diarization Output ---")
        diarization = run_diarization(self.diarization_pipeline, self.audio_path)
        print(diarization)

        print("\n--- STEP 2: Raw Transcription Output ---")
        transcription_result = run_transcription(self.whisper_model, self.audio_path)
        print(transcription_result)

        print("\n--- STEP 3: Raw Aligned Segments ---")
        aligned_segments = align_diarization_with_transcription(
            diarization, transcription_result['segments']
        )
        for seg in aligned_segments:
            print(seg)

        role_aware_transcript = []
        print("\n--- STEP 4: Role Classification ---")
        for seg in aligned_segments:
            entry = {"role": seg['role'], "text": seg['text']}
            role_aware_transcript.append(entry)
            print(f"[{seg['role']}] {seg['start']:.2f}-{seg['end']:.2f}s: {seg['text']}")

        self.output_text.insert(tk.END, "\n--- ROLE-CLASSIFIED TRANSCRIPT ---\n")
        for seg in role_aware_transcript:
            self.output_text.insert(tk.END, f"[{seg['role']}] {seg['text']}\n")

        print("\n--- STEP 5: Generating summary ---")
        gemini = load_gemini_model()
        summary = generate_summary(gemini, role_aware_transcript)
        print("\nSUMMARY:\n", summary)

        self.output_text.insert(tk.END, "\n--- SUMMARY ---\n")
        self.output_text.insert(tk.END, summary + "\n")

        # Save summary so email function can use it
        self.last_summary = summary

        # Show Send button
        self.send_btn.pack(pady=5)
# 📧 Send Email
   # inside class MedicalTranscriptionApp
    def send_email(self):
        if not self.last_summary:
            messagebox.showerror("Error", "No summary available to send. Please run the pipeline first.")
            return

        patient_email = simpledialog.askstring("Patient Email", "Enter patient email:")
        if not patient_email:
            return

        doctor_name = simpledialog.askstring("Doctor Name", "Enter your name:")
        if not doctor_name:
            return

        # Show loading dialog
        loading_window = tk.Toplevel(self.root)
        loading_window.title("Sending Email")
        loading_window.geometry("300x100")
        tk.Label(loading_window, text="Sending email, please wait...").pack(pady=20)
        loading_window.update()

        try:
            success, msg = send_summary_email(patient_email, doctor_name, self.last_summary)
            loading_window.destroy()
            if success:
                messagebox.showinfo("Success", msg)
            else:
                error_msg = f"Failed to send email:\n\n{msg}"
                if "non-browser" in error_msg.lower():
                    error_msg += "\n\nCheck your EmailJS account and ensure server-side accessToken is used."
                elif "template" in error_msg.lower():
                    error_msg += "\n\nVerify template variable names match your payload (e.g., to_email)."
                elif "recipient" in error_msg.lower() or "empty" in error_msg.lower():
                    error_msg += "\n\nEnsure the template 'To' field uses {{to_email}} (or your chosen var)."
                messagebox.showerror("Error", error_msg)
        except Exception as e:
            loading_window.destroy()
            messagebox.showerror("Error", f"Unexpected error: {str(e)}")


def start_gui():
    root = tk.Tk()
    app = MedicalTranscriptionApp(root)
    root.mainloop()


if __name__ == "__main__":
    start_gui()
