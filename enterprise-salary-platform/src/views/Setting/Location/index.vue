<template>
  <div class="app-content">
    <div class="page-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input 
            v-model="keyword" 
            placeholder="请输入存放地点" 
            style="width: 300px" 
            :suffix-icon="Search"
          />
        </div>
        <div class="toolbar-right">
          <el-button class="btn-orange" @click="handleAddRoot">新增</el-button>
          <el-button @click="showImportDialog = true">导入</el-button>
          <el-button>导出</el-button>
        </div>
      </div>

      <div class="table-container">
        <el-table
          :data="tableData"
          style="width: 100%; height: 100%;"
          row-key="id"
          border
          default-expand-all
          header-row-class-name="custom-header"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        >
          <el-table-column prop="name" label="存放地点" min-width="300">
            <template #default="{ row }">
              <span>{{ row.name }}</span>
              <span v-if="row.countText" class="sub-count">{{ row.countText }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="parentName" label="上级存放地点" width="200">
             <template #default="{ row }">{{ row.parentName || '-' }}</template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="primary" v-if="row.level < 3" @click="handleAddSub(row)">新增下级</el-button>
              <el-button link type="info" style="color: #999">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog v-model="showEditDialog" :title="dialogTitle" width="500px">
      <el-alert
        title="地点架构最多支持 3 级，仅显示符合条件的【上级存放地点】。"
        type="info"
        show-icon
        :closable="false"
        class="mb-20"
      />
      <el-form :model="form" label-width="110px">
        <el-form-item label="上级存放地点" required>
          <el-select v-model="form.parentId" disabled style="width: 100%">
             <el-option label="无 (当前为一级地点)" value="" v-if="!form.parentId"/>
             <el-option :label="form.parentName" :value="form.parentId" v-else />
          </el-select>
        </el-form-item>
        <el-form-item label="存放地点" required>
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer center-footer">
          <el-button class="btn-orange" @click="showEditDialog = false">保存</el-button>
          <el-button @click="showEditDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showImportDialog" title="导入存放地点" width="600px">
      <div class="import-steps">
         <div class="step-item"><div class="step-num">1</div><div class="step-content"><div class="step-text">下载模板，在模板中填入存放地点</div><el-button size="small" class="download-btn"><el-icon><Download /></el-icon> 下载模板</el-button></div></div>
         <div class="step-line"></div>
         <div class="step-item"><div class="step-num">2</div><div class="step-content"><div class="step-text">上传填好的文件</div></div></div>
      </div>
      <el-upload class="upload-area" drag :show-file-list="false" :http-request="handleLocationUpload"><el-icon class="el-icon--upload"><upload-filled /></el-icon><div class="el-upload__text">单击、拖拽文件到这个区域进行上传</div></el-upload>
      <template #footer>
        <div class="dialog-footer center-footer">
          <el-button class="btn-orange" @click="showImportDialog = false">导入</el-button>
          <el-button @click="showImportDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { Search, Download, UploadFilled } from '@element-plus/icons-vue';
import type { UploadRequestOptions } from 'element-plus/es/components/upload/src/upload';
import { ElMessage } from 'element-plus';
import { uploadAndParse } from '@/api/file';
import { getLocationList, type LocationItem } from '@/api/location';

const tableData = ref<LocationItem[]>([]);
const keyword = ref('');

// 弹窗控制
const showEditDialog = ref(false);
const showImportDialog = ref(false);
const isEditMode = ref(false);

type LocationForm = {
  id: number | '';
  name: string;
  parentId: number | '';
  parentName: string;
};

const form = reactive<LocationForm>({
  id: '',
  name: '',
  parentId: '',
  parentName: '无 (当前为一级地点)'
});

const dialogTitle = computed(() => {
  if (isEditMode.value) return '编辑';
  // 如果有 parentId 则是新增下级，否则是新增一级
  return form.parentId ? '新增' : '新增';
});

const loadData = async () => {
  tableData.value = await getLocationList();
};

// 操作方法
const handleAddRoot = () => {
  isEditMode.value = false;
  form.id = '';
  form.name = '';
  form.parentId = '';
  form.parentName = '无 (当前为一级地点)';
  showEditDialog.value = true;
};

const handleAddSub = (row: LocationItem) => {
  isEditMode.value = false;
  form.id = '';
  form.name = '';
  form.parentId = row.id;
  form.parentName = row.name;
  showEditDialog.value = true;
};

const handleEdit = (row: LocationItem) => {
  isEditMode.value = true;
  form.id = row.id;
  form.name = row.name;
  form.parentId = row.parentId || '';
  form.parentName = row.parentName || '无 (当前为一级地点)';
  showEditDialog.value = true;
};

onMounted(() => {
  loadData();
});

const handleLocationUpload = async (options: UploadRequestOptions) => {
  await uploadAndParse('locations', options.file as File);
  ElMessage.success('存放地点文件解析完成');
};
</script>

<style scoped lang="scss">
$primary-orange: #ff6b3b;
$bg-gray: #F0F2F5;

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
    .toolbar-right { display: flex; gap: 10px; }
  }

  .table-container {
    flex: 1;
    overflow: hidden;
    
    .sub-count {
      color: $primary-orange; // 红色字体
      margin-left: 5px;
      font-size: 12px;
    }
  }
}

// 通用样式
.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
:deep(.custom-header th) { background-color: #f5f7fa !important; color: #333; font-weight: 600; }
.mb-20 { margin-bottom: 20px; }

// 导入弹窗样式 (复用)
.import-steps { display: flex; flex-direction: column; margin-bottom: 20px; padding-left: 10px; .step-item { display: flex; gap: 10px; .step-num { width: 20px; height: 20px; border-radius: 50%; border: 1px solid #ccc; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #999; flex-shrink: 0; } .step-content { .step-text { font-size: 14px; color: #333; margin-bottom: 5px; } .download-btn { color: $primary-orange; border-color: #ffdecb; background: #fff5f0; } } } .step-line { width: 1px; height: 20px; background: #eee; margin-left: 10px; margin-top: 2px; margin-bottom: 2px; } }
.upload-area :deep(.el-upload-dragger) { padding: 40px 10px; }
.dialog-footer.center-footer { display: flex; justify-content: center; gap: 10px; }
</style>
