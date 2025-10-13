import subprocess
import os
import tempfile

def convert_to_wav(input_file: str) -> str:
    """
    Converts any audio file (mp3, m4a, etc.) into WAV using ffmpeg.
    Returns the path to the converted .wav file.
    """
    # Temporary output file
    tmp_wav = tempfile.NamedTemporaryFile(delete=False, suffix=".wav").name

    try:
        # Run ffmpeg conversion
        subprocess.run(
            ["ffmpeg", "-y", "-i", input_file, "-ac", "1", "-ar", "16000", tmp_wav],
            check=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL
        )
        return tmp_wav
    except subprocess.CalledProcessError as e:
        raise RuntimeError(f"ffmpeg conversion failed: {e}")
