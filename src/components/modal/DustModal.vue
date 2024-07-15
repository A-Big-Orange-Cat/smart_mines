<template>
  <el-dialog
    class="custom-modal"
    :visible="visible"
    :fullscreen="true"
    :modal="false"
    :close-on-click-modal="false"
    @update:visible="updateVisible"
  >
    <el-form ref="form" class="form" :inline="true" :model="form">
      <el-form-item label="请选择日期" prop="alarm">
        <el-date-picker
          v-model="form.time"
          type="datetimerange"
          value-format="timestamp"
          :picker-options="pickerOptions"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="medium"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item v-show="deviceType == 'mine'">
        <el-select v-model="form.type" clearable placeholder="请选择粉尘浓度">
          <el-option value="ZC_XH_FCND1" label="粉尘浓度1"></el-option>
          <el-option value="ZC_XH_FCND2" label="粉尘浓度2"></el-option>
          <el-option value="ZC_XH_FCND3" label="粉尘浓度3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="medium" icon="el-icon-search" @click="onSubmit">查询</el-button>
      </el-form-item>
    </el-form>
    <div class="lineChart" id="lineChart"></div>
    <div slot="footer">
      <el-button @click="updateVisible(false)">取 消</el-button>
      <el-button type="primary" @click="updateVisible(false)">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'DustModal',
  props: {
    visible: Boolean,
    deviceType: String,
    paramSetting: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
      form: {
        time: [],
        type: null
      },
      chartData: []
    }
  },
  methods: {
    updateVisible(newVisible) {
      this.$refs.form.resetFields();
      this.$emit("update:visible", newVisible);
    },
    async onSubmit() {
      // 精准测风
      if (this.deviceType == 'measureWind') {
        await this.querySignalLogCurve(119)
        this.initChartWind()
      }
      // 多功能喷雾
      if (this.deviceType == 'mainLane') {
        await this.querySignalLogCurve('PW_XH_FCND')
        this.initChartMainLane(Number(this.paramSetting.PW_CS_FCNDSX))
      }
      // 综采喷雾
      if (this.deviceType == 'mine') {
        if (this.form.type == 'ZC_XH_FCND1') {
          await this.querySignalLogCurve('ZC_XH_FCND1')
          this.initChartMine('粉尘浓度1', Number(this.paramSetting.ZC_CS_FCNDSX1))
        }
        if (this.form.type == 'ZC_XH_FCND2') {
          await this.querySignalLogCurve('ZC_XH_FCND2')
          this.initChartMine('粉尘浓度2', Number(this.paramSetting.ZC_CS_FCNDSX2))
        }
        if (this.form.type == 'ZC_XH_FCND3') {
          await this.querySignalLogCurve('ZC_XH_FCND3')
          this.initChartMine('粉尘浓度3', Number(this.paramSetting.ZC_CS_FCNDSX3))
        }

      }
    },
    // 折线图 获取
    async querySignalLogCurve(code) {
      try {
        const res = await this.$http.post('/log/querySignalLogCurve', {
          deviceId: Number(this.$route.params.deviceId),
          deviceTypeId: Number(this.$route.params.deviceTypeId),
          code,
          time: this.form.time,
        })
        this.chartData = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    // 精准测风
    initChartWind() {
      let list = [];
      this.chartData.map((item) => {
        let arr = [];
        arr.push(item.timeMillis)
        arr.push(item.signalValue)
        list.push(arr)
      })
      var myChart = this.$echarts.init(document.getElementById("lineChart"));

      var option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function (params, ticket, callback) {
            let index = params[0].dataIndex;
            const dateObj = new Date(list[index][0])
            const Moth = (dateObj.getMonth() + 1 < 10 ? '0' + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1);
            const Day = (dateObj.getDate() < 10 ? '0' + dateObj.getDate() : dateObj.getDate());
            const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
            const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
            const UnixTimeToDate = Moth + '-' + Day + ' ' + hours + ':' + minutes
            let Htm = `时间：${UnixTimeToDate}<br>风速：${list[index][1]}m/s `
            return Htm;
          }
        },
        xAxis: {
          type: 'time',
          min: this.form.time[0],
          max: this.form.time[1],
          // interval: 3600 * 1000 * 2,
          // boundaryGap: false,
          axisLabel: {
            color: '#c0c4cc',
            fontSize: 14,
            formatter: function(value) {
              const dateObj = new Date(value)
              const Moth = (dateObj.getMonth() + 1 < 10 ? '0' + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1);
              const Day = (dateObj.getDate() < 10 ? '0' + dateObj.getDate() : dateObj.getDate());
              const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
              const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
              const UnixTimeToDate = Moth + '-' + Day + ' ' + hours + ':' + minutes
              return UnixTimeToDate
            }
          },
        },
        yAxis: {
          name: '风速(m/s)',
          nameTextStyle: { color: '#c0c4cc' },
          axisLabel: {
            color: '#c0c4cc',
            fontSize: 14
          },
          splitLine: {
            lineStyle: { color: '#606266', type: 'dashed' }
          },
          axisLine: { show: false },
          axisTick: { show: false },
        },
        series: [{
          type: 'line',
          color: '#FFE282',
          smooth: true,
          symbolSize: 3,
          // -- 渐变 --
          areaStyle: {
            // 修改 x1,y1,x,y 得到不同的渐变方式
            color: new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [{
              offset: 0,
              color: 'rgba(137,19,227, 0.8)'
            }, {
              offset: 1,
              color: 'rgba(0,255,255,0.2)'
            }])
          },
          lineStyle: {
            color: {
              // 修改 x,y,x1,y1 得到不同的渐变方式
              type: 'linear', x: 1, y: 0, x2: 0, y2: 0,
              // 0% 处的颜色 // 100% 处的颜色
              colorStops: [
                { offset: 0, color: '#00FFFF' },
                { offset: 1, color: '#8D12E9' }
              ],
              global: false
            },
            width: 2
          },
          data: list
        }]
      };

      myChart.setOption(option);
    },
    // 多功能喷雾
    initChartMainLane(maxNumber) {
      let list = [];
      this.chartData.map((item) => {
        let arr = [];
        arr.push(item.timeMillis)
        arr.push(item.signalValue)
        list.push(arr)
      })
      var myChart = this.$echarts.init(document.getElementById("lineChart"));

      var option = {
        tooltip: {
          trigger: 'axis',
          // axisPointer: { type: 'cross' },
          formatter: function (params, ticket, callback) {
            let index = params[0].dataIndex;
            const dateObj = new Date(list[index][0])
            const Moth = (dateObj.getMonth() + 1 < 10 ? '0' + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1);
            const Day = (dateObj.getDate() < 10 ? '0' + dateObj.getDate() : dateObj.getDate());
            const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
            const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
            const UnixTimeToDate = Moth + '-' + Day + ' ' + hours + ':' + minutes
            let Htm = `时间：${UnixTimeToDate}<br>浓度：${list[index][1]}mg/cm³ `
            return Htm;
          }
        },
        xAxis: {
          type: 'time',
          min: this.form.time[0],
          max: this.form.time[1],
          // interval: 3600 * 1000 * 2,
          // boundaryGap: false,
          axisLabel: {
            color: '#c0c4cc',
            fontSize: 14,
            formatter: function(value) {
              const dateObj = new Date(value)
              const Moth = (dateObj.getMonth() + 1 < 10 ? '0' + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1);
              const Day = (dateObj.getDate() < 10 ? '0' + dateObj.getDate() : dateObj.getDate());
              const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
              const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
              const UnixTimeToDate = Moth + '-' + Day + ' ' + hours + ':' + minutes
              return UnixTimeToDate
            }
          },
        },
        yAxis: {
          name: '(mg/cm³)',
          nameGap: 20,
          nameTextStyle: { color: '#c0c4cc' },
          axisLabel: {
            // color: '#c0c4cc',
            color: function (value) {
              if (value == maxNumber) {
                return 'red'
              } else {
                return '#c0c4cc'
              }
            },
            fontSize: 14
          },
          splitLine: {
            show: false
            // lineStyle: { color: '#606266', type: 'dashed' }
          },
          axisLine: { show: false },
          axisTick: { show: false },
        },
        series: [{
          type: 'line',
          color: '#FFE282',
          smooth: true,
          symbolSize: 3,
          // -- 渐变 --
          areaStyle: {
            // 修改 x1,y1,x,y 得到不同的渐变方式
            color: new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [{
              offset: 0,
              color: 'rgba(137,19,227, 0.8)'
            }, {
              offset: 1,
              color: 'rgba(0,255,255,0.2)'
            }])
          },
          lineStyle: {
            color: {
              // 修改 x,y,x1,y1 得到不同的渐变方式
              type: 'linear', x: 1, y: 0, x2: 0, y2: 0,
              // 0% 处的颜色 // 100% 处的颜色
              colorStops: [
                { offset: 0, color: '#00FFFF' },
                { offset: 1, color: '#8D12E9' }
              ],
              global: false
            },
            width: 2
          },
          markLine: {
            symbol: "none",
            data: [{
              yAxis: maxNumber,
              lineStyle: {
                color: 'red',
                type: 'solid'
              }
            }]
          },
          markArea: {
            itemStyle: {
              color: 'rgba(255, 69, 0, 0.8)'
            },
            data: [
              [{
                // name: '报警粉尘浓度范围',
                yAxis: '1000'
              }, {
                yAxis: maxNumber
              }]
            ]
          },
          data: list
        }]
      };
      myChart.setOption(option);
    },
    // 综采喷雾
    initChartMine(name, maxNumber) {
      let list = [];
      this.chartData.map((item) => {
        let arr = [];
        arr.push(item.timeMillis)
        arr.push(item.signalValue)
        list.push(arr)
      })

      var myChart = this.$echarts.init(document.getElementById('lineChart'));

      var option = {
        title: {
          text: name,
          left: 'center',
          textStyle: {
            color: '#333'
          }
        },
        tooltip: {
          trigger: 'axis',
          // axisPointer: { type: 'cross' },
          formatter: function (params, ticket, callback) {
            let index = params[0].dataIndex;
            const dateObj = new Date(list[index][0])
            const Moth = (dateObj.getMonth() + 1 < 10 ? '0' + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1);
            const Day = (dateObj.getDate() < 10 ? '0' + dateObj.getDate() : dateObj.getDate());
            const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
            const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
            const UnixTimeToDate = Moth + '-' + Day + ' ' + hours + ':' + minutes
            let Htm = `时间：${UnixTimeToDate}<br>粉尘浓度：${list[index][1]}(mg/cm³) `
            return Htm;
          }
        },
        xAxis: {
          type: 'time',
          min: this.form.time[0],
          max: this.form.time[1],
          axisLabel: {
            textStyle: { color: '#c0c4cc', fontSize: 12 },
            formatter: function(value) {
              const dateObj = new Date(value)
              const Moth = (dateObj.getMonth() + 1 < 10 ? '0' + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1);
              const Day = (dateObj.getDate() < 10 ? '0' + dateObj.getDate() : dateObj.getDate());
              const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
              const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
              const UnixTimeToDate = Moth + '-' + Day + ' ' + hours + ':' + minutes
              return UnixTimeToDate
            }
          },
          // data: x_list
        },
        yAxis: {
          name: '(mg/cm³)',
          nameTextStyle: { color: '#c0c4cc' },
          axisLabel: {
            textStyle: {
              // color: '#c0c4cc',
              color: function (value) {
                if (value == maxNumber) {
                  return 'red'
                } else {
                  return '#c0c4cc'
                }
              },
              fontSize: 12
            }
          },
          splitLine: { show: false },
          axisLine: { show: false },
          axisTick: { show: false },
        },
        series: [{
          type: 'line',
          color: '#FFE282',
          smooth: true,
          symbolSize: 3,
          // -- 渐变 --
          areaStyle: {
            // 修改 x1,y1,x,y 得到不同的渐变方式
            color: new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [{
              offset: 0,
              color: 'rgba(137, 19, 227, 0.8)' // rgba(137,19,227, 0.8)
            },
            {
              offset: 1,
              color: 'rgba(0, 255, 255, 0.2)' // rgba(0,255,255,0.2)
            }])
          },
          lineStyle: {
            color: {
              // 修改 x,y,x1,y1 得到不同的渐变方式
              type: 'linear', x: 1, y: 0, x2: 0, y2: 0,
              // 0% 处的颜色 // 100% 处的颜色
              colorStops: [
                { offset: 0, color: '#00FFFF' },
                { offset: 1, color: '#8D12E9' }
              ],
              global: false
            },
            width: 2
          },
          markLine: {
            symbol: "none",
            data: [{
              yAxis: maxNumber,
              lineStyle: {
                color: 'red',
                type: 'solid'
              }
            }]
          },
          markArea: {
            itemStyle: {
              color: 'rgba(255, 69, 0, 0.8)'
            },
            data: [
              [{
                // name: '报警粉尘浓度范围',
                yAxis: '1000'
              }, {
                yAxis: maxNumber
              }]
            ]
          },
          data: list
        }]
      };

      myChart.setOption(option);
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.custom-modal >>> .el-dialog__body {
  padding: 24px;
}
.custom-modal >>> .el-dialog__header {
  text-align: center;
}

.form {
  margin-top: 24px;
  margin-bottom: 12px;
}

.lineChart {
  width: 100%;
  height: calc(100vh - 246px); /* no */
}
</style>
