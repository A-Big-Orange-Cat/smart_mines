<template>
  <div class="info-flex">
    <div class="flex-left">
      <div class="flex-top">
        <div class="monitor-box">
          <span class="monitor-title">监控</span>
          <video ref="video1" id="video1" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>

        <div class="wind-img"></div>
      </div>
      <div class="chart" id="main"></div>
    </div>
    <div class="flex-main">
      <div class="box-left">
        <div class="operate-layout">
          <div class="operate-box">
            <p class="operate-title">参数设置</p>
            <div class="button-box" style="justify-content: center">
                <div class="button-orange" @click="openParam">参数设置</div>
            </div>
          </div>
          <div class="operate-box" style="margin-top: 24px;">
            <p class="operate-title">历史记录</p>
            <div class="button-box" style="justify-content: center">
              <div class="button-list">
                <div class="button-blue" @click="openHistory">操作记录</div>
                <div class="button-blue" @click="openDust">历史曲线</div>
              </div>
            </div>
          </div>
        </div>
        <div class="desc-list">
          <div class="desc">风速：{{ paramSignal[119] }}m/s</div>
          <div class="desc">断面：{{ paramSignal[120] }}m²</div>
          <div class="desc">风量：{{ (paramSignal[119] * 100) * (paramSignal[120] * 100) * 60 / 10000 }}m³/min</div>
        </div>
      </div>
    </div>

    <param-modal :visible.sync="paramVisible" :paramsList="paramsList" deviceType="measureWind"></param-modal>
    <history-modal :visible.sync="historyVisible" :paramsList="paramsList"></history-modal>
    <dust-modal :visible.sync="dustVisible" deviceType="measureWind"></dust-modal>
  </div>
</template>

<script>
import { VALUE_TYPE_LIST, PARAM_TYPE_LIST } from '../../../static/config/constant'
import ParamModal from '../modal/ParamModal.vue'
import HistoryModal from '../modal/HistoryModal.vue'
import DustModal from '../modal/DustModal.vue'
import JMuxer from 'jmuxer'
export default {
  name: "MeasureWindInfo",
  components: {
    ParamModal,
    HistoryModal,
    DustModal
  },
  props: {
    paramsList: Array,
    cameraList: Array,
    paramSignal: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      paramVisible: false,
      historyVisible: false,
      dustVisible: false,
      chartData: []
    };
  },
  watch: {
    'paramSignal.119': { // 风速信号
      async handler(val) {
        console.log('val:', val);
        this.chartData.push({
          timeMillis: Date.now(),
          signalValue: val
        })
        this.initChart()
      }
    },
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
      this.initChart()
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
      this.paramVisible = true
    },
    openHistory() {
      this.historyVisible = true
    },
    openDust() {
      this.dustVisible = true;
    },
    // 折线图 风速获取
    async querySignalLogCurve() {
      try {
        const res = await this.$http.post('/log/querySignalLogCurve', {
          deviceId: Number(this.$route.params.deviceId),
          deviceParameterId: 119,
          time: [this.getTimeNum(), this.getTimeNum() + 3600 * 1000 * 24],
        })
        this.chartData = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    // 操作指令
    async operateInstruct(deviceParameterId) {
      let deviceParameter;
      this.paramsList.map((item) => {
        if (item.deviceParameterId == deviceParameterId) {
          deviceParameter = item
          deviceParameter.parameterValue = 1
          PARAM_TYPE_LIST.map(param => {
            if (param.label == deviceParameter.parameterType) {
              deviceParameter.parameterType = param.value
            }
          })
          VALUE_TYPE_LIST.map(value => {
            if (value.label == deviceParameter.valueType) {
              deviceParameter.valueType = value.value
            }
          })
        }
      })
      try {
        const res = await this.$http.post('/monitor/updateCommandValue', {
          deviceTypeId: Number(this.$route.params.deviceTypeId),
          deviceId: Number(this.$route.params.deviceId),
          deviceParameter: deviceParameter
        })
        if (res.data.code == 200) {
          if (res.data.data[deviceParameterId].success) {
            this.$message.success('操作成功')
          } else {
            this.$message.error(res.data.data[deviceParameterId].message)
          }
        } else {
          this.$message.error(res.data.msg)
        }
      } catch (error) {
        console.log(error);
      }
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

    initChart() {
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
          left: '6%',
          top: '15%',
          right: '3%',
          bottom: '10%'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function (params, ticket, callback) {
            let index = params[0].dataIndex;
            if (list.length > 0) {
              const dateObj = new Date(list[index][0])
              const hours = dateObj.getHours() < 10 ? '0' + dateObj.getHours() : dateObj.getHours()
              const minutes = dateObj.getMinutes() < 10 ? '0' + dateObj.getMinutes() : dateObj.getMinutes()
              const UnixTimeToDate = hours + ':' + minutes
              let Htm = `时间：${UnixTimeToDate}<br>风速：${list[index][1]}m/s `
              return Htm;
            }
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
            // max: x_list.length,
            // min: 0,
            // interval: Math.floor(x_list.length / 10),
          },
          // data: x_list
        },
        yAxis: {
          name: '风速(m/s)',
          // nameGap: 20,
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
  /* justify-content: space-between; */
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
  width: 400px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.wind-img {
  width: 660px;
  height: 375px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url('../../../static/images/layout/roadway.jpg');
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
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.box-left {
  margin-left: 50px;
}
.operate-layout {
  display: flex;
  flex-direction: column;
  height: 380px;
}
.chart {
  width: 1150px;
  height: 340px;
  text-align: left;
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
  width: 230PX;
  margin-bottom: 16px;
}
</style>
