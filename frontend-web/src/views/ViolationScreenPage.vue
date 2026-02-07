<template>
  <div class="violation-screen-page">
    <ViolationScreen />
  </div>
</template>

<script setup>
import ViolationScreen from '@/components/ViolationScreen.vue'
</script>

<style scoped>
/* 容器不应影响全屏组件 */
.violation-screen-page {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}
</style> 