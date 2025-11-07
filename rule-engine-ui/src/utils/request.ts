import axios, { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

// 常用header
export enum CommonHeaderEnum {
	'TENANT_ID' = 'TENANT-ID',
	'ENC_FLAG' = 'Enc-Flag',
	'AUTHORIZATION' = 'Authorization',
	'VERSION' = 'VERSION',
}

/**
 * 创建并配置一个 Axios 实例对象
 */
const service: AxiosInstance = axios.create({
	baseURL: import.meta.env.VITE_API_ADMIN_URL,
	timeout: 50000
});
// 导出 axios 实例
export default service;
