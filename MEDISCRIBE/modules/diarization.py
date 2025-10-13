def run_diarization(diarization_pipeline, audio_path):
    """
    Run speaker diarization on the given audio file.
    Args:
        diarization_pipeline: Pre-loaded pyannote pipeline
        audio_path (str): Path to the audio file
    Returns:
        list of dicts: [{start, end, speaker}, ...]
    """
    diarization = diarization_pipeline(audio_path)

    segments = []
    for turn, _, speaker in diarization.itertracks(yield_label=True):
        segments.append({
            "start": turn.start,
            "end": turn.end,
            "speaker": speaker
        })
    return segments
