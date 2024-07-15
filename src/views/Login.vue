<template>
  <div class="layout-bg">
    <div class="login-bg">
      <h1>欢迎登录金科星SCADA系统</h1>
      <h3>WELCOME TO JINKEXING SCADA SYSTEM</h3>
      <div class="form-box" >
        <el-form
          v-if="is_active"
          :model="userInfo"
          status-icon
          :rules="rules"
          ref="userInfo"
          class="user-info"
        >
          <el-form-item prop="account">
            <el-input
              class="input-box"
              prefix-icon="el-icon-user"
              placeholder="请输入账号"
              v-model="userInfo.account"
              autocomplete="off"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              class="input-box"
              prefix-icon="el-icon-unlock"
              placeholder="请输入密码"
              v-model="userInfo.password"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item class="item-box">
            <div class="login-button" @keyup.enter="keyDown" @click="submitForm('userInfo')">用户登录</div>
          </el-form-item>
          <!-- <el-form-item>
            <div class="login-button cancel-button" @click="resetForm('userInfo')">取消</div>
          </el-form-item> -->
        </el-form>
        <el-form
          v-else
          class="user-info"
        >
          <el-form-item>
            <el-input
              class="input-box"
              placeholder="系统未激活，请填写激活码或联系管理员"
              disabled
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              class="input-box"
              placeholder="请输入激活码"
              v-model="active"
            ></el-input>
          </el-form-item>
          <el-form-item class="item-box">
            <div class="login-button" @click="register">立即激活</div>
          </el-form-item>
        </el-form>
      </div>
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
      imageUrl: '',
      is_active: false,
      active: ''
    };
  },
  created() {
    this.getVerify()
  },
  mounted() {
    this.getTitleImage()
    //绑定事件
    window.addEventListener('keydown',this.keyDown);
  },
  destroyed(){
    //销毁事件
    window.removeEventListener('keydown',this.keyDown,false);
  },
  methods: {
    //键盘事件
    keyDown(e) {
      //如果是回车则执行登录方法
      if (e.keyCode === 13) {
        //需要执行的登录方法
        this.submitForm('userInfo');
      }
    },
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
    resetForm(formName) {
      this.$refs[formName].resetFields();
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
          localStorage.setItem('token', result.data.tokenInfo.tokenValue);
          localStorage.setItem('user_info', JSON.stringify(result.data.user));
          localStorage.setItem('mapInfo', JSON.stringify({x: 0, y: 0, scale: 1}));
          this.$message.success('登录成功');
          if (this.$route.params.to) {
            this.$router.go(-1)
          } else {
            this.$router.push({ name: 'home' })
          }
        } else {
          this.$message.error(result.msg);
        }
      } catch (error) {
        console.log(error)
      }
    },
    // 查询主界面标题与地图路径接口
    async getTitleImage() {
      try {
        const res = await this.$http.get('/system/query');
        this.imageUrl = res.data.data.mapPath;
        this.preloadImage(this.imageUrl)
        localStorage.setItem('system', JSON.stringify(res.data.data))
        if (res.data.data.websocketURL.indexOf('ws://') == -1) {
          this.$message.error(res.data.data.websocketURL)
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 预加载图片
    preloadImage(url) {
      return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = () => reject(new Error('图片加载失败'));
        img.src = url;
      });
    },
    // 验证使用权限接口
    async getVerify() {
      try {
        const res = await this.$http.get('/system/verify')
        if (res.data.code == 200) {
          this.is_active = true
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 注册系统使用权限接口
    async register() {
      try {
        const res = await this.$http.post('/system/register', {
          activationCode: this.active
        })
        if (res.data.code == 200) {
          this.$message.success('激活成功')
          this.is_active = true
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.layout-bg {
  width: 100%;
  height: 100%;
  background: linear-gradient(
    180deg,
    rgba(16, 39, 100, 1) 0%,
    rgba(12, 33, 80, 1) 100%
  );
  background: linear-gradient(
    180deg,
    rgba(16, 39, 100, 1) 0%,
    rgba(12, 33, 80, 1) 100%
  );
}

.login-bg {
  width: 100%;
  height: 100%;
  /* background-size: cover; */
  background-repeat: no-repeat;
  background-position: center bottom;
  background-image: url("../../static/images/login/login-bg.png");
}

h1 {
  font-family: cursive;
  font-size: 48px;
  font-weight: bold;
  letter-spacing: 4px;
  line-height: 78px;
  background: linear-gradient(
    180deg,
    rgba(230, 236, 255, 1) 70%,
    rgba(50, 100, 237, 1) 100%
  );
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-align: center;
  vertical-align: top;
  padding-top: 6%;
  text-shadow: 0px 2px 4px rgba(0, 0, 0, 0.25);
}

h3 {
  font-family: cursive;
  font-size: 24px;
  font-weight: 300;
  text-align: center;
  vertical-align: top;
  background: linear-gradient(
    180deg,
    rgba(230, 236, 255, 1) 0%,
    rgba(50, 100, 237, 1) 100%
  );
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.form-box {
  width: 500px;
  height: 500px;
  background-repeat: no-repeat;
  background-size: cover;
  background-image: url("../../static/images/login/form-bg.png");
  margin: 20px auto;
}

.user-info {
  width: 340px;
  height: 260px;
  margin: 0 auto;
  padding-top: 150px;
}
.item-box {
  margin-top: 50px;
}
.input-box >>> .el-input__inner {
  background: transparent !important;
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
.cancel-button {
  background: #fff;
  color: #606266;
}

</style>
