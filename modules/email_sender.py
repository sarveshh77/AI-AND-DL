# d:\FineTune\MEDISCRIBE\modules\email_sender.py
import os
import requests
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

EMAILJS_API_URL = "https://api.emailjs.com/api/v1.0/email/send"

def send_summary_email(patient_email: str, doctor_name: str, summary: str):
    """
    Send medical summary via EmailJS API (server-side).
    Requires:
    - EMAILJS_SERVICE_ID
    - EMAILJS_TEMPLATE_ID
    - EMAILJS_USER_ID (public key) or EMAILJS_PUBLIC_KEY
    - EMAILJS_PRIVATE_KEY (access token)
    Template must use {{to_email}} (or {{patient_email}}) in the To field.
    """
    try:
        # Read envs
        service_id = os.getenv("EMAILJS_SERVICE_ID")
        template_id = os.getenv("EMAILJS_TEMPLATE_ID")
        public_key = os.getenv("EMAILJS_USER_ID") or os.getenv("EMAILJS_PUBLIC_KEY")
        private_key = os.getenv("EMAILJS_PRIVATE_KEY")

        # Validate envs
        missing = [name for name, val in {
            "EMAILJS_SERVICE_ID": service_id,
            "EMAILJS_TEMPLATE_ID": template_id,
            "EMAILJS_USER_ID/EMAILJS_PUBLIC_KEY": public_key,
            "EMAILJS_PRIVATE_KEY": private_key,
        }.items() if not val]
        if missing:
            return False, f"Missing required environment variables: {', '.join(missing)}"

        # Build payload — include accessToken for server-side use
        payload = {
            "service_id": service_id,
            "template_id": template_id,
            "user_id": public_key,         # EmailJS still accepts 'user_id' for public key
            "accessToken": private_key,    # Critical for server-side requests
            "template_params": {
                # Ensure your EmailJS template uses {{to_email}} or {{patient_email}} in the 'To' field
                "to_email": patient_email,
                "patient_email": patient_email,
                "doctor_name": doctor_name,
                "summary": summary,
                "name": doctor_name,
            }
        }

        headers = {"Content-Type": "application/json"}
        response = requests.post(EMAILJS_API_URL, json=payload, headers=headers, timeout=30)

        # Helpful debugging
        print(f"EmailJS status: {response.status_code}")
        print(f"EmailJS response: {response.text}")

        if response.status_code == 200:
            return True, "✅ Email sent successfully!"
        else:
            # Try to parse JSON error when possible
            try:
                error_data = response.json()
                msg = error_data.get("message") or error_data
            except Exception:
                msg = response.text
            return False, f"❌ EmailJS Error ({response.status_code}): {msg}"

    except Exception as e:
        return False, f"❌ Connection/Error: {str(e)}"
