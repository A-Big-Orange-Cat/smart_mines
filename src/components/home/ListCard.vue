<template>
  <div class="card-box">
    <div class="ray"></div>
    <p class="medium-title">设备列表</p>
    <div class="select-box">
      <div class="select-label">设备类型：</div>
      <el-select v-model="device_type" clearable placeholder="请选择设备类型"
        size="medium" @change="changeType">
        <el-option
          v-for="item in typeList"
          :key="item.value"
          :label="`${item.deviceTypeName} (${item.communicationProtocol.name})`"
          :value="item.deviceTypeId">
        </el-option>
      </el-select>
    </div>
    <el-table
      :data="tableList"
      :border="true"
      style="width: 100%"
      class="custom-table"
    >
      <el-table-column prop="deviceTypeName" label="类型" align="left" :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column label="名称" align="left" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span class="name-text" @click="handleInfo(scope.row)">{{ scope.row.deviceName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="60px" align="center">
        <template slot-scope="scope">
          <span
            :style="{
              'color': scope.row.status ? '#4be200' : '#e6a23c',
            }"
          >
            {{ scope.row.status ? '在线' : '离线' }}</span
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: "ListCard",
  props: {
    tableList: Array,
    typeList: Array
  },
  data() {
    return {
      device_type: '',
      tableData: [],
      deviceList: [],
    };
  },
  watch: {
    'typeList': {
      handler(val) {
        this.queryNormalAll()
      }
    }
  },
  methods: {
    // 点击跳转设备设备详情页
    handleInfo(data) {
      this.$router.push({
        name: "deviceDetail",
        params: {
          name: data.deviceName,
          deviceId: data.deviceId,
          deviceTypeId: data.deviceTypeId
        }
      })
    },
    // 查询所有非禁用设备接口
    async queryNormalAll() {
      try {
        const res = await this.$http.post('/device/queryNormalByNameOrTypeId', {})
        this.tableData = res.data.data
        this.changeTableData()
        this.deviceList = res.data.data
        this.$emit('updateList', this.deviceList)
      } catch (error) {
        console.log(error);
      }
    },
    // 操作数据
    changeTableData() {
      this.tableData.map(item => {
        this.typeList.forEach(type => {
          if (item.deviceTypeId === type.deviceTypeId) {
            item.deviceTypeName = type.deviceTypeName
          }
        })
      })
      // 传值给父级组件
      this.$emit('updateTable', this.tableData)
    },
    // 按设备类型id查询非禁用设备接口
    async queryNormalByNameOrTypeId(deviceTypeId) {
      try {
        const res = await this.$http.post('/device/queryNormalByNameOrTypeId', {
          deviceTypeId
        })
        this.tableData = res.data.data
        this.changeTableData()
      } catch (error) {
        console.log(error);
      }
    },
    changeType(value) {
      this.queryNormalByNameOrTypeId(value)
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.card-box {
  width: 360px;
  height: 470px;
  background-image: url("../../../static/images/layout/box4.png");
  background-repeat: no-repeat;
  background-size: contain;
  box-sizing: border-box;
  padding: 36px 24px 24px 24px;
  position: relative;
}
.ray {
  width: 200px;
  height: 36px;
  background-image: url("../../../static/images/layout/ray.png");
  background-repeat: no-repeat;
  background-size: contain;
  margin: 0 auto;
  animation: ray-animation 1.5s linear infinite alternate;
}
@keyframes ray-animation {
  0% {
    opacity: 0.4;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}
.medium-title {
  position: absolute;
  top: 8px;
  left: calc(180px - 28px);
  font-size: 14px;
  color: #fff;
  font-weight: bold;
  text-align: left;
  box-sizing: border-box;
}
.select-box {
  display: flex;
  align-items: center;
}
.select-label {
  width: 100px;
  font-size: 16px;
  color: #fff;
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
  margin-top: 24px;
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
.custom-table >>> .el-table__cell {
  padding: 8px 0;
  height: 40px;
}

.custom-table >>> .el-table__body-wrapper {
  max-height: 280px;
  overflow-y: auto;
}
.custom-table >>> .el-table__body-wrapper::-webkit-scrollbar {
  width: 6px;
  background-color: rgba(8, 72, 138, 1);
}
.custom-table >>> .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background-color: #3494e0;
  border-radius: 3px;
}

.name-text {
  color: #63d8ff;
  cursor: pointer;
}
</style>
