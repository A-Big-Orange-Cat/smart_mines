<template>
  <div class="info-flex">
    <div class="flex-left">
      <div class="monitor-box">
        <span class="monitor-title">监控1</span>
        <div class="monitor-video">
          <video ref="video1" id="video1" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>
      </div>
      <div class="monitor-box">
        <span class="monitor-title">监控2</span>
        <div class="monitor-video">
          <video ref="video2" id="video2" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>
      </div>
      <div class="monitor-box">
        <span class="monitor-title">监控3</span>
        <div class="monitor-video">
          <video ref="video3" id="video3" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>
      </div>
      <div class="monitor-box">
        <span class="monitor-title">监控4</span>
        <div class="monitor-video">
          <video ref="video4" id="video4" controls style="width: 100%;height: 100%;" autoplay muted></video>
        </div>
      </div>
    </div>
    <div class="flex-center">
      <div class="door-box">
        <div class="door-img"></div>
        <!-- a门打开 关闭 序列帧 -->
        <div class="door-gif-a" ref="doorA"></div>
        <div class="door-gif-b" ref="doorB"></div>
        <!-- 警示灯 等其他元素 -->
        <img
          class="door-shan shan1"
          v-show="paramSignal.FM_XH_YDCGQ1 == 'true'"
          src="../../../static/images/layout/shan.gif"
          alt=""
        />
        <img
          class="door-shan shan2"
          v-show="paramSignal.FM_XH_YDCGQ2 == 'true'"
          src="../../../static/images/layout/shan.gif"
          alt=""
        />
        <img
          class="door-shan shan3"
          v-show="paramSignal.FM_XH_EDCGQ1 == 'true'"
          src="../../../static/images/layout/shan.gif"
          alt=""
        />
        <img
          class="door-shan shan4"
          v-show="paramSignal.FM_XH_EDMCHQ2 == 'true'"
          src="../../../static/images/layout/shan.gif"
          alt=""
        />
        <img
          class="door-a-light"
          src="../../../static/images/layout/greenlight.png"
          alt=""
        />
        <img
          class="door-b-light"
          src="../../../static/images/layout/greenlight.png"
          alt=""
        />
        <img
          class="warning-a-light"
          v-show="paramSignal.FM_XH_YDFJ == 'true'"
          src="../../../static/images/layout/warninglight.png"
          alt=""
        />
        <img
          class="warning-b-light"
          v-show="paramSignal.FM_XH_EDFJ == 'true'"
          src="../../../static/images/layout/warninglight.png"
          alt=""
        />
        <div class="countdown-a" v-show="paramSignal.FM_XH_YDYS != '0'">{{ paramSignal.FM_XH_YDYS }}</div>
        <div class="countdown-b" v-show="paramSignal.FM_XH_EDYS != '0'">{{ paramSignal.FM_XH_EDYS }}</div>
      </div>
      <div class="operate-layout">
        <div class="operate-box">
          <p class="operate-title">远程控制</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-list">
              <div style="color: #fff;">{{ (paramSignal.FM_XH_CM == '0' || paramSignal.FM_XH_CM == 'false') ?
                '就地模式' : ((paramSignal.FM_XH_CM == '1' || paramSignal.FM_XH_CM == 'true') ? '远程模式' : '') }}</div>
              <div class="button-blue" v-show="paramSignal.FM_XH_CM == '0' || paramSignal.FM_XH_CM == 'false'"
                @click="remoteControl('FM_CS_CM', 1)">远程启动</div>
              <div class="button-orange" v-show="paramSignal.FM_XH_CM == '1' || paramSignal.FM_XH_CM == 'true'"
                @click="remoteControl('FM_CS_CM', 0)">远程停止</div>
            </div>
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">风门操作</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-list">
              <div class="button-orange" v-show="paramSignal.FM_XH_KB1 == 'true'"
                @click="operateInstruct('FM_CZ_OPEN1', 1)"> A 门打开</div>
              <div class="button-blue" v-show="paramSignal.FM_XH_KB1 == 'false'"
                @click="operateInstruct('FM_CZ_CLOSE1', 1)"> A 门关闭</div>
              <div class="button-orange" v-show="paramSignal.FM_XH_KB2 == 'true'"
                @click="operateInstruct('FM_CZ_OPEN2', 1)"> B 门打开</div>
              <div class="button-blue" v-show="paramSignal.FM_XH_KB2 == 'false'"
                @click="operateInstruct('FM_CZ_CLOSE2', 1)"> B 门关闭</div>
            </div>
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">参数设置</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-orange" @click="openParam">参数设置</div>
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
    <div class="flex-right">
      <div class="table-layout">
        <div class="table-box">
          <el-table
            :data="switchLog.aSwitchLog"
            :border="true"
            style="width: 100%"
            class="custom-table"
            :header-cell-style="headerCellStyle"
          >
            <el-table-column
              label="A风门"
              prop="operationValue"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ (scope.row.controlMode.name) +
                  (scope.row.operationValue == 'true' ? '关闭' : '开启') }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="A风门"
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
        <div class="table-box">
          <el-table
            :data="switchLog.bSwitchLog"
            :border="true"
            style="width: 100%"
            class="custom-table"
            :header-cell-style="headerCellStyle"
          >
            <el-table-column
              label="B风门"
              prop="type"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ (scope.row.controlMode.name) +
                  (scope.row.operationValue == 'true' ? '关闭' : '开启') }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="B风门"
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
    <param-modal :visible.sync="paramVisible" deviceType="damper"></param-modal>
    <history-modal ref="history" :visible.sync="historyVisible"></history-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import ParamModal from '../modal/ParamModal.vue'
import HistoryModal from '../modal/HistoryModal.vue'
import JMuxer from 'jmuxer'
export default {
  name: "DamperInfo",
  components: {
    ParamModal,
    HistoryModal
  },
  props: {
    cameraList: Array,
    paramSignal: {
      type: Object,
      default: () => ({})
    },
    switchLog: Object
  },
  data() {
    return {
      isOpenA: false, // A门是否打开
      isClickA: true, // 是否可以操作A门(2秒的打开关闭动画时不能操作)
      isOpenB: false,
      isClickB: true,
      paramVisible: false,
      historyVisible: false,
      tableData: [],
      socketList: [],
      timerList: []
    };
  },
  watch: {
    'paramSignal.FM_XH_KB1': { // A门开闭状态信号
      handler(val) {
        if (JSON.parse(val)) {
          this.closeDoorA()
        } else {
          this.openDoorA()
        }
      }
    },
    'paramSignal.FM_XH_KB2': { // B门开闭状态信号
      handler(val) {
        if (JSON.parse(val)) {
          this.closeDoorB()
        } else {
          this.openDoorB()
        }
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
            "url(" + require("../../../static/images/layout/a-open7.png") + ")";
          this.isClickA = true
        }, 1900);
        this.timerList.push(timer)
      } else {
        return;
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
            "url(" + require("../../../static/images/layout/a-open1.png") + ")";
          this.isClickA = true;
        }, 1900);
        this.timerList.push(timer)
      } else {
        return;
      }
    },
    // 点击打开B门
    openDoorB() {
      if (!this.isOpenB && this.isClickB) {
        this.isOpenB = true;
        this.isClickB = false;

        this.$refs.doorB.classList.remove("door-close-b");
        this.$refs.doorB.classList.add("door-open-b");
        let timer = setTimeout(() => {
          this.$refs.doorB.classList.remove("door-open-b");
          this.$refs.doorB.style.backgroundImage =
            "url(" + require("../../../static/images/layout/b-open7.png") + ")";
          this.isClickB = true
        }, 1900);
        this.timerList.push(timer)
      } else {
        return;
      }
    },
    // 点击关闭B门
    closeDoorB() {
      if (this.isOpenB && this.isClickB) {
        this.isOpenB = false;
        this.isClickB = false;

        this.$refs.doorB.classList.remove("door-open-b");
        this.$refs.doorB.classList.add("door-close-b");
        let timer = setTimeout(() => {
          this.$refs.doorB.classList.remove("door-close-b");
          this.$refs.doorB.style.backgroundImage =
            "url(" + require("../../../static/images/layout/b-open1.png") + ")";
          this.isClickB = true;
        }, 1900);
        this.timerList.push(timer)
      } else {
        return;
      }
    },
    openParam() {
      this.paramVisible = true
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
  justify-content: space-between;
}
.monitor-box {
  width: 330px;
  height: 170px;
  padding: 30px 20px 20px;
  box-sizing: border-box;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/monitor-bg.png");
  position: relative;
}
.monitor-title {
  position: absolute;
  top: 4px;
  left: 20px;
  color: #fff;
}
.monitor-video {
  width: 100%;
  height: 100%;
}
.flex-center {
  width: 1050px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.door-box {
  width: 1050px;
  height: 597px;
  position: relative;
}
.door-img {
  width: 100%;
  height: 100%;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/door.png");
}
.door-shan {
  width: 40px;
  height: 40px;
}
.shan1 {
  position: absolute;
  left: 15px;
  top: 230px;
}
.shan2 {
  position: absolute;
  left: 315px;
  top: 250px;
}
.shan3 {
  position: absolute;
  left: 500px;
  top: 265px;
}
.shan4 {
  position: absolute;
  left: 930px;
  top: 290px;
}
.door-a-light {
  width: 16px;
  height: auto;
  position: absolute;
  left: 170px;
  top: 163px;
}
.door-b-light {
  width: 26px;
  height: auto;
  position: absolute;
  left: 755px;
  top: 115px;
}
.warning-a-light {
  width: 20px;
  height: auto;
  position: absolute;
  left: 275px;
  top: 200px;
}
.warning-b-light {
  width: 20px;
  height: auto;
  position: absolute;
  left: 950px;
  top: 200px;
}
.countdown-a {
  font-size: 40px;
  color: #fff;
  position: absolute;
  left: 180px;
  top: 340px
}
.countdown-b {
  font-size: 40px;
  color: #fff;
  position: absolute;
  left: 720px;
  top: 480px
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
.door-open-a {
  animation: doorgifa 2s linear 1 normal;
}
.door-close-a {
  animation: doorgifa 2s linear 1 reverse;
}
@keyframes doorgifa {
  0% {
    background-image: url("../../../static/images/layout/a-open1.png");
  }
  20% {
    background-image: url("../../../static/images/layout/a-open2.png");
  }
  40% {
    background-image: url("../../../static/images/layout/a-open3.png");
  }
  55% {
    background-image: url("../../../static/images/layout/a-open4.png");
  }
  70% {
    background-image: url("../../../static/images/layout/a-open5.png");
  }
  85% {
    background-image: url("../../../static/images/layout/a-open6.png");
  }
  100% {
    background-image: url("../../../static/images/layout/a-open7.png");
  }
}

.door-gif-b {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-size: contain;
  background-repeat: no-repeat;
}
.door-open-b {
  animation: doorgifb 2s linear 1 normal;
}
.door-close-b {
  animation: doorgifb 2s linear 1 reverse;
}
@keyframes doorgifb {
  0% {
    background-image: url("../../../static/images/layout/b-open1.png");
  }
  20% {
    background-image: url("../../../static/images/layout/b-open2.png");
  }
  40% {
    background-image: url("../../../static/images/layout/b-open3.png");
  }
  55% {
    background-image: url("../../../static/images/layout/b-open4.png");
  }
  70% {
    background-image: url("../../../static/images/layout/b-open5.png");
  }
  85% {
    background-image: url("../../../static/images/layout/b-open6.png");
  }
  100% {
    background-image: url("../../../static/images/layout/b-open7.png");
  }
}

.operate-layout {
  display: flex;
  align-items: center;
  justify-content: space-around;
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

.desc-list {
  width: 160px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: start;
  justify-content: space-around;
  color: #63d8ff;
  font-size: 14px;
  font-weight: bold;
  padding-left: 4px;
  box-sizing: border-box;
}

.flex-right {
  width: 320px;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.table-layout {
  display: flex;
  flex-direction: column;
  justify-content: space-between
}
.table-box {
  width: 320px;
  margin-bottom: 40px;
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

</style>
