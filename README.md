# Mudra Check
 
**Mudra Check** is an Android app built for **Smart India Hackathon (SIH) 2026** that acts as a compliance inspection scanner for packaged commodities. It helps inspectors and consumers quickly check whether a product's packaging meets Indian regulatory requirements — covering the **Legal Metrology (Packaged Commodities) Rules, 2011**, along with agriculture-related rules for seeds and fertilizers, and general consumer product labeling standards.
 
## 📱 Overview
 
Point the app's camera at a product label, and it uses Google's **Gemini API** to read and analyze the packaging text and layout, then flags whether required disclosures — MRP, net quantity, manufacturer details, date of manufacture, consumer care details, and more — are present and correctly formatted.
 
 Itis built with **Kotlin** and **Jetpack Compose**.
 
## ✨ Features
 
- 📷 Scan product packaging using the device camera
- 🤖 AI-powered compliance analysis via the Gemini API
- ⚖️ Checks against Legal Metrology (Packaged Commodities) Rules, 2011
- 🌾 Coverage for agriculture-related packaging rules (seeds, fertilizers)
- ✅ Clear pass/fail-style breakdown of required label elements
## 🛠️ Tech Stack
 
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Build system:** Gradle (Kotlin DSL)
- **Annotation processing:** KSP (Kotlin Symbol Processing)
- **AI:** Google Gemini API
- **Backend services:** Google Services (Firebase)
- **Testing:** Roborazzi (screenshot testing)
- **Secrets management:** Gradle Secrets Plugin
## 📂 Project Structure
 
```
SIH-2026/
├── app/                    # Main Android application module
├── assets/.aistudio/       # AI Studio project assets
├── gradle/                 # Gradle wrapper files
├── .env.example            # Template for local environment variables
├── build.gradle.kts        # Top-level Gradle build file
├── settings.gradle.kts     # Gradle project settings
├── gradle.properties       # Gradle configuration properties
├── metadata.json           # App metadata (name, description, capabilities)
└── mudra-check-debug.apk   # Prebuilt debug APK for quick testing
```
 
## 🚀 Getting Started
 
### Prerequisites
 
- [Android Studio](https://developer.android.com/studio) (latest stable version recommended)
- JDK 17+
- A Google **Gemini API key** ([get one here](https://aistudio.google.com/app/apikey))
### Setup
 
1. **Clone the repository**
```bash
   git clone https://github.com/abhinav2404-hub/SIH-2026.git
   cd SIH-2026
```
 
2. **Configure your API key**
   Copy `.env.example` to `.env` and add your Gemini API key:
```bash
   cp .env.example .env
```
```
   GEMINI_API_KEY=your_actual_api_key_here
```
   > The key is injected at build time via the Secrets Gradle Plugin and is **not** committed to version control.
 
3. **Open in Android Studio**
   Open the project folder in Android Studio and let Gradle sync automatically.
4. **Run the app**
   Connect an Android device or start an emulator, then click **Run** ▶️ in Android Studio.
### Quick Test (No Build Required)
 
A prebuilt debug APK is available directly in this repo and via [Releases](https://github.com/abhinav2404-hub/SIH-2026/releases):
 
- [`mudra-check-debug.apk`](./mudra-check-debug.apk)
Download and install it on an Android device (you may need to enable "Install from unknown sources") to try the app without setting up a dev environment.
 
## 🔐 Environment Variables
 
| Variable          | Description                                  |
|-------------------|-----------------------------------------------|
| `GEMINI_API_KEY`  | Your Google Gemini API key, used for on-device compliance analysis calls |
 
## 🤝 Contributing
 
This project was built for SIH 2026. Contributions, issues, and feature requests are welcome — feel free to open an issue or submit a pull request.
 
## 📄 License
 
No license has been specified yet for this project. Consider adding one (e.g., MIT, Apache 2.0) if you plan to share or open-source this work.
 
## 🙏 Acknowledgements
 
- Built for **Smart India Hackathon (SIH) 2026**
- Bootstrapped from [google-gemini/aistudio-repository-template](https://github.com/google-gemini/aistudio-repository-template)
- Powered by the [Google Gemini API](https://ai.google.dev/)
 
