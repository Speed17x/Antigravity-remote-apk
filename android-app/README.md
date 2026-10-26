# Antigravity Remote (Android App)

Aplicación nativa para Android para conectarte y controlar tus agentes de Google Antigravity desde tu teléfono.

## Características
- **Pantalla completa nativa:** Experiencia inmersiva sin la barra de direcciones del navegador.
- **Swipe-to-Refresh:** Desliza hacia abajo para recargar la sesión si es necesario.
- **Navegación con botón atrás:** Usa el botón o gesto de volver atrás de Android para navegar dentro de la app sin cerrarla accidentalmente.
- **Configuración rápida:** Botón flotante para cambiar o actualizar el enlace de conexión en cualquier momento.

## Cómo generar el archivo APK

### Opción A (Recomendada: Con Android Studio)
1. Abre **Android Studio** en tu computadora.
2. Selecciona **Open** y abre la carpeta `android-app`.
3. Espera a que termine la sincronización de Gradle.
4. En el menú superior ve a:
   **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Al terminar, haz clic en **"locate"** para encontrar el archivo `app-debug.apk`.
6. Pásalo a tu teléfono por USB, Google Drive, WhatsApp o Telegram e instálalo.

### Opción B (Desde la terminal)
Si tienes el Android SDK instalado en tu máquina:
```bash
cd android-app
./gradlew assembleDebug
```
El archivo `.apk` generado quedará en:
`app/build/outputs/apk/debug/app-debug.apk`
