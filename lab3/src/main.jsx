import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './i18n'; // <--- ОСЬ ЦЕЙ РЯДОК ОБОВ'ЯЗКОВИЙ

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);