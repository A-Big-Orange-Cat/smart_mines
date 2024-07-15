<template>
  <div class="card-box">
    <div class="ray"></div>
    <p class="medium-title" @click="handleDevice">设备类型</p>
    <div id="main"></div>
  </div>
</template>

<script>
import { CHART_COLOR } from '../../../static/config/constant'
export default {
  name: "ChartCard",
  props: {
    typeList: Array
  },
  data() {
    return {
      timeTicket: null // 计时器
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
    handleDevice() {
      this.$router.push({
        name: "deviceList",
      });
    },
    // 环图高亮
    handleChartLoop(option, myChart) {
      if (!myChart) {
        return;
      }
      let currentIndex = -1; // 当前高亮图形在饼图数据中的下标
      this.timeTicket = setInterval(selectPie, 1000); // 设置自动切换高亮图形的定时器

      // 取消所有高亮并高亮当前图形
      function highlightPie() {
        // 遍历饼图数据，取消所有图形的高亮效果
        for (var idx in option.series[0].data) {
          myChart.dispatchAction({
            type: "downplay",
            seriesIndex: 0,
            dataIndex: idx,
          });
        }
        // 高亮当前图形
        myChart.dispatchAction({
          type: "highlight",
          seriesIndex: 0,
          dataIndex: currentIndex,
        });
      }

      // 用户鼠标悬浮到某一图形时，停止自动切换并高亮鼠标悬浮的图形
      myChart.on("mouseover", (params) => {
        clearInterval(this.timeTicket);
        currentIndex = params.dataIndex;
        highlightPie();
      });

      // 用户鼠标移出时，重新开始自动切换
      myChart.on("mouseout", (params) => {
        if (this.timeTicket) {
          clearInterval(this.timeTicket);
        }
        this.timeTicket = setInterval(selectPie, 1000);
      });

      // 高亮效果切换到下一个图形
      function selectPie() {
        var dataLen = option.series[0].data.length;
        currentIndex = (currentIndex + 1) % dataLen;
        highlightPie();
      }
    },
    initChart() {
      var list = [];
      this.typeList.map(item => {
        // if (item.deviceCount != 0) {
          var obj = {
            name: item.deviceTypeName,
            value: 1,
            id: item.deviceTypeId
          }
          list.push(obj)
        // }
      })
      // 基于准备好的dom，初始化echarts实例
      var myChart = this.$echarts.init(document.getElementById("main"));
      // 绘制图表
      var option = {
        legend: {
          type: "scroll",
          orient: 'horizontal',
          bottom: "20px",
          right: "center",
          selectedMode: false,
          textStyle: {
            color: "#fff",
          },
          width: 260,
          pageIconColor: '#ff781f', // 设置翻页箭头颜色
          pageTextStyle: {
            color: '#fff' // 设置翻页数字颜色
          },
          pageIconSize: 12, // 设置翻页箭头大小
        },
        tooltip: {
          confine: true,
          trigger: "item",
          formatter: "{a} <br /> {b}", //：{c} 个{d}%
          // backgroundColor: 'rgba(255, 255, 255, 0.8)'
          textStyle: {
            // color: ''
            fontSize: 14,
            align: "left",
          },
        },
        series: [
          {
            name: "智能设备",
            type: "pie",
            radius: ["30%", "60%"],
            center: ["50%", "35%"],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 5, // 设置圆环的圆角弧度
              color: function (params) {
                return CHART_COLOR[params.data.id - 1];
              }
            },
            label: {
              show: false,
            },
            emphasis: {
              label: {
                show: false,
              },
              scale: true, // 设置高亮时放大图形
              scaleSize: 8
            },
            labelLine: {
              show: false,
            },
            data: list
          },
        ],
      };

      myChart.setOption(option);
      this.handleChartLoop(option, myChart);
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.card-box {
  position: relative;
  width: 360px;
  height: 380px;
  box-sizing: border-box;
  background-image: url("../../../static/images/layout/box2.png");
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

#main {
  padding-top: 20px;
  width: 360px;
  height: 320px;
}
</style>
