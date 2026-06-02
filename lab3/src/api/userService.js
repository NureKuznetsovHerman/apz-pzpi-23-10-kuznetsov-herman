import axiosClient from './axiosClient';

export const userService = {
    getAllUsers: async () => {
        const response = await axiosClient.get('/users');
        return response.data;
    },
    createUser: async (userData) => {
        const response = await axiosClient.post('/users', userData);
        return response.data;
    },
    updateUser: async (id, userData) => {
        const response = await axiosClient.put(`/users/${id}`, userData);
        return response.data;
    },
    deleteUser: async (id) => {
        const response = await axiosClient.delete(`/users/${id}`);
        return response.data;
    }
};