<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps({
	/* icon 图标名称 */
	iconClass: {
		type: String,
		required: true
	},
	/* icon 图标类名 */
	className: {
		type: String,
		default: ''
	},
	/* icon 图标颜色 */
	iconColor: {
		type: String,
		default: 'CurrentColor'
	}
});
const iconName = computed(() => `${props.iconClass}`);
const iconColor = computed(() => `${props.iconColor}`);

const svgClass = computed(() => {
	console.log("   iconName: " + iconName.value);
	if (props.className) {
		return 'svg-icon ' + props.className;
	} else {
		return 'svg-icon';
	}
});
</script>
<template>
	<el-icon>
		<component :class="svgClass" :style="{ '--icon-color': iconColor }" :is="iconName"></component>
	</el-icon>
	<!-- <svg :class="svgClass" aria-hidden="true" :fill="iconColor">
		<use :xlink:href="iconName" />
	</svg> -->
</template>
<!-- iconColor 的值将作为 CSS 变量 --icon-color 传递给样式，并在 .svg-icon 类中使用 var(--icon-color) 引用它。 -->
<style scoped>
.svg-icon {
	width: 40px;
	height: 40px;
	color: var(--icon-color); 
	padding-top: 5px;

	fill: currentcolor;
}
</style>
