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
      label-width="70px"
    >
      <el-form-item label="ip地址" prop="ip">
        <el-input
          v-model="form.ip"
          clearable
          placeholder="请输入ip地址"
        ></el-input>
      </el-form-item>
      <el-form-item label="端口号" prop="port">
        <el-input-number
          class="input-number"
          v-model="form.port"
          controls-position="right"
          :min="0"
          placeholder="请输入端口号"
        ></el-input-number>
      </el-form-item>
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
          clearable
          placeholder="请输入密码"
        ></el-input>
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
  name: "CameraModal",
  props: {
    visible: Boolean,
    addOrEdit: String,
    cameraInfo: Object,
  },
  data() {
    return {
      title: "",
      form: {
        ip: "",
        port: 8000,
        userName: "",
        password: "",
      },
      rules: {
        ip: [{ required: true, message: "ip地址不能为空", trigger: "blur" }],
        port: [{ required: true, message: "端口号不能为空", trigger: "blur" }],
        userName: [
          { required: true, message: "用户名不能为空", trigger: "blur" },
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
      },
    };
  },
  watch: {
    addOrEdit: {
      handler(val) {
        if (val == "add") {
          this.title = "新建摄像头";
        } else {
          this.title = "编辑摄像头";
        }
      },
    },
    visible: {
      handler(val) {
        if (val && this.addOrEdit == "edit") {
          this.$nextTick(() => {
            this.form.ip = this.cameraInfo.ip;
            this.form.port = this.cameraInfo.port;
            this.form.userName = this.cameraInfo.userName;
            this.form.password = this.cameraInfo.password;
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
            this.insertCamera();
          } else {
            this.updateCamera();
          }
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    // 添加
    async insertCamera() {
      console.log("deviceId:", this.cameraInfo.deviceId);
      try {
        const res = await this.$http.post("/hik/insert", {
          deviceId: Number(this.$route.params.deviceId),
          ip: this.form.ip,
          port: this.form.port,
          userName: this.form.userName,
          password: this.form.password,
        });
        if (res.data.code == 200) {
          this.$message.success("添加成功");
          this.updateVisible(false);
          this.$parent.queryCamera();
        } else {
          this.$message.error(res.data.msg);
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 编辑设备类型
    async updateCamera() {
      try {
        const res = await this.$http.post("/hik/update", {
          deviceId: Number(this.$route.params.deviceId),
          dvrId: this.cameraInfo.dvrId,
          ip: this.form.ip,
          port: this.form.port,
          userName: this.form.userName,
          password: this.form.password,
        });
        if (res.data.code == 200) {
          this.$message.success("编辑成功");
          this.updateVisible(false);
          this.$parent.queryCamera();
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
.modal >>> .el-dialog__body {
  padding: 30px 40px;
}
.input-number {
  width: 100%;
}
.input-number >>> .el-input__inner {
  text-align: left;
}
</style>
