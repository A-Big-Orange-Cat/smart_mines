<template>
  <div>
    <el-card class="card" shadow="hover">
      <el-descriptions class="list" title="系统管理" direction="vertical" :column="2" border>
        <template slot="extra">
          <el-button type="primary" size="small" v-if="!isEdit" @click="handleEdit">编辑</el-button>
          <el-button type="primary" size="small" v-else @click="handleConfirm">完成</el-button>
        </template>
        <el-descriptions-item label="主界面大标题" :span="1">
          <span v-if="!isEdit">{{ title }}</span>
          <el-input v-else v-model="title" style="width: 300px;"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="界面大图" :span="1">
          <el-image
            v-if="!isEdit"
            style="width: 100px; height: 100px"
            v-loading="loading"
            fit="contain"
            :src="imageUrl"
            :preview-src-list="srcList">
          </el-image>
          <el-upload
            v-else
            class="uploader"
            :action="uploadUrl"
            :headers="{
              'satoken': token
            }"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload">
            <img v-if="imageUrl" :src="imageUrl" class="image">
            <i v-else class="el-icon-plus uploader-icon"></i>
          </el-upload>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

  </div>
</template>

<script>
export default {
  name: 'SystemManage',
  data () {
    return {
      isEdit: false,
      uploadUrl: `${this.$http.defaults.baseURL}/system/updateMapPath`,
      srcList: [],
      title: '',
      imageUrl: '',
      loading: true,
      token: localStorage.getItem('token')
    }
  },
  created() {
    this.getTitleImage()
  },
  methods: {
    handleEdit() {
      this.isEdit = true
    },
    handleConfirm() {
      this.isEdit = false
      this.updateTitle()
    },
    handleUploadSuccess(res, file) {
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      // const isLt2M = file.size / 1024 / 1024 < 2;

      // if (!isJPG) {
      //   this.$message.error('上传图片只能是 JPG 格式!');
      // }
      // if (!isLt2M) {
      //   this.$message.error('上传图片大小不能超过 2MB!');
      // }
      // return isJPG && isLt2M;
    },
    // 查询主界面标题与地图路径接口
    async getTitleImage() {
      try {
        const res = await this.$http.get('/system/query')
        this.title = res.data.data.title;
        this.imageUrl = res.data.data.mapPath;
        this.loading = false;
        this.srcList.push(this.imageUrl)
        this.preloadImage(this.imageUrl)
        localStorage.setItem('system', JSON.stringify(res.data.data))
      } catch (error) {
        console.log(error);
      }
    },
    // 更新主界面标题接口
    async updateTitle() {
      try {
        await this.$http.post('/system/updateTitle', {
          title: this.title
        })
        this.getTitleImage()
      } catch (error) {
        console.log(error);
      }
    },
    // 预加载图片
    preloadImage(url) {
      return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = () => reject(new Error('图片加载失败'));
        img.src = url;
      });
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.card {
  margin: 24px;
}
.list >>> .el-descriptions-item__cell {
  width: 50%;
}
.uploader >>> .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.uploader >>> .el-upload:hover {
  border-color: #409EFF;
}
.uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.image {
  width: 120px;
  height: 120px;
  display: block;
}
</style>
