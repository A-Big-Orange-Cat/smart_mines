<template>
  <div class="login-bg">
    <div class="form-box">
      <div class="logo"></div>
      <div class="form-title">系统平台登录</div>
      <el-form :model="userInfo" :rules="rules" ref="userInfo" class="user-info">
        <el-form-item prop="account">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="userInfo.account"
            autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-unlock" placeholder="请输入密码" v-model="userInfo.password"
            show-password></el-input>
        </el-form-item>
        <el-form-item class="item-box">
          <div class="login-button" @click="submitForm('userInfo')">用户登录</div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      userInfo: {
        account: "",
        password: "",
      },
      rules: {
        account: [{ required: true, message: "账号不能为空", trigger: "blur" }],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // alert("submit!");
          this.login()
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    //调用登录接口
    async login() {
      try {
        const res = await this.$http.post('/user/loginPC', {
          userName: this.userInfo.account,
          password: this.userInfo.password
        })
        const result = res.data;
        console.log('登录成功', result);
        if (result.code === 200) {
          localStorage.setItem('token', result.data.tokenValue);
          localStorage.setItem('user_info', JSON.stringify(result.data));
          this.$message.success('登录成功');
          this.$router.push({
            name: 'home'
          })
        } else {
          this.$message.error(result.msg);
        }
      } catch (error) {
        console.log(error)
      }
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.login-bg {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  background-image: url("../../static/images/login/login-bg1.png");

  display: flex;
  align-items: center;
  justify-content: end;
}

.form-box {
  width: 400px;
  height: 400px;
  background: #fff;
  border-radius: 10px;
  margin-right: 200px;
}

.logo {
  width: 245px;
  height: 60px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../static/images/login/logo.png");
  margin: 20px auto 10px;
}

.form-title {
  font-size: 24px;
  color: #000;
  position: relative;
}

.form-title::after {
  content: "";
  position: absolute;
  left: 130px;
  bottom: -8px;
  width: 140px;
  height: 4px;
  background: #007AFF;
}

.user-info {
  width: 340px;
  height: 260px;
  margin: 0 auto;
  padding-top: 40px;
}

.item-box {
  margin-top: 40px;
}

.login-button {
  width: 100%;
  border-radius: 6px;
  background: rgba(99, 141, 255, 1);
  cursor: pointer;
  font-size: 16px;
  color: #fff;
  letter-spacing: 2px;
}

.login-button:hover {
  box-shadow: 0 0 2px 0 rgb(232 237 250 / 60%), 0 1px 2px 0 rgb(232 237 250 / 50%);
}
</style>
