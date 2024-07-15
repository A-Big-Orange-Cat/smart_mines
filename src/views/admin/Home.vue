<template>
  <el-card class="box-card">
    <el-carousel class="carousel">
      <el-carousel-item v-for="item in carouselList" :key="item">
        <el-image style="width: 100%; height: 100%; pointer-events: none;" :src="item" fit="cover"></el-image>
      </el-carousel-item>
    </el-carousel>
  </el-card>
</template>

<script>
export default {
  name: 'Home',
  data () {
    return {
      carouselList: [
        require('../../../static/images/banner/banner3.jpg'),
        require('../../../static/images/banner/banner1.jpg'),
        require('../../../static/images/banner/banner2.jpg'),
        require('../../../static/images/banner/banner4.jpg'),
        require('../../../static/images/banner/banner5.jpg'),
        require('../../../static/images/banner/banner0.jpg'),
      ]
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.carousel {
  width: 100%;
  height: 700px;
}
.carousel >>> .el-carousel__container {
  height: 700px;
}
</style>
