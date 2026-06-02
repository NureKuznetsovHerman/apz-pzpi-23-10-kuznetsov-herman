import axios from 'axios';


const baseURL = 'http://localhost:5114/api';

const axiosClient = axios.create({
    baseURL: baseURL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export default axiosClient;