<template>
  <div class="app-content">
    <div class="stats-panel">
      <div class="stat-group left-group">
        <div class="stat-item">
          <div class="label">待盘</div>
          <div class="value">{{ stats.pending }}</div>
        </div>
        <div class="stat-item">
          <div class="label">盘实</div>
          <div class="value">{{ stats.verified }}</div>
        </div>
        <div class="stat-item">
          <div class="label">盘盈</div>
          <div class="value">{{ stats.surplus }}</div>
        </div>
        <div class="stat-item">
          <div class="label">盘亏</div>
          <div class="value">{{ stats.loss }}</div>
        </div>
      </div>

      <div class="stat-divider"></div>

      <div class="stat-group right-group">
        <div class="stat-item">
          <div class="label">待贴标</div>
          <div class="value">{{ stats.pendingTag }}</div>
        </div>
        <div class="stat-item">
          <div class="label">已贴标</div>
          <div class="value">{{ stats.tagged }}</div>
        </div>
        <div class="stat-item">
          <div class="label">无需贴标 <el-icon class="help-icon"><QuestionFilled /></el-icon></div>
          <div class="value">{{ stats.noTag }}</div>
        </div>
      </div>
    </div>

    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="searchParams.status" placeholder="盘点状态" style="width: 110px" clearable @change="handleSearch">
            <el-option label="盘实" value="verified"/>
            <el-option label="盘亏" value="loss"/>
            <el-option label="盘盈" value="surplus"/>
          </el-select>
          <el-select v-model="searchParams.tagStatus" placeholder="贴标状态" style="width: 110px" class="ml-10" clearable @change="handleSearch">
            <el-option label="待贴标" value="pending_tag"/>
            <el-option label="已贴标" value="tagged"/>
            <el-option label="无需贴标" value="no_tag"/>
          </el-select>
          <el-input 
            v-model="searchParams.keyword" 
            placeholder="资产名称/编码/规格/品牌" 
            style="width: 220px" 
            class="ml-10" 
            :suffix-icon="Search"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" class="ml-10" @click="handleSearch">查询</el-button>
          <el-button class="ml-10" @click="handleReset">重置</el-button>
          
          <el-button type="primary" link class="ml-10" title="高级统计"><el-icon size="16"><DataAnalysis /></el-icon></el-button>
          
          <el-button class="btn-orange ml-10" @click="router.push('/inventory/match')">编码匹配</el-button>
          
          <el-dropdown class="ml-10">
            <el-button>状态设置 <el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>设为盘盈</el-dropdown-item>
                <el-dropdown-item>设为盘亏</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <el-button class="ml-10">盘亏资产获取</el-button>
          
          <el-dropdown class="ml-10">
            <el-button>导出 <el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showExportDialog = true">导出核实数据</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="table-container">
        <el-table 
          :data="tableData" 
          style="width: 100%; height: 100%;" 
          border 
          header-row-class-name="custom-header"
        >
          <el-table-column type="selection" width="45" align="center" fixed="left" />
          <el-table-column prop="status" label="状态" width="80" align="center" fixed="left">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="plain" size="small">{{ row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="照片" width="70" align="center">
            <template #default>
              <div class="img-placeholder"><el-icon><Picture /></el-icon></div>
            </template>
          </el-table-column>
          <el-table-column prop="codeFiscal" label="资产编码(财政)" width="160" show-overflow-tooltip sortable />
          <el-table-column prop="nameFiscal" label="资产名称" min-width="140" show-overflow-tooltip />
          <el-table-column prop="category" label="资产分类" width="140" show-overflow-tooltip />
          <el-table-column prop="spec" label="规格型号" width="120" show-overflow-tooltip />
          <el-table-column prop="brand" label="品牌" width="100" show-overflow-tooltip />
          
          <el-table-column prop="originalValue" label="原值(元)" width="110" align="right" sortable>
            <template #default="{ row }">{{ row.originalValue.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="netValue" label="净值(元)" width="110" align="right">
            <template #default="{ row }">{{ row.netValue.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="acquisitionDate" label="取得日期" width="110" align="center" sortable />
          
          <el-table-column prop="location" label="存放地点" width="120" show-overflow-tooltip />
          
          <el-table-column label="操作" width="110" fixed="right" align="center">
            <template #default>
              <el-button link type="primary" @click="showMatchDialog = true">匹配资产</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="pagination-bar">
        <div class="total-info">当前第 {{ startIndex }} 到 {{ endIndex }} 条，总共 {{ total }} 条</div>
        <el-pagination 
          background 
          layout="prev, pager, next, slot" 
          :total="total" 
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        >
           <template #default>
             <span style="margin-left: 10px">
               <el-select v-model="pageSize" size="small" style="width: 120px" @change="handlePageSizeChange">
                 <el-option label="25 条/页" :value="25" />
                 <el-option label="50 条/页" :value="50" />
               </el-select>
             </span>
           </template>
        </el-pagination>
      </div>
    </div>

    <el-dialog v-model="showMatchDialog" title="匹配财政资产" width="600px">
       <el-alert title="支持匹配未被匹配的财政资产，包含盘亏。" type="info" show-icon :closable="false" class="mb-20" />
       <el-form label-width="100px">
         <el-form-item label="匹配资产" required><el-select placeholder="请输入资产名称/编码" style="width: 100%" /></el-form-item>
         </el-form>
       <template #footer><div class="dialog-footer center-footer"><el-button class="btn-orange" @click="showMatchDialog = false">确定</el-button><el-button @click="showMatchDialog = false">取消</el-button></div></template>
    </el-dialog>

    <el-dialog v-model="showExportDialog" title="导出" width="500px">
       <div class="warning-alert"><el-icon class="icon"><WarningFilled /></el-icon>带图导出需要一定时间...</div>
       <el-form label-width="140px">
         <el-form-item label="是否带资产照片导出：">
           <el-radio-group v-model="exportType"><el-radio label="with">带资产照片</el-radio><el-radio label="without">不带资产照片</el-radio></el-radio-group>
         </el-form-item>
       </el-form>
       <template #footer><div class="dialog-footer center-footer"><el-button class="btn-orange" @click="showExportDialog = false">确定</el-button><el-button @click="showExportDialog = false">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Search, ArrowDown, Picture, WarningFilled, QuestionFilled, DataAnalysis } from '@element-plus/icons-vue';
import { getInventoryData, type InventoryStats, type InventoryItem } from '@/api/inventory';

const router = useRouter();
const stats = ref<InventoryStats>({ pending: 0, verified: 0, surplus: 0, loss: 0, pendingTag: 0, tagged: 0, noTag: 0 });
const tableData = ref<InventoryItem[]>([]);
const total = ref(0);
const pageSize = ref(25);
const currentPage = ref(1);

const showMatchDialog = ref(false);
const showExportDialog = ref(false);
const exportType = ref('with');
const searchParams = reactive({ status: '', tagStatus: '', keyword: '' });

const loadData = async (page = currentPage.value) => {
  currentPage.value = page;
  const res = await getInventoryData({
    status: searchParams.status || undefined,
    tagStatus: searchParams.tagStatus || undefined,
    keyword: searchParams.keyword || undefined,
    page: currentPage.value,
    size: pageSize.value
  });
  stats.value = res.stats;
  tableData.value = res.list;
  total.value = res.total;
};

const getStatusType = (status: string) => {
  if (status === 'verified') return 'primary';
  if (status === 'surplus') return 'success';
  if (status === 'loss') return 'danger';
  return 'info';
};

const handleSearch = () => {
  loadData(1);
};

const handleReset = () => {
  searchParams.status = '';
  searchParams.tagStatus = '';
  searchParams.keyword = '';
  loadData(1);
};

const handlePageChange = (page: number) => {
  loadData(page);
};

const handlePageSizeChange = (size: number) => {
  pageSize.value = size;
  loadData(1);
};

const startIndex = computed(() => (total.value === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1));
const endIndex = computed(() => Math.min(total.value, currentPage.value * pageSize.value));

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
$bg-gray: #F0F2F5;
$primary-orange: #ff6b3b;

.app-content {
  background-color: $bg-gray;
  padding: 15px;
  height: 100%;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

// --- 看板样式修复 ---
.stats-panel {
  background: #fff;
  border-radius: 4px;
  padding: 20px 10px; // 增加垂直 padding
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  
  // 左侧组 4 个
  .left-group {
    flex: 4; // 占比 4份
    display: flex;
    justify-content: space-around;
  }
  
  // 右侧组 3 个
  .right-group {
    flex: 3; // 占比 3份
    display: flex;
    justify-content: space-around;
  }

  .stat-item {
    text-align: center;
    .label { 
      font-size: 13px; color: #666; margin-bottom: 8px; 
      display: flex; align-items: center; justify-content: center; gap: 4px; 
      .help-icon { color: #999; cursor: pointer; }
    }
    .value { font-size: 26px; font-weight: bold; color: #333; font-family: 'DIN Alternate', sans-serif; } // 优化数字字体
  }
  
  .stat-divider { 
    width: 1px; 
    height: 40px; 
    background: #eee; 
    margin: 0 10px;
  }
}

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
  .toolbar-left { display: flex; align-items: center; flex-wrap: wrap; gap: 5px; }
}
.ml-10 { margin-left: 10px; }

// 表格容器
.table-container {
  flex: 1;
  overflow: hidden;
  .img-placeholder { 
    width: 32px; height: 32px; background: #f5f7fa; border-radius: 4px; 
    display: flex; align-items: center; justify-content: center; color: #c0c4cc; margin: 0 auto; 
  }
}

.pagination-bar { padding-top: 15px; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #eee; flex-shrink: 0; .total-info { font-size: 13px; color: #666; } }

.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
:deep(.custom-header th) { background-color: #f5f7fa !important; color: #333; font-weight: 600; }

// 弹窗通用
.warning-alert { background-color: #FFFBE6; border: 1px solid #FFE58F; padding: 10px; font-size: 12px; display: flex; align-items: center; gap: 8px; margin-bottom: 20px; color: #595959; .icon { color: #FAAD14; font-size: 16px; } }
.dialog-footer.center-footer { display: flex; justify-content: center; gap: 10px; }
.mb-20 { margin-bottom: 20px; }
</style>
