<template>
  <el-dialog
    class="modal"
    title="设备位置"
    :visible="visible"
    fullscreen
    append-to-body
    :close-on-click-modal="false"
    @update:visible="updateVisible"
    @opened="openDialog"
  >
    <div class="drag-box" @mousewheel="wheelMarker">
      <element-drag ref="drag" >
        <div class="drag-main">
          <img :src="imageUrl" alt="" @click="clickImg" />
          <i class="el-icon-location" ref="marker" style="display: none"></i>
        </div>
      </element-drag>
    </div>

    <span slot="footer">
      <el-button @click="updateVisible(false)">取 消</el-button>
      <el-button type="primary" @click="submitConfirm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import ElementDrag from '../common/ElementDrag.vue'
export default {
  name: "ImageModal",
  components: {
    ElementDrag
  },
  props: {
    visible: Boolean,
    deviceInfo: Object,
    imageUrl: String
  },
  data() {
    return {
      location: null,
      scale: 1
    };
  },
  // mounted() {
  //   this.$refs.drag.addEventListener("mousewheel", this.wheelMarker, false);
  // },
  methods: {
    updateVisible(newVisible) {
      this.$emit("update:visible", newVisible);
    },
    submitConfirm() {
      this.updateLocation()
      this.updateVisible(false)
    },
    clickImg(event) {
      if (this.$refs.drag.beforeX === this.$refs.drag.afterX &&
        this.$refs.drag.beforeY === this.$refs.drag.afterY) {
        let marker = this.$refs.marker;
        this.location = event.offsetX + ',' + event.offsetY;
        marker.style.display = "inline-block";
        marker.style.fontSize = "40px";
        marker.style.color = "red";
        marker.style.position = "absolute";
        marker.style.top = (event.offsetY - [(20 * (this.scale + 0.44)) - (18 * (this.scale-0.04-1))] ) + "px";
        marker.style.left = (event.offsetX - 20) + "px";
      }
    },
    // 获取到标注点 对标注点设置样式 相对于图片定位到相应位置
    openDialog() {
      let marker = this.$refs.marker;
      if (this.deviceInfo.deviceLocation && this.deviceInfo.deviceLocation !== ''
        && this.deviceInfo.deviceLocation !== null) {
        let location = this.deviceInfo.deviceLocation.split(",");
        marker.style.display = "inline-block";
        marker.style.fontSize = "40px";
        marker.style.color = "red";
        marker.style.position = "absolute";
        marker.style.top = (location[1] - [(20 * (this.scale + 0.44)) - (18 * (this.scale-0.04-1))] ) + "px";
        marker.style.left = (location[0] - 20) + "px";
      }
    },
    wheelMarker(e) {
      let marker = this.$refs.marker;
      let number = 18;
      if (marker) {
        let speed = e.wheelDelta / 120;
        if (this.scale >= 0.2 && this.scale <= 5) {
          this.scale += 0.04 * speed;
          if (this.scale > 5) this.scale = 5;
          if (this.scale < 0.2) this.scale = 0.2;
          this.scale = parseFloat(this.scale.toFixed(2));
          marker.style.transform = `scale(${1 / this.scale}) translate(0, ${number * (this.scale - 0.04 - 1)}px)`;
        }
      }
    },
    // 更新设备位置接口
    async updateLocation() {
      try {
        await this.$http.post('/device/updateLocation', {
          deviceId: this.deviceInfo.deviceId,
          deviceLocation: this.location
        })
        this.$message.success('更新设备位置成功')
        this.$parent.queryByNameOrTypeId();
      } catch (error) {
        console.log(error);
      }
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.modal {
  text-align: left;
}
.drag-box {
  width: 100%;
  height: calc(100vh - 184px); /* no */
}
.drag-main {
  position: relative;
}
.drag-main > * {
  -webkit-user-drag: none;
}
</style>
