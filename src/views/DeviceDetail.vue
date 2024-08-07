<template>
  <div class="info-box">
    <div class="info-main">
      <div class="info-header">
        <i class="el-icon-arrow-left arrow-left" @click="handleBack">返回</i>
        <div class="title-box">
          <span class="title">{{ name }}</span>
          <el-tag size="mini" :type="status ? 'success' : 'warning'">{{ status ? '在线' : '离线' }}</el-tag>
        </div>
      </div>
      <div class="main-box">
        <!-- 设备 -->
        <!-- 风门 -->
        <damper-info ref="1" v-if="deviceTypeId == '1'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
          :switchLog="switchLog"
        ></damper-info>
        <!-- 风窗双 -->
        <window-info ref="5" v-if="deviceTypeId == '5'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
        ></window-info>
        <!-- 风窗单 -->
        <window-info-one ref="4" v-if="deviceTypeId == '4'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
        ></window-info-one>
        <!-- <fire-door-info v-if="deviceTypeId == '防火门'"></fire-door-info> -->
        <!-- 精准测风 -->
        <measure-wind-info ref="6" v-if="deviceTypeId == '6'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
        ></measure-wind-info>
        <!-- 大巷 转载点 合称为 多功能喷雾 -->
        <main-lane-info ref="8" v-if="deviceTypeId == '8'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
          :operationLogs="operationLogs"
          :paramSetting="paramSetting"
          :alarm="alarm"
          :status="status"
        ></main-lane-info>
        <!-- 综采喷雾 -->
        <mine-info ref="7" v-if="deviceTypeId == '7'"
          :paramSignal="paramSignal"
          :paramSetting="paramSetting"
          :alarm="alarm"
          :status="status"
        ></mine-info>
        <!-- 风门带风窗 百叶 -->
        <damper-window-info ref="2" v-if="deviceTypeId == '2'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
          :switchLog="switchLog"
        ></damper-window-info>
        <!-- 风门带风窗 推拉 -->
        <damper-window-slide ref="3" v-if="deviceTypeId == '3'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
          :switchLog="switchLog"
        ></damper-window-slide>
        <!-- 副井口风门 -->
        <damper-pithead ref="11" v-if="deviceTypeId == '11'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
          :switchLog="switchLog"
          :paramSetting="paramSetting"
        ></damper-pithead>
        <!-- 防火门 -->
        <fire-door-info ref="10" v-if="deviceTypeId == '10'"
          :cameraList="cameraList"
          :paramSignal="paramSignal"
          :switchLog="doorSwitchLog"
          :paramSetting="paramSetting"
        ></fire-door-info>
      </div>
    </div>

    <el-dialog
      title="提示"
      class="dialog-modal"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <span>当前设备已离线</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="() => dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import DamperInfo from '../components/detail/DamperInfo.vue'
import WindowInfo from '../components/detail/WindowInfo.vue'
import WindowInfoOne from '../components/detail/WindowInfoOne.vue'
import FireDoorInfo from '../components/detail/FireDoorInfo.vue'
import MeasureWindInfo from '../components/detail/MeasureWindInfo.vue'
import MainLaneInfo from '../components/detail/MainLaneInfo.vue'
import MineInfo from '../components/detail/MineInfo.vue'
import DamperWindowInfo from '../components/detail/DamperWindowInfo.vue'
import DamperPithead from '../components/detail/DamperPithead.vue'
import DamperWindowSlide from '../components/detail/DamperWindowSlide.vue'

export default {
  components: {
    DamperInfo,
    WindowInfo,
    WindowInfoOne,
    FireDoorInfo,
    MeasureWindInfo,
    MainLaneInfo,
    MineInfo,
    DamperWindowInfo,
    DamperPithead,
    DamperWindowSlide
  },
  name: "DeviceDetail",
  data() {
    return {
      name: this.$route.params.name,
      deviceTypeId: this.$route.params.deviceTypeId,
      socket: null,
      cameraList: [],
      paramSignal: {},
      switchLog: {},
      doorSwitchLog: [],
      operationLogs: [],
      paramSetting: {},
      alarm: {},
      status: true,
      dialogVisible: false
    }
  },
  watch: {
    'status': {
      handler(val) {
        if (val) {
          this.dialogVisible = false
        } else {
          this.dialogVisible = true
        }
      },
      immediate: false
    }
  },
  mounted() {
    this.initWebsocket()
    this.startCamera()
  },
  beforeDestroy() {
    this.socket.close()
  },
  methods: {
    handleBack() {
      this.$router.push({
        name: "home",
      });
    },
    initWebsocket() {
      // let ip = this.$http.defaults.baseURL.replace('http', 'ws');
      let ip = JSON.parse(localStorage.getItem('system')).websocketURL;
      let path = `${ip}/monitorWebSocket/${this.deviceTypeId}/${this.$route.params.deviceId}`;
      // 实例化
      this.socket = new WebSocket(path);
      // 连接
      this.socket.onopen = (e) => {
        console.log("连接打开->", e);
      };
      // 错误信息
      this.socket.onerror = (e) => {
        console.log("连接错误->", e);
      };
      // 接收信息
      this.socket.onmessage = (res) => {
        var json = JSON.parse(res.data);
        this.paramSignal = json.parametersOfSignal
        this.paramSetting = json.parametersOfParaSetting
        this.switchLog = json.switchLog
        this.doorSwitchLog = json.switchLog
        this.operationLogs = json.operationLogs
        this.status = json.connStatus
        this.alarm = json.alarm
        // console.log('接收信息monitor', json);
        // this.table_data.map(item => {
          // this.$set(item, 'status', json[item.deviceId].connStatus ? true : false)
        // })
      };
    },
    // 摄像头登录并开启预览接口
    async startCamera() {
      try {
        const res = await this.$http.post('/hik/start', {
          deviceId: Number(this.$route.params.deviceId)
        })
        this.cameraList = res.data.data
        if (res.data.code == 200) {
          this.$nextTick(() => {
            if (this.deviceTypeId != '7') {
              this.$refs[this.deviceTypeId].startPlay()
            }
          })
        }
      } catch (error) {
        console.log(error);
      }
    }

  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.info-box {
  width: 100%;
  height: calc(100% - 75px);
  padding: 28px 48px;
  box-sizing: border-box;
  /* 居中 */
  display: flex;
  align-items: center;
  justify-content: center;
}
.info-main {
  width: 100%;
  height: 100%;
  background: rgba(5, 13, 75, 0.8);
  border: 2px solid rgba(8, 72, 138, 1);
  box-shadow: inset 0px 1px 20px rgba(18, 142, 232, 0.34);
  box-sizing: border-box;
  /* background-size: cover;
  background-repeat: no-repeat;
  background-image: url("../../static/images/layout/home-bg.png"); */
}
.info-header {
  width: 100%;
  height: 50px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../static/images/layout/modal-header.png");
  position: relative;
}
.arrow-left {
  position: absolute;
  top: 16px;
  left: 24px;
  font-size: 16px;
  color: #63d8ff;
  cursor: pointer;
  z-index: 99;
}
.title-box {
  height: 50px;
  line-height: 50px;
}
.title {
  font-family: 'fangsong';
  font-size: 24px;
  font-weight: bold;
  color: #cdddf7;
  vertical-align: middle;
}
.main-box {
  width: 100%;
  height: calc(100% - 50px);
  padding: 12px 36px;
  box-sizing: border-box;
}
.dialog-modal {
  text-align: left;
}
.dialog-modal >>> .el-dialog__body {
  padding: 10px 20px;
}
</style>
