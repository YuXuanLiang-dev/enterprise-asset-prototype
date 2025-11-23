<template>
  <div class="app-content">
    <div class="page-card match-page">
      <div class="page-header">
        <div class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回
        </div>
        <div class="divider">|</div>
        <div class="title">编码匹配</div>
      </div>

      <div class="scroll-content">
        <div class="steps-wrapper">
          <el-steps :active="1" align-center finish-status="success">
            <el-step title="上传文件"></el-step>
            <el-step title="数据确认"></el-step>
            <el-step title="导入结果"></el-step>
          </el-steps>
        </div>

        <div class="step-content">
          <div class="section">
            <div class="section-title">
              <span class="num">1</span>
              点击下载【编码匹配表】与【财政资产卡片】
            </div>
            <div class="section-desc">
              1. 编码匹配表提供盘盈、盘实、盘亏数据，请填入财政信息并删除资产照片列后另存为上传；<br>
              2. 进行编码匹配时请参照已核实盘点的财政资产卡片，避免对已匹配的资产重复进行匹配；
            </div>
            <div class="btn-group">
              <el-button><el-icon><Download /></el-icon> 下载编码匹配表</el-button>
              <el-button class="btn-text-orange">下载编码匹配表-带资产照片</el-button>
              <el-button class="btn-text-orange"><el-icon><Download /></el-icon> 下载财政资产卡片</el-button>
            </div>
          </div>

          <div class="section">
            <div class="section-title">
               <span class="num">2</span>
               上传编码匹配表
            </div>
            <div class="section-desc">1. 请删除编码匹配表中的【资产照片】列再上传；</div>
            <el-upload class="upload-area" drag :show-file-list="false" :http-request="handleMatchUpload">
               <el-icon class="el-icon--upload"><upload-filled /></el-icon>
               <div class="el-upload__text">
                 单击、拖拽文件到这个区域进行上传<br>
                 <small style="color: #999">文件最大不超过 20M；仅支持 .xlsx 和 .xls 格式的文件</small>
               </div>
            </el-upload>
          </div>

          <div class="section">
            <div class="section-title">
               <span class="num">3</span>
               定义编码匹配后数据取值规则
            </div>
            <div class="match-rules">
               <div class="rule-row">1、匹配后，资产分类、品牌、规格型号与参数值，优先取 <el-select model-value="1" size="small" style="width: 120px"><el-option label="财政资产卡片" value="1"/></el-select></div>
               <div class="rule-row">2、存放地及归属信息默认以 <span class="highlight">采集数据</span> 优先，其他资产信息，以 <span class="highlight-orange">财政资产卡片</span> 优先 <span class="link">具体字段</span></div>
               <div class="rule-row">3、优先取值为空，数据 <el-select model-value="1" size="small" style="width: 100px"><el-option label="不补全" value="1"/></el-select></div>
            </div>
          </div>

          <div class="import-warning">
            <el-icon><WarningFilled /></el-icon>
            <div class="text">
              <b>温馨提示：</b><br/>
              1、导入后，【资产编码-财政】【资产名称-财政】的资产状态自动更新为【盘实】【已贴标】；<br/>
              2、如果发现匹配错误，可手动解除匹配，或在导入文件中更新信息，重新导入；
            </div>
          </div>
        </div>
      </div>

      <div class="footer-actions">
        <el-button class="btn-orange">导入</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeft, Download, UploadFilled, WarningFilled } from '@element-plus/icons-vue';
import type { UploadRequestOptions } from 'element-plus/es/components/upload/src/upload';
import { ElMessage } from 'element-plus';
import { uploadAndParse } from '@/api/file';

const handleMatchUpload = async (options: UploadRequestOptions) => {
  await uploadAndParse('asset-match', options.file as File);
  ElMessage.success('编码匹配表解析完成');
};
</script>

<style scoped lang="scss">
$bg-gray: #F0F2F5;
$primary-orange: #ff6b3b;

.app-content { background-color: $bg-gray; padding: 15px; height: 100%; width: 100%; box-sizing: border-box; display: flex; flex-direction: column; }

.match-page {
  background: #fff;
  border-radius: 4px;
  flex: 1; // 撑满高度
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;

  .page-header {
    padding: 20px;
    display: flex; align-items: center; font-size: 14px; border-bottom: 1px solid #f0f0f0;
    .back-btn { cursor: pointer; display: flex; align-items: center; color: #666; &:hover { color: $primary-orange; } }
    .divider { margin: 0 10px; color: #ddd; }
    .title { font-weight: bold; color: #333; }
  }

  .scroll-content {
    flex: 1; // 占据剩余空间
    overflow-y: auto; // 内部滚动
    padding: 20px;
    padding-bottom: 80px; // 给底部按钮留空间
  }

  .steps-wrapper { width: 60%; margin: 0 auto 40px; }

  .step-content {
    width: 80%; margin: 0 auto;
    .section {
      margin-bottom: 30px;
      .section-title { font-weight: bold; font-size: 14px; margin-bottom: 10px; display: flex; align-items: center; 
         .num { background: $primary-orange; color: #fff; width: 18px; height: 18px; border-radius: 50%; text-align: center; line-height: 18px; font-size: 12px; margin-right: 8px; }
      }
      .section-desc { font-size: 12px; color: #666; line-height: 1.8; margin-bottom: 10px; padding-left: 26px; }
      .btn-group { padding-left: 26px; .btn-text-orange { color: $primary-orange; border-color: #ffdecb; background: #fff5f0; margin-left: 10px; } }
    }
  }

  .upload-area :deep(.el-upload-dragger) { padding: 40px; }

  .match-rules {
    padding-left: 26px; font-size: 12px; color: #666; line-height: 2.5;
    .highlight { color: $primary-orange; font-weight: bold; margin: 0 2px; }
    .highlight-orange { color: #E6A23C; font-weight: bold; margin: 0 2px; }
    .link { color: #409eff; cursor: pointer; margin-left: 5px; }
  }

  .import-warning {
    margin-top: 20px; background: #F6F8FA; padding: 15px; display: flex; gap: 10px; font-size: 12px; color: #666; border-radius: 4px; line-height: 1.8; margin-bottom: 20px;
    .el-icon { color: #FAAD14; font-size: 16px; margin-top: 2px; }
  }

  // 关键修改：底部按钮绝对定位在容器底部
  .footer-actions {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    padding: 15px;
    text-align: center;
    border-top: 1px solid #eee;
    z-index: 10;
  }
}

.btn-orange { background-color: $primary-orange; border-color: $primary-orange; color: #fff; &:hover { opacity: 0.9; color: #fff; border-color: $primary-orange; background-color: $primary-orange;} }
</style>
