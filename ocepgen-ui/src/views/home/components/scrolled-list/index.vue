<template>
	<vue3-seamless-scroll
		hover-stop="true"
		:list="scrollList"
		:hover="true"
		:limitScrollNum="5"
		:step="0.5"
		class="h-full overflow-hidden">
		<div class="flex flex-col" v-for="(item, index) in scrollList" :key="index">
			<div class="scroll-item flex-bc pl-2.5 pr-2.5 mt-3">
				<div class="avatar">{{ item.avatar }}</div>
				<div class="main-info flex flex-col flex-1 ml-2.5">
					<span class="address">{{ item.address }}</span>
					<span class="goods-info mt-1">{{ item.goodsInfo }}</span>
				</div>
				<div class="time">
					{{ item.number >= 2 ? item.number + '分钟前' : '刚刚' }}
				</div>
			</div>
		</div>
	</vue3-seamless-scroll>
</template>
<script lang="ts" setup>
import { ref } from 'vue';

const scrollList = ref([
	{
		avatar: '浦东',
		address: '上海浦东分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 12
	},
	{
		avatar: '徐汇',
		address: '上海徐汇分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 1
	},
	{
		avatar: '成都',
		address: '四川成都分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 19
	},
	{
		avatar: '武汉',
		address: '湖北武汉分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 18
	},
	{
		avatar: '长沙',
		address: '湖南长沙分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 21
	},
	{
		avatar: '宝山',
		address: '上海宝山分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 1
	},
	{
		avatar: '重庆',
		address: '重庆分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 14
	},
	{
		avatar: '四川乐山',
		address: '四川乐山分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 42
	},
	{
		avatar: '松江',
		address: '上海松江分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 15
	},
	{
		avatar: '合肥',
		address: '安徽合肥分店',
		goodsInfo: '恭喜成功卖出1314元商品',
		number: 34
	}
]);
</script>

<style lang="scss" scoped>
.scroll-item {
	height: 70px;
	background-color: #f0f2f5;
	border-radius: 5px;

	.avatar {
		width: 42px;
		height: 42px;
		line-height: 42px;
		color: #fff;
		text-align: center;
		background-color: #c0c4cc;
		border-radius: 100%;
	}

	.address {
		font-size: 16px;
		color: #000;
	}

	.goods-info {
		font-size: 14px;
		color: #8c96a7;
	}

	.time {
		font-size: 14px;
		color: #8c96a7;
	}
}
</style>
