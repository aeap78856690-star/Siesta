Siesta/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/siestaauth/
│   │   │   │   ├── 🎯 ACTIVIDADES
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── AuthActivity.kt
│   │   │   │   │   ├── UserSetupActivity.kt
│   │   │   │   │   └── ChatActivity.kt
│   │   │   │   │
│   │   │   │   ├── 🔐 SEGURIDAD Y AUTENTICACIÓN
│   │   │   │   │   ├── FingerPrintIdentifier.kt
│   │   │   │   │   ├── BiometricVoiceListener.kt
│   │   │   │   │   ├── BiometricVoiceAuthenticator.kt
│   │   │   │   │   ├── PermissionController.kt
│   │   │   │   │   └── PermissionHelper.kt
│   │   │   │   │
│   │   │   │   ├── 🤖 IA Y PROCESAMIENTO
│   │   │   │   │   ├── AIProvider.kt (interfaz)
│   │   │   │   │   ├── GeminiClient.kt (IA Online)
│   │   │   │   │   ├── OpenAIClient.kt (IA Online alternativa)
│   │   │   │   │   ├── LocalAIModel.kt (IA Offline)
│   │   │   │   │   └── SmartAISelector.kt (selector automático)
│   │   │   │   │
│   │   │   │   ├── 🎙️ VOZ Y RECONOCIMIENTO
│   │   │   │   │   ├── MikoNakanoVoice.kt (Text-to-Speech)
│   │   │   │   │   ├── RealSpeechRecognizer.kt (Speech-to-Text real)
│   │   │   │   │   └── BiometricVoiceListener.kt (palabra clave)
│   │   │   │   │
│   │   │   │   ├── 💾 BASE DE DATOS Y MEMORIA
│   │   │   │   │   ├── ConversationDatabase.kt (SharedPreferences)
│   │   │   │   │   ├── SiestaDatabase.kt (Room Database)
│   │   │   │   │   ├── ConversationDao.kt (Data Access Object)
│   │   │   │   │   └── ConversationEntity.kt (Modelo de datos)
│   │   │   │   │
│   │   │   │   ├── 🌍 CONFIGURACIÓN Y IDIOMAS
│   │   │   │   │   ├── LanguageManager.kt (gestor de idiomas)
│   │   │   │   │   └── NetworkManager.kt (detector de conexión)
│   │   │   │   │
│   │   │   │   ├── 🔋 OPTIMIZACIÓN
│   │   │   │   │   └── BatteryOptimizer.kt (batería y temperatura)
│   │   │   │   │
│   │   │   │   └── 📊 MODELOS DE DATOS
│   │   │   │       ├── UserProfile.kt
│   │   │   │       ├── UserPermissions.kt
│   │   │   │       └── Message.kt
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_auth.xml
│   │   │   │   │   ├── activity_user_setup.xml
│   │   │   │   │   └── activity_chat.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── dimens.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   ├── ic_microphone.xml
│   │   │   │   │   └── ic_settings.xml
│   │   │   │   │
│   │   │   │   └── mipmap/
│   │   │   │       ├── ic_launcher.png
│   │   │   │       └── ic_launcher_round.png
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/ (tests unitarios)
│   │       └── java/com/siestaauth/
│   │           ├── GeminiClientTest.kt
│   │           ├── BatteryOptimizerTest.kt
│   │           └── LanguageManagerTest.kt
│   │
│   ├── build.gradle (dependencias)
│   └── proguard-rules.pro
│
├── build.gradle (proyecto)
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
│
├── 📄 ARCHIVOS DE CONFIGURACIÓN
│   ├── .gitignore
│   ├── README.md
│   ├── CHANGELOG.md
│   └── LICENSE
│
└── 📚 DOCUMENTACIÓN
    ├── INSTALL.md (cómo instalar)
    ├── USAGE.md (cómo usar)
    ├── API.md (documentación de API)
    └── ARCHITECTURE.md (arquitectura del proyecto)