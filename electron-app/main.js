const { app, BrowserWindow, Menu, ipcMain, shell, dialog } = require('electron');
const path = require('path');
const fs = require('fs');

let mainWindow;
const configPath = path.join(app.getPath('userData'), 'config.json');

function loadConfig() {
  try {
    if (fs.existsSync(configPath)) {
      return JSON.parse(fs.readFileSync(configPath, 'utf8'));
    }
  } catch (err) {
    console.error('Error loading config:', err);
  }
  return { remoteUrl: '' };
}

function saveConfig(config) {
  try {
    fs.writeFileSync(configPath, JSON.stringify(config, null, 2), 'utf8');
  } catch (err) {
    console.error('Error saving config:', err);
  }
}

function createMenu() {
  const template = [
    {
      label: 'Conexión',
      submenu: [
        {
          label: 'Cambiar URL del Dispositivo',
          accelerator: 'CmdOrCtrl+Shift+U',
          click: () => {
            if (mainWindow) {
              mainWindow.loadFile(path.join(__dirname, 'setup.html'));
            }
          }
        },
        { type: 'separator' },
        {
          label: 'Recargar',
          accelerator: 'CmdOrCtrl+R',
          click: () => {
            if (mainWindow) mainWindow.reload();
          }
        },
        {
          label: 'Forzar Recarga',
          accelerator: 'CmdOrCtrl+Shift+R',
          click: () => {
            if (mainWindow) mainWindow.webContents.reloadIgnoringCache();
          }
        },
        { type: 'separator' },
        {
          label: 'Salir',
          accelerator: 'CmdOrCtrl+Q',
          click: () => {
            app.quit();
          }
        }
      ]
    },
    {
      label: 'Ver',
      submenu: [
        { role: 'togglefullscreen', label: 'Pantalla Completa' },
        { role: 'zoomIn', label: 'Acercar' },
        { role: 'zoomOut', label: 'Alejar' },
        { role: 'resetZoom', label: 'Restablecer Zoom' },
        { type: 'separator' },
        { role: 'toggledevtools', label: 'Herramientas de Desarrollador' }
      ]
    },
    {
      label: 'Ayuda',
      submenu: [
        {
          label: 'Abrir Documentación Antigravity',
          click: async () => {
            await shell.openExternal('https://antigravity.google/docs');
          }
        },
        {
          label: 'Acerca de',
          click: () => {
            dialog.showMessageBox(mainWindow, {
              type: 'info',
              title: 'Antigravity Remote',
              message: 'Antigravity Remote Desktop Client',
              detail: 'Versión 1.0.0\nControla tus agentes de Antigravity remotamente.'
            });
          }
        }
      ]
    }
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 850,
    minWidth: 800,
    minHeight: 600,
    title: 'Antigravity Remote',
    backgroundColor: '#121212',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false
    }
  });

  createMenu();

  const config = loadConfig();
  if (config.remoteUrl && config.remoteUrl.trim().startsWith('http')) {
    mainWindow.loadURL(config.remoteUrl);
  } else {
    mainWindow.loadFile(path.join(__dirname, 'setup.html'));
  }

  // Handle external links safely
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    if (url.startsWith('http:') || url.startsWith('https:')) {
      // If it's another domain, open in system browser, else in current window
      if (!url.includes('antigravity.google') && !url.includes('localhost')) {
        shell.openExternal(url);
        return { action: 'deny' };
      }
    }
    return { action: 'allow' };
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit();
});

// IPC handlers for setup page
ipcMain.handle('get-config', () => {
  return loadConfig();
});

ipcMain.handle('save-url', (event, url) => {
  const cleanUrl = url.trim();
  saveConfig({ remoteUrl: cleanUrl });
  if (mainWindow && cleanUrl.startsWith('http')) {
    mainWindow.loadURL(cleanUrl);
    return { success: true };
  }
  return { success: false, error: 'URL no válida' };
});
