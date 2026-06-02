import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { deviceService } from '../api/deviceService';

function AdminDeviceManager() {
    const { t } = useTranslation(); // <-- ДОДАЛИ ХУК ЛОКАЛІЗАЦІЇ
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingId, setEditingId] = useState(null);

    const [formData, setFormData] = useState({
        name: '', type: 'Solar Panel', maxPowerOutput: 0, isActive: true
    });

    useEffect(() => { fetchData(); }, []);

    const fetchData = async () => {
        setLoading(true);
        try { setDevices(await deviceService.getAllDevices()); }
        catch (error) { console.error(error); }
        setLoading(false);
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({ ...formData, [name]: type === 'checkbox' ? checked : value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingId) await deviceService.updateDevice(editingId, formData);
            else await deviceService.createDevice(formData);
            setFormData({ name: '', type: 'Solar Panel', maxPowerOutput: 0, isActive: true });
            setEditingId(null);
            fetchData();
        } catch (error) { alert('Error!'); }
    };

    const handleEdit = (device) => {
        setEditingId(device.deviceId);
        setFormData({ name: device.name, type: device.type, maxPowerOutput: device.maxPowerOutput || 0, isActive: device.isActive });
    };

    const handleDelete = async (id) => {
        if (window.confirm('Видалити? / Delete?')) {
            await deviceService.deleteDevice(id);
            fetchData();
        }
    };

    return (
        <div>
            <div className="form-container">
                {/* Замінюємо хардкод на t() */}
                <h3>{editingId ? t('editGenerator') : t('addGenerator')}</h3>
                <form onSubmit={handleSubmit}>
                    <div className="form-grid">
                        <div>
                            <label>{t('deviceName')}</label>
                            <input type="text" name="name" className="input-field" value={formData.name} onChange={handleInputChange} required />
                        </div>
                        <div>
                            <label>{t('deviceType')}</label>
                            <select name="type" className="input-field" value={formData.type} onChange={handleInputChange}>
                                <option value="Solar Panel">Solar Panel</option>
                                <option value="Wind Turbine">Wind Turbine</option>
                                <option value="Diesel Generator">Diesel Generator</option>
                                <option value="Grid Connection">Grid Connection</option>
                            </select>
                        </div>
                        <div>
                            <label>{t('maxPower')}</label>
                            <input type="number" name="maxPowerOutput" className="input-field" value={formData.maxPowerOutput} onChange={handleInputChange} min="0" step="0.1" required />
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: '10px' }}>
                            <input type="checkbox" name="isActive" checked={formData.isActive} onChange={handleInputChange} style={{ width: '20px', height: '20px' }} />
                            <label>{t('isActive')}</label>
                        </div>
                    </div>

                    <div style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
                        <button type="submit" className="btn btn-success">
                            {editingId ? t('saveChanges') : t('createDevice')}
                        </button>
                        {editingId && (
                            <button type="button" className="btn btn-warning" onClick={() => setEditingId(null)}>
                                {t('cancel')}
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <h3>{t('generatorsList')}</h3>
            {loading ? <p>Loading...</p> : (
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
                    {devices.map(dev => (
                        <tr key={dev.deviceId}>
                            <td>{dev.deviceId}</td>
                            <td style={{ fontWeight: 'bold' }}>{dev.name}</td>
                            <td>{dev.type}</td>
                            <td>{dev.maxPowerOutput} kW</td>
                            <td>{dev.isActive ? t('active') : t('inactive')}</td>
                            <td>
                                <button className="btn btn-info" onClick={() => handleEdit(dev)} style={{ padding: '6px 10px', marginRight: '5px', fontSize: '0.8rem' }}>{t('edit')}</button>
                                <button className="btn btn-danger" onClick={() => handleDelete(dev.deviceId)} style={{ padding: '6px 10px', fontSize: '0.8rem' }}>{t('delete')}</button>
                            </td>
                        </tr>
                    ))}
                    {devices.length === 0 && <tr><td colSpan="6" style={{ textAlign: 'center' }}>{t('noDevices')}</td></tr>}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default AdminDeviceManager;