<template>
  <div class="home-main">
    <div class="drag-box" v-if="system.mapPath" @mousewheel="wheelMarker">
      <element-drag :location="location" :curScale="scale">
        <div class="drag-main">
          <img ref="image" :src="system.mapPath" alt="" />
          <el-tooltip v-for="(item, index) in deviceList" :key="index" v-show="item.deviceLocation"
            effect="dark" :content="item.deviceName" placement="top">
            <i ref="marker" class="el-icon-location" @click="clickMarker(item)" style="cursor: pointer;"></i>
          </el-tooltip>
        </div>
      </element-drag>
    </div>
    <div class="layout-left">
      <count-card :deviceNum="deviceList.length"></count-card>
      <chart-card :typeList="typeList"></chart-card>
      <warn-card :tableList="deviceList"></warn-card>
    </div>
    <div class="layout-right">
      <list-card
        :typeList="typeList"
        :tableList="table_data"
        @updateTable="updateTable"
        @updateList="updateList"
      ></list-card>
      <chart-bar :typeList="typeList"></chart-Bar>
      <!-- 加载 背景图 -->
      <load-bg></load-bg>
    </div>

  </div>
</template>

<script>
import { CHART_COLOR } from '../../static/config/constant'
import CountCard from "../components/home/CountCard.vue";
import ChartCard from "../components/home/ChartCard.vue";
import WarnCard from "../components/home/WarnCard.vue";
import ElementDrag from "../components/common/ElementDrag.vue";
import ListCard from "../components/home/ListCard.vue";
import LoadBg from "../components/home/LoadBg.vue";
import ChartBar from "../components/home/ChartBar.vue";
export default {
  components: {
    CountCard,
    ChartCard,
    WarnCard,
    ElementDrag,
    ListCard,
    LoadBg,
    ChartBar
  },
  name: "Home",
  data() {
    return {
      socket: null,
      table_data: [],
      deviceList: [],
      system: JSON.parse(localStorage.getItem('system')),
      typeList: [],
      scale: JSON.parse(localStorage.getItem('mapInfo')).scale || 1
    };
  },
  computed: {
    location() {
      let mapInfo = JSON.parse(localStorage.getItem('mapInfo'))
      if (mapInfo) {
        return {
          x: mapInfo.x,
          y: mapInfo.y
        }
      } else {
        return {
          x: 0,
          y: 0
        }
      }
    }
  },
  watch: {
    'deviceList': {
      handler(val) {
        this.getMarker();
      },
      deep: true
    }
  },
  created() {
    this.queryDeviceTypeAndDevices()
  },
  mounted() {
    this.initWebsocket();
    this.preloadImage()
  },
  beforeDestroy() {
    this.socket.close()
  },
  methods: {
    // 获取到标注点 对标注点设置样式 相对于图片定位到相应位置
    getMarker() {
      this.$nextTick(() => {
        this.deviceList.map((item, index) => {
          if (item.deviceLocation) {
            var arrLocation = item.deviceLocation.split(",");
            var marker = this.$refs.marker[index];
            marker.style.fontSize = "40px";
            marker.style.position = "absolute";
            marker.style.top = (arrLocation[1] - [(20 * (this.scale + 0.44)) - (18 * (this.scale-0.04-1))] ) + "px";
            marker.style.left = (arrLocation[0] - 20) + "px";

            // var colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452'];
            if (item.status) {
              marker.style.color = CHART_COLOR[item.deviceTypeId - 1];
            } else {
              marker.style.color = '#808285'
            }
          }
        })
      })
    },
    wheelMarker(e) {
      let speed = e.wheelDelta / 120;
      if (this.scale >= 0.2 && this.scale <= 5) {
        this.scale += 0.04 * speed;
        if (this.scale > 5) this.scale = 5;
        if (this.scale < 0.2) this.scale = 0.2;
        this.scale = parseFloat(this.scale.toFixed(2));
        this.deviceList.map((item, index) => {
          let number = 18;
          let marker = this.$refs.marker[index];
          if (marker) {
            marker.style.transform = `scale(${1 / this.scale}) translate(0, ${number * (this.scale - 0.04 - 1)}px)`;
          }
        })
      }
    },
    // 点击图标
    clickMarker(data) {
      this.$router.push({
        name: "deviceDetail",
        params: {
          name: data.deviceName,
          deviceId: data.deviceId,
          deviceTypeId: data.deviceTypeId
        }
      })
    },
    initWebsocket() {
      // let ip = this.$http.defaults.baseURL.replace('http', 'ws');
      let ip = JSON.parse(localStorage.getItem('system')).websocketURL;
      let path = `${ip}/infoWebSocket`
      // 'ws://192.168.11.11:8090/infoWebSocket/1'
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
        this.table_data.map(item => {
          this.$set(item, 'status', json[item.deviceId].connStatus ? true : false)
        })
        this.deviceList.map(item => {
          this.$set(item, 'status', json[item.deviceId].connStatus ? true : false)

          this.$set(item, 'alarm_status', false)
          var itemAlarm = json[item.deviceId].alarm
          for (let key in itemAlarm) {
            if (itemAlarm[key] != 0) {
              this.$set(item, 'alarm_status', true)
            }
          }
        })
      };
      // 连接关闭
      this.socket.onclose = (e) => {
        console.log('infoWebSocket连接关闭');
      }
    },
    updateTable(data) {
      this.table_data = data
    },
    updateList(data) {
      this.deviceList = data
    },
    // 预加载图片
    preloadImage() {
      return new Promise((resolve, reject) => {
        var list = []
        for (var i = 1; i < 9; i++) {
          var img_src = require(`../../static/images/layout/wind_${i}.png`)
          const img = new Image();
          img.onload = () => resolve(img);
          img.onerror = () => reject(new Error('图片加载失败'));
          img.src = img_src;
          list.push(img_src)
        }
      });
    },
    // 查询所有设备类型接口
    async queryDeviceTypeAndDevices() {
      try {
        const res = await this.$http.get('/deviceType/queryDeviceTypeAndEnabledDeviceCount')
        this.typeList = res.data.data
      } catch (error) {
        console.log(error);
      }
    },
  },

};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.home-main {
  width: 100%;
  height: calc(100% - 75px);
  padding: 28px 48px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}
.drag-box {
  width: calc(100% - 864px);
  height: calc(100% - 56px);
  position: absolute;
  top: 28px;
  left: 432px;
}
.drag-main {
  position: relative;
}
.drag-main > * {
  -webkit-user-drag: none;
}
.layout-left {
  /* height: 957px; */
  height: 100%;
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  justify-content: space-between;
}
.layout-right {
  height: 100%;
  z-index: 99;
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  justify-content: space-between;
}
</style>
