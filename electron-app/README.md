# Antigravity Remote Desktop (Electron)

Cliente de escritorio para controlar tus agentes locales de Antigravity remotamente desde otra PC o laptop.

## Requisitos
- Node.js instalado (v18 o superior).

## Cómo ejecutarlo en desarrollo
1. Abre una terminal en esta carpeta:
   ```bash
   cd electron-app
   ```
2. Instala las dependencias:
   ```bash
   npm install
   ```
3. Inicia la aplicación:
   ```bash
   npm start
   ```

## Cómo empaquetar un ejecutable (.AppImage, .deb o instalador según tu SO)
Para generar un binario ejecutable que puedas pasar a otra computadora:
```bash
npx electron-builder
```
El archivo ejecutable se creará dentro de la carpeta `dist/`.
