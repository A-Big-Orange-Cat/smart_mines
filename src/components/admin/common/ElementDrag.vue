<template>
  <div
    class="drag-outer"
    ref="dragWrap"
    :style="outerOptions"
    @mouseenter="isHover = true"
    @mouseleave="isHover = isMousedown = false"
    @mousemove="dragMousemove"
  >
    <div
      class="drag-inner"
      ref="dragElement"
      :style="innerOptions"
      @mousedown="dragMousedown"
      @mouseup.stop="dragMouseup"
    >
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "ElementDrag",
  props: {
    outerOptions: {
      type: Object,
      default() {
        return {
          background: "rgba(0,0,0,0)",
        };
      },
    },
    innerOptions: {
      type: Object,
      default() {
        return {
          background: "rgba(0,0,0,0)",
        };
      },
    },
    scaleZoom: {
      type: Object,
      default() {
        return {
          max: 5,
          min: 0.2,
        };
      },
    },
  },
  data() {
    return {
      isMousedown: false,
      isHover: false,
      moveStart: {},
      translate: { x: 0, y: 0 },
      scale: 1,
      // 为了拖拽时阻止点击事件（根据鼠标按下松开的位置）
      beforeX: null,
      beforeY: null,
      afterX: null,
      afterY: null
    };
  },
  methods: {
    handleScroll(e) {
      if (this.isHover && this.$refs.dragElement) {
        let speed = e.wheelDelta / 120;
        if (e.wheelDelta > 0 && this.scale < this.scaleZoom.max) {
          this.scale += 0.04 * speed;
          this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
        } else if (e.wheelDelta < 0 && this.scale > this.scaleZoom.min) {
          this.scale += 0.04 * speed;
          this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
        }
      }
    },
    dragMousedown(event) {
      this.moveStart.x = event.clientX;
      this.moveStart.y = event.clientY;
      this.isMousedown = true;
      this.beforeX = event.clientX
      this.beforeY = event.clientX
    },
    dragMouseup(event) {
      this.isMousedown = false;
      this.afterX = event.clientX
      this.afterY = event.clientX
    },
    dragMousemove() {
      if (this.isMousedown) {
        this.translate.x += (event.clientX - this.moveStart.x) / this.scale;
        this.translate.y += (event.clientY - this.moveStart.y) / this.scale;
        this.$refs.dragElement.style.transform = `scale(${this.scale}) translate(${this.translate.x}px, ${this.translate.y}px)`;
        this.moveStart.x = event.clientX;
        this.moveStart.y = event.clientY;
      }
    },
  },
  mounted() {
    window.addEventListener("mousewheel", this.handleScroll, false);
  },
};
</script>

<style scoped>
.drag-outer {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}
.drag-inner {
  transform-origin: center center;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  user-select: none;
  -webkit-user-drag: none;
  /* user-drag: none; */
}
.drag-inner > * {
  -webkit-user-drag: none;
}
</style>
