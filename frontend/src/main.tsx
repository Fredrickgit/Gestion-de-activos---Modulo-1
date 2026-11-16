import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';

import '@fontsource-variable/plus-jakarta-sans';
import '@fontsource-variable/plus-jakarta-sans/wght-italic.css';
import '@fontsource-variable/inter';
import '@fontsource-variable/inter/wght-italic.css';
import '@fontsource/cousine/400.css';
import '@fontsource/cousine/700.css';

import './index.css';
import App from './App';
import { ResourcesProvider } from './context/ResourcesContext';
import { ToastProvider } from './context/ToastContext';
import { ToastViewport } from './components/ui/Toast';

const rootElement = document.getElementById('root');

if (!rootElement) {
  throw new Error('No se encontró el elemento raíz de la aplicación.');
}

createRoot(rootElement).render(
  <StrictMode>
    <BrowserRouter>
      <ToastProvider>
        <ResourcesProvider>
          <App />
          <ToastViewport />
        </ResourcesProvider>
      </ToastProvider>
    </BrowserRouter>
  </StrictMode>,
);
