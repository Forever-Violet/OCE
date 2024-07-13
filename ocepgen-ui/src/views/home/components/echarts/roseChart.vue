<script setup lang="ts">
import baseService from "@/service/baseService";
import { ref, reactive,onBeforeMount  } from 'vue';
import Echarts from '@/components/echarts/index.vue';

const autoPaperCount = ref<any>();
const manualPaperCount = ref<any>();

onBeforeMount(() => {
	getAutoAndManualPaperCount();
});

// 获取手动组卷和智能组卷的考卷数量
const getAutoAndManualPaperCount = () => {
  return baseService.post("/dataDisplay/autoAndManualPaperCount").then((res) => {
	autoPaperCount.value =  res.data.autoPaperCount;
	manualPaperCount.value = res.data.manualPaperCount;
  });
};
const pieData = reactive([
	{
		name: '智能组卷',
		value: autoPaperCount
	},
	{
		name: '手动组卷',
		value: manualPaperCount
	}

]);

const option = reactive({
	color: ['#fff799', '#5bc9f8', '#ecb0c1'],
	legend: {
		top: '10px',
		icon: 'circle',
		color: '#555',
		itemWidth: 10,
		itemHeight: 10
	},
	tooltip: {
		confine: true,
		trigger: 'item',
		formatter: '{b} : {c}'
	},
	series: [
		{
			avoidLabelOverlap: false,
			type: 'pie',
			roseType: 'area', // 玫瑰图
			center: ['50%', '50%'],
			radius: ['30%', '60%'],
			label: {
				formatter: '{b}\n-------------\n{d}%\t{c}',
				fontSize: 16
			},
			labelLine: {
				length: 10,
				length2: 30
			},
			data: pieData
		}
	]
});
</script>

<template>
	<div class="h-full" style="width: 100%; height: 100%">
		<Echarts :option="option" />
	</div>
</template>
