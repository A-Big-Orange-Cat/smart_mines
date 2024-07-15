<template>
  <div>
    <el-dialog
      class="window-modal"
      :visible="visible"
      width="700px"
      :modal="false"
      :title="windowInfo.name"
      :close-on-click-modal="false"
      @update:visible="updateVisible"
    >
      <div class="content-box">
        <img :src="window_url" class="window-img" />
        <div class="window-info">
          <div class="button-operate button-blue" @click="operateInstruct(`FC_CZ_OPEN${windowInfo.id}`, 1)">启动风窗</div>
          <div class="button-operate button-red" @click="operateInstruct(`FC_CZ_CLOSE${windowInfo.id}`, 1)">关闭风窗</div>
          <div class="button-operate button-blue" @click="openParam">参数设置</div>
          <div class="window-text">风窗角度：{{ windowSignal }}</div>
        </div>

      </div>
    </el-dialog>

    <param-modal
      :visible.sync="paramVisible"
      deviceType="window"
      :windowIndex="windowInfo.id"
    ></param-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import ParamModal from './ParamModal.vue'
export default {
  name: 'WindowModal',
  components: {
    ParamModal
  },
  props: {
    visible: Boolean,
    paramSignal: {
      type: Object,
      default: () => ({})
    },
    windowInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      paramVisible: false,
      window_url: require('../../../static/images/layout/wind_1.png')
    }
  },
  computed: {
    windowSignal() {
      return this.paramSignal[`FC_XH_JD${this.windowInfo.id}`]
    }
  },
  watch: {
    'windowSignal': {
      handler(newVal, oldVal) {
        this.$nextTick(() => {
          this.changeWindow(newVal, oldVal)
        })
      }
    }
  },
  created() {
  },
  methods: {
    updateVisible(newVisible) {
      // this.$refs.form.resetFields();
      this.$emit("update:visible", newVisible);
    },
    openParam() {
      this.paramVisible = true
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
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.window-modal {
  display: flex;
  align-items: center;
  justify-content: center;
}
.window-modal >>> .el-dialog {
  background: rgba(5, 13, 75, 1);
  border: 2px solid rgba(8, 72, 138, 1);
  box-shadow: inset 0px 1px 20px rgba(18, 142, 232, 0.34);
  box-sizing: border-box;
  margin: auto !important;
}
.window-modal >>> .el-dialog__body {
  padding: 24px;
}
.window-modal >>> .el-dialog__header {
  padding: 0;
  text-align: center;
  width: 100%;
  height: 48px;
  line-height: 48px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/modal-window.png");
}
.window-modal >>> .el-dialog__title {
  font-family: 'fangsong';
  font-weight: bold;
  color: #fff;
}

.content-box {
  display: flex;
  align-items: start;
  justify-content: center;
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
.button-operate {
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  margin-bottom: 12px;
}
.button-blue {
  background-color: #2daebf;
}
.button-red {
  background-color: #e33100;
}
.button-blue:hover {
  background-color: #5dbdca;
}
.button-red:hover {
  background-color: #fa4817;
}

.window-text {
  font-size: 16px;
  color: #fff;
}
</style>
