<template>
  <el-container class="layout-container">
    <AppHeader />
    <el-container class="main-body">
      <AppSidebar />
      
      <el-main class="app-main-wrapper">
        <router-view />
      </el-main>

    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import AppHeader from './components/AppHeader.vue';
import AppSidebar from './components/AppSidebar.vue';
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-body {
  flex: 1;
  display: flex;
  overflow: hidden; // 防止产生双重滚动条
}

// 关键样式：让内容区自动撑开，填满右侧剩余空间
.app-main-wrapper {
  flex: 1;        // 占据剩余宽度
  padding: 0;     // 清除默认 padding，由子页面自己控制
  overflow: hidden; // 内部滚动
  display: flex;
  flex-direction: column;
  width: 100%;    // 确保宽度铺满
  position: relative;
}
</style>