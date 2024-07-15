<template>
  <div style="text-align: left">
    <el-card shadow="hover">
      <el-button class="space-button" type="primary" size="medium" icon="el-icon-plus" @click="addUser">新建</el-button>
      <el-table :data="tableData" :border="true" :stripe="true">
        <el-table-column type="index" width="80" align="center"></el-table-column>
        <el-table-column prop="userName" label="用户名" align="center"></el-table-column>
        <el-table-column prop="password" label="密码" align="center"></el-table-column>
        <el-table-column prop="userTrueName" label="真实姓名" align="center"></el-table-column>
        <el-table-column prop="role.name" label="用户权限" align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
            <el-popconfirm title="确定删除吗？" @confirm="deleteUser(scope.row)">
              <el-button slot="reference" type="text" size="small">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <user-modal  :visible.sync="userVisible" :addOrEdit="addOrEdit" :userInfo="userInfo"></user-modal>
  </div>
</template>

<script>
import UserModal from '../../components/admin/modal/UserModal.vue'
export default {
  name: 'UserManage',
  components: {
    UserModal
  },
  data () {
    return {
      tableData: [],
      userVisible: false,
      addOrEdit: '',
      userInfo: {}
    }
  },
  created() {
    this.queryUserAll()
  },
  methods: {
    handleEdit(row) {
      this.addOrEdit = 'edit'
      this.userInfo = row
      this.userVisible = true
    },
    addUser() {
      this.addOrEdit = 'add'
      this.userVisible = true
    },
    // 查询所有用户接口
    async queryUserAll() {
      try {
        const res = await this.$http.get('/user/queryAll')
        if (res.data.code == 200) {
          this.tableData = res.data.data
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
    },
    // 删除用户接口
    async deleteUser(row) {
      try {
        const res = await this.$http.post('/user/delete', {
          userId: row.userId
        })
        if (res.data.code == 200) {
          this.$message.success('删除用户成功')
          this.queryUserAll()
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
.space-button {
  margin: 0 0 24px;
}
</style>
