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
        <!-- 风门延时 -->
        <div class="countdown-a" v-show="paramSignal.FM_XH_YDYS != '0'">{{ paramSignal.FM_XH_YDYS }}</div>
        <div class="countdown-b" v-show="paramSignal.FM_XH_EDYS != '0'">{{ paramSignal.FM_XH_EDYS }}</div>
        <!-- 风窗信息 -->
        <div class="desc-list">
          <div>风窗1角度：{{ paramSignal.FFM_FC_XH_JD1 }}度</div>
          <div>风窗2角度：{{ paramSignal.FFM_FC_XH_JD2 }}度</div>
          <div>风窗3角度：{{ paramSignal.FFM_FC_XH_JD3 }}度</div>
          <div>风窗4角度：{{ paramSignal.FFM_FC_XH_JD4 }}度</div>
        </div>
      </div>
    </div>
    <div class="flex-right">
      <div class="operate-layout">
        <div class="operate-box">
          <p class="operate-title">远程控制</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-list">
              <div style="color: #fff;">{{ (paramSetting.FFM_CS_CM == '0' || paramSetting.FFM_CS_CM == 'false') ?
                '就地模式' : ((paramSetting.FFM_CS_CM == '1' || paramSetting.FFM_CS_CM == 'true') ? '远程模式' : '') }}</div>
              <div class="button-blue" v-show="paramSetting.FFM_CS_CM == '0' || paramSetting.FFM_CS_CM == 'false'"
                @click="remoteControl('FFM_CS_CM', 1)">远程启动</div>
              <div class="button-orange" v-show="paramSetting.FFM_CS_CM == '1' || paramSetting.FFM_CS_CM == 'true'"
                @click="remoteControl('FFM_CS_CM', 0)">远程停止</div>
            </div>
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">设备操作</p>
          <div class="button-box" style="justify-content: center">
            <div class="button-list">
              <div class="button-orange" v-show="paramSignal.FFM_XH_CLOSE == 'true'"
                @click="operateInstruct('FFM_CZ_OPEN', 1)">风门打开</div>
              <div class="button-blue" v-show="paramSignal.FFM_XH_OPEN == 'true' || paramSignal.FFM_XH_CLOSE == 'false'"
                @click="operateInstruct('FFM_CZ_CLOSE', 1)">风门关闭</div>
              <div class="button-orange" v-show="paramSignal.FFM_FC_XH_CLOSE1 == 'true' && paramSignal.FFM_FC_XH_CLOSE2 == 'true'"
                @click="operateInstruct('FFM_FC_CZ_OPEN', 1)">风窗打开</div>
              <div class="button-blue" v-show="paramSignal.FFM_FC_XH_CLOSE1 == 'false' || paramSignal.FFM_FC_XH_CLOSE2 == 'false'"
                @click="operateInstruct('FFM_FC_CZ_CLOSE', 1)">风窗关闭</div>
            </div>
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">风窗操作</p>
          <div class="button-box">
            <div class="button-list">
              <div class="button-blue" @click="openWindow('A风门风窗1', 1)">风门风窗1</div>
              <div class="button-blue" @click="openWindow('A风门风窗2', 2)">风门风窗2</div>
            </div>
            <div class="button-list">
              <div class="button-blue" @click="openWindow('B风门风窗3', 3)">风门风窗3</div>
              <div class="button-blue" @click="openWindow('B风门风窗4', 4)">风门风窗4</div>
            </div>
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">参数设置</p>
          <div class="button-box" style="justify-content: center">
            <!-- <div class="button-list"> -->
              <div class="button-blue" @click="openParam">参数设置</div>
            <!-- </div> -->
          </div>
        </div>
        <div class="operate-box">
          <p class="operate-title">历史数据</p>
          <div class="button-box" style="justify-content: center">
            <!-- <div class="button-list"> -->
              <div class="button-blue" @click="openHistory">历史数据</div>
            <!-- </div> -->
          </div>
        </div>
      </div>
    </div>
    <param-modal :visible.sync="paramVisible" deviceType="pithead"></param-modal>
    <history-modal ref="history" :visible.sync="historyVisible"></history-modal>
    <window-modal :visible.sync="windowVisible" :windowInfo="windowInfo" :paramSignal="paramSignal"></window-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import ParamModal from '../modal/ParamModal.vue'
import HistoryModal from '../modal/HistoryModal.vue'
import WindowModal from '../modal/WindowModal.vue'
import JMuxer from 'jmuxer'
export default {
  name: "DamperPithead",
  components: {
    ParamModal,
    HistoryModal,
    WindowModal
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
      timerList: [],
      windowVisible: false,
      windowInfo: {
        name: '',
        id: ''
      },
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
        }, 1800);
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
        }, 1800);
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
        }, 1800);
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
        }, 1800);
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
    openWindow(name, id) {
      this.windowInfo.name = name
      this.windowInfo.id = id
      this.windowVisible = true
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
  justify-content: space-between;
}
.monitor-box {
  width: 440px;
  height: 300px;
  padding: 48px 20px 20px;
  box-sizing: border-box;
  background-size: contain;
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
.flex-center {
  width: 1000px;
  height: 100%;
}
.door-box {
  width: 100%;
  height: 100%;
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
  /* animation-delay: -2s; */
}
.door-close-a {
  animation: doorgifa 4s linear 1 reverse;
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
  width: 100%;
  height: 100%;
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
  justify-content: space-between;
}
.button-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.desc-list {
  position: absolute;
  left: 10px;
  bottom: 10px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: start;
  justify-content: space-around;
  color: #1480f0;
  font-size: 16px;
  font-weight: bold;
}
.desc-list > div {
  cursor: pointer;
  text-decoration: underline;
}

.flex-right {
  width: 200px;
  height: 100%;
}

</style>
