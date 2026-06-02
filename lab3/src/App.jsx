import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import './index.css'; // <--- ОСЬ ЦЕЙ РЯДОК ПОВЕРТАЄ ДИЗАЙН (якщо ваш файл називається App.css, змініть назву тут)
import AdminDashboard from './pages/admin/AdminDashboard';
import ClientDashboard from './pages/client/ClientDashboard';

function App() {
  const { t, i18n } = useTranslation();
  const [role, setRole] = useState('admin');

  const changeLanguage = (e) => {
    i18n.changeLanguage(e.target.value);
  };

  return (
      <div className="app-container">

        <header className="app-header">
          <h1>⚡ PowerMonitor</h1>

          <div style={{ display: 'flex', gap: '20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label style={{ fontWeight: 'bold' }}>{t('roleLbl')}</label>
              <select
                  className="input-field"
                  style={{ margin: 0, padding: '8px', width: 'auto' }}
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
              >
                <option value="admin">{t('adminPanel')}</option>
                <option value="client">{t('roleClient')}</option>
              </select>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label style={{ fontWeight: 'bold' }}>{t('langLbl')}</label>
              <select
                  className="input-field"
                  style={{ margin: 0, padding: '8px', width: 'auto' }}
                  value={i18n.language}
                  onChange={changeLanguage}
              >
                <option value="uk">{t('languageUA')}</option>
                <option value="en">{t('languageEN')}</option>
              </select>
            </div>
          </div>
        </header>

        <main>
          {role === 'admin' ? <AdminDashboard /> : <ClientDashboard />}
        </main>
      </div>
  );
}

export default App;