<template>
  <section class="data-toolbar">
    <div class="left">
      <slot name="left" />
    </div>
    <div class="right">
      <slot name="right" />
    </div>
  </section>
</template>

<style scoped>
.data-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.left,
.right {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .data-toolbar,
  .left,
  .right {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>

