<template>
  <div class="medium-box">
    <div class="ray"></div>
    <p class="medium-title">设备数量</p>
    <div class="base-box">
      <p class="base-num tiao">
        <vue-count-up :start-value="0" :end-value="deviceNum"></vue-count-up>
        <span class="num-unit">台</span>
      </p>
      <!-- <p class="base-name">设备数量</p> -->
    </div>
  </div>
</template>

<script>
import VueCountUp from "vue-countupjs";
export default {
  components: {
    VueCountUp
  },
  name: "CountCard",
  props: {
    deviceNum: Number
  },
  data() {
    return {};
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.medium-box {
  width: 360px;
  height: 180px;
  background-image: url("../../../static/images/layout/box1.png");
  background-repeat: no-repeat;
  background-size: contain;
  box-sizing: border-box;
  position: relative;
  /* z-index: 99; */
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
.base-box {
  width: 150px;
  height: 150px;
  margin: 0 auto;
  position: relative;
}
.base-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("../../../static/images/layout/base-bg.png");
  background-repeat: no-repeat;
  background-size: contain;
}
.base-box::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("../../../static/images/layout/base-star.png");
  background-repeat: no-repeat;
  background-size: contain;
  animation: flicker-animation 1.5s linear infinite alternate;
}
@keyframes flicker-animation {
  0% {
    opacity: 0.4;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
    /* transform: translateY(0); */
  }
}
.tiao {
  animation: tiao-animation 1.5s linear infinite alternate;
}
@keyframes tiao-animation {
  0% {
    opacity: 0.4;
    transform: translateY(12px);
  }
  50% {
    opacity: 0.7;
    transform: translateY(6px);
  }
  100% {
    opacity: 1;
    transform: translateY(0px);
  }
}
.base-num {
  position: absolute;
  top: 24px;
  font-family: fantasy;
  font-size: 32px;
  letter-spacing: 2px;
  color: #00ffff;
  width: 100%;
  text-shadow: 2px 2px 0 #76767e, 3px 3px 0 #010377;
}
.num-unit {
  font-size: 24px;
  font-weight: bold;
}
.base-name {
  position: absolute;
  bottom: 0;
  width: 100%;
  font-family: cursive;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 2px;
  background: linear-gradient(
    180deg,
    rgba(230, 236, 255, 1) 50%,
    rgba(50, 100, 237, 1) 100%
  );
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-align: center;
  vertical-align: top;
  text-shadow: 0px 2px 4px rgba(0, 0, 0, 0.25);
}
</style>
