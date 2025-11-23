<template>
  <div class="app-content">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input 
            v-model="keyword" 
            placeholder="请输入参数名称" 
            style="width: 300px" 
            :suffix-icon="Search"
            @keyup.enter="loadData" @clear="loadData"
            clearable
          />
        </div>
        <div class="toolbar-right">
          <el-button class="btn-orange" @click="showDialog = true">新增</el-button>
        </div>
      </div>

      <div class="table-container" v-if="tableData.length > 0">
        <el-table :data="tableData" style="width: 100%; height: 100%;" border header-row-class-name="custom-header">
          <el-table-column prop="name" label="参数名称" />
          <el-table-column prop="typeText" label="类型" />
          <el-table-column label="操作" width="150" align="center">
            <template #default>
              <el-button link type="primary">编辑</el-button>
              <el-button link type="info" style="color: #999">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="empty-container" v-else>
        <el-empty description="暂无参数" :image-size="120"></el-empty>
      </div>
    </div>

    <el-dialog v-model="showDialog" title="新增" width="500px">
      <el-form :model="form" label-width="100px" style="padding-right: 20px; padding-top: 20px;">
        <el-form-item label="参数名称" required>
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option label="文本" value="text" />
            <el-option label="选项项" value="select" />
            <el-option label="日期" value="date" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer center-footer">
          <el-button class="btn-orange" @click="showDialog = false">确定</el-button>
          <el-button @click="showDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { getParamList, type ParamItem } from '@/api/setting';

const tableData = ref<ParamItem[]>([]);
const keyword = ref('');
const showDialog = ref(false);
const form = reactive({ name: '', type: '' });

const loadData = async () => {
  // 传入 keyword 才会返回模拟数据，否则返回空数组以展示空状态
  tableData.value = await getParamList(keyword.value);
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
$primary-orange: #ff6b3b;
$bg-gray: #F0F2F5;

// 复用通用样式
.app-content {
  background-color: $bg-gray;
  padding: 15px;
  height: 100%;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .page-card {
    background: #fff;
    border-radius: 4px;
    padding: 15px;
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }

  .toolbar {
    display: flex; justify-content: space-between; margin-bottom: 15px; flex-shrink: 0;
    .toolbar-left { display: flex; gap: 10px; }
  }

  .table-container { flex: 1; overflow: hidden; }
  
  // 空状态居中
  .empty-container {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
:deep(.custom-header th) { background-color: #f5f7fa !important; color: #333; font-weight: 600; }
.dialog-footer.center-footer { display: flex; justify-content: center; gap: 10px; }
</style>