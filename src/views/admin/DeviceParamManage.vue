<template>
  <div style="height: 100%;">
    <div class="box-card">
      <el-page-header @back="goBack" :content="$route.params.deviceTypeName">
      </el-page-header>
    </div>
    <div class="box-table">
      <el-table :data="tableData" :border="true" :stripe="true" height="calc(100% - 70px)">
        <el-table-column type="index" width="80" label="序号" align="center"></el-table-column>
        <el-table-column prop="deviceTypeParameterId" label="参数ID" align="center"></el-table-column>
        <el-table-column prop="deviceTypeParameterName" label="参数名" align="center"></el-table-column>
        <el-table-column prop="value" label="参数值" align="center">
          <template slot-scope="scope">
            <span v-show="!scope.row.showEdit">{{ scope.row.value }}</span>
            <el-input-number v-show="scope.row.showEdit" v-model="paramValue"
            controls-position="right" :step-strictly="true" :min="1"></el-input-number>
          </template>
        </el-table-column>
        <el-table-column prop="sortNumber" label="排序序号" align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button v-show="!scope.row.showEdit" @click="editParam(scope.row)" type="text" size="small">编辑</el-button>
            <el-button v-show="scope.row.showEdit" @click="updateDeviceParam(scope.row)" type="text" size="small">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DeviceParamManage',
  data () {
    return {
      tableData: [],
      paramValue: ''
    }
  },
  created() {
    this.queryDeviceParam()
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    editParam(row) {
      this.paramValue = row.value
      row.showEdit = true
    },
    // 按设备类型id查询所有设备参数接口
    async queryDeviceParam() {
      try {
        const res = await this.$http.post('/deviceType/queryPByTypeId', {
          deviceTypeId: Number(this.$route.params.deviceTypeId),
        })
        if (res.data.code == 200) {
          this.tableData = res.data.data
          this.tableData.map((item) => {
            this.$set(item, 'showEdit', false)
          })
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 编辑参数值
    async updateDeviceParam(row) {
      try {
        const res = await this.$http.post('/deviceType/updatePByTypeId', {
          deviceTypeId: Number(this.$route.params.deviceTypeId),
          deviceTypeParameterId: Number(row.deviceTypeParameterId),
          value: this.paramValue
        })
        this.$set(row, 'showEdit', false)
        this.queryDeviceParam()
        if (res.data.code == 200) {
          this.$message.success('编辑成功')
        } else {
          this.$message.error(res.data.msg)
        }
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
.box-table {
  margin-top: 32px;
  text-align: left;
  height: calc(100% - 98px);
}
</style>
