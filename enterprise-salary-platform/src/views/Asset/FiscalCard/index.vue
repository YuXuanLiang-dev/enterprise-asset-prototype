<template>
  <div class="page-content">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="searchParams.category" clearable placeholder="请选择分类" style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
          <el-input 
            v-model="searchParams.keyword" 
            placeholder="请输入资产名称/编码/规格型号/品牌" 
            style="width: 300px; margin-left: 10px"
            :suffix-icon="Search"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="handleSearch" style="margin-left: 10px">查询</el-button>
          <el-button type="primary" link @click="showFilterDrawer = true">高级筛选 <el-icon><Filter /></el-icon></el-button>
          <el-button type="primary" link @click="handleReset">重置</el-button>
        </div>
        <div class="toolbar-right">
          <el-button class="btn-orange" @click="showImportDialog = true">导入财政资产卡片</el-button>
          <el-button @click="handleBatchDelete">删除</el-button>
          <el-dropdown>
            <el-button>
              导出 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>导出选中</el-dropdown-item>
                <el-dropdown-item>导出全部</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="table-container">
        <el-table 
          :data="tableData" 
          style="width: 100%; height: 100%;" 
          @selection-change="handleSelectionChange"
          header-row-class-name="custom-header"
          border
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column prop="code" label="资产编码" width="160" show-overflow-tooltip />
          <el-table-column prop="name" label="资产名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="originalValue" label="原值(元)" width="120" align="right">
            <template #default="{ row }">{{ row.originalValue.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="acquisitionDate" label="取得日期" width="120" />
          <el-table-column prop="accumulatedDepreciation" label="累计折旧(元)" width="120" align="right">
            <template #default="{ row }">{{ row.accumulatedDepreciation.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="postingDate" label="记账日期" width="120" />
          <el-table-column prop="voucherNo" label="记账凭证号" width="120" />
          <el-table-column prop="depreciationMonths" label="折旧年月" width="100" align="center" />
          <el-table-column prop="remarks" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="80" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-bar">
        <div class="total-info">当前第 1 到 25 条，总共 {{ total }} 条</div>
        <el-pagination background layout="prev, pager, next, slot" :total="total" :page-size="25">
          <template #default><span style="margin-left: 10px"><el-select v-model="pageSize" size="small" style="width: 100px"><el-option label="25 条/页" :value="25" /><el-option label="50 条/页" :value="50" /></el-select></span></template>
        </el-pagination>
      </div>
    </div>

    <el-drawer v-model="showFilterDrawer" title="高级筛选" size="320px">
        <el-form :model="filterForm" label-position="top">
        <el-form-item label="使用部门">
          <el-input v-model="filterForm.useDept" placeholder="请输入使用部门" />
        </el-form-item>
        <el-form-item label="使用人">
          <el-input v-model="filterForm.user" placeholder="请输入使用人" />
        </el-form-item>
        <el-form-item label="管理部门">
          <el-input v-model="filterForm.managerDept" placeholder="请输入管理部门" />
        </el-form-item>
        <el-form-item label="管理人">
          <el-input v-model="filterForm.manager" placeholder="请输入管理人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="drawer-footer">
          <el-button type="primary" class="btn-orange" @click="handleSearch">筛选</el-button>
          <el-button @click="handleResetFilter">重置</el-button>
        </div>
      </template>
    </el-drawer>

    <el-dialog v-model="showEditDialog" title="编辑财政资产卡片" width="900px" top="5vh">
       <div class="warning-alert"><el-icon class="icon"><Warning /></el-icon>编辑<span class="highlight">已匹配</span>的财政卡片不会更新资产列表...</div>
       <el-form :model="editForm" label-width="110px" class="edit-form">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="资产编码"><el-input v-model="editForm.code" disabled /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资产名称"><el-input v-model="editForm.name"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="分类"><el-input v-model="editForm.category"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="规格型号"><el-input v-model="editForm.spec"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="editForm.brand"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数量"><el-input-number v-model="editForm.quantity" :min="0" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="使用部门"><el-input v-model="editForm.useDept"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="使用人"><el-input v-model="editForm.userName"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="管理部门"><el-input v-model="editForm.managerDept"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="管理人"><el-input v-model="editForm.managerName"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="原值"><el-input v-model="editForm.originalValue"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="取得日期"><el-date-picker v-model="editForm.acquisitionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="累计折旧"><el-input v-model="editForm.accumulatedDepreciation"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="记账日期"><el-date-picker v-model="editForm.postingDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="记账凭证号"><el-input v-model="editForm.voucherNo"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="折旧年月"><el-input-number v-model="editForm.depreciationMonths" :min="0" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input v-model="editForm.remarks" type="textarea" rows="2"/></el-form-item></el-col>
        </el-row>
       </el-form>
       <template #footer>
         <div class="dialog-footer"><el-button class="btn-orange" @click="handleSaveEdit">确定</el-button><el-button @click="showEditDialog = false">取消</el-button></div>
       </template>
    </el-dialog>

    <el-dialog v-model="showImportDialog" title="导入财政资产卡片" width="600px">
       <div class="import-steps">
         <div class="step-item">
           <div class="step-text">下载模板</div>
           <el-link type="primary" :href="templateUrl" target="_blank">点击下载模板</el-link>
         </div>
       </div>
       <el-upload class="upload-area" drag :show-file-list="false" :http-request="handleFiscalUpload">
         <div class="el-upload__text">拖拽上传或点击上传</div>
       </el-upload>
       <template #footer>
         <div class="dialog-footer center-footer"><el-button class="btn-orange" @click="showImportDialog = false">导入</el-button><el-button @click="showImportDialog = false">取消</el-button></div>
       </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// ... Script 内容完全保持不变，不需要修改逻辑 ...
import { ref, reactive, onMounted } from 'vue';
import { ArrowDown, Filter, Search, Warning } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { UploadRequestOptions } from 'element-plus/es/components/upload/src/upload';
import { uploadAndParse } from '@/api/file';
import { deleteAssets, getAssetList, updateAsset, type AssetItem } from '@/api/asset';

const tableData = ref<AssetItem[]>([]);
const allData = ref<AssetItem[]>([]);
const total = ref(0);
const pageSize = ref(25);
const showFilterDrawer = ref(false);
const showEditDialog = ref(false);
const showImportDialog = ref(false);
const selectedRows = ref<AssetItem[]>([]);
const templateUrl = '/templates/fiscal-card-template.csv';
const categoryOptions = ref<string[]>([]);

const searchParams = reactive({ category: '', keyword: '' });
const filterForm = reactive({ useDept: '', user: '', managerDept: '', manager: '' });
const editForm = ref<AssetItem>({} as AssetItem);

const loadData = async () => {
  const res = await getAssetList(searchParams);
  allData.value = res.list;
  categoryOptions.value = Array.from(new Set(res.list.map((item) => item.category))).filter(Boolean);
  applyFilters();
};
const applyFilters = () => {
  const keyword = searchParams.keyword?.toLowerCase() || '';
  const category = searchParams.category || '';
  const useDept = filterForm.useDept?.toLowerCase() || '';
  const user = filterForm.user?.toLowerCase() || '';
  const managerDept = filterForm.managerDept?.toLowerCase() || '';
  const manager = filterForm.manager?.toLowerCase() || '';

  const filtered = allData.value.filter((item) => {
    const matchKeyword =
      !keyword ||
      [item.name, item.code, item.spec, item.brand].some((f) => (f || '').toLowerCase().includes(keyword));
    const matchCategory = !category || item.category === category;
    const matchUseDept = !useDept || (item.useDept || '').toLowerCase().includes(useDept);
    const matchUser = !user || (item.userName || '').toLowerCase().includes(user);
    const matchManagerDept = !managerDept || (item.managerDept || '').toLowerCase().includes(managerDept);
    const matchManager = !manager || (item.managerName || '').toLowerCase().includes(manager);
    return matchKeyword && matchCategory && matchUseDept && matchUser && matchManagerDept && matchManager;
  });
  tableData.value = filtered;
  total.value = filtered.length;
};
const handleReset = () => {
  searchParams.category = '';
  searchParams.keyword = '';
  handleResetFilter();
};
const handleSelectionChange = (val: AssetItem[]) => { selectedRows.value = val; };
const handleEdit = (row: AssetItem) => { editForm.value = JSON.parse(JSON.stringify(row)); showEditDialog.value = true; };
const handleSaveEdit = async () => { 
  await updateAsset(editForm.value); 
  ElMessage.success('保存成功'); 
  showEditDialog.value = false; 
  loadData(); 
};
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return ElMessage.warning('请先选择数据');
  await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 条数据吗？`, '警告', { type: 'warning' });
  await deleteAssets(selectedRows.value.map(item => item.id));
  ElMessage.success('删除成功'); 
  loadData();
};
const handleSearch = () => { showFilterDrawer.value = false; applyFilters(); };
const handleResetFilter = () => { filterForm.useDept = ''; filterForm.user = ''; filterForm.managerDept = ''; filterForm.manager = ''; applyFilters(); };

onMounted(() => { loadData(); });

const handleFiscalUpload = async (options: UploadRequestOptions) => {
  await uploadAndParse('fiscal-cards', options.file as File);
  ElMessage.success('文件解析完成');
};
</script>

<style scoped lang="scss">
$bg-gray: #F0F2F5;
$primary-orange: #ff6b3b;

// 瘦身后的样式
.page-content {
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
    .toolbar-left, .toolbar-right { display: flex; align-items: center; gap: 10px; }
  }

  .table-container {
    flex: 1;
    overflow: hidden; // 交给内部的 el-table 处理滚动
  }
  
  .pagination-bar {
    padding-top: 15px;
    display: flex; justify-content: space-between; align-items: center;
    border-top: 1px solid #eee;
    flex-shrink: 0;
    .total-info { font-size: 13px; color: #666; }
  }
}

// 通用样式
.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
:deep(.custom-header th) { background-color: #f5f7fa !important; color: #333; font-weight: 600; }

// 弹窗样式保持不变 (请保留原来的 .warning-alert, .import-steps 等样式)
.warning-alert { background-color: #FFFBE6; border: 1px solid #FFE58F; padding: 8px 15px; font-size: 12px; color: #595959; display: flex; align-items: center; margin-bottom: 20px; border-radius: 2px; .icon { color: #FAAD14; margin-right: 8px; font-size: 16px; } .highlight { color: #FF4D4F; margin: 0 2px; } }
.form-section-title { font-size: 14px; font-weight: bold; color: #333; margin-bottom: 15px; margin-top: 10px; border-left: 3px solid $primary-orange; padding-left: 8px; line-height: 1; }
.import-steps { display: flex; flex-direction: column; margin-bottom: 20px; padding-left: 10px; .step-item { display: flex; gap: 10px; .step-num { width: 20px; height: 20px; border-radius: 50%; border: 1px solid #ccc; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #999; flex-shrink: 0; } .step-content { .step-text { font-size: 14px; color: #333; margin-bottom: 5px; } .download-btn { color: $primary-orange; border-color: #ffdecb; background: #fff5f0; } } } .step-line { width: 1px; height: 20px; background: #eee; margin-left: 10px; margin-top: 2px; margin-bottom: 2px; } }
.upload-area :deep(.el-upload-dragger) { padding: 40px 10px; }
.drawer-footer, .dialog-footer { display: flex; justify-content: flex-end; gap: 10px; &.center-footer { justify-content: center; } }
</style>
