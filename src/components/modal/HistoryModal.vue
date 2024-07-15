<template>
  <el-dialog
    class="custom-modal"
    :visible="visible"
    width="60%"
    :modal="false"
    :close-on-click-modal="false"
    @update:visible="updateVisible"
  >
    <el-form ref="form" :inline="true" :model="form_query">
      <el-form-item>
        <el-input
          v-model="form_query.param_name"
          placeholder="参数名称"
          clearable
          size="medium"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="form_query.time_period"
          type="daterange"
          value-format="timestamp"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
          size="medium">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="medium" icon="el-icon-search" @click="onSubmit">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
        class="custom-table"
        :data="tableData"
        :border="true"
        style="width: 100%"
      >
        <el-table-column label="序号" width="80" align="center">
          <template slot-scope="scope">
            <span>{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="baseDeviceTypeParameter.baseDeviceTypeParameterName" label="参数名称" min-width="120px"></el-table-column>
        <el-table-column prop="baseDeviceTypeParameter.address" label="操作地址"> </el-table-column>
        <el-table-column prop="baseDeviceTypeParameter.type.name" label="数值类型"></el-table-column>
        <el-table-column prop="controlMode" label="控制方式">
          <template slot-scope="scope">
            <span>{{ scope.row.controlMode.name + '控制' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operationValue" label="操作值"></el-table-column>
        <el-table-column prop="user.userTrueName" label="操作人"></el-table-column>
        <el-table-column prop="timeMillis" label="操作时间" min-width="120px">
          <template slot-scope="scope">
            <span>{{ getTime(scope.row.timeMillis) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="box-page"
        @current-change="handleCurrentChange"
        :current-page.sync="pageSize"
        :page-size="10"
        layout="total, prev, pager, next"
        :total="operationData.total">
      </el-pagination>
  </el-dialog>
</template>

<script>
export default {
  name: "HistoryModal",
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    paramsList: {
      type: Array
    }
  },
  data() {
    return {
      form_query: {
        param_name: "",
        time_period: null,
      },
      tableData: [],
      operationData: {},
      pageSize: 1
    };
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.queryOperationLog()
        }
      },
      immediate: true
    }
  },
  methods: {
    updateVisible(newVisible) {
      this.$refs.form.resetFields();
      this.$emit("update:visible", newVisible);
    },
    onSubmit() {
      this.queryOperationLog(this.form_query.time_period)
    },
    async queryOperationLog(time) {
      try {
        const res = await this.$http.post('/log/queryOperationLog', {
          deviceId: Number(this.$route.params.deviceId),
          pageIndex: this.pageSize,
          parameterName: this.form_query.param_name,
          time
        })
        this.tableData = res.data.data.records
        this.operationData = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    getTime(timestamp) {
    // 此处时间戳以毫秒为单位
      let date = new Date(parseInt(timestamp));
      let Year = date.getFullYear();
      let Moth = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1);
      let Day = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate());
      let Hour = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours());
      let Minute = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
      let Sechond = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
      let time =  Year + '-' + Moth + '-' + Day + '   '+ Hour +':'+ Minute  + ':' + Sechond;
      return time
    },
    handleCurrentChange() {
      this.queryOperationLog()
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.custom-modal >>> .el-dialog__body {
  padding: 24px;
}

.custom-table >>> .el-table__cell {
  padding: 8px 0;
  height: 40px;
}
/* .custom-table >>> .el-table__body-wrapper {
  max-height: 440px;
  overflow-y: auto;
}
.custom-table >>> .el-table__body-wrapper::-webkit-scrollbar {
  width: 8px;
  background-color: rgba(50,50,50,0.2);
}
.custom-table >>> .el-table__body-wrapper::-webkit-scrollbar-thumb {
  background-color: rgba(50,50,50,0.25);
  border-radius: 8px;
} */
.box-page {
  margin-top: 20px;
  text-align: right;
}
</style>
