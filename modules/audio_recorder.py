import sounddevice as sd
import wave
import threading
import os
from datetime import datetime

UPLOAD_DIR = os.path.join("data", "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)


class AudioRecorder:
    def __init__(self, samplerate=16000, channels=1):
        self.samplerate = samplerate
        self.channels = channels
        self.recording = False
        self.thread = None
        self.frames = []

    def _record(self, filepath):
        """Background thread for recording audio"""
        import numpy as np
        self.frames = []
        with sd.InputStream(samplerate=self.samplerate, channels=self.channels, dtype="int16") as stream:
            while self.recording:
                data, _ = stream.read(1024)
                self.frames.append(data.copy())

        # Save to WAV after stop
        if self.frames:
            import numpy as np
            audio_data = np.concatenate(self.frames, axis=0)
            with wave.open(filepath, "wb") as wf:
                wf.setnchannels(self.channels)
                wf.setsampwidth(2)  # 16-bit
                wf.setframerate(self.samplerate)
                wf.writeframes(audio_data.tobytes())

    def start_recording(self):
        if self.recording:
            return None
        self.recording = True
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.filepath = os.path.join(UPLOAD_DIR, f"recording_{timestamp}.wav")
        self.thread = threading.Thread(target=self._record, args=(self.filepath,))
        self.thread.start()
        return self.filepath

    def stop_recording(self):
        if not self.recording:
            return None
        self.recording = False
        if self.thread:
            self.thread.join()
        return self.filepath
