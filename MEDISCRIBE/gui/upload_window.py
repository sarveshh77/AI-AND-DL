import os
import shutil
from tkinter import filedialog

def select_audio_file():
    """Opens file dialog and copies selected audio to data/uploads."""
    file_path = filedialog.askopenfilename(
        title="Select Audio File",
        filetypes=(("WAV files", "*.wav"), ("All files", "*.*"))
    )
    if file_path:
        upload_dir = os.path.join("data", "uploads")
        os.makedirs(upload_dir, exist_ok=True)

        new_path = os.path.join(upload_dir, os.path.basename(file_path))
        shutil.copy(file_path, new_path)  # Copy instead of move
        return new_path
    return None
