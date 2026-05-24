<template>
  <article class="metric-card">
    <p class="label">{{ label }}</p>
    <strong class="value">{{ value }}</strong>
    <p v-if="hint" class="hint">{{ hint }}</p>
  </article>
</template>

<script setup>
defineProps({
  label: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  hint: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.metric-card {
  padding: 16px;
  border: 1px solid var(--am-line-soft);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(18, 30, 54, 0.62), rgba(13, 24, 45, 0.88));
}

.label,
.hint {
  margin: 0;
  color: var(--am-muted);
}

.label {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.value {
  display: block;
  margin-top: 8px;
  color: var(--am-text);
  font-size: 24px;
}

.hint {
  margin-top: 6px;
  font-size: 12px;
}
</style>

