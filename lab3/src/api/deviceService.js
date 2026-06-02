import axiosClient from './axiosClient';

export const deviceService = {
    getAllDevices: async () => {
        const response = await axiosClient.get('/devices');
        return response.data;
    },

    createDevice: async (deviceData) => {
        const response = await axiosClient.post('/devices', deviceData);
        return response.data;
    },

    updateDevice: async (id, deviceData) => {
        const response = await axiosClient.put(`/devices/${id}`, deviceData);
        return response.data;
    },

    deleteDevice: async (id) => {
        const response = await axiosClient.delete(`/devices/${id}`);
        return response.data;
    }
};