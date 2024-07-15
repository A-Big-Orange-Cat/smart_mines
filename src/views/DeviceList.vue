<template>
  <div class="list-box">
    <div class="list-main">
      <i class="el-icon-arrow-left arrow-left" @click="handleBack">返回</i>
      <p class="title">智能设备列表</p>
      <el-form :inline="true" :model="form_query" class="form-inline">
        <el-form-item>
          <el-input
            v-model="form_query.name"
            placeholder="请输入设备名称"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="form_query.type" clearable placeholder="请选择设备类型">
            <el-option
              v-for="item in type_list"
              :key="item.value"
              :label="`${item.deviceTypeName} (${item.communicationProtocol.name})`"
              :value="item.deviceTypeId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSubmit">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="tableData"
        :border="true"
        style="width: 100%"
        class="custom-table"
      >
        <el-table-column label="序号" type="index" width="120" align="center"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称" align="center">
          <template slot-scope="scope">
            <span class="region-text" @click="handleInfo(scope.row)">{{ scope.row.deviceName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceTypeName" label="设备类型" align="center"></el-table-column>
        <el-table-column prop="deviceLocation" label="设备位置" align="center"></el-table-column>
        <el-table-column prop="ip" label="IP" align="center"></el-table-column>
        <el-table-column prop="port" label="端口号" align="center"></el-table-column>
      </el-table>
      <!-- <div class="page-layout">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page.sync="current_page"
          :total="1000"
          @current-change="handlePage"
        >
        </el-pagination>
      </div> -->
    </div>
  </div>
</template>

<script>
export default {
  name: "DeviceList",
  data() {
    return {
      form_query: {
        name: '',
        type: "",
      },
      type_list: [],
      tableData: [],
      current_page: 1,
    };
  },
  async created() {
    await this.getQueryAll()
    await this.queryNormalByNameOrTypeId()
  },
  methods: {
    onSubmit() {
      this.queryNormalByNameOrTypeId(this.form_query.name, this.form_query.type)
    },
    handleBack() {
      this.$router.push({
        name: "home",
      });
    },
    // 查询所有设备类型接口
    async getQueryAll() {
      try {
        const res = await this.$http.get('/deviceType/queryAll')
        this.type_list = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    // 按设备类型id查询非禁用设备接口
    async queryNormalByNameOrTypeId(deviceName, deviceTypeId) {
      try {
        const res = await this.$http.post('/device/queryNormalByNameOrTypeId', {
          deviceName,
          deviceTypeId
        })
        this.tableData = res.data.data
        this.changeTableData()
      } catch (error) {
        console.log(error);
      }
    },
    // 操作数据
    changeTableData() {
      this.tableData.map(item => {
        this.type_list.forEach(type => {
          if (item.deviceTypeId === type.deviceTypeId) {
            item.deviceTypeName = type.deviceTypeName
          }
        })
      })
    },
    // 点击切换分页
    handlePage(page) {
      this.current_page = page;
      console.log("切换分页", page);
    },
    handleInfo(data) {
      this.$router.push({
        name: "deviceDetail",
        params: {
          name: data.deviceName,
          deviceId: data.deviceId,
          deviceTypeId: data.deviceTypeId
        }
      })
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.list-box {
  width: 100%;
  height: calc(100% - 75px);
  padding: 28px 48px;
  /* padding: 28px 200px; */
  box-sizing: border-box;

  display: flex;
  align-items: center;
  justify-content: center;
}
.list-main {
  width: 100%;
  height: 100%;
  /* width: 1100px;
  height: 788px; */
  background: rgba(5, 13, 75, 0.8);
  border: 2px solid rgba(8, 72, 138, 1);
  box-shadow: inset 0px 1px 20px rgba(18, 142, 232, 0.34);
  box-sizing: border-box;
  padding: 24px;
  position: relative;
}
.arrow-left {
  position: absolute;
  top: 16px;
  left: 24px;
  font-size: 16px;
  color: #63d8ff;
  cursor: pointer;
}
.title {
  font-family: 'fangsong';
  font-size: 24px;
  font-weight: bold;
  color: #63d8ff;
  margin-top: 12px;
}
.form-inline {
  margin-top: 24px;
  margin-bottom: 12px;
}
.region-text {
  color: #63d8ff;
  cursor: pointer;
}

.custom-table,
.custom-table >>> .el-table__header-wrapper,
.custom-table >>> tr,
.custom-table >>> th.el-table__cell,
.custom-table >>> tr:hover > td.el-table__cell {
  background-color: transparent !important;
}
.custom-table {
  border-color: #1480f0;
  color: #fff;
}
.custom-table >>> thead {
  color: #fff;
}
.custom-table::before,
.custom-table::after {
  background-color: #1480f0;
}
.custom-table >>> td.el-table__cell,
.custom-table >>> th.el-table__cell.is-leaf {
  border-color: #1480f0;
}
.custom-table >>> tr th.el-table__cell {
  color: #409eff;
}

.custom-table >>> .el-table__body-wrapper {
  max-height: 520px;
  overflow-y: auto;
}
.custom-table >>> .el-table__body-wrapper::-webkit-scrollbar {
  width: 10px;
  background-color: rgba(8, 72, 138, 1);
}
.custom-table >>> .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background-color: #3494e0;
  border-radius: 5px;
}

.page-layout {
  text-align: right;
  padding: 24px 0;
}
</style>
