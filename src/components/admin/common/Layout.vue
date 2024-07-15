<template>
  <el-container class="common-layout">
    <el-aside width="auto">
      <nav-menu> </nav-menu>
    </el-aside>
    <el-container>
      <el-header height="60px">
        <nav-header></nav-header>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import NavMenu from './NavMenu.vue'
import NavHeader from './NavHeader.vue'
export default {
  name: 'Layout',
  components: {
    NavMenu,
    NavHeader
  },
  data() {
    return {
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.common-layout {
  width: 100%;
  height: 100%;
}
.el-aside {
  background-color: #001529;
  color: #fff;
}
.el-header {
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
}
</style>
