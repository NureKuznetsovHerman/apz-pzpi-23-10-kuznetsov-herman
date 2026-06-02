import axiosClient from './axiosClient';

export const sensorService = {
    getAllSensors: async () => {
        const response = await axiosClient.get('/sensors');
        return response.data;
    },
    createSensor: async (sensorData) => {
        const response = await axiosClient.post('/sensors', sensorData);
        return response.data;
    },
    updateSensor: async (id, sensorData) => {
        // ДОДАНО: Метод для оновлення сенсора
        const response = await axiosClient.put(`/sensors/${id}`, sensorData);
        return response.data;
    },
    deleteSensor: async (id) => {
        const response = await axiosClient.delete(`/sensors/${id}`);
        return response.data;
    }
};