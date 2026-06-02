import React, { useEffect, useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { userService } from '../../api/userService';
import { formatDateTime } from '../../utils/formatDate';
import AdminDeviceManager from '../../components/AdminDeviceManager';
import AdminSensorManager from '../../components/AdminSensorManager';

function AdminDashboard() {
    const { t, i18n } = useTranslation();
    const [activeTab, setActiveTab] = useState('devices');
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const fileInputRef = useRef(null);

    const [userForm, setUserForm] = useState({ username: '', passwordHash: '', fullName: '', role: 'user' });

    useEffect(() => {
        if (activeTab === 'users') fetchUsers();
    }, [activeTab]);

    const fetchUsers = async () => {
        setLoading(true);
        try { setUsers(await userService.getAllUsers()); }
        catch (err) { console.error(err); }
        setLoading(false);
    };

    const handleUserSubmit = async (e) => {
        e.preventDefault();
        try {
            await userService.createUser(userForm);
            setUserForm({ username: '', passwordHash: '', fullName: '', role: 'user' });
            fetchUsers();
        } catch (error) { console.error(error); }
    };

    const handleDeleteUser = async (id) => {
        if (window.confirm(t('confirmDelete'))) {
            await userService.deleteUser(id);
            fetchUsers();
        }
    };

    const handleExport = () => {
        const blob = new Blob([JSON.stringify(users, null, 2)], { type: 'application/json' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `backup_users.json`;
        link.click();
    };

    const handleImport = (e) => {
        const reader = new FileReader();
        reader.onload = (event) => {
            try { setUsers(JSON.parse(event.target.result)); }
            catch (err) { alert('File Error'); }
        };
        if(e.target.files[0]) reader.readAsText(e.target.files[0]);
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2 style={{ margin: 0 }}>🛡️ {t('adminPanel')}</h2>

                <div style={{ display: 'flex', gap: '10px' }}>
                    <button className={`btn ${activeTab === 'devices' ? 'btn-info' : ''}`} style={{ backgroundColor: activeTab === 'devices' ? 'var(--primary)' : '#e5e7eb', color: activeTab === 'devices' ? 'white' : 'black' }} onClick={() => setActiveTab('devices')}>{t('generatorsTab')}</button>
                    <button className={`btn ${activeTab === 'sensors' ? 'btn-info' : ''}`} style={{ backgroundColor: activeTab === 'sensors' ? 'var(--primary)' : '#e5e7eb', color: activeTab === 'sensors' ? 'white' : 'black' }} onClick={() => setActiveTab('sensors')}>{t('sensorsTab')}</button>
                    <button className={`btn ${activeTab === 'users' ? 'btn-info' : ''}`} style={{ backgroundColor: activeTab === 'users' ? 'var(--primary)' : '#e5e7eb', color: activeTab === 'users' ? 'white' : 'black' }} onClick={() => setActiveTab('users')}>{t('usersTab')}</button>
                </div>
            </div>

            {activeTab === 'devices' && <AdminDeviceManager />}
            {activeTab === 'sensors' && <AdminSensorManager />}

            {activeTab === 'users' && (
                <div>
                    <div className="form-container">
                        <h3>{t('createNewUser')}</h3>
                        <form onSubmit={handleUserSubmit} className="form-grid">
                            <div>
                                <label>{t('username')}</label>
                                <input type="text" className="input-field" value={userForm.username} onChange={e => setUserForm({...userForm, username: e.target.value})} required />
                            </div>
                            <div>
                                <label>{t('fullName')}</label>
                                <input type="text" className="input-field" value={userForm.fullName} onChange={e => setUserForm({...userForm, fullName: e.target.value})} required />
                            </div>
                            <div>
                                <label>{t('password')}</label>
                                <input type="password" className="input-field" value={userForm.passwordHash} onChange={e => setUserForm({...userForm, passwordHash: e.target.value})} required />
                            </div>
                            <div>
                                <label>{t('role')}</label>
                                <select className="input-field" value={userForm.role} onChange={e => setUserForm({...userForm, role: e.target.value})}>
                                    <option value="user">{t('roleUser')}</option>
                                    <option value="admin">{t('roleAdmin')}</option>
                                    <option value="operator">{t('roleOp')}</option>
                                </select>
                            </div>
                            <button type="submit" className="btn btn-success" style={{ gridColumn: '1 / -1', width: '200px' }}>{t('createUserBtn')}</button>
                        </form>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '20px 0 10px 0' }}>
                        <h3>{t('usersList')}</h3>
                        <div>
                            <button className="btn btn-success" onClick={handleExport} style={{ marginRight: '10px' }}>{t('exportBackup')}</button>
                            <button className="btn btn-info" onClick={() => fileInputRef.current.click()}>{t('importBtn')}</button>
                            <input type="file" accept=".json" ref={fileInputRef} style={{ display: 'none' }} onChange={handleImport} />
                        </div>
                    </div>

                    {loading ? <p>Loading...</p> : (
                        <table className="data-table">
                            <thead>
                            <tr><th>{t('id')}</th><th>Username</th><th>{t('fullName')}</th><th>{t('role')}</th><th>{t('regDate')}</th><th>{t('actions')}</th></tr>
                            </thead>
                            <tbody>
                            {users.map(user => (
                                <tr key={user.id}>
                                    <td>{user.id}</td>
                                    <td>{user.username}</td>
                                    <td style={{ fontWeight: '500' }}>{user.fullName}</td>
                                    <td><span style={{ backgroundColor: user.role === 'admin' ? '#fee2e2' : '#e0e7ff', color: user.role === 'admin' ? '#991b1b' : '#3730a3', padding: '3px 8px', borderRadius: '10px', fontSize: '0.85em' }}>{user.role}</span></td>
                                    <td>{formatDateTime(user.createdAt, i18n.language)}</td>
                                    <td><button className="btn btn-danger" onClick={() => handleDeleteUser(user.id)} style={{ padding: '4px 8px', fontSize: '0.8rem' }}>{t('delete')}</button></td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}
        </div>
    );
}

export default AdminDashboard;