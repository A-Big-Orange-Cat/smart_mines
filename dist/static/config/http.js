import axios from 'axios';
import router from '../../src/router';
import { Message } from 'element-ui'

// axios 配置
axios.defaults.timeout = 8000;
// axios.defaults.baseURL = 'http://192.168.11.11:8090'; // 127.0.0.1 // 192.168.11.11
axios.defaults.baseURL = ''

// http request 拦截器
axios.interceptors.request.use(
  config => {
    // 从本地存储获取token
    const token = localStorage.getItem('token');
    if (token) { // 判断token是否存在
      // config.headers.Authorization = token;  // 将token设置成请求头 `Bearer ${token}`
      config.headers.satoken = token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);


// http response 拦截器
let flag = 0;
axios.interceptors.response.use(
  response => {
    if (response.data.code === 401) {
      console.log('666666', flag);
      if (flag++ == 0) {
        setTimeout(() => {
          flag = 0
        }, 2000)
        localStorage.removeItem('token');
        router.push({
          name: 'login',
          params: {
            to: -1
          }
        });
        Message({
          message: response.data.msg,
          type: 'error'
        });
      }
    }
    return response;
  },
  error => {
    return Promise.reject(error);
  }
);

export default axios;
