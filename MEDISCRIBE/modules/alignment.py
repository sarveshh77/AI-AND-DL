from modules.role_classifier import predict_role

def align_diarization_with_transcription(diarization, transcription_segments):
    """
    Align diarization output with transcription segments and add role labels.
    Args:
        diarization (list): [{start, end, speaker}]
        transcription_segments (list): [{start, end, text}]
    Returns:
        list of dicts: [{start, end, speaker, role, text}]
    """
    aligned_segments = []

    for t in transcription_segments:
        # Find matching diarization speaker
        speaker_label = "Unknown"
        for d in diarization:
            if d["start"] <= t["start"] < d["end"]:
                speaker_label = d["speaker"]
                break

        # Role classification
        role = predict_role(t["text"])

        aligned_segments.append({
            "start": t["start"],
            "end": t["end"],
            "speaker": speaker_label,
            "role": role,
            "text": t["text"]
        })

    return aligned_segments
