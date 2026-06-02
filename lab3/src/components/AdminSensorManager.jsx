import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { sensorService } from '../api/sensorService';
import { deviceService } from '../api/deviceService';

function AdminSensorManager() {
    const { t } = useTranslation();
    const [sensors, setSensors] = useState([]);
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingSensorId, setEditingSensorId] = useState(null);

    const [formData, setFormData] = useState({
        deviceId: '', sensorType: 'power', unit: 'kW', description: ''
    });

    useEffect(() => { fetchData(); }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [sensorsData, devicesData] = await Promise.all([
                sensorService.getAllSensors().catch(() => []),
                deviceService.getAllDevices()
            ]);
            setSensors(sensorsData);
            setDevices(devicesData);
            if (devicesData.length > 0 && !editingSensorId) {
                setFormData(prev => ({ ...prev, deviceId: devicesData[0].deviceId }));
            }
        } catch (error) { console.error(error); }
        setLoading(false);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: name === 'deviceId' ? parseInt(value, 10) : value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (editingSensorId) {
            await sensorService.updateSensor(editingSensorId, formData);
            setEditingSensorId(null);
        } else {
            await sensorService.createSensor(formData);
        }
        setFormData({ ...formData, description: '' });
        fetchData();
    };

    const handleEdit = (sensor) => {
        setEditingSensorId(sensor.sensorId);
        setFormData({ deviceId: sensor.deviceId, sensorType: sensor.sensorType, unit: sensor.unit || '', description: sensor.description || '' });
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleCancelEdit = () => {
        setEditingSensorId(null);
        if (devices.length > 0) setFormData({ deviceId: devices[0].deviceId, sensorType: 'power', unit: 'kW', description: '' });
    };

    const handleDelete = async (id) => {
        if (window.confirm(t('confirmDelete'))) {
            await sensorService.deleteSensor(id);
            fetchData();
        }
    };

    const getDeviceName = (deviceId) => {
        const device = devices.find(d => d.deviceId === deviceId);
        return device ? device.name : `ID: ${deviceId}`;
    };

    return (
        <div>
            <div className="form-container" style={{ border: editingSensorId ? '2px solid var(--primary)' : '1px solid var(--border)' }}>
                <h3>{editingSensorId ? `${t('editSensor')} (ID: ${editingSensorId})` : t('addSensor')}</h3>
                {devices.length === 0 ? (
                    <p style={{ color: 'red', fontWeight: 'bold' }}>{t('noGenerators')}</p>
                ) : (
                    <form onSubmit={handleSubmit}>
                        <div className="form-grid">
                            <div>
                                <label>{t('linkToGenerator')}</label>
                                <select name="deviceId" className="input-field" value={formData.deviceId} onChange={handleInputChange} required>
                                    {devices.map(d => <option key={d.deviceId} value={d.deviceId}>{d.name} (ID: {d.deviceId})</option>)}
                                </select>
                            </div>
                            <div>
                                <label>{t('sensorType')}</label>
                                <select name="sensorType" className="input-field" value={formData.sensorType} onChange={handleInputChange}>
                                    <option value="power">{t('powerUnit')}</option>
                                    <option value="voltage">{t('voltageUnit')}</option>
                                    <option value="temperature">{t('tempUnit')}</option>
                                    <option value="rpm">{t('rpmUnit')}</option>
                                </select>
                            </div>
                            <div>
                                <label>{t('unit')}</label>
                                <input type="text" name="unit" className="input-field" value={formData.unit} onChange={handleInputChange} required />
                            </div>
                            <div>
                                <label>{t('description')}</label>
                                <input type="text" name="description" className="input-field" value={formData.description} onChange={handleInputChange} />
                            </div>
                        </div>
                        <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                            <button type="submit" className="btn btn-success">{editingSensorId ? t('saveChanges') : t('createAndConnect')}</button>
                            {editingSensorId && <button type="button" className="btn btn-danger" onClick={handleCancelEdit}>{t('cancel')}</button>}
                        </div>
                    </form>
                )}
            </div>

            <h3>{t('connectedSensorsList')}</h3>
            {loading ? <p>Loading...</p> : (
                <table className="data-table">
                    <thead>
                    <tr><th>{t('sensorId')}</th><th>{t('linkedGenerator')}</th><th>{t('type')}</th><th>{t('unit')}</th><th>{t('description')}</th><th>{t('actions')}</th></tr>
                    </thead>
                    <tbody>
                    {sensors.map(s => (
                        <tr key={s.sensorId} style={{ backgroundColor: editingSensorId === s.sensorId ? '#fef3c7' : '' }}>
                            <td>{s.sensorId}</td>
                            <td style={{ fontWeight: '600', color: 'var(--primary)' }}>{getDeviceName(s.deviceId)}</td>
                            <td><span style={{ backgroundColor: '#e0e7ff', color: '#3730a3', padding: '4px 8px', borderRadius: '6px', fontSize: '0.85em', fontWeight: 'bold' }}>{s.sensorType.toUpperCase()}</span></td>
                            <td>{s.unit}</td>
                            <td>{s.description || '—'}</td>
                            <td>
                                <div style={{ display: 'flex', gap: '5px' }}>
                                    <button className="btn btn-info" onClick={() => handleEdit(s)} style={{ padding: '4px 8px', fontSize: '0.8rem' }}>{t('edit')}</button>
                                    <button className="btn btn-danger" onClick={() => handleDelete(s.sensorId)} style={{ padding: '4px 8px', fontSize: '0.8rem' }}>{t('delete')}</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    {sensors.length === 0 && <tr><td colSpan="6" style={{ textAlign: 'center' }}>{t('noSensors')}</td></tr>}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default AdminSensorManager;