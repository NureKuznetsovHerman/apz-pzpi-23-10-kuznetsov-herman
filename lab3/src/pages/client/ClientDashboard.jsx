import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { deviceService } from '../../api/deviceService';
import { formatDateTime } from '../../utils/formatDate';

function ClientDashboard() {
    const { t, i18n } = useTranslation();
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [selectedDevice, setSelectedDevice] = useState(null);

    useEffect(() => {
        const fetchDevices = async () => {
            try {
                const data = await deviceService.getAllDevices();
                setDevices(data);
            } catch (err) {
                setError('Помилка / Error');
            }
            setLoading(false);
        };

        fetchDevices();
    }, []);

    return (
        <div>
            <h2 style={{ marginBottom: '5px' }}>👤 {t('userPanel')}</h2>
            <p style={{ color: '#6b7280', marginBottom: '25px' }}>
                {t('clientDesc')}
            </p>

            {loading && <p>{t('loadingSystem')}</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}

            {!loading && !error && (
                <div style={{ display: 'flex', gap: '20px', flexDirection: 'column' }}>

                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>{t('id')}</th>
                            <th>{t('name')}</th>
                            <th>{t('type')}</th>
                            <th>{t('power')}</th>
                            <th>{t('status')}</th>
                            <th>{t('actions')}</th>
                        </tr>
                        </thead>
                        <tbody>
                        {devices.length === 0 ? (
                            <tr><td colSpan="6" style={{ textAlign: 'center' }}>{t('noDevicesFound')}</td></tr>
                        ) : (
                            devices.map(device => (
                                <tr key={device.deviceId} style={{ backgroundColor: selectedDevice?.deviceId === device.deviceId ? '#e0e7ff' : '' }}>
                                    <td>{device.deviceId}</td>
                                    <td style={{ fontWeight: '600', color: 'var(--primary)' }}>{device.name}</td>
                                    <td>{device.type}</td>
                                    <td>{device.maxPowerOutput} kW</td>
                                    <td>
                                        {device.isActive ? (
                                            <span style={{ backgroundColor: '#d1fae5', color: '#065f46', padding: '4px 8px', borderRadius: '12px', fontSize: '0.85em', fontWeight: '600' }}>{t('active')}</span>
                                        ) : (
                                            <span style={{ backgroundColor: '#fee2e2', color: '#991b1b', padding: '4px 8px', borderRadius: '12px', fontSize: '0.85em', fontWeight: '600' }}>{t('inactive')}</span>
                                        )}
                                    </td>
                                    <td>
                                        <button
                                            className="btn btn-info"
                                            style={{ padding: '6px 12px', fontSize: '0.85rem' }}
                                            onClick={() => setSelectedDevice(selectedDevice?.deviceId === device.deviceId ? null : device)}
                                        >
                                            {selectedDevice?.deviceId === device.deviceId ? t('hide') : t('details')}
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>

                    {selectedDevice && (
                        <div style={{
                            marginTop: '10px',
                            padding: '20px',
                            backgroundColor: '#f8fafc',
                            border: '1px solid var(--border)',
                            borderRadius: '8px',
                            borderLeft: '4px solid var(--primary)'
                        }}>
                            <h3 style={{ marginTop: 0, color: 'var(--text-main)' }}>
                                📊 {t('detailsTitle')} {selectedDevice.name}
                            </h3>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px' }}>
                                <div><strong>{t('deviceIdLbl')}</strong> {selectedDevice.deviceId}</div>
                                <div><strong>{t('type')}:</strong> {selectedDevice.type}</div>
                                <div><strong>{t('power')}:</strong> {selectedDevice.maxPowerOutput} kW</div>
                                <div><strong>{t('regDateLbl')}</strong> {formatDateTime(selectedDevice.createdAt, i18n.language)}</div>
                            </div>

                            <h4 style={{ borderBottom: '1px solid var(--border)', paddingBottom: '5px' }}>{t('connectedSensors')}</h4>

                            {(!selectedDevice.sensors || selectedDevice.sensors.length === 0) ? (
                                <p style={{ color: '#6b7280', fontSize: '0.9rem' }}>{t('noSensorsConnected')}</p>
                            ) : (
                                <ul style={{ paddingLeft: '20px' }}>
                                    {selectedDevice.sensors.map(sensor => (
                                        <li key={sensor.sensorId} style={{ marginBottom: '8px' }}>
                                            <strong>{sensor.sensorType}</strong> — {sensor.description || t('noDescription')}
                                            {sensor.unit && <span style={{ color: '#6b7280' }}> ({t('unitLbl')} {sensor.unit})</span>}
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    )}

                </div>
            )}
        </div>
    );
}

export default ClientDashboard;