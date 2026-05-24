<template>
  <header class="page-header">
    <div class="title-wrap">
      <p v-if="kicker" class="kicker">{{ kicker }}</p>
      <h2>{{ title }}</h2>
      <p v-if="description" class="desc">{{ description }}</p>
    </div>
    <div v-if="$slots.actions" class="actions">
      <slot name="actions" />
    </div>
  </header>
</template>

<script setup>
defineProps({
  kicker: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.title-wrap h2 {
  margin: 0;
  font-size: 28px;
  line-height: 1.2;
}

.kicker {
  margin: 0 0 6px;
  color: var(--am-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.desc {
  margin: 8px 0 0;
  color: var(--am-muted);
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
  }
}
</style>

