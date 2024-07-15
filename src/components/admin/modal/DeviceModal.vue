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
      <el-form-item label="设备类型" prop="type">
        <el-select
          v-model="form.type"
          clearable
          placeholder="请选择设备类型"
          style="width: 100%"
          :disabled="addOrEdit == 'edit' ? true : false"
        >
          <el-option
            v-for="(item, index) in typeList"
            :key="index"
            :label="`${item.deviceTypeName} (${item.communicationProtocol.name})`"
            :value="item.deviceTypeId"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称" prop="name">
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入设备名称"
        ></el-input>
      </el-form-item>
      <el-form-item label="IP地址" prop="ip">
        <el-input
          v-model="form.ip"
          clearable
          placeholder="请输入IP地址"
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
      <el-form-item
        label="设备状态"
        prop="status"
        v-if="this.addOrEdit == 'edit'"
      >
        <el-switch
          v-model="form.status"
          active-color="#13ce66"
          inactive-color="#ff4949"
          :active-value="1"
          :inactive-value="0"
          active-text="启用"
          inactive-text="禁用"
        ></el-switch>
      </el-form-item>
      <el-form-item label="排序序号" prop="sort">
        <el-input-number
          class="input-number"
          v-model="form.sort"
          controls-position="right"
          :min="0"
          placeholder="请输入排序序号(默认200)"
        ></el-input-number>
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
  name: "DeviceModal",
  props: {
    visible: Boolean,
    addOrEdit: String,
    deviceInfo: Object,
  },
  data() {
    return {
      title: "",
      typeList: [],
      form: {
        name: "",
        type: "",
        ip: "",
        port: 502,
        sort: undefined,
        status: "",
      },
      rules: {
        name: [
          { required: true, message: "设备名称不能为空", trigger: "blur" },
        ],
        type: [
          { required: true, message: "设备类型不能为空", trigger: "change" },
        ],
        ip: [{ required: true, message: "IP地址不能为空", trigger: "blur" }],
        port: [{ required: true, message: "端口号不能为空", trigger: "blur" }],
      },
    };
  },
  watch: {
    addOrEdit: {
      handler(val) {
        if (val == "add") {
          this.title = "新建设备";
        } else {
          this.title = "编辑设备";
        }
      },
    },
    visible: {
      handler(val) {
        if (val) {
          this.getDeviceTypeAll();
          if (this.addOrEdit == "edit") {
            this.$nextTick(() => {
              this.form.status = this.deviceInfo.enableStatus.value;
              this.form.name = this.deviceInfo.deviceName;
              this.form.type = this.deviceInfo.deviceTypeId;
              this.form.ip = this.deviceInfo.ip;
              this.form.port = this.deviceInfo.port;
              this.form.sort = this.deviceInfo.sortNumber;
            })
          }
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
            this.insertDevice();
          } else {
            this.updateDevice();
          }
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    // 查询所有设备类型接口
    async getDeviceTypeAll() {
      try {
        const res = await this.$http.get("/deviceType/queryAll");
        this.typeList = res.data.data;
      } catch (error) {
        console.log(error);
      }
    },
    // 添加设备
    async insertDevice() {
      try {
        const res = await this.$http.post("/device/insert", {
          deviceName: this.form.name,
          deviceTypeId: this.form.type,
          ip: this.form.ip,
          port: this.form.port,
          sortNumber: this.form.sort,
        });
        if (res.data.code == 200) {
          this.$message.success("添加设备成功");
          this.updateVisible(false);
          this.$parent.queryByNameOrTypeId();
        } else {
          this.$message.error(res.data.msg);
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 编辑设备
    async updateDevice() {
      try {
        const res = await this.$http.post("/device/update", {
          deviceName: this.form.name,
          deviceTypeId: this.form.type,
          ip: this.form.ip,
          port: this.form.port,
          sortNumber: this.form.sort == "" ? null : this.form.sort,
          deviceId: this.deviceInfo.deviceId,
          enableStatus: this.form.status,
        });
        if (res.data.code == 200) {
          this.$message.success("编辑设备成功");
          this.updateVisible(false);
          this.$parent.queryByNameOrTypeId();
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
