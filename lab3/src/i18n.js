import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Імпортуємо наші файли з перекладами
import translationEN from './locales/en.json';
import translationUK from './locales/uk.json';

const resources = {
    en: {
        translation: translationEN
    },
    uk: {
        translation: translationUK
    }
};

i18n
    .use(initReactI18next)
    .init({
        resources,
        lng: 'uk', // Мова за замовчуванням
        fallbackLng: 'en',
        interpolation: {
            escapeValue: false
        }
    });

export default i18n;