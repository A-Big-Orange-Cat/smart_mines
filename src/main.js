// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '../static/config/flexible'
import http from '../static/config/http'
import * as echarts from 'echarts'
// import wfs from '../static/config/wfs2'
import md5 from 'js-md5';

Vue.use(ElementUI);
Vue.config.productionTip = false
Vue.prototype.$http = http;
Vue.prototype.$echarts = echarts;
// Vue.prototype.$wfs = wfs;
Vue.prototype.$md5 = md5;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
