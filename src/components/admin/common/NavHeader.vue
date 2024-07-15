<template>
  <div class="header-layout">
    <div class="flex-center">
      <div class="back-ping" @click="backPing">
        <i class="el-icon-house"></i>
        <span>返回大屏</span>
      </div>
      <el-divider direction="vertical"></el-divider>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ name: 'home_index' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item,index) in levelList" :key="index">
          <span>{{ item.meta.title }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div>
      <el-dropdown @command="handleCommand">
        <div style="cursor: pointer;">
          <el-avatar :src="avatar_src" style="vertical-align: middle;pointer-events: none;"></el-avatar>
          <span class="user-name">{{ user_info.userName }}</span>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="ping">跳转大屏</el-dropdown-item>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NavHeader',
  data () {
    return {
      levelList: null,
      user_info: JSON.parse(localStorage.getItem('user_info')) || '',
      avatar_src: require("../../../../static/images/layout/logo-icon1.png"),
    }
  },
  watch: {
    $route: {
      handler() {
        this.getBreadcrumb()
      },
      immediate: true
    }
  },
  created() {},
  methods: {
    backPing() {
      this.$router.push({ name: 'home' })
    },
    getBreadcrumb() {
      let matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      this.levelList = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
    },
    handleCommand(command) {
      if (command == 'ping') {
        this.$router.push({ name: 'home' })
      } else {
        this.logOut()
      }
    },
    async logOut() {
      try {
        const res = await this.$http.get('/user/loginOutPC')
        if (res.data.code == 200) {
          this.$message.success('退出登录成功')
          localStorage.removeItem('token');
          localStorage.removeItem('user_info');
          this.$router.replace({ name: 'login' })
        }
      } catch (error) {
        console.log(error);
      }
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.header-layout {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.flex-center {
  display: flex;
  align-items: center;
}
.back-ping {
  font-size: 14px; /* no */
  cursor: pointer;
}
.user-name {
  font-size: 14px; /* no */
  margin-left: 12px; /* no */
}
</style>
