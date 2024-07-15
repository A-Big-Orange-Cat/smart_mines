<template>
  <div class="card-box">
    <div class="ray"></div>
    <p class="medium-title">设备信息</p>
    <div id="bar"></div>
  </div>
</template>

<script>
import { CHART_COLOR } from '../../../static/config/constant'
export default {
  name: "ChartBar",
  props: {
    typeList: Array
  },
  data() {
    return {

    };
  },
  watch: {
    'typeList': {
      handler(val) {
        this.initChart()
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  methods: {
    initChart() {
      var arrX = [];
      var arrY = [];
      this.typeList.map(item => {
        arrX.push(item.deviceTypeName)
        arrY.push(item.deviceCount)
      })
      var colorArray = [];
      CHART_COLOR.map(item => {
        var obj = {
          top: item,
          bottom: 'rgba(11,42,84,.3)'
        }
        colorArray.push(obj)
      })
      // 基于准备好的dom，初始化echarts实例
      var myChart = this.$echarts.init(document.getElementById("bar"));
      // 绘制图表
      var option = {
        grid: {
          left: '4%',
          top: '5%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        tooltip: {
          show: true,
          confine: true,
          formatter: "{b}: {c} 个"
        },
        xAxis: {
          type: 'category',
          axisLine: {
            lineStyle: {
              color: 'rgba(255,255,255,0.12)'
            }
          },
          axisLabel: {
            interval: 0,
            rotate: 45,
            color: '#e2e9ff',
          },
          data: arrX
        },
        yAxis: {
          type: 'value',
          minInterval: 1,
          splitLine: { //网格线
            show: false
          },
        },
        series: [
          {
            data: arrY,
            type: 'bar',
            itemStyle: {
              normal: {
                show: true,
                color: function (params) {
                  let num = colorArray.length;
                  return {
                    x: 0,
                    y: 1,
                    x2: 0,
                    y2: 0,
                    type: 'linear',
                    colorStops: [{
                      offset: 0,
                      color: colorArray[params.dataIndex % num].bottom
                    }, {
                      offset: 1,
                      color: colorArray[params.dataIndex % num].top
                    }, {
                      offset: 0,
                      color: colorArray[params.dataIndex % num].bottom
                    }, {
                      offset: 1,
                      color: colorArray[params.dataIndex % num].top
                    }, {
                      offset: 0,
                      color: colorArray[params.dataIndex % num].bottom
                    }, {
                      offset: 1,
                      color: colorArray[params.dataIndex % num].top
                    }, {
                      offset: 0,
                      color: colorArray[params.dataIndex % num].bottom
                    }, {
                      offset: 1,
                      color: colorArray[params.dataIndex % num].top
                    }, {
                      offset: 0,
                      color: colorArray[params.dataIndex % num].bottom
                    }, {
                      offset: 1,
                      color: colorArray[params.dataIndex % num].top
                    }, {
                      offset: 0,
                      color: colorArray[params.dataIndex % num].bottom
                    }, {
                      offset: 1,
                      color: colorArray[params.dataIndex % num].top
                    }],
                    //globalCoord: false
                  }
                },
                barBorderRadius: 70,
                borderWidth: 0,
                borderColor: '#333',
              }
            },
          }
        ]
      };

      myChart.setOption(option);

    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.card-box {
  position: relative;
  width: 360px;
  height: 320px;
  box-sizing: border-box;
  background-image: url("../../../static/images/layout/box5.png");
  background-repeat: no-repeat;
  background-size: contain;
  overflow: hidden;
}
.ray {
  position: absolute;
  top: 36px;
  left: calc(180px - 100px);
  width: 200px;
  height: 36px;
  background-image: url("../../../static/images/layout/ray.png");
  background-repeat: no-repeat;
  background-size: contain;
  animation: ray-animation 1.5s linear infinite alternate;
}
@keyframes ray-animation {
  0% {
    opacity: 0.4;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}

.medium-title {
  font-size: 14px;
  color: #fff;
  font-weight: bold;
  text-align: left;
  padding-top: 8px;
  box-sizing: border-box;
  text-align: center;
}
.medium-title:hover {
  color: #66ffff;
  cursor: pointer;
}

#bar {
  padding-top: 20px;
  width: 360px;
  height: 300px;
}
</style>
