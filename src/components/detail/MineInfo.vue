<template>
  <div class="info-layout">
    <div class="flex-main">
      <div class="chart" id="top1" v-show="dustNumber >= 1"></div>
      <div class="chart" id="top2" v-show="dustNumber >= 2"></div>
      <div class="chart" id="top3" v-show="dustNumber == 3"></div>
    </div>
    <div class="flex-main box-space">
      <div class="chart" id="top4"></div>
      <div class="chart" id="top5"></div>
      <div class="chart" id="top6"></div>
    </div>
    <!-- 移动采矿机 -->
    <div class="car-move" ref="car"></div>
    <!-- 操作 -->
    <div class="operate-layout">

      <div class="operate-box">
        <p class="operate-title">当前分机</p>
        <div class="button-box" style="justify-content: center">
          <el-select v-model="curSpray" placeholder="请选择分机" @change="(val) => operateInstruct('ZC_CZ_CXFJ', val)">
            <el-option
              v-for="item in sprayList"
              :key="item"
              :value="item"
              :label="item"
            ></el-option>
          </el-select>
        </div>
      </div>
      <div class="operate-box" style="margin-top: 24px;">
        <p class="operate-title">分机状态</p>
        <div class="button-box" style="justify-content: center;">
          <div class="button-list" v-show="curSpray">
            <div style="color: #fff;">{{ paramSignal.ZC_XH_FJQT == 0 ? '停用状态' : (paramSignal.ZC_XH_FJQT == 1 ? '启用状态' : '') }}</div>
            <!-- curSpray * 256 + 后面的命令值 -->
            <div class="button-blue" @click="operateInstruct('ZC_CZ_YCQTPW', curSpray * 256 + 1)" v-show="paramSignal.ZC_XH_FJQT == 0">启用</div>
            <div class="button-orange"  @click="operateInstruct('ZC_CZ_YCQTPW', curSpray * 256)" v-show="paramSignal.ZC_XH_FJQT == 1">停用</div>
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
        <p class="operate-title">远程点动</p>
        <div class="button-box">
          <div class="button-list">
            <!-- curSpray * 256 + 后面的命令值 -->
            <div class="button-blue" @click="operateInstruct('ZC_CZ_YCDDKQ', curSpray * 256 + 1)">跟踪开启</div>
            <div class="button-blue" @click="operateInstruct('ZC_CZ_YCDDKQ', curSpray * 256 + 2)">移架开启</div>
          </div>
          <div class="button-list">
            <div class="button-blue" @click="operateInstruct('ZC_CZ_YCDDKQ', curSpray * 256 + 3)">放煤开启</div>
            <div class="button-blue">备用</div>
          </div>
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
    </div>

    <param-modal :visible.sync="paramVisible" :dustNumber="dustNumber" deviceType="mine"></param-modal>
    <history-modal :visible.sync="historyVisible"></history-modal>
    <dust-modal :visible.sync="dustVisible" deviceType="mine" :paramSignal="paramSignal" :paramSetting="paramSetting"></dust-modal>
    <alarm-modal :visible.sync="alarmVisible"></alarm-modal>
  </div>
</template>

<script>
import { deviceWrite } from '../../../static/config/api'
import ParamModal from '../modal/ParamModal.vue'
import HistoryModal from '../modal/HistoryModal.vue'
import DustModal from '../modal/DustModal.vue'
import AlarmModal from '../modal/AlarmModal.vue'
export default {
  name: "MineInfo",
  components: {
    ParamModal,
    HistoryModal,
    DustModal,
    AlarmModal
  },
  props: {
    paramSignal: {
      type: Object,
      default: () => ({})
    },
    paramSetting: {
      type: Object,
      default: () => ({})
    },
    alarm: {
      type: Object,
      default: () => ({})
    },
    status: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      paramVisible: false,
      historyVisible: false,
      dustVisible: false,
      alarmVisible: false,
      sprayNumber: 100, // 分机总数
      dustNumber: 1, // 粉尘浓度个数
      arrX1: this.generateArray(1, 19),
      arrX2: this.generateArray(1, 19),
      arrX3: this.generateArray(1, 19),
      arrY1: new Array(19).fill(0),
      arrY2: new Array(19).fill(0),
      arrY3: new Array(19).fill(0),

      lineData1: [],
      lineData2: [],
      lineData3: [],
      sprayList: [],
      curSpray: null
    };
  },
  watch: {
    'paramSignal.ZC_XH_GZ': { // 当前分机（跟踪）
      handler(val) {
        if (typeof(old) !== 'undefined') {
          var now = -1;
          var start = Number(val)
          var begin = Number(val) + Number(this.paramSetting.ZC_CS_YJJG) + 1
          var end = begin + Number(this.paramSetting.ZC_CS_YJTP) - 1
          var delayTime = Number(this.paramSetting.ZC_CS_GZYS) * 1000
          this.update(this.arrX1, this.arrY1, now, start, begin, end)
          this.initChartBar('top4', '跟踪喷雾', this.arrX1, this.arrY1)
          setTimeout(() => {
            this.arrY1.fill(0)
            this.initChartBar('top4', '跟踪喷雾', this.arrX1, this.arrY1)
          }, delayTime)
          // 移动采煤机的位置
          var scale = Number(val) / this.sprayNumber
          var move = scale * 800
          this.$refs.car.style.left = 140 + move + 'px'
        } else {
          this.initChartBar('top4', '跟踪喷雾', this.arrX1, this.arrY1)
        }
      }
    },
    'paramSignal.ZC_XH_YJ': { // 当前分机（移架）
      handler(val) {
        if (typeof(old) !== 'undefined') {
          var now = Number(val);
          var start = Number(val)
          var begin = Number(val) + Number(this.paramSetting.ZC_CS_YJJG) + 1
          var end = begin + Number(this.paramSetting.ZC_CS_YJTP) - 1
          var delayTime = Number(this.paramSetting.ZC_CS_YJYS) * 1000
          this.update(this.arrX2, this.arrY2, now, start, begin, end)
          this.initChartBar('top5', '移架喷雾', this.arrX2, this.arrY2)
          setTimeout(() => {
            this.arrY2.fill(0)
            this.initChartBar('top5', '移架喷雾', this.arrX2, this.arrY2)
          }, delayTime)
        } else {
          this.initChartBar('top5', '移架喷雾', this.arrX2, this.arrY2)
        }
      }
    },
    'paramSignal.ZC_XH_FM': { // 当前分机（放煤）
      handler(val, old) {
        if (typeof(old) !== 'undefined') {
          var now = -1;
          var start = Number(val) - Number(this.paramSetting.ZC_CS_FMSDTP)
          var begin = Number(val) - Number(this.paramSetting.ZC_CS_FMSDTP)
          var end = Number(val) + Number(this.paramSetting.ZC_CS_FMXDTP)
          var delayTime = Number(this.paramSetting.ZC_CS_FMYS) * 1000
          this.update(this.arrX3, this.arrY3, now, start, begin, end)
          this.initChartBar('top6', '放煤喷雾', this.arrX3, this.arrY3)
          setTimeout(() => {
            this.arrY3.fill(0)
            this.initChartBar('top6', '放煤喷雾', this.arrX3, this.arrY3)
          }, delayTime)
        } else {
          this.initChartBar('top6', '放煤喷雾', this.arrX3, this.arrY3)
        }
      }
    },
    'paramSignal.ZC_XH_FCND1': { // 粉尘浓度1
      handler(val) {
        this.lineData1.push({
          timeMillis: Date.now(),
          signalValue: val
        })
        this.initChart('top1', '粉尘浓度1', this.lineData1, Number(this.paramSetting.ZC_CS_FCNDSX1))
      }
    },
    'paramSignal.ZC_XH_FCND2': { // 粉尘浓度2
      handler(val) {
        this.lineData2.push({
          timeMillis: Date.now(),
          signalValue: val
        })
        this.initChart('top2', '粉尘浓度2', this.lineData2, Number(this.paramSetting.ZC_CS_FCNDSX2))
      }
    },
    'paramSignal.ZC_XH_FCND3': { // 粉尘浓度3
      handler(val) {
        this.lineData3.push({
          timeMillis: Date.now(),
          signalValue: val
        })
        this.initChart('top3', '粉尘浓度3', this.lineData3, Number(this.paramSetting.ZC_CS_FCNDSX3))
      }
    },
    'paramSetting.ZC_CS_FCNDSX1': { // 粉尘浓度1上限
      handler(val) {
        this.initChart('top1', '粉尘浓度1', this.lineData1, Number(val))
      }
    },
    'paramSetting.ZC_CS_FCNDSX2': { // 粉尘浓度2上限
      handler(val) {
        this.initChart('top2', '粉尘浓度2', this.lineData2, Number(val))
      }
    },
    'paramSetting.ZC_CS_FCNDSX3': { // 粉尘浓度3上限
      handler(val) {
        this.initChart('top3', '粉尘浓度3', this.lineData3, Number(val))
      }
    },
    'alarm': {
      handler(val) {
        if (this.status) {
          var message = '';
          for (let key in val) {
            if (key === 'ZC_BJ_FCND1' || key === 'ZC_XH_FCND1') {
              if (val[key] != 0) {
                message += `<p style="padding: 4px">粉尘浓度1报警: ${val[key]}</p>`
              }
              continue;
            }
            if (key === 'ZC_BJ_FCND2' || key === 'ZC_XH_FCND2') {
              if (val[key] != 0) {
                message += `<p style="padding: 4px">粉尘浓度2报警: ${val[key]}</p>`
              }
              continue;
            }
            if (key === 'ZC_BJ_FCND3' || key === 'ZC_XH_FCND3') {
              if (val[key] != 0) {
                message += `<p style="padding: 4px">粉尘浓度3报警: ${val[key]}</p>`
              }
              continue;
            }
          }
          if (message != '') {
            this.$message.error({
              dangerouslyUseHTMLString: true,
              message
            })
            message = ''
          }
        }
      },
      deep: true
    },
  },
  created() {
    this.queryConfigValues()
    this.querySignalLogCurve('ZC_XH_FCND1').then(res => {
      this.lineData1 = res
    })
    this.querySignalLogCurve('ZC_XH_FCND2').then(res => {
      this.lineData2 = res
    })
    this.querySignalLogCurve('ZC_XH_FCND3').then(res => {
      this.lineData3 = res
    })

  },
  methods: {
    /**
      * 封装方法：生成一个从 start 到 end 的连续数组
      * @param start
      * @param end
    */
    generateArray (start, end) {
      return Array.from(new Array(end + 1).keys()).slice(start);
    },
    /**
     * 更新图表
     * @param arrX X轴数组
     * @param arrY Y轴数组
     * @param now 当前分机喷传当前分机号，否则传-1
     * @param start 最左侧范围（跟踪、移架为当前分机号，放煤为当前分机号－上道）
     * @param begin 最左侧喷的分机号(移架为不包括当前分机号的范围)
     * @param end 最右侧范围/最右侧喷的分机号
     * @param delayTime 延时时间
     */
    update(arrX, arrY, now, start, begin, end){
      if(end > this.sprayNumber){
        end = this.sprayNumber
      }
      if(begin < 1){
        begin = 1
      }
      if(start < arrX[0]){
        this.updateX(arrX, end - 19 + 1)
        this.updateY(arrY, arrX.indexOf(now), arrX.indexOf(begin), arrX.indexOf(end))
      } else if(end > arrX[18]){
        this.updateX(arrX, start)
        this.updateY(arrY, arrX.indexOf(now), arrX.indexOf(begin), arrX.indexOf(end))
      } else{
        this.updateY(arrY, arrX.indexOf(now), arrX.indexOf(begin), arrX.indexOf(end))
      }
    },
    updateX(arrX, begin){
      if(begin < 1){
        for(var i = 0; i < 19; i++){
          arrX[i] = i + 1
        }
      } else if(begin > this.sprayNumber - 19 + 1){
        for(var i = 0; i < 19; i++){
          arrX[i] = this.sprayNumber - 19 + 1 + i
        }
      } else{
        for(var i = 0; i < 19; i++){
          arrX[i] = begin + i
        }
      }
    },
    updateY(arrY, nowIndex, beginIndex, endIndex){
      if(nowIndex != -1){
        arrY[nowIndex] = 1
      }
      for (var i = beginIndex; i <= endIndex; i++){
        arrY[i] = 1
      }
    },

    openParam() {
      this.paramVisible = true
    },
    openHistory() {
      this.historyVisible = true
    },
    openDust() {
      this.dustVisible = true
    },
    openAlarm() {
      this.alarmVisible = true;
    },
    // 查询 配置 接口
    async queryConfigValues() {
      try {
        const res = await this.$http.post('/deviceType/queryPByTypeId', {
          deviceId: Number(this.$route.params.deviceId),
          deviceTypeId: Number(this.$route.params.deviceTypeId)
        })
        res.data.data.map(item => {
          if (item.deviceTypeParameterCode == 'ZC_FJ_COUNT') {
            this.sprayNumber = Number(item.value)
          }
          if (item.deviceTypeParameterCode == 'ZC_CGQ_COUNT') {
            this.dustNumber = Number(item.value)
          }
        })
        this.sprayList = this.generateArray(1, this.sprayNumber)
      } catch (error) {
        console.log(error);
      }
    },
    // 折线图 粉尘浓度获取
    async querySignalLogCurve(code) {
      try {
        const res = await this.$http.post('/log/querySignalLogCurve', {
          deviceId: Number(this.$route.params.deviceId),
          deviceTypeId: Number(this.$route.params.deviceTypeId),
          code,
          time: [new Date().getTime() - 2 * 60 * 60 * 1000, new Date().getTime()],
        })
        return res.data.data
      } catch (error) {
        console.log(error);
      }
    },
    // 操作指令
    async operateInstruct(code, val) {
      await deviceWrite('command', code, val, Number(this.$route.params.deviceTypeId), Number(this.$route.params.deviceId))
    },
    initChart(id, name, lineData, maxNumber) {
      let list = [];
      lineData.map((item) => {
        let arr = [];
        arr.push(item.timeMillis)
        arr.push(item.signalValue)
        list.push(arr)
      })

      // if (this.$echarts.getInstanceByDom(document.getElementById(id))) {
      //   this.$echarts.dispose(document.getElementById(id))
      // }
      var chart = this.$echarts.init(document.getElementById(id));

      var option = {
        grid: {
          left: '10%',
          top: '25%',
          right: '8%',
          bottom: '15%'
        },
        title: {
          text: name,
          left: 'center',
          textStyle: {
            color: '#fff'
          }
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
            let Htm = `时间：${UnixTimeToDate}<br>粉尘浓度：${list[index][1]}(mg/cm³) `
            return Htm;
          }
        },
        xAxis: {
          type: 'time',
          min: new Date().getTime() - 2 * 60 * 60 * 1000,
          max: new Date().getTime(),
          axisLabel: {
            textStyle: { color: '#c0c4cc', fontSize: 12 }
          },
          // data: x_list
        },
        yAxis: {
          name: '(mg/cm³)',
          // nameGap: 15,
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
          splitLine: {
            show: false
            // lineStyle: {
            //   color: '#606266',
            //   type: 'dashed'
            // }
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

      chart.setOption(option);
    },
    initChartBar(id, name, arrX, arrY) {
      // if (this.$echarts.getInstanceByDom(document.getElementById(id))) {
      //   this.$echarts.dispose(document.getElementById(id))
      // }
      var chart = this.$echarts.init(document.getElementById(id));
      var option = {
        grid: {
          left: '10%',
          top: '25%',
          right: '5%',
          bottom: '15%'
        },
        title: {
          text: name,
          left: 'center',
          textStyle: {
            color: '#fff'
          }
        },
        xAxis: {
          type: 'category',
          axisLabel: {
            interval: 0,
            textStyle: { color: '#c0c4cc', fontSize: 12 }
          },
          data: arrX
        },
        yAxis: {
          type: 'value',
          nameTextStyle: { color: '#c0c4cc' },
          axisLabel: {
            textStyle: { color: '#c0c4cc', fontSize: 12 },
            formatter: function (value) {
              if (value == 0) {
                return "关";
              } else if (value == 1) {
                return "开"
              } else {
                return
              }
            }
          },
          splitLine: {
            show: false
            // lineStyle: { color: '#606266', type: 'dashed' }
          },
        },
        series: [
          {
            data: arrY,
            type: 'bar'
          }
        ]
      };
      chart.setOption(option);
    },
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped src='./button.css'></style>
<style scoped>
.info-layout {
  width: 100%;
  height: 100%;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url('../../../static/images/layout/zongcai.png');
  background-position: left bottom;
  position: relative;
}
.flex-main {
  width: 1400px;
  display: flex;
  align-content: center;
  /* justify-content: space-between; */
  margin-left: 140px;
}
.chart {
  width: 450px;
  height: 260px;
}
.box-space {
  margin-top: 32px;
}
.car-move {
  position: absolute;
  left: 140px;
  bottom: 70px;
  width: 419px;
  height: 72px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url('../../../static/images/layout/caimeiji1.png');
  /* animation: move 3s linear infinite alternate; */
}
@keyframes move {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(800px);
  }
}


.operate-layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: absolute;
  top: 0;
  right: 0;
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

</style>
