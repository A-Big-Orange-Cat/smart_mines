<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :class="{ menu: !isCollapse }"
    background-color="#001529"
    text-color="#f4f4f5"
  >
    <div :class="['menu-title', { 'title-fold': isCollapse }]">
      <img src="../../../../static/images/layout/logo-icon.png" alt="" style="pointer-events: none;" />
      <span :style="{ display: isCollapse ? 'none' : 'inline-block' }">
        后台管理系统
      </span>
    </div>
    <el-menu-item
      class="menu-item"
      v-for="(item, index) in routeList"
      :key="index"
      :index="item.meta.title"
      @click="handerMenuItem(item)"
    >
      <i :class="item.meta.icon"></i>
      <span slot="title">{{ item.meta.title }}</span>
    </el-menu-item>
    <div class="footer-collapse">
      <i class="el-icon-s-fold" v-if="!isCollapse" @click="() => {isCollapse = true}"></i>
      <i class="el-icon-s-unfold" v-else @click="() => {isCollapse = false}"></i>
    </div>
  </el-menu>
</template>

<script>
export default {
  name: "NavMenu",
  data() {
    return {
      isCollapse: false,
    };
  },
  computed: {
    routeList() {
      const routes = this.$router.options.routes;
      let list = [];
      routes.forEach(item => {
        if (item.children && item.name == 'admin') {
          item.children.forEach(child => {
            if (!child.hidden) {
              list.push(child)
            }
          })
        }
      });
      return list;
    },
    activeMenu() {
      const route = this.$route;
      return route.meta.title;
    },
  },
  methods: {
    handerMenuItem(route) {
      this.$router.push({
        name: route.name
      })
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.menu {
  width: 210px; /*no*/
}

.el-menu {
  border-right: none;
  text-align: left;
}

.menu-item {
  font-size: 16px; /*no*/
}

.menu-title {
  height: 80px; /*no*/
  padding: 24px 0px 24px 20px; /*no*/
  box-sizing: border-box;
  display: flex;
  align-items: center;
}

.menu-title > img {
  width: 45px; /*no*/
  height: auto;
}

.menu-title > span {
  font-size: 20px; /*no*/
  color: #fff;
  font-weight: bold;
  margin-left: 8px; /*no*/
}

.title-fold {
  padding: 24px 9px; /*no*/
  text-align: center;
}

.footer-collapse {
  padding: 80px 20px; /*no*/
}

.footer-collapse > i {
  color: #c0c4cc;
  width: 24px; /*no*/
  font-size: 20px; /*no*/
  text-align: center;
}
</style>
