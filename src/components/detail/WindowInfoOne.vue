<template>
  <div class="info-flex">
    <div class="flex-left">
      <div class="monitor-box">
        <span class="monitor-title">监控1</span>
        <div class="monitor-video">
          <video ref="video1" id="video1" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>
      </div>
    </div>
    <div class="flex-main">
      <div class="window-box">
        <img :src="window_url" class="window-img" />
        <div class="window-info">
            <div class="btn-space button-blue" @click="operateInstruct('FC_CZ_OPEN1', 1)">启动风窗</div>
            <div class="btn-space button-orange" @click="operateInstruct('FC_CZ_CLOSE1', 1)">关闭风窗</div>
            <div class="btn-space button-blue" @click="openParam">参数设置</div>
            <div class="btn-space button-orange" @click="openHistory">历史数据</div>
        </div>
      </div>

    </div>
    <div class="flex-main">
      <div class="chart" id="main1"></div>
    </div>

    <param-modal :visible.sync="paramVisible" deviceType="windOne"></param-modal>
    <history-modal :visible.sync="historyVisible"></history-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import ParamModal from '../modal/ParamModal.vue'
import HistoryModal from '../modal/HistoryModal.vue'
import JMuxer from 'jmuxer'
export default {
  name: "WindowInfoOne",
  components: {
    ParamModal,
    HistoryModal
  },
  props: {
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
      socketList: [],
      window_url: require('../../../static/images/layout/wind_1.png')
    };
  },
  watch: {
    'paramSignal.FC_XH_JD1': { // 风窗角度
      handler(newVal, oldVal) {
        this.initChart()
        this.changeWindow(newVal, oldVal)
      }
    }
  },
  created() {
    const videos = document.querySelectorAll("video");   //获取所有video标签
    videos.forEach(video => {
      video.muted = false
    });
  },
  mounted() {
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
    // 处理风窗动画
    changeWindow(newVal, oldVal) {
      var new_index = newVal == 90 ? 8 : Math.ceil(Number(newVal) / 15) + 1
      var old_index = oldVal == 90 ? 8 : Math.ceil(Number(oldVal) / 15) + 1
      // var img = document.getElementById("img");
      if (oldVal) {
        if (new_index != old_index) {
          var i = old_index;
          if (old_index < new_index) {
            var timer = setInterval(() => {
              this.window_url = require(`../../../static/images/layout/wind_${++i}.png`)
              if (i == new_index) {
                clearInterval(timer)
              }
            }, 300)
          } else {
            var timer = setInterval(() => {
              this.window_url = require(`../../../static/images/layout/wind_${--i}.png`)
              if (i == new_index) {
                clearInterval(timer)
              }
            }, 300)
          }
        }
      } else {
        this.window_url = require(`../../../static/images/layout/wind_${new_index}.png`)
      }
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

    initChart() {
      if (this.$echarts.getInstanceByDom(document.getElementById("main1"))) {
        this.$echarts.dispose(document.getElementById("main1"))
      }
      var myChart1 = this.$echarts.init(document.getElementById("main1"));
      const dataAngleA = this.paramSignal.FC_XH_JD1 || 0;
      const dataX = 100;
      var option1 = {
        title: [
          {
            text: `${dataAngleA} °`,
            bottom: "5%",
            left: "center",
            textStyle: {
              fontSize: "24",
              color: "#ffff",
              fontWeight: 800,
            },
            triggerEvent: true,
          },
        ],

        legend: {
          show: false,
        },
        series: [
          {
            name: "刻度尺",
            type: "gauge",
            radius: "88%",
            splitNumber: 10, // 刻度数量
            min: 0, // 最小刻度
            max: dataX, // 最大刻度
            // 仪表盘轴线相关配置
            axisLine: {
              lineStyle: {
                color: [
                  [
                    1,
                    {
                      type: "radial",
                      x: 0.5,
                      y: 0.6,
                      r: 0.6,
                      colorStops: [
                        {
                          offset: 0.85,
                          color: "#031F46", // 0% 处的颜色
                        },
                        {
                          offset: 0.93,
                          color: "#060d25", // 100% 处的颜色
                        },
                        {
                          offset: 1,
                          color: "#12D7EF", // 100% 处的颜色
                        },
                      ],
                    },
                  ],
                ],
              },
            },
            // 分隔线
            splitLine: {
              show: true,
              length: 14,
              lineStyle: {
                width: 3,
                color: "#12E5FE",
              },
            },
            // 刻度线
            axisTick: {
              show: true,
              splitNumber: 10,
              length: 10,
              lineStyle: {
                color: "#12E5FE",
                width: 2,
              },
            },
            // 刻度标签
            axisLabel: {
              distance: 10,
              color: "#CEF3FE",
              fontSize: "14",
              fontWeight: 500,
            },
            detail: {
              show: false,
            },
          },

          {
            name: "内层带指针",
            type: "gauge",
            radius: "55%",
            splitNumber: 10, // 刻度数量
            min: 0, // 最小刻度
            max: dataX, // 最大刻度
            // 仪表盘轴线相关配置
            axisLine: {
              lineStyle: {
                color: [
                  [
                    1,
                    {
                      type: "radial",
                      x: 0.5,
                      y: 0.59,
                      r: 0.6,
                      colorStops: [
                        {
                          offset: 0.72,
                          color: "#032046",
                        },
                        {
                          offset: 0.94,
                          color: "#086989",
                        },
                        {
                          offset: 0.98,
                          color: "#0FAFCB",
                        },
                        {
                          offset: 1,
                          color: "#0EA4C1",
                        },
                      ],
                    },
                  ],
                ],
                width: 1000,
              },
            },
            // 分隔线
            splitLine: {
              show: false,
            },
            // 刻度线
            axisTick: {
              show: false,
            },
            // 刻度标签
            axisLabel: {
              show: false,
            },
            // 仪表盘指针
            pointer: {
              show: true,
              length: "90%",
              width: 5, // 指针粗细
            },
            // 仪表盘指针样式
            itemStyle: {
              color: "#01eaf8",
            },
            data: [
              {
                value: dataAngleA,
              },
            ],
            detail: {
              show: false,
            },
          },
        ],
      };

      myChart1.setOption(option1);
    }
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
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: start;
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
  width: 600px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.window-box {
  width: 100%;
  height: 300px;
  display: flex;
}
.window-img {
  width: 460px;
  height: 309px;
}
.window-info {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: start;
  margin-left: 12px;
}
.btn-space {
  margin-bottom: 16px;
}

.chart {
  width: 500px;
  height: 500px;
}
</style>
