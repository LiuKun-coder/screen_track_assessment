<template>
  <div class="screen-container">
    <div class="screen-wrapper" ref="screenWrapper">
      <!-- 顶部标题栏 -->
      <div class="screen-header">
        <div class="header-decoration-left"></div>
        <div class="header-content">
          <div class="header-title">
            <span class="logo-icon">🛡️</span>
            <h1>校园电动车智能违规监控平台</h1>
          </div>
          <div class="header-subtitle">Intelligent Violation Monitoring Platform</div>
        </div>
        <div class="header-info">
          <div class="info-item weather">
            <span class="icon">🌤️</span>
            <span class="text">多云 24°C</span>
          </div>
          <div class="info-item time">
            <span class="date">{{ currentDate }}</span>
            <span class="time-text">{{ currentTime }}</span>
          </div>
          <div class="back-button" @click="goBackHome">
            <span class="back-icon">⬅</span>
            <span class="back-text">返回主页</span>
          </div>
        </div>
        <div class="header-decoration-right"></div>
      </div>

      <!-- 主体内容 -->
      <div class="screen-body">
        <!-- 左侧栏 -->
        <div class="column left-column">
          <div class="data-card">
            <div class="card-header">
              <span class="card-icon">📊</span>
              <span class="card-title">违规类型分布</span>
            </div>
            <div class="card-body">
              <div ref="pieChartRef" class="chart-container"></div>
            </div>
          </div>
          
          <div class="data-card">
            <div class="card-header">
              <span class="card-icon">📈</span>
              <span class="card-title">近七日违规趋势</span>
            </div>
            <div class="card-body">
              <div ref="lineChartRef" class="chart-container"></div>
            </div>
          </div>
        </div>

        <!-- 中间栏 -->
        <div class="column center-column">
          <!-- 核心指标卡 -->
          <div class="kpi-row">
            <div class="kpi-card">
              <div class="kpi-icon icon-red">🚨</div>
              <div class="kpi-text">
                <div class="kpi-label">今日违规</div>
                <div class="kpi-value num-font">{{ todayViolations }}</div>
              </div>
            </div>
            <div class="kpi-card">
              <div class="kpi-icon icon-blue">📅</div>
              <div class="kpi-text">
                <div class="kpi-label">本月累计</div>
                <div class="kpi-value num-font">{{ totalViolations }}</div>
              </div>
            </div>
            <div class="kpi-card">
              <div class="kpi-icon icon-green">✅</div>
              <div class="kpi-text">
                <div class="kpi-label">处理率</div>
                <div class="kpi-value num-font">{{ processingRate }}<small>%</small></div>
              </div>
            </div>
            <div class="kpi-card">
              <div class="kpi-icon icon-yellow">📡</div>
              <div class="kpi-text">
                <div class="kpi-label">在线设备</div>
                <div class="kpi-value num-font">{{ onlineDevices }}</div>
              </div>
            </div>
          </div>

          <!-- 动态地图区域 -->
          <div class="map-container-wrapper">
            <div class="map-border-decoration"></div>
            <div ref="mapChartRef" class="map-chart-container"></div>
          </div>
        </div>

        <!-- 右侧栏 -->
        <div class="column right-column">
          <div class="data-card">
            <div class="card-header">
              <span class="card-icon">📋</span>
              <span class="card-title">实时报警列表</span>
            </div>
            <div class="card-body list-body">
              <div class="list-header">
                <span>时间</span>
                <span>类型</span>
                <span>位置</span>
                <span>状态</span>
              </div>
              <div class="list-wrapper">
                <div class="list-scroll" :class="{ 'animate-scroll': recentViolations.length > 5 }">
                  <div 
                    v-for="(item, index) in recentViolations" 
                    :key="item.id" 
                    class="list-item"
                    :class="{ 'even': index % 2 === 0 }"
                  >
                    <span class="time">{{ item.time.split(' ')[1] }}</span>
                    <span class="type" :class="getTypeClass(item.type)">{{ item.typeName }}</span>
                    <span class="location" :title="item.location">{{ item.location }}</span>
                    <span class="status" :class="item.status">{{ item.statusName }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="data-card">
            <div class="card-header">
              <span class="card-icon">🏆</span>
              <span class="card-title">区域违规排行</span>
            </div>
            <div class="card-body">
              <div ref="barChartRef" class="chart-container"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import mapJson from '@/assets/cumt_nanhu.json';
import { getScreenStats } from '@/api/screen';

const router = useRouter();

// --- 地图引用 ---
const mapChartRef = ref(null);
let mapChart = null;

// --- 响应式数据 ---
const currentDate = ref('');
const currentTime = ref('');
const screenWrapper = ref(null);
const pieChartRef = ref(null);
const lineChartRef = ref(null);
const barChartRef = ref(null);

// 核心指标 - 从后端获取
const todayViolations = ref(0);
const totalViolations = ref(0);
const processingRate = ref(0);
const onlineDevices = ref(0);

// 模拟地图点位 (坐标基于中国矿业大学南湖校区范围)
const violationPoints = ref([
  { id: 1, lng: 117.1480, lat: 34.2200, type: 'danger', location: '北门入口' },
  { id: 2, lng: 117.1380, lat: 34.2180, type: 'warning', location: '博学楼A区' },
  { id: 3, lng: 117.1420, lat: 34.2160, type: 'danger', location: '图书馆前' },
  { id: 4, lng: 117.1360, lat: 34.2120, type: 'info', location: '梅苑餐厅' },
  { id: 5, lng: 117.1450, lat: 34.2120, type: 'warning', location: '南湖体育场' },
  { id: 6, lng: 117.1350, lat: 34.2100, type: 'danger', location: '松苑宿舍' },
]);

// 模拟实时列表
const recentViolations = ref([
  { id: 1, time: '2023-10-27 14:30:22', type: 'parking', typeName: '违停', location: '图书馆正门', status: 'pending', statusName: '待处理' },
  { id: 2, time: '2023-10-27 14:28:15', type: 'speeding', typeName: '超速', location: '北区主干道', status: 'processing', statusName: '处理中' },
  { id: 3, time: '2023-10-27 14:25:01', type: 'helmet', typeName: '未戴头盔', location: '东门出口', status: 'completed', statusName: '已处理' },
  { id: 4, time: '2023-10-27 14:22:45', type: 'parking', typeName: '违停', location: '三食堂侧门', status: 'pending', statusName: '待处理' },
  { id: 5, time: '2023-10-27 14:18:30', type: 'speeding', typeName: '超速', location: '实验楼B座', status: 'completed', statusName: '已处理' },
  { id: 6, time: '2023-10-27 14:15:10', type: 'helmet', typeName: '未戴头盔', location: '南门入口', status: 'processing', statusName: '处理中' },
  { id: 7, time: '2023-10-27 14:12:05', type: 'parking', typeName: '违停', location: '行政楼后', status: 'completed', statusName: '已处理' },
  { id: 8, time: '2023-10-27 14:09:55', type: 'speeding', typeName: '超速', location: '体育馆西侧', status: 'pending', statusName: '待处理' },
]);

// 图表数据 - 从后端获取
const typeDistribution = ref([]);
const dailyTrend = ref([]);
const areaRanking = ref([]);

// 从后端获取大屏统计数据
async function fetchScreenStats() {
  try {
    const data = await getScreenStats();
    
    // 更新核心指标
    if (data.todayViolations !== undefined) {
      todayViolations.value = data.todayViolations;
    }
    if (data.totalViolations !== undefined) {
      totalViolations.value = data.totalViolations;
    }
    if (data.processingRate !== undefined) {
      processingRate.value = data.processingRate;
    }
    if (data.onlineDevices !== undefined) {
      onlineDevices.value = data.onlineDevices;
    }
    
    // 更新图表数据
    if (data.typeDistribution) {
      typeDistribution.value = data.typeDistribution;
    }
    if (data.dailyTrend) {
      dailyTrend.value = data.dailyTrend;
    }
    if (data.areaRanking) {
      areaRanking.value = data.areaRanking;
    }
    if (data.recentViolations) {
      recentViolations.value = data.recentViolations;
    }
    if (data.violationPoints) {
      violationPoints.value = data.violationPoints;
    }

    updateChartsData();
    
  } catch (error) {
    console.error('获取大屏统计数据失败:', error);
    // 使用默认模拟数据
    todayViolations.value = 127;
    totalViolations.value = 3452;
    processingRate.value = 98.5;
    onlineDevices.value = 42;
    updateChartsData();
  }
}

// --- 工具函数 ---
const getTypeColor = (type) => {
  const map = {
    parking: '#ef4444', // danger
    speeding: '#f59e0b', // warning
    helmet: '#3b82f6', // info
    danger: '#ef4444',
    warning: '#f59e0b',
    info: '#3b82f6',
    default: '#10b981'
  };
  return map[type] || map.default;
};

const getTypeClass = (type) => {
  const map = {
    parking: 'text-danger',
    speeding: 'text-warning',
    helmet: 'text-info',
    default: 'text-normal'
  };
  return map[type] || map.default;
};

// --- ECharts 实例 ---
let pieChart = null;
let lineChart = null;
let barChart = null;

// --- 初始化图表 ---
const initCharts = () => {
  // 1. 饼图 - 违规类型分布
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value);
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%', textStyle: { color: '#fff' } },
      series: [
        {
          name: '违规类型',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 5,
            borderColor: '#0b1120',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}',
            color: '#fff'
          },
          labelLine: { show: true },
          data: [
            { value: 1048, name: '违停', itemStyle: { color: '#ef4444' } },
            { value: 735, name: '超速', itemStyle: { color: '#f59e0b' } },
            { value: 580, name: '未戴头盔', itemStyle: { color: '#3b82f6' } },
            { value: 484, name: '逆行', itemStyle: { color: '#8b5cf6' } },
            { value: 300, name: '载人', itemStyle: { color: '#10b981' } }
          ]
        }
      ]
    });
  }

  // 2. 折线图 - 7日趋势
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value);
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { top: '15%', left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        axisLine: { lineStyle: { color: '#4b5563' } },
        axisLabel: { color: '#9ca3af' }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: '#1f2937', type: 'dashed' } },
        axisLabel: { color: '#9ca3af' }
      },
      series: [
        {
          name: '违规总数',
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(59, 130, 246, 0.5)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.01)' }
            ])
          },
          itemStyle: { color: '#3b82f6' },
          data: [120, 132, 101, 134, 90, 230, 210]
        },
        {
          name: '已处理',
          type: 'line',
          smooth: true,
          itemStyle: { color: '#10b981' },
          data: [110, 120, 95, 120, 85, 220, 200]
        }
      ]
    });
  }

  // 3. 柱状图 - 区域排行
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value);
    barChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { top: '5%', left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'value',
        splitLine: { show: false },
        axisLabel: { color: '#9ca3af' }
      },
      yAxis: {
        type: 'category',
        data: ['行政楼', '体育馆', '食堂', '宿舍区', '教学楼'],
        axisLine: { show: false },
        axisTick: { show: false },
        axisLabel: { color: '#fff' }
      },
      series: [
        {
          name: '违规次数',
          type: 'bar',
          data: [80, 150, 220, 340, 480],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: '#00f2f1' },
              { offset: 1, color: '#005bea' }
            ]),
            borderRadius: [0, 10, 10, 0]
          },
          label: {
            show: true,
            position: 'right',
            color: '#fff'
          }
        }
      ]
    });
  }

  // 4. 中间地图 (GeoJSON)
  if (mapChartRef.value) {
    echarts.registerMap('cumt_nanhu', mapJson);
    mapChart = echarts.init(mapChartRef.value);
    mapChart.setOption({
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(0,0,0,0.7)',
        borderColor: '#0ea5e9',
        textStyle: { color: '#fff' },
        formatter: (params) => {
          if (params.seriesType === 'effectScatter') {
             return `${params.marker} ${params.name}<br/>坐标: ${params.value[0].toFixed(3)}, ${params.value[1].toFixed(3)}`;
          }
          return params.name;
        }
      },
      geo: {
        map: 'cumt_nanhu',
        roam: true,
        label: { show: false },
        itemStyle: {
          areaColor: 'rgba(14, 165, 233, 0.15)',
          borderColor: '#0ea5e9',
          borderWidth: 1.5,
          shadowColor: 'rgba(14, 165, 233, 0.5)',
          shadowBlur: 15
        },
        emphasis: {
          itemStyle: {
            areaColor: 'rgba(14, 165, 233, 0.4)'
          },
          label: { show: true, color: '#fff' }
        }
      },
      series: [
        {
          name: '违规点位',
          type: 'effectScatter',
          coordinateSystem: 'geo',
          data: violationPoints.value.map(p => ({
            name: p.location,
            value: [p.lng, p.lat],
            itemStyle: { color: getTypeColor(p.type) }
          })),
          symbolSize: 15,
          showEffectOn: 'render',
          rippleEffect: {
            brushType: 'stroke',
            scale: 3
          },
          label: {
            formatter: '{b}',
            position: 'right',
            show: true,
            color: '#fff'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowColor: '#333'
          },
          zlevel: 1
        }
      ]
    });
  }
};

const updateChartsData = () => {
  if (pieChart) {
    const pieData = (typeDistribution.value || []).map((item, index) => {
      const colorList = ['#ef4444', '#f59e0b', '#3b82f6', '#8b5cf6', '#10b981'];
      return {
        name: item.name || item.type || `类型${index + 1}`,
        value: item.value ?? item.count ?? 0,
        itemStyle: { color: colorList[index % colorList.length] }
      };
    });

    pieChart.setOption({
      series: [
        {
          data: pieData.length ? pieData : [
            { value: 0, name: '暂无数据', itemStyle: { color: '#64748b' } }
          ]
        }
      ]
    });
  }

  if (lineChart) {
    const trendData = dailyTrend.value || [];
    const xAxisData = trendData.map(item => item.date || item.day || item.name || '');
    const totalSeries = trendData.map(item => item.count ?? item.total ?? 0);
    const handledSeries = trendData.map(item => item.processed ?? item.handled ?? 0);

    lineChart.setOption({
      xAxis: {
        data: xAxisData.length ? xAxisData : ['暂无数据']
      },
      series: [
        {
          name: '违规总数',
          data: totalSeries.length ? totalSeries : [0]
        },
        {
          name: '已处理',
          data: handledSeries.length ? handledSeries : [0]
        }
      ]
    });
  }

  if (barChart) {
    const rankingData = areaRanking.value || [];
    const yAxisData = rankingData.map(item => item.area || item.name || '未知区域');
    const seriesData = rankingData.map(item => item.count ?? item.value ?? 0);

    barChart.setOption({
      yAxis: {
        data: yAxisData.length ? yAxisData : ['暂无数据']
      },
      series: [
        {
          data: seriesData.length ? seriesData : [0]
        }
      ]
    });
  }

  if (mapChart) {
    mapChart.setOption({
      series: [
        {
          data: (violationPoints.value || []).map(p => ({
            name: p.location || p.name || '未知点位',
            value: [p.lng ?? p.longitude ?? 0, p.lat ?? p.latitude ?? 0],
            itemStyle: { color: getTypeColor(p.type || p.level || 'default') }
          }))
        }
      ]
    });
  }
};

// --- 屏幕适配逻辑 ---
const handleResize = () => {
  if (screenWrapper.value) {
    const width = window.innerWidth;
    const height = window.innerHeight;
    const baseWidth = 1920;
    const baseHeight = 1080;
    
    // 计算缩放比例，保持长宽比
    const scaleX = width / baseWidth;
    const scaleY = height / baseHeight;
    const scale = Math.min(scaleX, scaleY);
    
    // 居中显示
    const left = (width - baseWidth * scale) / 2;
    const top = (height - baseHeight * scale) / 2;
    
    screenWrapper.value.style.transform = `scale(${scale}) translate(-50%, -50%)`;
    // 注意：translate(-50%, -50%) 配合 absolute 50% 50% 使用，这里简化逻辑
    // 更好的方式是 transform-origin: left top，然后设置 scale
    
    screenWrapper.value.style.transform = `scale(${scale})`;
    screenWrapper.value.style.left = `${(width - baseWidth * scale) / 2}px`;
    screenWrapper.value.style.top = `${(height - baseHeight * scale) / 2}px`;
  }
  
  // ECharts resize
  pieChart?.resize();
  lineChart?.resize();
  barChart?.resize();
  mapChart?.resize();
};

// --- 时间更新 ---
let timer = null;
let refreshTimer = null;
const updateTime = () => {
  const now = new Date();
  const dateStr = now.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
  const timeStr = now.toLocaleTimeString('zh-CN', { hour12: false });
  currentDate.value = dateStr;
  currentTime.value = timeStr;
};

// --- 返回主页 ---
const goBackHome = () => {
  router.push('/home');
};

onMounted(async () => {
  updateTime();
  timer = setInterval(updateTime, 1000);

  nextTick(() => {
    initCharts();
    fetchScreenStats();
    updateChartsData();
    handleResize();
    window.addEventListener('resize', handleResize);
  });
  
  // 每30秒刷新一次大屏数据
  refreshTimer = setInterval(fetchScreenStats, 30000);
});

onUnmounted(() => {
  clearInterval(timer);
  clearInterval(refreshTimer);
  window.removeEventListener('resize', handleResize);
  pieChart?.dispose();
  lineChart?.dispose();
  barChart?.dispose();
  mapChart?.dispose();
});
</script>

<style scoped>
/* --- 全局变量与基础设置 --- */
:root {
  --bg-color: #0b1120;
  --card-bg: rgba(16, 23, 42, 0.7);
  --border-color: rgba(59, 130, 246, 0.3);
  --text-primary: #ffffff;
  --text-secondary: #94a3b8;
  --accent-color: #00f2f1;
  --danger-color: #ef4444;
  --warning-color: #f59e0b;
}

.screen-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #0b1120;
  background-image: 
    radial-gradient(circle at 50% 50%, rgba(14, 165, 233, 0.1) 0%, transparent 50%),
    linear-gradient(rgba(0,0,0,0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0,0,0,0.1) 1px, transparent 1px);
  background-size: 100% 100%, 20px 20px, 20px 20px;
  overflow: hidden;
  z-index: 999;
  font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  color: #fff;
}

.screen-wrapper {
  position: absolute;
  width: 1920px;
  height: 1080px;
  transform-origin: left top;
  /* border: 1px solid red;  Debug purpose */
  display: flex;
  flex-direction: column;
}

/* --- Header --- */
.screen-header {
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: url("data:image/svg+xml,%3Csvg width='1920' height='80' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M0,0 L1920,0 L1920,60 L1300,60 L1260,80 L660,80 L620,60 L0,60 Z' fill='rgba(15, 23, 42, 0.8)'/%3E%3C/svg%3E") no-repeat center top;
  background-size: 100% 100%;
  padding: 0 40px;
  position: relative;
}

.header-content {
  text-align: center;
  flex: 1;
}

.header-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.header-title h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(to bottom, #fff, #a5f3fc);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 20px rgba(6, 182, 212, 0.5);
  letter-spacing: 2px;
}

.logo-icon {
  font-size: 32px;
}

.header-subtitle {
  font-size: 12px;
  color: #67e8f9;
  letter-spacing: 4px;
  margin-top: 4px;
  text-transform: uppercase;
  opacity: 0.8;
}

.header-info {
  display: flex;
  gap: 20px;
  font-size: 18px;
  color: #a5f3fc;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.back-btn {
  cursor: pointer;
  background: rgba(255, 255, 255, 0.1);
  padding: 5px 15px;
  border-radius: 20px;
  border: 1px solid rgba(165, 243, 252, 0.3);
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(165, 243, 252, 0.2);
  box-shadow: 0 0 10px rgba(165, 243, 252, 0.3);
}

.time-text {
  font-weight: bold;
  font-family: 'Courier New', Courier, monospace;
}

/* 返回按钮样式 */
.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.15), rgba(14, 165, 233, 0.1));
  padding: 8px 18px;
  border-radius: 20px;
  border: 1px solid rgba(165, 243, 252, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 16px;
  color: #a5f3fc;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(6, 182, 212, 0.2);
}

.back-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(165, 243, 252, 0.3), transparent);
  transition: left 0.5s;
}

.back-button:hover {
  background: linear-gradient(135deg, rgba(6, 182, 212, 0.3), rgba(14, 165, 233, 0.2));
  border-color: #67e8f9;
  box-shadow: 0 0 15px rgba(6, 182, 212, 0.5), 0 0 30px rgba(6, 182, 212, 0.2);
  transform: translateY(-1px);
  color: #ffffff;
}

.back-button:hover::before {
  left: 100%;
}

.back-button:active {
  transform: translateY(0);
  box-shadow: 0 0 10px rgba(6, 182, 212, 0.4);
}

.back-icon {
  font-size: 18px;
  transition: transform 0.3s;
}

.back-button:hover .back-icon {
  transform: translateX(-3px);
}

.back-text {
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* --- Body Layout --- */
.screen-body {
  flex: 1;
  padding: 20px 30px 30px;
  display: grid;
  grid-template-columns: 25% 50% 25%;
  gap: 20px;
  height: calc(100% - 80px);
}

.column {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
}

/* --- Common Card Style --- */
.data-card {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(56, 189, 248, 0.2);
  border-radius: 4px;
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
}

/* Card Decorations (Corners) */
.data-card::before {
  content: '';
  position: absolute;
  top: -1px; left: -1px;
  width: 10px; height: 10px;
  border-top: 2px solid #06b6d4;
  border-left: 2px solid #06b6d4;
}
.data-card::after {
  content: '';
  position: absolute;
  bottom: -1px; right: -1px;
  width: 10px; height: 10px;
  border-bottom: 2px solid #06b6d4;
  border-right: 2px solid #06b6d4;
}

.card-header {
  height: 40px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  background: linear-gradient(90deg, rgba(6, 182, 212, 0.15), transparent);
  border-bottom: 1px solid rgba(56, 189, 248, 0.1);
}

.card-icon {
  margin-right: 8px;
  font-size: 18px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #e0f2fe;
}

.card-body {
  flex: 1;
  padding: 10px;
  position: relative;
  overflow: hidden;
}

.chart-container {
  width: 100%;
  height: 100%;
}

/* --- Center Column --- */
.kpi-row {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  height: 100px;
}

.kpi-card {
  flex: 1;
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid rgba(56, 189, 248, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 15px;
  position: relative;
}
.kpi-card::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; width: 100%; height: 2px;
  background: linear-gradient(90deg, transparent, rgba(6, 182, 212, 0.5), transparent);
}

.kpi-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
}

.kpi-text {
  display: flex;
  flex-direction: column;
}

.kpi-label {
  font-size: 14px;
  color: #94a3b8;
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.num-font {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Map Area */
.map-container-wrapper {
  flex: 1;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(56, 189, 248, 0.2);
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.map-chart-container {
  width: 100%;
  height: 100%;
}

/* --- List Style --- */
.list-body {
  display: flex;
  flex-direction: column;
  padding: 0;
}

.list-header {
  display: grid;
  grid-template-columns: 1fr 1fr 1.5fr 1fr;
  padding: 10px 15px;
  background: rgba(6, 182, 212, 0.1);
  font-weight: bold;
  color: #67e8f9;
  font-size: 14px;
}

.list-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.list-scroll {
  /* Simple animation for scrolling effect if needed, currently static with overflow hidden */
  /* For demo purposes, let's keep it static but styled */
  padding: 0;
}

.list-item {
  display: grid;
  grid-template-columns: 1fr 1fr 1.5fr 1fr;
  padding: 12px 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 14px;
  align-items: center;
  transition: background 0.3s;
}

.list-item:hover {
  background: rgba(255, 255, 255, 0.05);
}

.list-item.even {
  background: rgba(0, 0, 0, 0.1);
}

.text-danger { color: #ef4444; }
.text-warning { color: #f59e0b; }
.text-info { color: #3b82f6; }
.text-normal { color: #fff; }

.status.pending { color: #f59e0b; }
.status.processing { color: #3b82f6; }
.status.completed { color: #10b981; }

</style>
