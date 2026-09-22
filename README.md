<!-- Badges -->
<p align="center">
  <img src="https://img.shields.io/badge/Android-Kotlin%20%2B%20Compose-3DDC84?logo=android&logoColor=white" alt="Android app built with Kotlin and Jetpack Compose" />
  <img src="https://img.shields.io/badge/AI-Google%20Gemini-4285F4?logo=google&logoColor=white" alt="Google Gemini integration" />
  <img src="https://img.shields.io/badge/Status-SIH%202026%20prototype-8A5CF6" alt="SIH 2026 prototype" />
</p>

<h1 align="center">🌾 Mudra Check</h1>

<p align="center"><strong>AI-assisted packaged-goods label inspection for a clearer, more transparent marketplace.</strong></p>

<p align="center">An Android project for <strong>Smart India Hackathon 2026</strong> that helps people review label information on packaged commodities, including food, seeds, and fertilizers.</p>

<p align="center">
  <a href="#how-it-works">How it works</a> ·
  <a href="#features">Features</a> ·
  <a href="#get-started">Get started</a> ·
  <a href="#download-the-apk">Download APK</a>
</p>

> **Status:** Hackathon prototype. Findings are AI-assisted and informational; they are not legal advice, an official inspection, or proof of compliance. Verify the package and applicable rules with a qualified authority.

## The problem

Important label details can be difficult to find and compare. Mudra Check is designed to make those details easier to review by turning a package photo or label text into a structured checklist. It focuses on declarations such as maximum retail price (MRP), net quantity, manufacturer details, dates, and consumer-care information, with additional checks for agricultural products.

## How it works

The app supports three ways to start an inspection: the live camera, a photo from the gallery, or manually entered label text. Benchmark sample packages are also available for a quick walkthrough.

~~~mermaid
flowchart TD
    A[Choose an inspection input] --> B{Input type}
    B -->|Camera| C[Capture package label]
    B -->|Gallery| D[Select package photo]
    B -->|Manual| E[Enter label text]
    B -->|Sample| F[Load benchmark package]
    C --> G[Extract and interpret label information]
    D --> G
    E --> H[Prepare product and label details]
    F --> H
    G --> I[Review against applicable checklist]
    H --> I
    I --> J[Show itemized findings and status]
    J --> K[Review result and inspection history]
~~~

The intended workflow combines label interpretation with rule-oriented checks, then presents findings in an itemized result screen. Image interpretation uses the configured Gemini integration where available; sample packages and manually entered text also support demos and testing.

## Features

| Area | What it does |
| --- | --- |
| **Flexible inspection input** | Start with CameraX, choose an image from the gallery, enter label text, or select a benchmark sample. |
| **AI-assisted label review** | Analyze package text and layout with the Gemini API when an API key is configured. |
| **Readable findings** | Review declaration checks and a clear compliance-style status in the results screen. |
| **Rule-oriented checks** | Includes checks inspired by the Legal Metrology (Packaged Commodities) Rules, 2011, plus seed and fertilizer label declarations. |
| **Food transparency concepts** | Includes a food-label transparency screen and data fields for ingredients, nutrition, and licensing details. |
| **Inspection history and dashboard** | Navigate inspection workflows and review saved inspection data in the app. |
| **Benchmark samples** | Try preconfigured agricultural and packaged-commodity examples without photographing a package. |

## Rule coverage in the prototype

The scanner UI describes checks related to:

- **Legal Metrology (Packaged Commodities) Rules, 2011:** manufacturer/packer details, net quantity, MRP, consumer-care information, and principal display panel declarations.
- **Display and readability:** principal display panel and minimum type-size checks based on package details.
- **Agriculture inputs:** seed and fertilizer declarations, such as composition and validity information.
- **Food transparency:** ingredient, nutrition, and license data fields.

Rules and applicability can depend on product type, package size, exemptions, and current amendments. The prototype does not replace the official legislation or an inspector’s judgement.

## Screens and project flow

~~~mermaid
flowchart LR
    subgraph Android[Android application]
      UI[Jetpack Compose screens]
      VM[MainViewModel]
      DATA[Data and inspection models]
      UI --> VM --> DATA
    end
    VM --> INPUT[Camera, gallery, sample, or text]
    VM --> AI[Gemini API, when configured]
    DATA --> RESULT[Analysis result and history]
    AI --> RESULT
    RESULT --> UI
~~~

The source is organized around a Compose UI, a shared MainViewModel, inspection data/models, and helper utilities. Gemini is an external service; image and label content sent to it is subject to Google's API terms and your configured account.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose and Material 3
- **Build:** Gradle with Kotlin DSL; Kotlin Symbol Processing (KSP)
- **AI:** Google Gemini API
- **Services:** Firebase / Google Services integration
- **Secrets:** Gradle Secrets Plugin
- **Screenshot tests:** Roborazzi

## Get started

### Requirements

- Android Studio (stable release recommended)
- JDK 17 or newer
- Android device or emulator
- A Google Gemini API key for Gemini-backed analysis

### 1. Clone the repository

~~~bash
git clone https://github.com/abhinav2404-hub/SIH-2026.git
cd SIH-2026
~~~

### 2. Configure the Gemini key

Copy [.env.example](.env.example) to a local .env file. Add your own key using the format shown there, and make sure the GEMINI_API_KEY line is uncommented for builds that use Gemini.

~~~bash
cp .env.example .env
~~~

Keep your real API key private. Do not commit .env, paste the key into source code, or share an APK containing a personal key. The Gradle Secrets Plugin is configured to inject the key during the build; follow its local setup requirements if Gradle does not detect it.

### 3. Build and run

Open the cloned folder in Android Studio, allow Gradle to sync, then run the app configuration on a connected device or emulator.

You can also build a debug APK from a terminal:

~~~bash
./gradlew :app:assembleDebug
~~~

On Windows, use:

~~~bat
gradlew.bat :app:assembleDebug
~~~

The generated APK is normally written to app/build/outputs/apk/debug/app-debug.apk.

## Download the APK

A prebuilt debug APK is available here: **[Download legal-metrology-inspector-debug.apk](legal-metrology-inspector-debug.apk)**.

It is an unsigned/debug build for evaluation. Android may ask you to allow installation from the source you used. Only install APKs you trust; for day-to-day use, build from source and configure your own API key.

## Repository structure

~~~text
SIH-2026/
├── app/                       # Android application module
│   └── src/main/java/com/example/
│       ├── data/              # Models, repositories, inspection data
│       ├── ui/                # Compose screens and app navigation
│       │   ├── dashboard/
│       │   ├── guide/
│       │   ├── history/
│       │   ├── scanner/
│       │   ├── tools/
│       │   └── transparency/
│       └── util/              # Shared image and app utilities
├── gradle/                    # Gradle wrapper and version catalog
├── docs/                      # Supporting project documentation
├── .env.example               # Gemini key template (placeholder only)
├── build.gradle.kts           # Root Gradle build configuration
└── legal-metrology-inspector-debug.apk  # Prebuilt debug APK
~~~

## Contributing

Ideas, bug reports, and pull requests are welcome. Please include the device/Android version and steps to reproduce when reporting an issue. Never include API keys, personal data, or unredacted package photos in an issue.

## License

No license file is currently provided. Please contact the repository owner before reusing or redistributing this project.

## Acknowledgements

Built for **Smart India Hackathon 2026**. The repository was bootstrapped from the [Google AI Studio repository template](https://github.com/google-gemini/aistudio-repository-template) and uses the [Google Gemini API](https://ai.google.dev/).
