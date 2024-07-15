<template>
  <div>
    <div class="box-card">
      <el-form ref="form" :inline="true" :model="formInfo" class="form-inline">
        <el-form-item label="设备类型名称" prop="name">
          <el-input v-model="formInfo.name" placeholder="请输入设备类型名称"></el-input>
        </el-form-item>
        <el-form-item label="通讯协议" prop="type">
          <el-select v-model="formInfo.type" clearable placeholder="请选择通讯协议">
            <el-option
              v-for="(item, index) in typeList"
              :key="index"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="medium" @click="submitQuery">查询</el-button>
          <el-button size="medium" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="box-table">
      <el-table :data="tableData" :border="true" :stripe="true">
        <el-table-column type="index" width="80" align="center"></el-table-column>
        <el-table-column prop="deviceTypeName" label="设备类型名称" align="center"></el-table-column>
        <el-table-column prop="communicationProtocol.name" label="通讯协议" align="center"></el-table-column>
        <el-table-column label="设备类型参数" align="center">
          <template slot-scope="scope">
            <el-button v-if="scope.row.deviceTypeId == 7" type="primary" plain size="small"
              @click="handleParam(scope.row)">设置</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortNumber" label="排序序号" align="center"></el-table-column>
      </el-table>
      <!-- <el-pagination
        class="box-page"
        @current-change="handleCurrentChange"
        :current-page.sync="curPage"
        layout="total, prev, pager, next"
        :total="400">
      </el-pagination> -->
    </div>
  </div>
</template>

<script>
export default {
  name: 'DeviceTypeManage',
  data () {
    return {
      formInfo: {
        name: '',
        type: ''
      },
      typeList: [
        {
          label: 'OPC',
          value: 1
        },
        {
          label: 'Modbus TCP',
          value: 2
        },
        {
          label: 'S7',
          value: 3
        }
      ],
      tableData: [],
      curPage: 1,
    }
  },
  created() {
    this.getDeviceTypeAll()
  },
  methods: {
    submitQuery() {
      this.queryByNameOrProtocol()
    },
    resetForm() {
      this.$refs.form.resetFields();
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    },
    handleParam(row) {
      this.$router.push({
        name: 'device_param_manage',
        params: {
          deviceTypeId: row.deviceTypeId,
          deviceTypeName: row.deviceTypeName
        }
      })
    },
    // 查询所有设备类型接口
    async getDeviceTypeAll() {
      try {
        const res = await this.$http.get('/deviceType/queryAll')
        if (res.data.code == 200) {
          this.tableData = res.data.data
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 按设备类型名称和通讯协议查询设备类型接口
    async queryByNameOrProtocol() {
      try {
        const res = await this.$http.post('/deviceType/queryByNameOrProtocol', {
          deviceTypeName: this.formInfo.name,
          communicationProtocol: this.formInfo.type
        })
        this.tableData = res.data.data
      } catch (error) {
        console.log(error);
      }
    },

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.box-card {
  border-radius: 4px;
  border: 1px solid #ebeef5;
  background-color: #FFF;
  overflow: hidden;
  color: #303133;
  padding: 20px;
  text-align: left;
}
.form-inline >>> .el-form-item {
  margin-bottom: 0;
}
.box-table {
  margin-top: 32px;
  text-align: left;
}
.box-page {
  margin-top: 20px;
  text-align: right;
}
</style>
