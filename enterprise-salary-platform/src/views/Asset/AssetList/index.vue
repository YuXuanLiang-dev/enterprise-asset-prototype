<template>
  <div class="app-content">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select class="status-select" v-model="searchParams.status" placeholder="请选择资产状态" clearable>
            <el-option label="闲置" value="idle" />
            <el-option label="在用" value="in_use" />
            <el-option label="维修" value="repair" />
            <el-option label="报废" value="scrapped" />
          </el-select>
          <el-input
            class="search-input"
            v-model="searchParams.keyword"
            placeholder="请输入资产名称/编码/规格型号/品牌/存放地点"
            :suffix-icon="Search"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" link @click="showFilterDrawer = true">高级筛选</el-button>
          <el-button type="primary" link @click="handleReset">重置</el-button>
        </div>
        <div class="toolbar-right">
          <el-button class="btn-orange" @click="openCreate">资产入库</el-button>
          <el-button class="btn-orange" @click="showImportDialog = true">批量导入</el-button>
          <el-button @click="handleBatchUpdate">批量更新</el-button>

          <el-dropdown split-button @command="handleAssetCommand">
            资产管理
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="change">变更</el-dropdown-item>
                <el-dropdown-item command="assign">领用</el-dropdown-item>
                <el-dropdown-item command="repair">报修</el-dropdown-item>
                <el-dropdown-item command="scrap">报废</el-dropdown-item>
                <el-dropdown-item command="return">归还</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-dropdown trigger="click">
            <el-button>导出 <el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showExportDialog = true">导出全部</el-dropdown-item>
                <el-dropdown-item @click="showExportDialog = true">导出勾选</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="selection-tip" v-if="selectedRows.length > 0">
        已勾选 <span class="count">{{ selectedRows.length }}</span> 个资产
        <el-button link type="primary" @click="clearSelection">取消选择</el-button>
      </div>

      <div class="table-container">
        <el-table
          ref="tableRef"
          :data="tableData"
          style="width: 100%; height: 100%;"
          @selection-change="handleSelectionChange"
          header-row-class-name="custom-header"
          border
          :loading="loading"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="资产照片" width="90" align="center">
            <template #default>
              <div class="img-placeholder"><el-icon><Picture /></el-icon></div>
            </template>
          </el-table-column>
          <el-table-column prop="code" label="资产编码" width="180" show-overflow-tooltip />
          <el-table-column prop="status" label="资产状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="plain">{{ row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="资产名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="category" label="资产分类" width="180" show-overflow-tooltip />
          <el-table-column prop="spec" label="规格型号" width="120" show-overflow-tooltip />
          <el-table-column prop="brand" label="品牌" width="100" show-overflow-tooltip />
          <el-table-column prop="location" label="存放地点" width="120" show-overflow-tooltip />

          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-btns">
                <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
                <el-button link type="primary" @click="viewRecord(row)">记录</el-button>
                <el-button link type="primary" @click="handleDelete([row.id])">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-bar">
        <div class="total-info">当前第 1 到 25 条，总共 {{ total }} 条</div>
        <el-pagination background layout="prev, pager, next, slot" :total="total" :page-size="25">
          <template #default>
            <span style="margin-left: 10px">
              <el-select v-model="pageSize" size="small" style="width: 100px">
                <el-option label="25 条/页" :value="25" />
              </el-select>
            </span>
          </template>
        </el-pagination>
      </div>
    </div>

    <el-drawer v-model="showFilterDrawer" title="高级筛选" size="320px">
      <el-form :model="searchParams" label-position="top">
        <el-form-item label="资产分类">
          <el-input v-model="searchParams.category" placeholder="请输入分类" />
        </el-form-item>
        <el-form-item label="存放地点">
          <el-input v-model="searchParams.location" placeholder="请输入地点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="drawer-footer">
          <el-button type="primary" class="btn-orange" @click="handleSearch">筛选</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </template>
    </el-drawer>

    <el-dialog v-model="showEditDialog" :title="editForm.id ? '编辑资产' : '资产入库'" width="700px">
      <el-form :model="editForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="资产编码"><el-input v-model="editForm.code" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="资产名称"><el-input v-model="editForm.name" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="editForm.status" placeholder="请选择"><el-option label="闲置" value="idle"/><el-option label="在用" value="in_use"/><el-option label="维修" value="repair"/><el-option label="报废" value="scrapped"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态文本"><el-input v-model="editForm.statusText" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="分类"><el-input v-model="editForm.category" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="规格型号"><el-input v-model="editForm.spec" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="editForm.brand" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="存放地点"><el-input v-model="editForm.location" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="价格"><el-input v-model="editForm.price" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="购置日期"><el-date-picker v-model="editForm.purchaseDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="备注"><el-input type="textarea" v-model="editForm.remarks" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="btn-orange" @click="saveAsset">保存</el-button>
          <el-button @click="showEditDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ArrowDown, Search, Picture } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import http from '@/api/http';
import { getAssetListMain, type AssetListItem } from '@/api/assetList';

const tableData = ref<AssetListItem[]>([]);
const total = ref(0);
const pageSize = ref(25);
const selectedRows = ref<AssetListItem[]>([]);
const tableRef = ref();
const loading = ref(false);

const showEditDialog = ref(false);
const editForm = reactive<Partial<AssetListItem & { remarks?: string }>>({});

// Dialog Flags
const showImportDialog = ref(false);
const showExportDialog = ref(false);
const showFilterDrawer = ref(false);

const searchParams = reactive({ status: '', keyword: '', location: '', category: '' });

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getAssetListMain(searchParams);
    tableData.value = res.list;
    total.value = res.total;
  } catch (e: any) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const getStatusType = (status: string) => {
  if (status === 'idle') return 'info';
  if (status === 'in_use') return 'success';
  if (status === 'repair') return 'warning';
  if (status === 'scrapped') return 'danger';
  return '';
};

const handleSelectionChange = (val: AssetListItem[]) => { selectedRows.value = val; };
const clearSelection = () => { tableRef.value?.clearSelection(); };
const handleReset = () => {
  searchParams.status = '';
  searchParams.keyword = '';
  searchParams.location = '';
  searchParams.category = '';
  loadData();
};
const handleAssetCommand = async (command: string) => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择资产');
    return;
  }
  if (command === 'delete') {
    await handleDelete(selectedRows.value.map((i) => i.id));
    return;
  }
  ElMessage.info(`操作 ${command} 暂未实现`);
};
const handleBatchUpdate = () => { showImportDialog.value = true; };
const handleSearch = () => { showFilterDrawer.value = false; loadData(); };

const openEdit = (row: AssetListItem) => {
  Object.assign(editForm, row);
  showEditDialog.value = true;
};

const openCreate = () => {
  Object.assign(editForm, {
    id: undefined,
    status: 'idle',
    statusText: '闲置',
    name: '',
    category: '',
    spec: '',
    brand: '',
    location: '',
    price: 0,
    code: ''
  });
  showEditDialog.value = true;
};

const saveAsset = async () => {
  try {
    if (!editForm.code || !editForm.name) {
      ElMessage.warning('请填写资产编码和名称');
      return;
    }
    loading.value = true;
    if (editForm.id) {
      await http.put(`/assets/${editForm.id}`, editForm);
      ElMessage.success('修改成功');
    } else {
      await http.post(`/assets`, editForm);
      ElMessage.success('入库成功');
    }
    showEditDialog.value = false;
    await loadData();
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || '保存失败');
  } finally {
    loading.value = false;
  }
};

const handleDelete = async (ids: number[]) => {
  if (!ids || ids.length === 0) return;
  await ElMessageBox.confirm(`确定删除选中的 ${ids.length} 条资产吗？`, '提示', { type: 'warning' });
  await http.request({ url: '/assets', method: 'DELETE', data: { ids } });
  ElMessage.success('删除成功');
  loadData();
};

const viewRecord = (row: AssetListItem) => {
  ElMessage.info(`资产 ${row.code} 操作记录请在“操作记录”页查看`);
};

onMounted(() => { loadData(); });
</script>

<style scoped lang="scss">
$bg-gray: #F0F2F5;
$primary-orange: #ff6b3b;

.app-content {
  background-color: $bg-gray;
  padding: 15px;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
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
    align-items: flex-start;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 15px;
    flex-shrink: 0;
    .toolbar-left,
    .toolbar-right {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 10px;
    }
    .toolbar-left {
      flex: 1 1 320px;
      min-width: 260px;
    }
    .toolbar-right {
      flex: 1 1 320px;
      justify-content: flex-end;
      .el-button,
      .el-dropdown {
        white-space: nowrap;
      }
    }
    .status-select {
      width: 150px;
      flex-shrink: 0;
    }
    .search-input {
      flex: 1;
      min-width: 220px;
    }
  }
  .selection-tip {
    background: #e6f7ff;
    border: 1px solid #91d5ff;
    padding: 8px 15px;
    margin-bottom: 10px;
    border-radius: 4px;
    font-size: 13px;
    color: #666;
    display: flex;
    align-items: center;
    gap: 10px;
    .count {
      color: #1890ff;
      font-weight: bold;
      margin: 0 2px;
    }
  }
  .table-container {
    flex: 1;
    overflow: hidden;
    .img-placeholder {
      width: 40px;
      height: 40px;
      background: #f5f7fa;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #c0c4cc;
      margin: 0 auto;
    }
    .action-btns {
      display: flex;
      justify-content: center;
      gap: 5px;
      white-space: nowrap;
      .el-button {
        margin-left: 0;
        padding: 4px 8px;
      }
    }
  }
  .pagination-bar {
    padding-top: 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #eee;
    flex-shrink: 0;
    .total-info {
      font-size: 13px;
      color: #666;
    }
  }
}

.btn-orange {
  background-color: $primary-orange;
  border-color: $primary-orange;
  color: #fff;
  &:hover {
    opacity: 0.9;
    color: #fff;
    border-color: $primary-orange;
    background-color: $primary-orange;
  }
}
:deep(.custom-header th) {
  background-color: #f5f7fa !important;
  color: #333;
  font-weight: 600;
}
.drawer-footer,
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
