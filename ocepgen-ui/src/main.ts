import "@/assets/icons/iconfont/iconfont.js";
import RenRadioGroup from "@/components/ren-radio-group";
import RenRegionTree from "@/components/ren-region-tree";
import RenSelect from "@/components/ren-select";
import ElementPlus from "element-plus";
import "element-plus/theme-chalk/display.css";
import "element-plus/theme-chalk/index.css";
import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import * as ElementPlusIcons from "@element-plus/icons-vue";
import locale from "element-plus/es/locale/lang/zh-cn";
import Particles from "particles.vue3";
import axios from "axios";
import "virtual:svg-icons-register";
import '@arco-design/web-vue/dist/arco.css';
import ArcoVue from '@arco-design/web-vue';
// svg 相关
import 'virtual:svg-icons-register';
// @ts-ignore
import ReSvg from './components/ReSvg/index.vue';

const app = createApp(App);
Object.keys(ElementPlusIcons).forEach((iconName) => {
  app.component(iconName, ElementPlusIcons[iconName as keyof typeof ElementPlusIcons])
});
app.component('svg-icon', ReSvg);

app
  .use(createPinia())
  .use(router)
  .use(ArcoVue)
  .use(RenRadioGroup)
  .use(RenSelect)
  .use(RenRegionTree)
  .use(Particles)
  .use(ElementPlus, { size: "default", locale: locale })
  .mount("#app");

window.axios = axios;
