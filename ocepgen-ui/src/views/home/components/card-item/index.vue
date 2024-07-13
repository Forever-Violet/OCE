<script setup lang="ts">
// 数字滚动插件
import { CountTo } from 'vue3-count-to';
import { ref, onActivated } from 'vue';

const reload = ref(true);

defineProps({
	cardData: {
		type: Object,
		default: () => {},
		required: true
	}
});

onActivated(() => {
	// 切换页面回来后，重新加载卡片数据，动态效果
	reload.value = false;
	setTimeout(()=> {
		reload.value = true;
	}, 100);
});

// 过滤卡片英文名称-将英文名称过滤为大写
const filterEnName = (enName: string) => {
	return enName.toUpperCase();
};
</script>

<template>
	<div class="dataCard-content bg-white p-4 mr-4" style="margin-top: 25px;">
		<div style="padding-left: 15px;padding-top: 10px;padding-bottom: 15px;">
			<!-- 卡片-中文名称 -->
			<h2 class="card-name">{{ cardData.name }}</h2>
			<!-- 卡片-英文名称 -->
			<div class="en-name">{{ filterEnName(cardData.enName) }}</div>
			<div class="card-info flex-bc">
				<!-- 卡片-数字 -->
				<count-to
					:startVal="0"
					:endVal="cardData.number"
					:decimals="0"
					:duration="3000"
					class="card-count"
					v-if="reload"
					:class="cardData.countClass" />
				<!-- 卡片-右下角图标 -->
				<svg-icon class="card-icon" :icon-class="cardData.iconClass" :icon-color="cardData.iconColor" />
			</div>
		</div>
	</div>
</template>

<style scoped lang="scss">
.dataCard-content {
	position: relative;
	cursor: pointer;
	border-radius: 15px;
	transition: all 0.5s;
	background: #eaeef1;

	&:last-child {
		margin-right: 0;
	}

	&:hover {
		box-shadow:
			0 0.8px 2.2px rgb(0 0 0 / 2%),
			0 1.9px 5.3px rgb(0 0 0 / 2.8%),
			0 3.5px 10px rgb(0 0 0 / 3.5%),
			0 6.3px 17.9px rgb(0 0 0 / 4.2%),
			0 11.7px 33.4px rgb(0 0 0 / 5%),
			0 28px 80px rgb(0 0 0 / 7%);
		transform: translateY(-5px);

		.card-name,
		.en-name,
		.card-count {
			color: #fff !important;
		}
	}

	&:hover::before {
		visibility: visible;
		opacity: 1;
	}

	&::before {
		position: absolute;
		top: 0;
		left: 0;
		z-index: -1;
		width: 100%;
		height: 100%;
		visibility: hidden;
		content: '';
		background: linear-gradient(135deg,#2afadf,#4c83ff);
		border-radius: 15px;
		opacity: 0;
		transition: 0.5s;
	}

	.card-name {
		font-size: 1.5rem;
		color: #333;
	}

	.en-name {
		padding: 10px 0;
		color: #ccc;
	}

	.card-info {
		height: 52px;
		line-height: 52px;

		.card-count {
			font-size: 3rem;
			font-weight: 700;
		}

		.count1 {
			color: #2baeeb;
		}

		.count2 {
			color: #00bab5;
		}

		.count3 {
			color: #e19904;
		}

		.count4 {
			color: #ef6666;
		}

		.card-icon {
			width: 62px !important;
			height: 62px !important;
			margin-left: 40%;
		}
	}
}
</style>
