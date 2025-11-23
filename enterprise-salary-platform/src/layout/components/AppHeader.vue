<template>
  <el-header class="app-header">
    <div class="header-left">
      <div class="brand-area">
        <div class="logo-icon-box">
           <svg viewBox="0 0 1024 1024" width="24" height="24"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6-372-372 372z" fill="#ffffff"/><path d="M512 140c-205.4 0-372 166.6-372 372s166.6 372 372 372 372-166.6 372-372-166.6-372-372-372z" fill-opacity=".2" fill="#ffffff"/></svg>
        </div>
        <span class="brand-text">广软信息系统原型设计平台</span>
      </div>
      <div class="divider"></div>
      <el-dropdown trigger="click" class="campus-dropdown" @command="handleEnterpriseSelect">
        <span class="el-dropdown-link">
          {{ currentEnterpriseName }} <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="item in enterprises"
              :key="item.id"
              :command="item.id"
              :disabled="item.id === currentEnterpriseId"
            >
              {{ item.name }}
              <span v-if="item.id === currentEnterpriseId">（当前）</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div class="header-tabs">
        <div class="nav-item active">资产管理</div>
    </div>

    <div class="header-right">
      <el-dropdown trigger="click" class="user-dropdown" @command="handleUserCommand">
        <div class="user-info">
          <el-avatar :size="28" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
          <span class="username">{{ username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue';
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useSessionStore } from '@/stores/session';

const sessionStore = useSessionStore();
const { enterprises, currentEnterprise, currentEnterpriseId, user } = storeToRefs(sessionStore);
const router = useRouter();

const currentEnterpriseName = computed(() =>
  currentEnterprise.value?.name || '请选择企业'
);

const username = computed(() => user.value?.name || '未登录');

const handleEnterpriseSelect = (enterpriseId: number) => {
  if (!enterpriseId || enterpriseId === currentEnterpriseId.value) return;
  sessionStore.switchEnterprise(enterpriseId);
  ElMessage.success('已切换企业');
  // 重新请求页面数据
  window.location.reload();
};

const handleUserCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/profile');
    return;
  }
  if (command === 'logout') {
    sessionStore.logout();
    router.push('/login');
  }
};
</script>

<style scoped lang="scss">
$header-h: 56px;
$header-bg: #2B73D8;

.app-header {
  height: $header-h;
  background-color: $header-bg;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  flex-shrink: 0;

  .header-left {
    display: flex; align-items: center; height: 100%;
    .brand-area { display: flex; align-items: center; gap: 10px; font-size: 18px; font-weight: bold; .logo-icon-box { display: flex; align-items: center; opacity: 0.9; } }
    .divider { width: 1px; height: 16px; background: rgba(255, 255, 255, 0.3); margin: 0 18px; }
    .campus-dropdown { cursor: pointer; color: #fff; .el-dropdown-link { display: flex; align-items: center; font-size: 14px; gap: 4px; opacity: 0.9; &:hover { opacity: 1; } } }
  }
  .header-tabs { flex: 1; display: flex; height: 100%; margin-left: 30px; .nav-item { height: 100%; padding: 0 24px; display: flex; align-items: center; cursor: pointer; font-size: 14px; color: rgba(255,255,255,0.85); transition: all 0.3s; &:hover { color: #fff; background: rgba(255,255,255,0.1); } &.active { color: #fff; background: rgba(255,255,255,0.15); font-weight: 500; } } }
  .header-right { .user-dropdown { cursor: pointer; color: #fff; .user-info { display: flex; align-items: center; gap: 8px; .username { font-size: 14px; opacity: 0.9; } } } }
}
</style>
