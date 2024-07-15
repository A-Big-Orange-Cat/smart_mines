<template>
  <div>
    <div class="box-card">
      <el-form ref="form" :inline="true" :model="formInfo" class="form-inline">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="formInfo.name" placeholder="请输入设备名称"></el-input>
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="formInfo.type" clearable placeholder="请选择设备类型">
            <el-option
              v-for="(item, index) in typeList"
              :key="index"
              :label="`${item.deviceTypeName}（${item.communicationProtocol.name}）`"
              :value="item.deviceTypeId">
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
      <el-button class="space-button" type="primary" size="medium" icon="el-icon-plus" @click="addDevice">新建</el-button>
      <el-table :data="tableData" :border="true" :stripe="true">
        <el-table-column type="index" width="80" align="center"></el-table-column>
        <el-table-column label="设备类型" align="center">
          <template slot-scope="scope">
            <span>{{scope.row.deviceTypeName}} <br> {{ '（' + scope.row.communicationProtocol + '）' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceName" label="设备名称" align="center"></el-table-column>
        <el-table-column prop="ip" label="IP地址" align="center"></el-table-column>
        <el-table-column prop="port" label="端口号" align="center"></el-table-column>
        <el-table-column label="摄像头" align="center">
          <template slot-scope="scope">
            <el-button type="primary" plain size="small" @click="handleCamera(scope.row)">设置</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="deviceLocation" label="设备位置" align="center">
          <template slot-scope="scope">
            <el-button type="primary" plain size="small" @click="handleImage(scope.row)">设置</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="enableStatus" label="设备状态" align="center">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.enableStatus.value"
              disabled
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
            ></el-switch>
            <span>{{ scope.row.enableStatus.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortNumber" label="排序序号" align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="deleteDevice(scope.row)">
              <el-button slot="reference" type="text" size="small">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!-- <el-pagination
        class="box-page"
        @current-change="handleCurrentChange"
        :current-page.sync="curPage"
        layout="total, prev, pager, next"
        :total="400">
      </el-pagination> -->
    </div>
    <device-modal :visible.sync="deviceVisible" :addOrEdit="addOrEdit" :deviceInfo="deviceInfo"></device-modal>
    <image-modal :visible.sync="imageVisible" :deviceInfo="deviceInfo" :imageUrl="imageUrl"></image-modal>
  </div>
</template>

<script>
import DeviceModal from '../../components/admin/modal/DeviceModal.vue'
import ImageModal from '../../components/admin/modal/ImageModal.vue'
export default {
  name: 'DeviceManage',
  components: {
    DeviceModal,
    ImageModal
  },
  data () {
    return {
      formInfo: {
        name: '',
        type: ''
      },
      typeList: [],
      tableData: [],
      curPage: 1,
      deviceVisible: false,
      addOrEdit: '',
      deviceInfo: {},
      imageVisible: false,
      imageUrl: ''
    }
  },
  async created() {
    await this.getDeviceTypeAll()
    await this.queryByNameOrTypeId()
    await this.getTitleImage()
  },
  methods: {
    submitQuery() {
      console.log('submit!');
      this.queryByNameOrTypeId(this.formInfo.name, this.formInfo.type)
    },
    resetForm() {
      this.$refs.form.resetFields();
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
    },
    handleCamera(row) {
      this.$router.push({
        name: 'set_camera',
        params: {
          deviceId: row.deviceId,
          deviceName: row.deviceName
        }
      })
    },
    handleImage(row) {
      this.deviceInfo = row
      this.imageVisible = true
    },
    handleEdit(row) {
      this.addOrEdit = 'edit'
      this.deviceInfo = row
      this.deviceVisible = true
    },
    addDevice() {
      this.addOrEdit = 'add'
      this.deviceVisible = true
    },
    // 按设备名称或设备类型id查询设备接口
    async queryByNameOrTypeId(deviceName, deviceTypeId) {
      try {
        const res = await this.$http.post('/device/queryByNameOrTypeId', {
          deviceName,
          deviceTypeId
        })
        this.tableData = res.data.data
        this.changeTableData()
      } catch (error) {
        console.log(error);
      }
    },
    // 删除设备接口
    async deleteDevice(row) {
      try {
        const res = await this.$http.post('/device/delete', {
          deviceId: row.deviceId
        })
        if (res.data.code == 200) {
          this.$message.success('删除设备成功')
          this.queryByNameOrTypeId()
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 查询所有设备类型接口
    async getDeviceTypeAll() {
      try {
        const res = await this.$http.get('/deviceType/queryAll')
        this.typeList = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    // 操作遍历数据
    changeTableData() {
      this.tableData.map(item => {
        this.typeList.forEach(type => {
          if (item.deviceTypeId === type.deviceTypeId) {
            item.deviceTypeName = type.deviceTypeName
            item.communicationProtocol = type.communicationProtocol.name
          }
        })
      })
    },
    // 查询主界面标题与地图路径接口
    async getTitleImage() {
      try {
        const res = await this.$http.get('/system/query');
        this.imageUrl = res.data.data.mapPath;
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
.space-button {
  margin: 10px 0 24px;
}
.box-page {
  margin-top: 20px;
  text-align: right;
}
</style>
