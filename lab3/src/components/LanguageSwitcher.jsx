import React from 'react';
import { useTranslation } from 'react-i18next';

function LanguageSwitcher() {
    const { i18n } = useTranslation();

    const changeLanguage = (lng) => {
        i18n.changeLanguage(lng);
    };

    // Стилі для кнопок (поки що прості, вбудовані)
    const buttonStyle = (isActive) => ({
        padding: '8px 12px',
        margin: '0 5px',
        border: '1px solid #007bff',
        borderRadius: '4px',
        backgroundColor: isActive ? '#007bff' : '#fff',
        color: isActive ? '#fff' : '#007bff',
        cursor: 'pointer',
        fontWeight: 'bold'
    });

    return (
        <div style={{ display: 'inline-block' }}>
            <button
                style={buttonStyle(i18n.language === 'uk')}
                onClick={() => changeLanguage('uk')}
            >
                UA
            </button>
            <button
                style={buttonStyle(i18n.language === 'en')}
                onClick={() => changeLanguage('en')}
            >
                EN
            </button>
        </div>
    );
}

export default LanguageSwitcher;