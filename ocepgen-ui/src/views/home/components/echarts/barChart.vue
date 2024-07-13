<script setup lang="ts">
import baseService from "@/service/baseService";
import { ref, reactive,onBeforeMount  } from 'vue';
import Echarts from '@/components/echarts/index.vue';

const lastMonthNewQuestionCount = ref<any[]>([]);
const nowMonthNewQuestionCount = ref<any[]>([]);

onBeforeMount(() => {
	getlastAndNowMonthQuestionCount();
});

// 获取题库上个与和当前月的新增题数
const getlastAndNowMonthQuestionCount = () => {
  return baseService.post("/dataDisplay/lastAndNowMonthQuestionCount").then((res) => {
	lastMonthNewQuestionCount.value =  res.data.lastMonthNewQuestionCount;
	nowMonthNewQuestionCount.value = res.data.nowMonthNewQuestionCount;
  });
};
const option = reactive({
	tooltip: {
		trigger: 'axis',
		axisPointer: {
			type: 'shadow',
			label: {
				show: true
			}
		}
	},
	grid: {
		left: '4%',
		top: '15%',
		right: '4%',
		bottom: '10%'
	},
	legend: {
		data: ['上月题库新增', '本月题库新增'],
		top: '4%',
		color: '#1FC3CE',
		fontSize: 14,
		//selected: { 昨日使用率: true } // 不需要显示的设置为false
	},
	xAxis: {
		data: [
			'单选题',
			'多选题',
			'判断题',
			'填空题',
			'主观题',
		],
		axisLine: {
			show: true, //隐藏X轴轴线
			lineStyle: {
				color: '#eee',
				width: 1
			}
		},
		axisTick: {
			show: true, //隐藏X轴刻度
			alignWithLabel: true
		},
		axisLabel: {
			show: true,
			color: '#333', //X轴文字颜色
			fontSize: 14
		}
	},
	yAxis: [
		{
			type: 'value',
			name: '题数',
			nameTextStyle: {
				color: '#333',
				fontSize: 14
				
			},
			splitLine: {
				show: true,
				lineStyle: {
					width: 1,
					color: '#eee'
				}
			},
			axisTick: {
				show: false
			},
			axisLine: {
				show: false
			},
			axisLabel: {
				show: true,
				color: '#333',
				fontSize: 14
			}
		}
	],
	series: [
		{
			name: '上月题库新增',
			type: 'bar',
			barWidth: 18,
			itemStyle: {
				color: {
					type: 'linear',
					x: 0, // 右
					y: 1, // 下
					x2: 0, // 左
					y2: 0, // 上
					colorStops: [
						{
							offset: 0,
							color: '#ffd3a5' // 0% 处的颜色
						},
						{
							offset: 1,
							color: '#fd6585' // 100% 处的颜色
						}
					]
				}
			},
			data: lastMonthNewQuestionCount
		},
		{
			name: '本月题库新增',
			type: 'bar',
			barWidth: 18,
			itemStyle: {
				color: {
					type: 'linear',
					x: 0, // 右
					y: 1, // 下
					x2: 0, // 左
					y2: 0, // 上
					colorStops: [
						{
							offset: 0,
							color: '#79f1a4' // 0% 处的颜色linear-gradient(135deg,#79f1a4,#0e5cad)
						},
						{
							offset: 1,
							color: '#0e5cad' // 100% 处的颜色
						}
					]
				}
			},
			data: nowMonthNewQuestionCount
		}
	]
});
</script>

<template>
	<div style="width: 100%;height: 100%">
		<Echarts :option="option" />
	</div>
</template>
