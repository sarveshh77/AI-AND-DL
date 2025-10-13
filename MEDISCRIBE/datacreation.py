# datacreation.py
import os
import pandas as pd
import chardet

DATA_DIR = "D:\Data\Clean Transcripts"   # folder containing your .txt transcripts
OUTPUT_FILE = "D:\Data\dataset.csv"

def parse_transcript(file_path):
    # Detect encoding of file
    with open(file_path, "rb") as f:
        raw_data = f.read()
        result = chardet.detect(raw_data)
        encoding = result["encoding"]

    rows = []
    with open(file_path, "r", encoding=encoding, errors="ignore") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            if line.startswith("D:"):
                rows.append({"text": line[2:].strip(), "label": "doctor"})
            elif line.startswith("P:"):
                rows.append({"text": line[2:].strip(), "label": "patient"})
    return rows

def main():
    all_rows = []
    for filename in os.listdir(DATA_DIR):
        if filename.endswith(".txt"):
            file_path = os.path.join(DATA_DIR, filename)
            print(f"Processing {file_path} ...")
            rows = parse_transcript(file_path)
            all_rows.extend(rows)

    df = pd.DataFrame(all_rows)
    print(f"✅ Parsed {len(df)} rows from {DATA_DIR}")
    df.to_csv(OUTPUT_FILE, index=False, encoding="utf-8")
    print(f"📁 Saved dataset to {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
