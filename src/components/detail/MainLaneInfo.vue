<template>
  <div class="info-flex">
    <div class="flex-left">
      <div class="flex-top">
        <div class="monitor-box">
          <span class="monitor-title">监控</span>
          <div class="monitor-video">
            <video ref="video1" id="video1" controls style="width: 100%;height: 100%;" autoplay muted></video>
          </div>
        </div>

        <div class="wind-img" v-show="paramSetting.PW_CS_PWGNMS != 2">
        <img
          v-show="paramSignal.PW_XH_IN1 == 0"
          class="light light-left"
          src="../../../static/images/layout/redlight.png"
        />
        <img
          v-show="paramSignal.PW_XH_IN2 == 0"
          class="light light-right"
          src="../../../static/images/layout/redlight.png"
        />
      </div>
      <div class="wind-reprint" v-show="paramSetting.PW_CS_PWGNMS == 2">
        <img
          v-show="paramSignal.PW_XH_IN1 == 0"
          class="light light-1"
          src="../../../static/images/layout/redlight.png"
        />
        <img
          v-show="paramSignal.PW_XH_IN2 == 0"
          class="light light-2"
          src="../../../static/images/layout/redlight.png"
        />
        <img
          v-show="paramSignal.PW_XH_IN3 == 0"
          class="light light-3"
          src="../../../static/images/layout/redlight.png"
        />
      </div>
      </div>

      <div class="chart" id="main"></div>
    </div>

    <div class="flex-main">
      <div class="operate-layout">
        <div class="operate-box">
          <p class="operate-title">远程控制</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-list">
              <div style="color: #fff;">{{ (paramSignal.PW_XH_CM == '0') ? '就地模式' : (paramSignal.PW_XH_CM == '1' ? '远程模式' : '') }}</div>
              <div class="button-blue" v-show="paramSignal.PW_XH_CM == '0'" @click="remoteControl('PW_CS_CM', 1)">远程启动</div>
              <div class="button-orange" v-show="paramSignal.PW_XH_CM == '1'" @click="remoteControl('PW_CS_CM', 0)">远程停止</div>
            </div>
          </div>
        </div>
        <div class="operate-box" style="margin-top: 24px;">
          <p class="operate-title">喷雾操作</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-list">
              <div style="color: #fff;">{{ paramSignal.PW_XH_GZZT == 0 ? '关闭状态' : (paramSignal.PW_XH_GZZT == 1 ? '开启状态' : '') }}</div>
              <div class="button-blue" v-show="paramSignal.PW_XH_GZZT == 0" @click="operateInstruct('PW_CZ_YCKZPWSS', 1)">启动设备</div>
              <div class="button-orange" v-show="paramSignal.PW_XH_GZZT == 1" @click="operateInstruct('PW_CZ_YCKZPWSS', 0)">关闭设备</div>
            </div>
          </div>
        </div>
        <div class="operate-box" style="margin-top: 24px;">
          <p class="operate-title">参数设置</p>
          <div class="button-box" style="justify-content: center;">
            <div class="button-orange" @click="openParam">参数设置</div>
          </div>
        </div>
        <div class="operate-box" style="margin-top: 24px;">
          <p class="operate-title">历史记录</p>
          <div class="button-box">
            <div class="button-list">
              <div class="button-blue" @click="openHistory">操作记录</div>
              <div class="button-blue" @click="openAlarm">报警记录</div>
            </div>
            <div class="button-list">
              <div class="button-blue" @click="openDust">历史曲线</div>
              <div class="button-blue">备用</div>
            </div>
          </div>
        </div>
        <div class="desc-list" style="margin-top: 24px">
          <div class="desc">粉尘浓度：{{ paramSignal.PW_XH_FCND }}mg/cm³</div>
        </div>
      </div>

      <div class="table-layout">
        <div class="table-box">
          <el-table
            :data="operationLogs"
            :border="true"
            style="width: 100%"
            class="custom-table"
            :show-header="false"
          >
            <el-table-column
              prop="operationValue"
              :show-overflow-tooltip="true"
              align="left"
            >
              <template slot-scope="scope">
                <span>{{ (scope.row.controlMode.name) +
                  (scope.row.operationValue == '0' ? '关闭' : '开启') }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="timeMillis"
              :show-overflow-tooltip="true"
              min-width="140px"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ $refs.history.getTime(scope.row.timeMillis) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <param-modal :visible.sync="paramVisible" deviceType="mainLane"></param-modal>
    <history-modal ref="history" :visible.sync="historyVisible"></history-modal>
    <dust-modal :visible.sync="dustVisible" deviceType="mainLane" :paramSetting="paramSetting"></dust-modal>
    <alarm-modal :visible.sync="alarmVisible"></alarm-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import ParamModal from '../modal/ParamModal.vue'
import HistoryModal from '../modal/HistoryModal.vue'
import DustModal from '../modal/DustModal.vue'
import AlarmModal from '../modal/AlarmModal.vue'
import JMuxer from 'jmuxer'
export default {
  name: "MainLaneInfo",
  components: {
    ParamModal,
    HistoryModal,
    DustModal,
    AlarmModal
  },
  props: {
    cameraList: Array,
    paramSignal: {
      type: Object,
      default: () => ({})
    },
    paramSetting: {
      type: Object,
      default: () => ({})
    },
    operationLogs: Array
  },
  data() {
    return {
      paramVisible: false,
      historyVisible: false,
      dustVisible: false,
      alarmVisible: false,
      chartData: []
    };
  },
  watch: {
    'paramSignal.PW_XH_FCND': {
      handler(val) {
        this.chartData.push({
          timeMillis: Date.now(),
          signalValue: val
        })
        this.initChart(Number(this.paramSetting.PW_CS_FCNDSX))
      }
    },
    'paramSetting.PW_CS_FCNDSX': { // 粉尘浓度上限
      handler(val) {
        this.initChart(Number(val))
      }
    }
  },
  created() {
    const videos = document.querySelectorAll("video");   //获取所有video标签
    videos.forEach(video => {
      video.muted = false
    });
  },
  async mounted() {
    await this.querySignalLogCurve()
    this.videoPlay()
    this.$nextTick(() => {
      this.initChart(Number(this.paramSetting.PW_CS_FCNDSX))
    })
  },
  updated() {
    const videos = document.querySelectorAll("video");   //获取所有video标签
    videos.forEach(video => {
      if (video.paused) {
        video.play()
      }
    });
  },
  beforeDestroy() {
    this.socketList.map((item) => {
      item.close()
    })
  },
  methods: {
    openParam() {
      this.paramVisible = true;
    },
    openHistory() {
      this.historyVisible = true;
    },
    openDust() {
      this.dustVisible = true;
    },
    openAlarm() {
      this.alarmVisible = true;
    },
    // 折线图 粉尘浓度获取
    async querySignalLogCurve() {
      try {
        const res = await this.$http.post('/log/querySignalLogCurve', {
          deviceId: Number(this.$route.params.deviceId),
          deviceTypeId: Number(this.$route.params.deviceTypeId),
          code: 'PW_XH_FCND',
          time: [this.getTimeNum(), this.getTimeNum() + 3600 * 1000 * 24],
        })
        this.chartData = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    // 远程/就地 控制
    async remoteControl(code, val) {
      await deviceWrite('control', code, val, Number(this.$route.params.deviceTypeId), Number(this.$route.params.deviceId))
    },
    // 操作指令
    async operateInstruct(code, val) {
      await deviceWrite('command', code, val, Number(this.$route.params.deviceTypeId), Number(this.$route.params.deviceId))
    },
    startPlay() {
      this.cameraList.map((item, index) => {
        // let ip = this.$http.defaults.baseURL.replace('http', 'ws');
        let ip = JSON.parse(localStorage.getItem('system')).websocketURL;
        var websocket_path = ip + "/videoSocket/" + item.luserId;
        var socket = new WebSocket(websocket_path);
        socket.binaryType = 'arraybuffer';
        var player = null;
        // 连接
        socket.onopen = (e) => {
          player = new JMuxer({
            node: "video" + (index + 1),
            mode: 'video',
            flushingTime: 15,
            // maxDelay: 3,
            fps: 25,
            clearBuffer: true,
            debug: false,
          })
        };
        // 接收信息
        socket.onmessage = (e) => {
          var re_msg = e.data;
          if (re_msg instanceof Object) {
            // 二进制时往video中push
            player.feed({
              video: new Uint8Array(re_msg),
            })
          }
        };
        // 连接关闭
        socket.onclose = (e) => {
          console.log('获取监控视频的WebSocket连接关闭');
        }
        this.socketList.push(socket)
      })

    },
    videoPlay() {
      const videos = document.querySelectorAll("video");   //获取所有video标签
      videos.forEach(video => {
        // 阻止单击暂停
        video.addEventListener("click", (event) => {
          event.preventDefault();
        })
      });
    },

    initChart(maxNumber) {
      let list = [];
      this.chartData.map((item) => {
        let arr = [];
        arr.push(item.timeMillis)
        arr.push(item.signalValue)
        list.push(arr)
      })
      var myChart = this.$echarts.init(document.getElementById("main"));

      var option = {
        grid: {
          left: '8%',
          top: '15%',
          right: '4%',
          bottom: '10%'
        },
        tooltip: {
          trigger: 'axis',
          // axisPointer: { type: 'cross' },
          formatter: function (params, ticket, callback) {
            let index = params[0].dataIndex;
            const dateObj = new Date(list[index][0])
            const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
            const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
            const UnixTimeToDate = hours + ':' + minutes
            let Htm = `时间：${UnixTimeToDate}<br>浓度：${list[index][1]}mg/cm³ `
            return Htm;
          }
        },
        xAxis: {
          type: 'time',
          min: this.getTimeNum(),
          max: this.getTimeNum() + 3600 * 1000 * 24 + 1,
          interval: 3600 * 1000 * 2,
          // boundaryGap: false,
          axisLabel: {
            color: '#c0c4cc',
            fontSize: 14,
            formatter: function(value, index) {
              if(index == 7){
                return '24:00'
              }
              const dateObj = new Date(value)
              const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
              const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
              const UnixTimeToDate = hours + ':' + minutes
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
    getTimeNum() {
      const now = new Date(); // 获取当前时间
      const year = now.getFullYear(); // 获取当前年份
      const month = now.getMonth(); // 获取当前月份
      const day = now.getDate(); // 获取当前日期
      const zeroTime = new Date(year, month, day); // 当天零点的时间对象
      return zeroTime.getTime(); // 当天零点的时间戳
    },
  },

};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style scoped src='./button.css'></style>
<style scoped>

.info-flex {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: start;
  justify-content: space-between;
}

.flex-left {
  width: 1150px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.flex-top {
  display: flex;
  justify-content: space-between;
}
.monitor-box {
  width: 440px;
  height: 300px;
  padding: 48px 20px 20px;
  box-sizing: border-box;
  background-size: cover;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/monitor-bg2.png");
  position: relative;
  margin-bottom: 40px;
}

.monitor-title {
  position: absolute;
  top: 8px;
  left: 20px;
  color: #fff;
}

.monitor-video {
  width: 100%;
  height: 100%;
}

.flex-main {
  width: 550px;
  height: 100%;
  display: flex;
  justify-content: space-between;
}

.wind-img {
  width: 660px;
  height: 375px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url('../../../static/images/layout/roadway.jpg');
  position: relative;
}

.light {
  width: 20px;
  height: auto;
  position: absolute;
}

.light-left {
  left: 55px;
  top: 35px;
}

.light-right {
  left: 520px;
  top: 35px;
}

.wind-reprint {
  width: 660px;
  height: 375px;
  background-size: cover;
  background-repeat: no-repeat;
  background-image: url('../../../static/images/layout/zhuanzaidian.png');
  position: relative;
}

.light-1 {
  left: 290px;
  top: 170px;
}

.light-2 {
  left: 460px;
  top: 170px;
}

.light-3 {
  right: 40px;
  top: 85px;
}

.operate-layout {
  display: flex;
  flex-direction: column;
  justify-content: start;
}

.operate-box {
  width: 198px;
  height: 120px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/operate-bg.png");
}

.operate-title {
  font-size: 14px;
  color: #fff;
  padding-top: 8px;
}

.button-box {
  height: 93px;
  padding: 14px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.button-list {
  height: 100%;
  display: flex; flex-direction: column;
  justify-content: space-between;
}

.chart {
  width: 1150px;
  height: 320px;
  text-align: left;
}

.table-layout {
  display: flex;
  align-items: start;
  justify-content: space-between;
}
.table-box {
  width: 320px;
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
.custom-table >>> .el-table__cell {
  padding: 8px 0;
  height: 40px;
}

.custom-table >>> .el-table__body-wrapper {
  max-height: 520px;
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

.desc-list {
  font-size: 20px;
  font-weight: bold;
  color: #A61717;
  text-align: left;
}
.desc {
  background: burlywood;
  padding: 5px;
  width: 180PX;
  margin-bottom: 16px;
}
</style>
