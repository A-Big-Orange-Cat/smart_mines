<template>
  <div class="list-box">
    <div class="list-main">
      <i class="el-icon-arrow-left arrow-left" @click="handleBack">返回</i>
      <p class="title">报警信息列表</p>
      <el-form :inline="true" :model="form_query" class="form-inline">
        <el-form-item>
          <el-select v-model="form_query.device_name" placeholder="选择设备">
            <el-option label="设备一" value="yi"></el-option>
            <el-option label="设备二" value="er"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form_query.param_name"
            placeholder="参数名称"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="form_query.start_time"
            type="date"
            placeholder="选择开始日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="form_query.end_time"
            type="date"
            placeholder="选择截止日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="onSubmit"
            >查询</el-button
          >
        </el-form-item>
      </el-form>
      <el-table
        :data="tableData"
        :border="true"
        style="width: 100%"
        class="custom-table"
      >
        <el-table-column label="序号">
          <template slot-scope="scope">
            <span>{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="device_name" label="设备名称"> </el-table-column>
        <el-table-column prop="param_name" label="参数名称"> </el-table-column>
        <el-table-column prop="alarm" label="报警信息"></el-table-column>
        <el-table-column prop="create_time" label="创建时间"></el-table-column>
      </el-table>
      <div class="page-layout">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page.sync="current_page"
          :total="1000"
          @current-change="handlePage"
        >
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "WarnList",
  data() {
    return {
      form_query: {
        device_name: "",
        param_name: "",
        start_time: "",
        end_time: ""
      },
      tableData: [
        {
          device_name: "设备1",
          param_name: "参数1",
          alarm: "设备报警",
          create_time: "2024-02-27"
        },
        {
          device_name: "设备2",
          param_name: "参数2",
          alarm: "设备报警",
          create_time: "2024-02-27"
        },
        {
          device_name: "设备3",
          param_name: "参数3",
          alarm: "设备报警",
          create_time: "2024-02-27"
        },
        {
          device_name: "设备4",
          param_name: "参数4",
          alarm: "设备报警",
          create_time: "2024-02-27"
        },
        {
          device_name: "设备5",
          param_name: "参数5",
          alarm: "设备报警",
          create_time: "2024-02-27"
        },
        {
          device_name: "设备6",
          param_name: "参数6",
          alarm: "设备报警",
          create_time: "2024-02-27"
        },
      ],
      current_page: 1,
    };
  },
  methods: {
    onSubmit() {
      console.log("submit!");
    },
    handleBack() {
      this.$router.push({
        name: "home",
      });
    },
    // 点击切换分页
    handlePage(page) {
      this.current_page = page;
      console.log("切换分页", page);
    },
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

.page-layout {
  text-align: right;
  padding: 24px 0;
}
</style>
