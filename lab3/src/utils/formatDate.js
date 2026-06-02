export const formatDateTime = (dateString, language) => {
    if (!dateString) return 'Немає даних';

    const date = new Date(dateString);
    // Визначаємо регіон залежно від обраної мови сайту
    const locale = language === 'uk' ? 'uk-UA' : 'en-US';

    return new Intl.DateTimeFormat(locale, {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
};