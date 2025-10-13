def run_transcription(whisper_model, audio_path):
    """
    Run transcription using Whisper model.
    Args:
        whisper_model: Pre-loaded Whisper model
        audio_path (str): Path to audio file
    Returns:
        dict with 'text' and 'segments'
    """
    result = whisper_model.transcribe(audio_path)
    return result
