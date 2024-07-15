import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/Login'

Vue.use(Router)

// 路由重复跳转
const VueRouterPush = Router.prototype.push
Router.prototype.push = function push (to) {
  return VueRouterPush.call(this, to).catch(err => err)
}

const router = new Router({
  scrollBehavior (to, from, savedPosition) {
    return { x: 0, y: 0 }
  },
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login/:to?',
      name: 'login',
      component: () => import('@/views/Login'),
      meta: {
        title: '登录'
      }
    },
    {
      path: '/layout',
      component: () => import('@/components/common/Layout'),
      redirect: '/layout/home',
      children: [
        {
          path: 'home',
          name: 'home',
          component: () => import('@/views/Home'),
          meta: {
            title: '首页'
          }
        },
        {
          path: 'device-list',
          name: 'deviceList',
          component: () => import('@/views/DeviceList'),
          meta: {
            title: '设备列表'
          }
        },
        {
          path: 'warn-list',
          name: 'warnList',
          component: () => import('@/views/WarnList'),
          meta: {
            title: '报警列表'
          }
        },
        {
          path: 'device-detail/:name/:deviceId/:deviceTypeId',
          name: 'deviceDetail',
          component: () => import('@/views/DeviceDetail'),
          meta: {
            title: '设备详情'
          }
        },
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/components/admin/common/Layout'),
      redirect: '/admin/home-index',
      children: [
        {
          path: 'home-index',
          name: 'home_index',
          component: () => import('@/views/admin/Home'),
          meta: {
            title: '首页',
            icon: 'el-icon-s-home',
            breadcrumb: false
          }
        },
        {
          path: 'device-mange',
          name: 'device_manage',
          component: () => import('@/views/admin/DeviceManage'),
          meta: {
            title: '设备管理',
            icon: 'el-icon-s-platform'
          }
        },
        {
          path: 'set-camera/:deviceId/:deviceName',
          name: 'set_camera',
          component: () => import('@/views/admin/SetCamera'),
          meta: {
            title: '设备管理',
            icon: 'el-icon-s-platform'
          },
          hidden: true
        },
        {
          path: 'device-type-manage',
          name: 'device_type_manage',
          component: () => import('@/views/admin/DeviceTypeManage'),
          meta: {
            title: '设备类型管理',
            icon: 'el-icon-menu'
          }
        },
        {
          path: 'device-param-manage/:deviceTypeId/:deviceTypeName',
          name: 'device_param_manage',
          component: () => import('@/views/admin/DeviceParamManage'),
          meta: {
            title: '设备类型管理',
            icon: 'el-icon-menu'
          },
          hidden: true
        },
        {
          path: 'user-manage',
          name: 'user_manage',
          component: () => import('@/views/admin/UserManage'),
          meta: {
            title: '用户管理',
            icon: 'el-icon-user-solid'
          }
        },
        {
          path: 'system-manage',
          name: 'system_manage',
          component: () => import('@/views/admin/SystemManage'),
          meta: {
            title: '系统管理',
            icon: 'el-icon-s-tools'
          }
        },
      ]
    },
  ],
  // mode:'history'
})

// 路由导航守卫 判断是否登录
router.beforeEach((to, from, next) => {
  document.title = to.meta.title + ' | 山东金科星机电股份有限公司'
  if (to.name === 'login') {
    next();
  } else {
    let token = localStorage.getItem('token');
    if (token === null || token === '') {
      next({
        name: 'login',
        params: {
          to: -1
        }
      });
    } else {
      next()
    }
  }
})

export default router
