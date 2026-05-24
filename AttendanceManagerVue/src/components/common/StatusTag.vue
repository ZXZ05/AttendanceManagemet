<template>
  <el-tag :type="tagType" effect="light" round>{{ text }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: ''
  },
  status: {
    type: [String, Number, Boolean],
    default: ''
  },
  map: {
    type: Object,
    default: () => ({})
  },
  fallbackType: {
    type: String,
    default: 'info'
  }
})

const tagType = computed(() => {
  const mapped = props.map[props.status]
  if (!mapped) {
    return props.fallbackType
  }
  return mapped.type || props.fallbackType
})
</script>

