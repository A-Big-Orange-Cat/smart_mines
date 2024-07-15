<template>
  <div class="bg-list">
    <!-- 风门 -->
    <div id="a1"></div>
    <div id="a2"></div>
    <div id="a3"></div>
    <div id="a4"></div>
    <div id="a5"></div>
    <div id="a6"></div>
    <div id="a7"></div>
    <div id="b1"></div>
    <div id="b2"></div>
    <div id="b3"></div>
    <div id="b4"></div>
    <div id="b5"></div>
    <div id="b6"></div>
    <div id="b7"></div>
    <div id="ab"></div>
    <!-- 防火门 -->
    <div id="firedoor"></div>
    <div id="fire1"></div>
    <div id="fire2"></div>
    <div id="fire3"></div>
    <div id="fire4"></div>
    <div id="fire5"></div>
    <div id="fire6"></div>
    <div id="fire7"></div>
  </div>
</template>

<script>
export default {
  name: "LoadBg",
  data() {
    return {};
  },
  mounted() {
    this.$nextTick(() => {
      this.preloadImage()
    })
  },
  methods: {
    // 预加载图片
    preloadImage() {
      return new Promise((resolve, reject) => {
        for (var i = 1; i < 8; i++) {
          var a_src = document.getElementById('a' + i)
          var b_src = document.getElementById('b' + i)
          a_src.style.backgroundImage = 'url(' + require(`../../../static/images/layout/a-open${i}.png`) + ')'
          b_src.style.backgroundImage = 'url(' + require(`../../../static/images/layout/b-open${i}.png`) + ')'
          // 防火门
          var fire_src = document.getElementById('fire' + i)
          fire_src.style.backgroundImage = 'url(' + require(`../../../static/images/layout/firedoor${i}.png`) + ')'
        }
        var ab_src = document.getElementById('ab')
        ab_src.style.backgroundImage = 'url(' + require(`../../../static/images/layout/door.png`) + ')'

        var firedoor_src = document.getElementById('firedoor')
        firedoor_src.style.backgroundImage = 'url(' + require(`../../../static/images/layout/firedoor.jpg`) + ')'
      });
    },
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.bg-list {
  width: 0;
  height: 0;
  position: absolute;
}
.bg-list > div {
  width: 0;
  height: 0;
}
</style>
