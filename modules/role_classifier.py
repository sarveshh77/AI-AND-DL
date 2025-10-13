import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification

# Load model once (so it doesn’t reload every time)
MODEL_PATH = "./finetuned_biobert"
tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH)
model = AutoModelForSequenceClassification.from_pretrained(MODEL_PATH)

id2label = {0: "Doctor", 1: "Patient"}

def predict_role(text):
    """
    Predict whether a text belongs to Doctor or Patient.
    Args:
        text (str): Segment text
    Returns:
        str: Role label ("Doctor" or "Patient")
    """
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=128)

    with torch.no_grad():
        outputs = model(**inputs)
        logits = outputs.logits
        predicted_class_id = torch.argmax(logits, dim=-1).item()

    return id2label[predicted_class_id]
