<template>
  <el-aside width="220px" class="app-sidebar">
    <el-menu
      :default-active="$route.path"
      class="sidebar-menu"
      :unique-opened="true"
      text-color="#333"
      active-text-color="#ff6b3b"
      router
    >
      <el-menu-item index="/dashboard" class="menu-item-home">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <div class="menu-spacer"></div>
      
      <el-sub-menu index="2">
        <template #title>
          <el-icon><Wallet /></el-icon>
          <span>资产管理</span>
        </template>
        <el-menu-item index="/asset/fiscal-card">财政资产卡片</el-menu-item>
        <el-menu-item index="/asset/list">资产列表</el-menu-item>
        <el-menu-item index="/asset/record">操作记录</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="3">
        <template #title>
          <el-icon><DocumentChecked /></el-icon>
          <span>资产盘点</span>
        </template>
         <el-menu-item index="/inventory/task">盘点任务</el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="4">
         <template #title>
          <el-icon><Setting /></el-icon>
          <span>资产设置</span>
         </template>
         <el-menu-item index="/setting/department">部门人员设置</el-menu-item>
         <el-menu-item index="/setting/category">分类设置</el-menu-item>
         <el-menu-item index="/setting/params">参数设置</el-menu-item>
         <el-menu-item index="/setting/location">存放地点设置</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
// 引入图标
import { HomeFilled, Wallet, DocumentChecked, Setting } from '@element-plus/icons-vue';
</script>

<style scoped lang="scss">
$primary-orange: #ff6b3b;

.app-sidebar {
  background: #fff;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  
  .sidebar-menu {
    border-right: none;
    padding-top: 20px;
    flex: 1;

    :deep(.el-menu-item), :deep(.el-sub-menu__title) {
      height: 46px; 
      line-height: 46px; 
      font-size: 14px; 
      color: #333; 
      margin-bottom: 2px;
      &:hover { background-color: #F6F8FA; }
      
      // 图标样式
      .el-icon {
        font-size: 18px;
        margin-right: 10px;
        color: #606266;
      }
    }
    
    // 首页样式
    .menu-item-home {
      padding-left: 20px !important; 
      color: #333; 
      font-weight: normal;
      
      &.is-active {
        background-color: #FFF2E8; 
        color: $primary-orange; 
        font-weight: 500;
        border-right: 3px solid $primary-orange; // 统一右边框风格
        .el-icon { color: $primary-orange; } // 选中时图标变色
      }
    }
    
    // 普通菜单选中样式
    :deep(.el-menu-item.is-active):not(.menu-item-home) {
      background-color: #FFF2E8; 
      color: $primary-orange; 
      border-right: 3px solid $primary-orange;
    }

    .menu-spacer { height: 10px; }
    
    // 子菜单缩进
    :deep(.el-sub-menu .el-menu-item) { 
      padding-left: 48px !important; 
      color: #666;
    }
  }
}
</style>