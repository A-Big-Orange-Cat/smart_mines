<template>
  <div class="info-flex">
    <div class="flex-left">
      <div class="monitor-box">
        <span class="monitor-title">监控1</span>
        <div class="monitor-video">
          <video ref="video1" id="video1" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>
      </div>

      <div class="table-layout">
        <div class="table-box">
          <el-table
            :data="switchLog"
            :border="true"
            style="width: 100%"
            class="custom-table"
            :header-cell-style="headerCellStyle"
          >
            <el-table-column
              label="防火门"
              prop="type"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ (scope.row.controlMode.name) +
                  (scope.row.operationValue == 'true' ? '关闭' : '开启') }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="防火门"
              prop="timeMillis"
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
    <div class="flex-center">
      <div class="door-box">
        <div class="door-img"></div>
        <!-- a门打开 关闭 序列帧 -->
        <div class="door-gif-a" ref="doorA"></div>
      </div>
    </div>
    <div class="flex-right">
      <div class="operate-layout">
        <div class="operate-box" style="margin-bottom: 20px;">
          <p class="operate-title">远程控制</p>
          <div class="button-box">
            <div class="button-list">
              <div style="color: #fff;">{{ (paramSetting.FHM_CS_CM == '0' || paramSetting.FHM_CS_CM == 'false') ?
                '就地模式' : ((paramSetting.FHM_CS_CM == '1' || paramSetting.FHM_CS_CM == 'true') ? '远程模式' : '') }}</div>
              <div class="button-blue" v-show="paramSetting.FHM_CS_CM == '0' || paramSetting.FHM_CS_CM == 'false'"
                @click="remoteControl('FHM_CS_CM', 1)">远程启动</div>
              <div class="button-orange" v-show="paramSetting.FHM_CS_CM == '1' || paramSetting.FHM_CS_CM == 'true'"
                @click="remoteControl('FHM_CS_CM', 0)">远程停止</div>
            </div>
          </div>
        </div>
        <div class="operate-box" style="margin-bottom: 20px;">
          <p class="operate-title">防火门操作</p>
          <div class="button-box">
            <div class="button-list" style="justify-content: center;">
              <div class="button-orange" v-show="paramSignal.FHM_XH_KB == 'true'"
                @click="operateInstruct('FHM_CZ_OPEN', 1)">防火门打开</div>
              <div class="button-blue" v-show="paramSignal.FHM_XH_KB == 'false'"
                @click="operateInstruct('FHM_CZ_CLOSE', 1)">防火门关闭</div>
            </div>
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">历史记录</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-blue" @click="openHistory">操作记录</div>
          </div>
        </div>
      </div>
    </div>

    <history-modal ref="history" :visible.sync="historyVisible"></history-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import HistoryModal from '../modal/HistoryModal.vue'
import JMuxer from 'jmuxer'
export default {
  name: "FireDoorInfo",
  components: {
    HistoryModal
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
    switchLog: Array,
  },
  data() {
    return {
      isOpenA: false, // A门是否打开
      isClickA: true, // 是否可以操作 打开关闭 A门
      historyVisible: false,
      tableData: [],
      socketList: [],
      timerList: []
    };
  },
  watch: {
    'paramSignal.FHM_XH_KB': { // 防火门开闭状态信号
      handler(val) {
        if (JSON.parse(val)) {
          this.closeDoorA()
        } else {
          this.openDoorA()
        }
      }
    },
  },
  created() {
    const videos = document.querySelectorAll("video");   //获取所有video标签
    videos.forEach(video => {
      video.muted = false
    });
  },
  mounted() {
    this.videoPlay()
  },
  updated() {
    const videos = document.querySelectorAll("video");   //获取所有video标签
    // 页面刷新播放
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
    this.timerList.map(item => {
      clearTimeout(item)
    })
  },
  methods: {
    // 表头合并与样式
    // { 当前行的值, 当前列的值, 行的下标, 列的下标 }
    headerCellStyle({ row, column, rowIndex, columnIndex }) {
      row[0].colSpan = 0;
      row[1].colSpan = 2;
      // 根据列数进行样式赋予
      if (columnIndex === 0) {
        return { display: "none" };
      }
      return {
        background: "rgba(20, 128, 240, 0.8) !important",
      };
    },
    // 点击打开A门
    openDoorA() {
      if (!this.isOpenA && this.isClickA) {
        this.isOpenA = true;
        this.isClickA = false;
        this.$refs.doorA.classList.remove("door-close-a");
        this.$refs.doorA.classList.add("door-open-a");
        let timer = setTimeout(() => {
          this.$refs.doorA.classList.remove("door-open-a");
          this.$refs.doorA.style.backgroundImage =
            "url(" + require("../../../static/images/layout/firedoor7.png") + ")";
          this.isClickA = true
        }, 1900);
        this.timerList.push(timer)
      } else {
        return
      }
    },
    // 点击关闭A门
    closeDoorA() {
      if (this.isOpenA && this.isClickA) {
        this.isOpenA = false;
        this.isClickA = false;
        this.$refs.doorA.classList.remove("door-open-a");
        this.$refs.doorA.classList.add("door-close-a");
        let timer = setTimeout(() => {
          this.$refs.doorA.classList.remove("door-close-a");
          this.$refs.doorA.style.backgroundImage =
            "url(" + require("../../../static/images/layout/firedoor1.png") + ")";
          this.isClickA = true
        }, 1900);
        this.timerList.push(timer)
      } else {
        return
      }
    },

    openHistory() {
      this.historyVisible = true
    },
    // 操作指令
    async operateInstruct(code, val) {
      await deviceWrite('command', code, val, Number(this.$route.params.deviceTypeId), Number(this.$route.params.deviceId))
    },
    // 远程/就地 控制
    async remoteControl(code, val) {
      await deviceWrite('control', code, val, Number(this.$route.params.deviceTypeId), Number(this.$route.params.deviceId))
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

  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped src="./button.css"></style>
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
  justify-content: flex-start;
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
.flex-center {
  width: 1000px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.door-box {
  width: 1000px;
  height: 100%;
  position: relative;
}
.door-img {
  width: 100%;
  height: 100%;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/firedoor.jpg");
}
.door-gif-a {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-size: contain;
  background-repeat: no-repeat;
}
@keyframes doorgifa {
  0% {
    background-image: url("../../../static/images/layout/firedoor1.png");
  }
  20% {
    background-image: url("../../../static/images/layout/firedoor2.png");
  }
  40% {
    background-image: url("../../../static/images/layout/firedoor3.png");
  }
  55% {
    background-image: url("../../../static/images/layout/firedoor4.png");
  }
  70% {
    background-image: url("../../../static/images/layout/firedoor5.png");
  }
  85% {
    background-image: url("../../../static/images/layout/firedoor6.png");
  }
  100% {
    background-image: url("../../../static/images/layout/firedoor7.png");
  }
}
.door-open-a {
  animation: doorgifa 2s linear 1 normal;
}
.door-close-a {
  animation: doorgifa 2s linear 1 reverse;
}

.operate-layout {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
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
  justify-content: center
}
.button-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.flex-right {
  width: 220px;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.table-layout {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.table-box {
  width: 440px;
  margin-top: 40px;
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
  max-height: 440px;
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
