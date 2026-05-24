<template>
  <div class="empty-wrap">
    <el-empty :description="description" :image-size="imageSize" />
    <slot />
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    default: '暂无数据'
  },
  imageSize: {
    type: Number,
    default: 88
  }
})
</script>

<style scoped>
.empty-wrap {
  padding: 10px 0;
}
</style>

