<template>
    <!-- <div class="mod-home">
      <h3>项目介绍</h3>
      <ul>
        一键生成考卷系统的主要目标是简化和自动化教师或教育机构在出卷过程中的工作，提高出卷效率，并保持试卷质量。
      </ul>
      <h3>获取帮助</h3>
      <ul>
        <li>Github地址：<a href="https://github.com/Forever-Violet/ocepgen" target="_blank">https://github.com/Forever-Violet/ocepgen</a></li>
        <li>如需关注项目最新动态，请Watch、Star项目，同时也是对项目最好的支持</li>
      </ul>
    </div> -->
	<div style="height: 700px;">
		<!-- 顶部-卡片 -->
		<el-card style="width: 100%; height: 35%; margin-bottom: 5px;" shadow="always">

			<el-row :gutter="20" class="flex-bc">
				<el-col :span="6" v-for="item in cardList" :key="item.name">
					<!-- 卡片组件 -->
					<CardItem :cardData="item" />
				</el-col>
			</el-row>
		</el-card>

		<el-card style="width: 100%; height: 75%;" shadow="always">
			<el-row>
					<el-card style="width: 64%; margin-right: 1%;" shadow="always">
						<!-- <template #header>
							<div>题库</div>
						</template> -->
						<BarChart/>
					</el-card>
				
					<el-card style="width: 35%;"  shadow="always">
						<!-- <template #header>
							<div>图三</div>
						</template> -->
						<!-- 玫瑰图 -->
						<roseChart />
					</el-card>


			</el-row>
		</el-card>
	</div>
</template>

<script setup lang="ts">
import baseService from "@/service/baseService";
import { ref, reactive,onBeforeMount, computed  } from 'vue';
// 卡片组件
import CardItem from './components/card-item/index.vue';
// 柱状图
import BarChart from './components/echarts/barChart.vue';
// 玫瑰图
import roseChart from './components/echarts/roseChart.vue';

const data = reactive({
  teacherCount: '',
  questionCount: '',
  examPaperCount: '',
  courseCount: '',
});
onBeforeMount(() => {
	getCardData();
});


// 获取顶部卡片所需数据
const getCardData = () => {
  return baseService.post("/dataDisplay/cardData").then((res) => {
	Object.assign(data, res.data);
	data.teacherCount = res.data.teacherCount;
	data.questionCount = res.data.questionCount;
	data.examPaperCount = res.data.examPaperCount;
	data.courseCount = res.data.courseCount;
	console.log("teacher++++ : " + data.teacherCount);
  });
};

// 卡片组件数据-渲染顶部卡片组件。响应式更新卡片数据。确保当 data 对象中的数据更新时，cardList 也应该相应更新。这可以通过几种方法实现，其中一个有效的方法是使用计算属性（Computed properties）。计算属性是基于它们的响应式依赖进行缓存的，只有当依赖项发生变化时才会重新计算
const cardList = computed(() => [
	{
		name: '教师用户',
		enName: 'Total number of teachers',
		number: data.teacherCount,
		iconClass: 'User',
		countClass: 'count1',
		iconColor: '#6ca8af'
	},
	{
		name: '试题总数',
		enName: 'Total number of questions',
		number: data.questionCount,
		iconClass: 'Coin',
		countClass: 'count2',
		iconColor: '#9ebc19'
	},
	{
		name: '考卷总数',
		enName: 'Total number of test papers',
		number: data.examPaperCount,
		iconClass: 'Tickets',
		countClass: 'count3',
		iconColor: '#fac03d'
	},
	{
		name: '课程总数',
		enName: 'Total number of courses',
		number: data.courseCount,
		iconClass: 'Notebook',
		countClass: 'count4',
		iconColor: '#c35c5d'
	}
]);


</script>

<style scoped>
.mod-home {
  line-height: 1.5;
}
.el-card {
	height: 450px;

	:deep(.el-card__body) {
		height: 400px;
	}
}
</style>