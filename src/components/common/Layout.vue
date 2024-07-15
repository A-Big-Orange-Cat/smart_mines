<template>
  <div class="layout-box">
    <Header></Header>
    <router-view></router-view>
  </div>
</template>

<script>
import Header from "./Header.vue";
export default {
  components: {
    Header
  },
  name: 'Layout',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.layout-box {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-repeat: no-repeat;
  background-image: url("../../../static/images/layout/home-bg2.png");
}
</style>
