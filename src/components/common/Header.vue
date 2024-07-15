<template>
  <div class="header-bg">
    <div class="date-box">
      <span class="current-time">{{ current_time }}</span>
      <span class="date-time">{{ date_time }}</span>
      <span class="week-day">{{ week_day }}</span>
    </div>
    <p class="header-title">{{ system.title }}</p>
    <div class="user-box">
      <el-dropdown @command="handleCommand">
        <div style="cursor: pointer;">
          <el-avatar :src="avatar_src" style="vertical-align: middle;pointer-events: none;"></el-avatar>
          <span class="user-name">{{ user_info.userName }}</span>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="admin">跳转后台</el-dropdown-item>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Header',
  data () {
    return {
      date_time: '', // 年月日
      current_time: '', // 小时分钟秒
      week_day: '', // 星期
      time_interval: null, // 时间定时器
      user_info: JSON.parse(localStorage.getItem('user_info')) || '',
      avatar_src: require("../../../static/images/layout/logo-icon1.png"),
      system: JSON.parse(localStorage.getItem('system')),
    }
  },
  created() {
    this.time_interval = setInterval(this.getCurrentTime, 1000)
  },
  methods: {
    // 获取当前时间
    getCurrentTime() {
      let date = new Date();
      let day = date.getDay();
      let year = date.getFullYear();
      let month = date.getMonth() + 1;
      let today = date.getDate();
      let hour = date.getHours();
      let minute = date.getMinutes();
      let second = date.getSeconds();
      this.current_time = this.fillZero(hour) + ":" + this.fillZero(minute) + ":" + this.fillZero(second);
      this.date_time = this.fillZero(year) + "/" + this.fillZero(month) + "/" + this.fillZero(today);
      var weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      this.week_day = weekDays[day];
    },
    // 日期格式化方法
    fillZero(str) {
      var realNum;
      if (str < 10) {
        realNum = '0' + str;
      } else {
        realNum = str;
      }
      return realNum
    },
    handleCommand(command) {
      if (command == 'admin') {
        // window.open(this.$router.resolve({ name: 'home_index' }).href)
        this.$router.push({ name: 'home_index' })
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
  },
  // 销毁计时器
  beforeDestroy() {
    if (this.time_interval) {
      clearInterval(this.time_interval)
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.header-bg {
  width: 100%;
  height: 75px;
  background-size: cover;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/header-bg.png");
  position: relative;
}
.ray {
  width: 360px;
  height: 36px;
  background-size: cover;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/ray.png");
  position: absolute;
  top: 50px;
  left: calc(50% - 180px);
}
.date-box {
  position: absolute;
  left: 36px;
  top: 12px;
  color: rgba(99, 216, 255, 1);
  display: flex;
  align-items: center;
}
.current-time {
  font-size: 24px;
  margin-right: 24px;
}
.date-time {
  font-size: 18px;
  margin-right: 8px;
}
.week-day {
  font-size: 16px;
  line-height: 1;
}
.header-title {
  font-family: cursive;
  font-size: 36px;
  /* color: rgba(99, 216, 255, 1); */
  color: #fff;
  text-align: center;
  vertical-align: top;
  padding-top: 16px;
}
.user-box {
  position: absolute;
  right: 36px;
  top: 12px;
  display: flex;
  align-items: center;
}
.user-name {
  font-size: 18px;
  color: #fff;
  margin-left: 12px;
}
</style>
