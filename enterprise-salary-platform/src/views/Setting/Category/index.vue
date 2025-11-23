<template>
  <div class="app-content">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input 
            v-model="keyword" 
            placeholder="请输入分类名称" 
            style="width: 300px" 
            :suffix-icon="Search" 
            @keyup.enter="loadData"
          />
        </div>
        <div class="toolbar-right">
          <el-button class="btn-orange" @click="openEditDrawer()">新增</el-button>
          <el-button @click="showConfigDrawer = true">分类配置</el-button>
        </div>
      </div>

      <div class="table-container">
        <el-table :data="tableData" style="width: 100%; height: 100%;" border header-row-class-name="custom-header">
          <el-table-column prop="name" label="分类名称" min-width="200" />
          <el-table-column label="参数" min-width="100">
            <template #default="{ row }">
              {{ row.paramCount > 0 ? `${row.paramCount} 个参数` : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="openEditDrawer(row)">编辑</el-button>
              <el-button link type="info" style="color: #999">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-bar">
        <div class="total-info">当前第 1 到 25 条，总共 {{ total }} 条</div>
        <el-pagination background layout="prev, pager, next, slot" :total="total" :page-size="25">
          <template #default><span style="margin-left: 10px"><el-select v-model="pageSize" size="small" style="width: 100px"><el-option label="25 条/页" :value="25" /></el-select></span></template>
        </el-pagination>
      </div>
    </div>

    <el-drawer v-model="showConfigDrawer" title="分类配置" size="400px">
      <div class="config-content">
        <span class="required-star">*</span>
        <span class="label">分类填写要求：</span>
        <el-radio-group v-model="configForm.isRequired">
          <el-radio :label="false">非必填</el-radio>
          <el-radio :label="true">必填</el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <div class="drawer-footer">
          <el-button class="btn-orange" @click="showConfigDrawer = false">保存</el-button>
          <el-button @click="showConfigDrawer = false">取消</el-button>
        </div>
      </template>
    </el-drawer>

    <el-drawer v-model="showEditDrawer" :title="isEdit ? '编辑分类' : '新增分类'" size="600px">
      <el-form :model="editForm" label-position="top" class="edit-form">
        <div class="form-section-title">| 分类信息</div>
        <el-form-item label="分类名称" required>
          <el-input v-model="editForm.name" placeholder="请输入" />
        </el-form-item>

        <div class="form-section-title">| 添加参数</div>
        <el-table :data="editForm.params" border style="margin-bottom: 10px">
          <el-table-column label="参数" width="200">
            <template #default>
              <el-select placeholder="请选择参数" style="width: 100%">
                 <el-option label="材质" value="1" />
                 <el-option label="保修期" value="2" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="是否必填" align="center">
            <template #default="{ row }">
              <el-checkbox v-model="row.required" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
             <template #default="{ $index }">
               <el-button link type="danger" @click="removeParam($index)">移除</el-button>
             </template>
          </el-table-column>
        </el-table>
        <el-button link type="primary" @click="addParam">+ 添加</el-button>

      </el-form>
      <template #footer>
        <div class="drawer-footer">
          <el-button class="btn-orange" @click="showEditDrawer = false">保存</el-button>
          <el-button @click="showEditDrawer = false">取消</el-button>
        </div>
      </template>
    </el-drawer>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { getCategoryList, type CategoryItem } from '@/api/setting';

const tableData = ref<CategoryItem[]>([]);
const total = ref(0);
const pageSize = ref(25);
const keyword = ref('');

const showConfigDrawer = ref(false);
const showEditDrawer = ref(false);
const isEdit = ref(false);

const configForm = reactive({ isRequired: false });
const editForm = reactive({
  name: '',
  params: [] as { id: string; required: boolean }[]
});

const loadData = async () => {
  const res = await getCategoryList(keyword.value);
  tableData.value = res.list;
  total.value = res.total;
};

const openEditDrawer = (row?: CategoryItem) => {
  isEdit.value = !!row;
  if (row) {
    editForm.name = row.name;
    editForm.params = []; // 模拟空参数
  } else {
    editForm.name = '';
    editForm.params = [];
  }
  showEditDrawer.value = true;
};

const addParam = () => {
  editForm.params.push({ id: '', required: false });
};

const removeParam = (index: number) => {
  editForm.params.splice(index, 1);
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="scss">
$primary-orange: #ff6b3b;
$bg-gray: #F0F2F5;

// 复用通用布局样式
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
    .toolbar-left, .toolbar-right { display: flex; align-items: center; gap: 10px; }
  }

  .table-container { flex: 1; overflow: hidden; }
  
  .pagination-bar {
    padding-top: 15px;
    display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #eee; flex-shrink: 0;
    .total-info { font-size: 13px; color: #666; }
  }
}

.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
:deep(.custom-header th) { background-color: #f5f7fa !important; color: #333; font-weight: 600; }

// 抽屉特定样式
.config-content {
  padding: 20px 0;
  .required-star { color: #F56C6C; margin-right: 4px; }
  .label { font-weight: bold; margin-right: 15px; }
}

.form-section-title {
  font-size: 14px; font-weight: bold; color: #333; margin-bottom: 15px; margin-top: 10px; border-left: 3px solid $primary-orange; padding-left: 8px; line-height: 1;
}
.drawer-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style>