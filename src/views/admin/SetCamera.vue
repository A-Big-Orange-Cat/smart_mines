<template>
  <div>
    <div class="box-card">
      <el-page-header @back="goBack" :content="$route.params.deviceName">
      </el-page-header>
    </div>
    <div class="box-table">
      <el-button class="space-button" type="primary" size="medium" icon="el-icon-plus" @click="addCamera">新建</el-button>
      <el-table :data="tableData" :border="true" :stripe="true">
        <el-table-column type="index" width="80" align="center"></el-table-column>
        <el-table-column prop="ip" label="ip地址" align="center"></el-table-column>
        <el-table-column prop="port" label="端口号" align="center"></el-table-column>
        <el-table-column prop="userName" label="用户名" align="center"></el-table-column>
        <el-table-column prop="password" label="密码" align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="deleteCamera(scope.row)">
              <el-button slot="reference" type="text" size="small">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <camera-modal  :visible.sync="cameraVisible" :addOrEdit="addOrEdit" :cameraInfo="cameraInfo"></camera-modal>
  </div>
</template>

<script>
import CameraModal from '../../components/admin/modal/CameraModal.vue'
export default {
  name: 'SetCamera',
  components: {
    CameraModal
  },
  data () {
    return {
      tableData: [],
      cameraVisible: false,
      addOrEdit: '',
      cameraInfo: {}
    }
  },
  created() {
    this.queryCamera()
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    handleEdit(row) {
      this.addOrEdit = 'edit'
      this.cameraInfo = row
      this.cameraVisible = true
    },
    addCamera() {
      this.addOrEdit = 'add'
      this.cameraVisible = true
    },
    // 按设备id查询摄像头接口
    async queryCamera() {
      try {
        const res = await this.$http.post('/hik/queryAllByDeviceId', {
          deviceId: Number(this.$route.params.deviceId)
        })
        if (res.data.code == 200) {
          this.tableData = res.data.data
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 删除摄像头接口
    async deleteCamera(row) {
      try {
        const res = await this.$http.post('/hik/delete', {
          dvrId: row.dvrId
        })
        if (res.data.code == 200) {
          this.$message.success('删除摄像头成功')
          this.queryCamera()
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
}
.space-button {
  margin: 10px 0 24px;
}
.box-page {
  margin-top: 20px;
  text-align: right;
}
</style>
