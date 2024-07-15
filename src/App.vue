<template>
  <div id="app">
    <!-- <img src="./assets/logo.png"> -->
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  padding: 0;
  margin: 0;
}
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  /* color: #2c3e50;
  margin-top: 60px; */
  width: 100vw;
  height: 100vh;
  user-select: none;
}
/* //播放按钮 */
video::-webkit-media-controls-play-button {
  display: none;
}
/* //进度条 */
video::-webkit-media-controls-timeline {
  display: none;
}
/* //观看的当前时间 */
video::-webkit-media-controls-current-time-display {
  display: none;
}
/* //剩余时间 */
video::-webkit-media-controls-time-remaining-display {
  display: none;
}
</style>
