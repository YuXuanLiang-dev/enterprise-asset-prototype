<template>
  <div class="dashboard-content">
    <div class="dashboard-grid">
      
      <div class="grid-item">
        <div class="content-card">
          <div class="card-header">
            <div class="title-box">
              <div class="blue-bar"></div>
              <span class="card-title">资产概况</span>
            </div>
            <div class="action-box">
              <span class="act-btn" :class="{ active: pieMode === 'count' }" @click="pieMode = 'count'; renderPie()">按数量统计</span>
              <span class="split">|</span>
              <span class="act-btn" :class="{ active: pieMode === 'value' }" @click="pieMode = 'value'; renderPie()">按价值统计</span>
            </div>
          </div>
          <div class="card-body">
            <div ref="pieChartRef" class="echart-box"></div>
          </div>
        </div>
      </div>

      <div class="grid-item">
        <div class="content-card">
          <div class="card-header">
            <div class="title-box">
              <div class="blue-bar"></div>
              <span class="card-title">资产数量统计</span>
            </div>
            <div class="action-box">
              <span class="act-btn" :class="{ active: barMode === 'category' }" @click="barMode = 'category'; renderBar()">按资产分类</span>
              <span class="split">|</span>
              <span class="act-btn" :class="{ active: barMode === 'location' }" @click="barMode = 'location'; renderBar()">按存放地</span>
            </div>
          </div>
          <div class="card-body">
            <div ref="barChartRef" class="echart-box"></div>
          </div>
        </div>
      </div>

      <div class="grid-item placeholder-item">
        <div class="placeholder-content">
          <el-icon class="plus-icon"><Plus /></el-icon>
          <div class="text">待添加看板模块 (3)</div>
        </div>
      </div>

      <div class="grid-item placeholder-item">
        <div class="placeholder-content">
          <el-icon class="plus-icon"><Plus /></el-icon>
          <div class="text">待添加看板模块 (4)</div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import { getDashboardData } from '@/api/dashboard';

const pieChartRef = ref<HTMLElement>();
const barChartRef = ref<HTMLElement>();
let pieChartInstance: echarts.ECharts | null = null;
let barChartInstance: echarts.ECharts | null = null;
const pieMode = ref<'count' | 'value'>('count');
const barMode = ref<'category' | 'location'>('category');
const chartData = ref<any>(null);

const handleResize = () => {
  pieChartInstance?.resize();
  barChartInstance?.resize();
};

const renderPie = async () => {
  if (!chartData.value) return;
  await nextTick();
  const isValue = pieMode.value === 'value';
  const source = isValue ? chartData.value.assetValueOverview : chartData.value.assetOverview;
  const totalLabel = isValue ? (source.totalValue || 0).toFixed(2) : source.total;

  if (pieChartRef.value) {
    if (!pieChartInstance) pieChartInstance = echarts.init(pieChartRef.value);
    pieChartInstance.setOption({
      color: ['#00C9A7', '#3B82F6', '#F56C6C', '#909399'],
      tooltip: { trigger: 'item' },
      legend: {
        type: 'scroll',
        bottom: '0',
        left: 'center',
        icon: 'circle',
        itemGap: 15,
        textStyle: { color: '#666', fontSize: 12 }
      },
      series: [
        {
          name: '资产概况',
          type: 'pie',
          radius: ['45%', '65%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            position: 'center',
            formatter: `{b|${totalLabel}}\n{a|${isValue ? '资产总值(元)' : '资产总数'}}`,
            rich: {
              a: { color: '#999', fontSize: 12, lineHeight: 18 },
              b: { color: '#333', fontSize: 24, fontWeight: 'bold', lineHeight: 30 }
            }
          },
          labelLine: { show: false },
          data: source.detail
        }
      ]
    });
  }
};

const renderBar = async () => {
  if (!chartData.value) return;
  await nextTick();
  const isLocation = barMode.value === 'location';
  const xData = isLocation ? chartData.value.locationValueStats.values : chartData.value.assetStats.values;
  const yData = isLocation ? chartData.value.locationValueStats.locations : chartData.value.assetStats.categories;

  if (barChartRef.value) {
    if (!barChartInstance) barChartInstance = echarts.init(barChartRef.value);
    barChartInstance.setOption({
      color: ['#5B8FF9'],
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { top: '30', left: '10', right: '10', bottom: '10', containLabel: true },
      xAxis: {
        type: 'value',
        position: 'top',
        splitLine: { lineStyle: { type: 'dashed', color: '#eee' } },
        axisLabel: { color: '#999' }
      },
      yAxis: {
        type: 'category',
        data: yData,
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#666', width: 100, overflow: 'truncate' }
      },
      series: [
        {
          type: 'bar',
          barWidth: 12,
          data: xData,
          label: { show: true, position: 'right', color: '#666' },
          itemStyle: { borderRadius: [0, 50, 50, 0] },
          showBackground: true,
          backgroundStyle: { color: 'rgba(180, 180, 180, 0.08)', borderRadius: [0, 50, 50, 0] }
        }
      ]
    });
  }
};

const initCharts = async () => {
  chartData.value = await getDashboardData();
  await renderPie();
  await renderBar();
};

onMounted(() => {
  initCharts();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  pieChartInstance?.dispose();
  barChartInstance?.dispose();
});
</script>

<style scoped lang="scss">
$bg-gray: #F0F2F5;
$header-bg: #2B73D8;

.dashboard-content {
  background-color: $bg-gray;
  padding: 15px;
  height: 100%;
  box-sizing: border-box;
  overflow: hidden; // 网格自动适应，不需要滚动条(除非屏幕极小)
}

// --- 核心：CSS Grid 布局 ---
.dashboard-grid {
  display: grid;
  height: 100%;
  width: 100%;
  // 2列，每列占 50%
  grid-template-columns: repeat(2, 1fr);
  // 2行，每行占 50%
  grid-template-rows: repeat(2, 1fr);
  gap: 15px; // 模块间距

  .grid-item {
    background: #fff;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    display: flex;
    flex-direction: column;

    // 预留模块的样式
    &.placeholder-item {
      border: 2px dashed #e0e0e0;
      background: transparent;
      box-shadow: none;
      align-items: center;
      justify-content: center;
      color: #ccc;
      transition: all 0.3s;
      cursor: pointer;
      
      &:hover {
        border-color: $header-bg;
        color: $header-bg;
        background: rgba($header-bg, 0.02);
      }

      .placeholder-content {
        text-align: center;
        .plus-icon { font-size: 32px; margin-bottom: 10px; }
        .text { font-size: 14px; }
      }
    }
  }
}

// 卡片通用样式
.content-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    flex-shrink: 0;

    .title-box {
      display: flex; align-items: center;
      .blue-bar { width: 4px; height: 14px; background: $header-bg; border-radius: 2px; margin-right: 8px; }
      .card-title { font-size: 15px; font-weight: bold; color: #333; }
    }

    .action-box {
      display: flex; align-items: center; font-size: 12px; color: #999;
      .act-btn { cursor: pointer; &:hover { color: $header-bg; } &.active { color: $header-bg; font-weight: 500; } }
      .split { margin: 0 8px; color: #eee; }
      
      .more-btn {
        cursor: pointer; display: flex; align-items: center; gap: 2px;
        &:hover { color: $header-bg; }
      }
    }
  }

  .card-body {
    flex: 1;
    width: 100%;
    min-height: 0; // Grid/Flex 嵌套必需
    position: relative;
    .echart-box { width: 100%; height: 100%; }
  }
}
</style>
