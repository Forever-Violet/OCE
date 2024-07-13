import Layout from "@/layout/layout.vue";
import Error from "@/views/error.vue";
import { RouteRecordRaw } from "vue-router";
import Login from "@/views/login.vue";
import Iframe from "@/views/iframe.vue";

/**
 * 框架基础路由
 */
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/home",
    component: Layout,
    meta: { title: "主页", isNavigationMenu: false },
    // children: [
    //   {
    //     path: "/home",
    //     component: () => import("@/views/home/home.vue"),
    //     meta: { title: "主页", icon: "icon-home" }
    //   }
    // ]
  },
  {
    path: "/home",
    component: () => import("@/views/home/home.vue"),
    meta: { title: "主页", icon: "icon-home" }
  },
  {
    path: "/login",
    component: Login,
    meta: { title: "登录", isNavigationMenu: false }
  },
  {
    path: "/user/password",
    component: () => import("@/views/sys/user-update-password.vue"),
    meta: { title: "修改密码", requiresAuth: true, isNavigationMenu: false }
  },
  {
    path: "/iframe/:id?",
    component: Iframe,
    meta: { title: "iframe", isNavigationMenu: false }
  },
  {
    path: "/error",
    name: "error",
    component: Error,
    meta: { title: "错误页面", isNavigationMenu: false }
  }
];

export const errorRoute: Array<RouteRecordRaw> = [
  {
    path: "/:path(.*)*",
    redirect: { path: "/error", query: { to: 404 }, replace: true },
    meta: { isNavigationMenu: false }
  }
];

export default routes;
