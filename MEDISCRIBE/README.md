# 🩺 MediScribe - AI-Powered Medical Transcription System

MediScribe is an intelligent **medical speech-to-text and summarization system** designed to automatically **transcribe doctor-patient conversations**, identify speakers, and generate structured medical summaries.  
It leverages **speech recognition, speaker diarization, NLP summarization**, and **role classification models** to create accurate and readable transcripts for healthcare use.

---

## 🚀 Key Features

- 🎙️ **Speech Transcription** using fine-tuned Whisper model.  
- 🧍‍♂️ **Speaker Diarization** to identify and separate doctor vs patient speech.  
- 🧠 **Context-Aware Role Classification** using BioBERT fine-tuned model.  
- 🩹 **Medical Summary Generation** via Gemini-based summarizer.  
- 📧 **Email Delivery** of summaries directly to patients.  
- 🌐 **Dual Interface:**  
  - **Tkinter GUI** for offline desktop use.  
  - **Streamlit Web App** for modern web-based interaction.  
- 🎛️ **Audio Recording, Upload & Auto Conversion** (MP3 → WAV).

---

## 🧩 Project Architecture

```
MediScribe
│
├── gui/
│   ├── main_gui.py          # Tkinter desktop application
│   ├── app.py               # Streamlit web interface
│
├── models/
│   ├── load_whisper.py      # Whisper ASR model loader
│   ├── load_pyannote.py     # Speaker diarization model
│   ├── load_gemini.py       # Medical summarization model
│
├── modules/
│   ├── diarization.py
│   ├── transcription.py
│   ├── alignment.py
│   ├── audio_utils.py
│   ├── audio_recorder.py
│   ├── email_sender.py
│
├── data/uploads/            # Temporary audio storage
│
├── main.py                  # Entry point for Tkinter GUI
│
└── requirements.txt         # Dependencies
```

---

## ⚙️ Installation & Setup

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/yourusername/MediScribe.git
cd MediScribe
```

### 2️⃣ Create Virtual Environment
```bash
python -m venv mediscribe_env
source mediscribe_env/bin/activate   # On Linux/Mac
mediscribe_env\Scripts\activate      # On Windows
```

### 3️⃣ Install Dependencies
```bash
pip install -r requirements.txt
```

### 4️⃣ Run the Tkinter Application
```bash
python main.py
```

### 5️⃣ Run the Streamlit Web Interface
```bash
streamlit run gui/app.py
```

---

## 📦 Model Management

> ⚠️ **Note:**  
> The pre-trained model weights (e.g., `model.safetensors`) and the virtual environment (`mediscribe_env/`) are **not included in the GitHub repository** due to their large size (approx. 400 MB+).  
>  
> To run the system, please download or generate the required models manually and place them in the appropriate folders:
>
> - `models/whisper/` → Whisper fine-tuned weights  
> - `models/biobert/` → BioBERT classification weights  
> - `models/gemini/` → Summarization model credentials/configs  

---

## 🧠 Tech Stack

| Component | Technology |
|------------|-------------|
| **Frontend (Web)** | Streamlit |
| **Frontend (Desktop)** | Tkinter |
| **Speech-to-Text** | Whisper (OpenAI) |
| **Speaker Diarization** | PyAnnote |
| **Language Model** | BioBERT / Gemini |
| **Email Integration** | EmailJS |
| **Audio Processing** | FFmpeg, Librosa, Torchaudio |
| **Backend Language** | Python 3.11 |

---

## 🧪 Example Workflow

1. Upload or record doctor-patient audio.
2. Whisper transcribes speech → text.
3. PyAnnote performs speaker diarization.
4. Aligned transcript is classified (doctor/patient).
5. Gemini generates concise medical summary.
6. Summary is optionally sent to the patient via email.

---

## 📩 Output Example

```
[Doctor]: How are you feeling today?
[Patient]: I've had a headache since last night.
[Doctor]: Take one paracetamol tablet and rest.

--- SUMMARY ---
Patient reported headache since last night. Doctor prescribed paracetamol and rest.
```

---

## 🧑‍💻 Contributors

- **Sarvesh [Lead Developer]**  
- **Ayush [NLP & Frontend Engineer]**

---

## 📘 License
This project is licensed under the **MIT License** – free to use and modify with attribution.

---

## 🌟 Note from the Developers

> MediScribe is built with ❤️ to help streamline medical documentation through AI.  
> We aim to make healthcare communication **faster, smarter, and more accurate.**
