<template>
  <div class="warn-box">
    <div class="ray"></div>
    <p class="warn-title" @click="handleWarn">报警信息</p>
    <div class="list-button"></div>
    <el-table
      :data="warnList"
      :show-header="false"
      :border="true"
      style="width: 100%"
      class="custom-table"
    >
      <el-table-column prop="deviceName" label="设备名称" align="left" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="alarm_status" label="报警" align="center">
        <template slot-scope="scope">
          <span
            :style="{
              'color': scope.row.alarm_status ? '#F56C6C' : '#67C23A',
            }"
          >
            {{ scope.row.alarm_status ? '报警' : '正常' }}</span
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: "WarnCard",
  props: {
    tableList: Array,
  },
  data() {
    return {
      warnList: []
    };
  },
  watch: {
    'tableList': {
      handler(val) {
        this.warnList = val.filter(item => item.alarm_status && item.status);
      },
      deep: true
    }
  },
  methods: {
    handleWarn() {
      this.$router.push({
        name: 'warnList'
      })
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.warn-box {
  position: relative;
  width: 360px;
  height: 220px;
  /* height: calc(100% - 600px); */
  background-image: url("../../../static/images/layout/box3.png");
  background-repeat: no-repeat;
  background-size: contain;
  padding: 0 20px 20px;
  box-sizing: border-box;
}
.ray {
  position: absolute;
  top: 36px;
  left: calc(180px - 100px);
  width: 200px;
  height: 36px;
  background-image: url("../../../static/images/layout/ray.png");
  background-repeat: no-repeat;
  background-size: contain;
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
.warn-title {
  font-size: 14px;
  color: #fff;
  font-weight: bold;
  text-align: left;
  padding-top: 8px;
  box-sizing: border-box;
  text-align: center;
}
.warn-title:hover {
  color: #66ffff;
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
  max-height: 150px;
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
</style>
