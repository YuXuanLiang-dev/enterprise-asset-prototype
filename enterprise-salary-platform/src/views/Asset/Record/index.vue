<template>
  <div class="app-content">
    <div class="page-card">
      
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select 
            v-model="searchParams.type" 
            placeholder="请选择操作类型" 
            style="width: 180px" 
            clearable
          >
            <el-option label="入库" value="entry" />
            <el-option label="编辑" value="edit" />
            <el-option label="变更" value="change" />
            <el-option label="领用" value="assign" />
            <el-option label="报修" value="repair" />
            <el-option label="报废" value="scrap" />
          </el-select>

          <el-input 
            v-model="searchParams.keyword" 
            placeholder="请输入资产编码" 
            style="width: 260px; margin-left: 10px"
            :prefix-icon="Search"
            clearable
            @clear="loadData" @keyup.enter="loadData"
          />
        </div>

        <div class="toolbar-right">
          <el-button @click="handleExport">导出</el-button>
        </div>
      </div>

      <div class="table-container">
        <el-table 
          :data="tableData" 
          style="width: 100%; height: 100%;" 
          header-row-class-name="custom-header"
          border
          @sort-change="handleSortChange"
        >
          <el-table-column label="操作类型" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="type-box">{{ row.type }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="assetCode" label="资产编码" min-width="180" show-overflow-tooltip />
          
          <el-table-column 
            prop="actionTime" 
            label="操作时间" 
            width="200" 
            sortable="custom" 
          />
          
          <el-table-column prop="operator" label="操作人" width="120" show-overflow-tooltip />
          
          <el-table-column label="操作" width="80" fixed="right" align="center">
            <template #default>
              <el-button link type="primary" class="view-link">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-bar">
        <div class="total-info">当前第 1 到 25 条，总共 {{ total }} 条</div>
        <el-pagination
          background
          layout="prev, pager, next, slot"
          :total="total"
          :page-size="25"
          @current-change="loadData"
        >
          <template #default>
             <span style="margin-left: 10px">
               <el-select v-model="pageSize" size="small" style="width: 100px">
                 <el-option label="25 条/页" :value="25" />
                 <el-option label="50 条/页" :value="50" />
               </el-select>
             </span>
          </template>
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getRecordList, type RecordItem } from '@/api/record';

// Data
const tableData = ref<RecordItem[]>([]);
const total = ref(0);
const pageSize = ref(25);
const searchParams = reactive({
  type: '',
  keyword: '',
  sort: ''
});

// Methods
const loadData = async () => {
  const res = await getRecordList(searchParams);
  tableData.value = res.list;
  total.value = res.total;
};

const handleSortChange = ({ prop, order }: any) => {
  console.log('排序:', prop, order);
  searchParams.sort = order; // ascending or descending
  // 实际项目中这里会重新调用 loadData
};

const handleExport = () => {
  ElMessage.success('正在导出操作记录...');
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
$bg-gray: #F0F2F5;

// 页面容器布局 (与 AssetList 保持一致)
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
    display: flex;
    justify-content: space-between;
    margin-bottom: 15px;
    flex-shrink: 0;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 10px;
    }
  }

  .table-container {
    flex: 1;
    overflow: hidden;
    
    // 还原截图中的样式细节
    .type-box {
      border: 1px solid #dcdfe6;
      padding: 4px 10px;
      border-radius: 2px;
      font-size: 12px;
      color: #606266;
      display: inline-block;
      line-height: 1.2;
    }

    .view-link {
      font-weight: bold; // “查看”两个字通常稍微加粗一点
    }
  }

  .pagination-bar {
    padding-top: 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #eee;
    flex-shrink: 0;
    .total-info { font-size: 13px; color: #666; }
  }
}

// 表头背景色
:deep(.custom-header th) {
  background-color: #f5f7fa !important;
  color: #333;
  font-weight: 600;
}
</style>
