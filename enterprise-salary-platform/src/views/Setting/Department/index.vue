<template>
  <div class="app-content">
    <div class="page-card dept-layout">
      
      <div class="left-panel">
        <div class="panel-title">单位架构</div>
        <div class="tree-wrapper">
          <el-tree
            :data="deptTree"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node">
                <span class="node-label">{{ node.label }}</span>
                <div class="node-actions">
                  <el-icon class="action-icon" @click.stop="handleAddSubDept(data)"><Plus /></el-icon>
                  <el-dropdown trigger="click" @command="handleTreeCommand">
                    <el-icon class="action-icon"><MoreFilled /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="edit">编辑部门</el-dropdown-item>
                        <el-dropdown-item command="delete" style="color: #F56C6C">删除部门</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <div class="right-panel">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-input 
              v-model="keyword" 
              placeholder="请输入姓名" 
              style="width: 240px" 
              :suffix-icon="Search"
              @keyup.enter="loadTableData"
            />
          </div>
          <div class="toolbar-right">
            <el-button class="btn-orange" @click="openAddPersonDialog">新增</el-button>
            <el-button @click="showImportDialog = true">导入</el-button>
            <el-button @click="handleBatchDelete">删除</el-button>
            <el-button>导出</el-button>
          </div>
        </div>

        <div class="table-wrapper">
          <el-table 
            :data="tableData" 
            style="width: 100%; height: 100%;" 
            border 
            header-row-class-name="custom-header"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="name" label="姓名" width="180" />
            <el-table-column prop="deptName" label="部门" min-width="200" />
            <el-table-column label="操作" width="150" fixed="right" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEditPerson(row)">编辑</el-button>
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

    </div>

    <el-dialog v-model="showPersonDialog" :title="personDialogTitle" width="500px">
      <el-form :model="personForm" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="personForm.name" placeholder="请输入" maxlength="10" show-word-limit />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="personForm.deptId" placeholder="请选择" style="width: 100%">
            <el-option label="石楼中学 / 电教" value="1" />
            <el-option label="石楼中学 / 后勤" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer center-footer">
          <el-button class="btn-orange" @click="showPersonDialog = false">确定</el-button>
          <el-button @click="showPersonDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showDeptDialog" title="添加子部门" width="500px">
      <el-alert
        title="单位架构最多支持 3 级"
        type="info"
        show-icon
        :closable="false"
        class="mb-20"
      />
      <el-form :model="deptForm" label-width="100px">
        <el-form-item label="上级部门" required>
          <el-select v-model="deptForm.parentId" disabled style="width: 100%">
            <el-option label="石楼中学" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门名称" required>
          <el-input v-model="deptForm.name" placeholder="测试" maxlength="20" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer center-footer">
          <el-button class="btn-orange" @click="showDeptDialog = false">确定</el-button>
          <el-button @click="showDeptDialog = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="showImportDialog" title="导入人员" width="600px">
       <div class="import-steps">
         <div class="step-item"><div class="step-num">1</div><div class="step-content"><div class="step-text">下载模板，在模板中填入对应信息</div><el-button size="small" class="download-btn"><el-icon><Download /></el-icon> 下载模板</el-button></div></div>
         <div class="step-line"></div>
         <div class="step-item"><div class="step-num">2</div><div class="step-content"><div class="step-text">上传填好的文件</div></div></div>
      </div>
      <el-upload class="upload-area" drag :show-file-list="false" :http-request="handlePersonnelUpload"><el-icon class="el-icon--upload"><upload-filled /></el-icon><div class="el-upload__text">单击、拖拽文件到这个区域进行上传</div></el-upload>
      <div class="import-warning">
        <el-icon><WarningFilled /></el-icon>
        <div class="text">
          <b>温馨提示：</b><br/>
          1、请先维护部门再导入人员，文件中需填入已存在的部门，否则人员会导入失败<br/>
          2、姓名不允许重复，同名人员可在后面增加数字编号
        </div>
      </div>
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
import { Search, Plus, MoreFilled, Download, UploadFilled, WarningFilled } from '@element-plus/icons-vue';
import type { UploadRequestOptions } from 'element-plus/es/components/upload/src/upload';
import { ElMessage } from 'element-plus';
import { uploadAndParse } from '@/api/file';
import { getDeptTree, getPersonnelList, type DeptNode, type PersonnelItem } from '@/api/department';

// 状态
const deptTree = ref<DeptNode[]>([]);
const tableData = ref<PersonnelItem[]>([]);
const total = ref(0);
const pageSize = ref(25);
const keyword = ref('');
const selectedRows = ref<PersonnelItem[]>([]);

// 弹窗控制
const showPersonDialog = ref(false);
const showDeptDialog = ref(false);
const showImportDialog = ref(false);
const isEditMode = ref(false);

// 表单数据
type PersonForm = { name: string; deptId: number | '' };
type DeptForm = { parentId: number | ''; name: string };

const personForm = reactive<PersonForm>({ name: '', deptId: '' });
const deptForm = reactive<DeptForm>({ parentId: 1, name: '' });

// 计算属性
const personDialogTitle = computed(() => isEditMode.value ? '编辑人员' : '新增人员');

// 加载数据
const loadTree = async () => {
  deptTree.value = await getDeptTree();
};
const loadTableData = async () => {
  const res = await getPersonnelList({ keyword: keyword.value });
  tableData.value = res.list;
  total.value = res.total;
};

// 树节点点击
const handleNodeClick = (data: DeptNode) => {
  console.log('Clicked Dept:', data.label);
  // 这里可以根据部门ID刷新右侧列表
  loadTableData();
};

// 树节点操作
const handleAddSubDept = (data: DeptNode) => {
  deptForm.parentId = data.id; // 实际应赋值ID，这里模拟
  deptForm.name = '';
  showDeptDialog.value = true;
};
const handleTreeCommand = (command: string) => {
  console.log('Tree Command:', command);
};

// 人员操作
const openAddPersonDialog = () => {
  isEditMode.value = false;
  personForm.name = '';
  personForm.deptId = 1; // 默认选中当前部门
  showPersonDialog.value = true;
};
const handleEditPerson = (row: PersonnelItem) => {
  isEditMode.value = true;
  personForm.name = row.name;
  personForm.deptId = 1; // 模拟回显
  showPersonDialog.value = true;
};
const handleSelectionChange = (val: PersonnelItem[]) => { selectedRows.value = val; };
const handleBatchDelete = () => { console.log('Delete', selectedRows.value); };

onMounted(() => {
  loadTree();
  loadTableData();
});

const handlePersonnelUpload = async (options: UploadRequestOptions) => {
  await uploadAndParse('personnel', options.file as File);
  ElMessage.success('人员导入文件解析完成');
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
}

.dept-layout {
  display: flex;
  background: #fff;
  border-radius: 4px;
  height: 100%;
  overflow: hidden;

  // 左侧树面板
  .left-panel {
    width: 260px;
    border-right: 1px solid #eee;
    display: flex;
    flex-direction: column;
    
    .panel-title {
      padding: 15px;
      font-weight: bold;
      font-size: 16px;
      color: #333;
      border-bottom: 1px solid #f5f5f5;
      display: flex;
      align-items: center;
      &::before { content: ''; display: block; width: 3px; height: 14px; background: $primary-orange; margin-right: 8px; }
    }

    .tree-wrapper {
      flex: 1;
      overflow-y: auto;
      padding: 10px;

      :deep(.el-tree-node__content) {
        height: 36px;
        &:hover { background-color: #f5f7fa; }
      }

      .custom-tree-node {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-right: 8px;
        font-size: 14px;
        
        .node-actions {
          display: none; // 默认隐藏操作图标
          align-items: center;
          gap: 4px;
          .action-icon { padding: 4px; border-radius: 4px; &:hover { background: #e6e6e6; } }
        }

        &:hover .node-actions {
          display: flex; // 悬浮显示
        }
      }
    }
  }

  // 右侧列表面板
  .right-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 15px;
    overflow: hidden;
    
    .toolbar {
      display: flex; justify-content: space-between; margin-bottom: 15px; flex-shrink: 0;
      .toolbar-left { display: flex; gap: 10px; }
      .toolbar-right { display: flex; gap: 10px; }
    }
    
    .table-wrapper { flex: 1; overflow: hidden; }
    
    .pagination-bar {
      padding-top: 15px;
      display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #eee; flex-shrink: 0;
      .total-info { font-size: 13px; color: #666; }
    }
  }
}

// 通用样式
.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
:deep(.custom-header th) { background-color: #f5f7fa !important; color: #333; font-weight: 600; }
.mb-20 { margin-bottom: 20px; }

// 弹窗样式复用 (Import Dialog)
.import-steps { display: flex; flex-direction: column; margin-bottom: 20px; padding-left: 10px; .step-item { display: flex; gap: 10px; .step-num { width: 20px; height: 20px; border-radius: 50%; border: 1px solid #ccc; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #999; flex-shrink: 0; } .step-content { .step-text { font-size: 14px; color: #333; margin-bottom: 5px; } .download-btn { color: $primary-orange; border-color: #ffdecb; background: #fff5f0; } } } .step-line { width: 1px; height: 20px; background: #eee; margin-left: 10px; margin-top: 2px; margin-bottom: 2px; } }
.upload-area :deep(.el-upload-dragger) { padding: 40px 10px; }
.import-warning { margin-top: 20px; background: #F6F8FA; padding: 15px; display: flex; gap: 10px; font-size: 12px; color: #666; border-radius: 4px; line-height: 1.8; .el-icon { color: #FAAD14; font-size: 16px; margin-top: 2px; } }
.dialog-footer.center-footer { display: flex; justify-content: center; gap: 10px; }
</style>
