<template>
  <el-dialog
    class="modal"
    :title="title"
    :visible="visible"
    width="30%"
    :close-on-click-modal="false"
    @update:visible="updateVisible"
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-position="right"
      label-width="80px"
    >
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="form.userName"
          clearable
          placeholder="请输入用户名"
        ></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="form.password"
          show-password
          placeholder="请输入密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="真实姓名" prop="userTrueName">
        <el-input
          v-model="form.userTrueName"
          clearable
          placeholder="请输入用户真实姓名"
        ></el-input>
      </el-form-item>
      <el-form-item label="用户权限" prop="role">
        <el-select
          style="width: 100%;"
          v-model="form.role"
          placeholder="请选择用户权限"
        >
          <el-option label="管理员" :value="1"></el-option>
          <el-option label="用户" :value="0"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="updateVisible(false)">取 消</el-button>
      <el-button type="primary" @click="submitForm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: "UserModal",
  props: {
    visible: Boolean,
    addOrEdit: String,
    userInfo: Object,
  },
  data() {
    return {
      title: "",
      form: {
        userName: "",
        password: "",
        userTrueName: "",
        role: "",
      },
      rules: {
        userName: [
          { required: true, message: "用户名不能为空", trigger: "blur" },
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        userTrueName: [
          { required: true, message: "用户真实姓名不能为空", trigger: "blur" },
        ],
        role: [
          { required: true, message: "用户权限不能为空", trigger: "change" },
        ],
      },
    };
  },
  watch: {
    addOrEdit: {
      handler(val) {
        if (val == "add") {
          this.title = "新建用户";
        } else {
          this.title = "编辑用户";
        }
      },
    },
    visible: {
      handler(val) {
        if (val && this.addOrEdit == "edit") {
          this.$nextTick(() => { // 必须要加，否则表单重置失效
            this.form.userName = this.userInfo.userName;
            this.form.password = this.userInfo.password;
            this.form.userTrueName = this.userInfo.userTrueName;
            this.form.role = this.userInfo.role.value
          })
        }
      },
      immediate: true,
    },
  },
  methods: {
    updateVisible(newVisible) {
      this.$refs.form.resetFields();
      this.$emit("update:visible", newVisible);
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.addOrEdit == "add") {
            this.insertUser();
          } else {
            this.updateUser();
          }
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    // 添加
    async insertUser() {
      try {
        const res = await this.$http.post("/user/insert", {
          userName: this.form.userName,
          password: this.form.password,
          userTrueName: this.form.userTrueName,
          role: this.form.role,
        });
        if (res.data.code == 200) {
          this.$message.success("添加成功");
          this.updateVisible(false);
          this.$parent.queryUserAll();
        } else {
          this.$message.error(res.data.msg);
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 编辑
    async updateUser() {
      try {
        const res = await this.$http.post("/user/update", {
          userId: this.userInfo.userId,
          userName: this.form.userName,
          password: this.form.password,
          userTrueName: this.form.userTrueName,
          role: this.form.role,
        });
        if (res.data.code == 200) {
          this.$message.success("编辑成功");
          this.updateVisible(false);
          this.$parent.queryUserAll();
        } else {
          this.$message.error(res.data.msg);
        }
      } catch (error) {
        console.log(error);
      }
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.modal {
  text-align: left;
}
.input-number {
  width: 100%;
}
.modal >>> .el-dialog__body {
  padding: 30px 40px;
}
.input-number >>> .el-input__inner {
  text-align: left;
}
</style>
