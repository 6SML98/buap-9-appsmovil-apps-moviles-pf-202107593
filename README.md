# Asistencias · Android

Aplicación Android nativa para alumnos, listas, asistencias y escaneo de códigos QR, desarrollada en Kotlin.

## Requisitos

Android Studio, JDK compatible con Gradle 8.13, Android SDK 36 y las dependencias definidas en Gradle.

## Ejecutar

Abre esta carpeta en Android Studio. Sustituye app/google-services.json por el archivo de tu propio proyecto Firebase, registrado con el applicationId com.example.pf_202107593.

```text
gradlew.bat assembleDebug
```

El APK aparece en app/build/outputs/apk/debug/.

## Verificación del 8 de octubre de 2026

Código compilado y APK debug generado con Gradle 8.13 y AGP 8.13.2. También se ejecutó la prueba unitaria de plantilla; no valida las funciones de la aplicación. No se probó en dispositivo/emulador ni contra Firestore. La configuración Firebase incluida es ficticia y requiere sustitución; el menú administrativo no reemplaza reglas de acceso a datos.
