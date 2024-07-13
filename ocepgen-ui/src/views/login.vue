<template>
  <div class="rr-login">
    <div class="rr-login-wrap">
      <div class="rr-login-left hidden-sm-and-down">
        <p class="rr-login-left-title">一键生成考卷系统</p>
        <p class="rr-login-left-content">一键生成考卷系统的主要目标是简化和自动化教师或教育机构在出卷过程中的工作，提高出卷效率，并保持试卷质量</p>
      </div>
      <!-- 粒子背景插件 -->
      <Particles
        id="tsparticles" 
        class="login__particles" 
        :options="particles_options"  
      />


            <div class="rr-login-right">
              <Transition  name="slide-up" mode="out-in" >
              <div class="rr-login-right-main" v-if="!forgetPasswordFlag" key="form-login">
                <h4 class="rr-login-right-main-title">欢迎登录</h4>
                <el-form ref="formRef" label-width="80px" :status-icon="true" :model="login" :rules="rules" @keyup.enter="onLogin">
                  <el-form-item prop="schoolId" label-width="0">
                    <el-select v-model="login.schoolId" filterable placeholder="选择学校" clearable style="width:100%">
                      <template #prefix>
                        <el-icon>
                          <School />
                        </el-icon>
                      </template>
                      <el-option v-for="school in schoolList" :key="school.schoolId" :label="school.schoolName" :value="school.schoolId"></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label-width="0" prop="username">
                    <el-input v-model="login.username" placeholder="用户名" prefix-icon="user" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label-width="0" prop="password">
                    <el-input placeholder="密码" v-model="login.password" prefix-icon="lock" autocomplete="off" show-password></el-input>
                  </el-form-item>
                  <el-form-item label-width="0" prop="captcha">
                    <el-space class="rr-login-right-main-code">
                      <el-input v-model="login.captcha" placeholder="验证码" prefix-icon="first-aid-kit"></el-input>
                      <img style="vertical-align: middle; height: 40px; cursor: pointer" :src="state.captchaUrl" @click="onRefreshCode" alt="" />
                    </el-space>
                  </el-form-item>
                  <el-form-item label-width="0">
                    <el-button type="primary" size="small" :disabled="state.loading" @click="onLogin" class="rr-login-right-main-btn"> 登录 </el-button>
                  </el-form-item>
                  <el-form-item label-width="0">
                    <el-button link @click="forgetPasswordFlag = true;">忘记密码？</el-button>
                  </el-form-item>

                </el-form>
              </div>

              <div class="rr-login-right-main" v-else key="form-reset-password">
                <h4 class="rr-login-right-main-title">重置密码</h4>
                <el-form ref="formRef" label-width="80px" :status-icon="true" :model="resetPassword" 
                 @keyup.enter="onResetPassword">

                  <el-form-item label-width="0" prop="email">
                    <el-input placeholder="邮箱" v-model="resetPassword.email" prefix-icon="message" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label-width="0" prop="password">
                    <el-input placeholder="密码" v-model="resetPassword.password" prefix-icon="lock" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label-width="0" prop="captcha">
                    <el-space class="rr-login-right-main-code">
                      <el-input v-model="resetPassword.captcha" placeholder="输入邮箱验证码" style="margin-right: 20px" prefix-icon="first-aid-kit"></el-input>
                      <el-button type="success" style="width:110px;" @click="postCaptchaToEmail()" :disabled="isDisposed">
                        {{ isDisposed ? `${time}s后重新获取` : "获取验证码" }}
                      </el-button>
                    </el-space>
                  </el-form-item>
                  <el-form-item label-width="0">
                    <el-button type="primary" size="small" :disabled="state.loading" @click="onResetPassword()" class="rr-login-right-main-btn"> 重置 </el-button>
                  </el-form-item>
                  <el-button link @click="forgetPasswordFlag = false;getCaptchaUrl();" >返回登录</el-button>

                </el-form>
              </div>
            </Transition>
            </div>


    </div>

    <div class="login-footer">
      <p><a href="https://github.com/Forever-Violet" target="_blank">佛山大学</a>{{ state.year }} © ocepgen.io</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, toRefs, onBeforeMount } from 'vue';
import { CacheToken } from "@/constants/cacheKey";
import baseService from "@/service/baseService";
import { setCache } from "@/utils/cache";
import { ElMessage } from "element-plus";
import { getUuid } from "@/utils/utils";
import app from "@/constants/app";
import useView from "@/hooks/useView";
import SvgIcon from "@/components/base/svg-icon/index";
import { useAppStore } from "@/store";
import { useRouter } from "vue-router";
import { isEmail } from "@/utils/utils";

const store = useAppStore();
const router = useRouter();
const login = reactive({ schoolId: "", username: "", password: "", captcha: "", uuid: "" });
const resetPassword = reactive({ password: "", email: "", captcha: "" });
const forgetPasswordFlag = ref(false);
const state = reactive({
  captchaUrl: "",
  loading: false,
  year: new Date().getFullYear(),
  ...useView(login), 
  ...toRefs(login)
});
const time = ref(60); //验证码发送后倒计时
const isDisposed = ref(false); // 是否成功发送

const handleCountdown = () => {
  if (time.value <= 0) {
    isDisposed.value = false;
    time.value = 60;
  } else {
    setTimeout(() => {
      time.value--;
      handleCountdown();
    }, 1000);
  }
};


onMounted(() => {
  //清理数据
  store.logout();
  getCaptchaUrl();


});

onBeforeMount(() => {
  getSchoolList();
})
const formRef = ref();


const rules = ref({
  username: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  schoolId: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  password: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  captcha: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
});

const schoolList = ref<any[]>([]);


// 获取学校列表
const getSchoolList = () => {
  return baseService.get("/sys/school/listInLogin").then((res) => {
    schoolList.value = res.data;
  });
};



const getCaptchaUrl = () => {
  login.uuid = getUuid();
  state.captchaUrl = `${app.api}/captcha?uuid=${login.uuid}`;
};

const onRefreshCode = () => {
  getCaptchaUrl();
};

const onLogin = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      state.loading = true;
      baseService
        .post("/login", login)
        .then((res) => {
          state.loading = false;
          if (res.code === 0) {
            setCache(CacheToken, res.data, true);
            ElMessage.success("登录成功");
            router.push("/");
          } else {
            ElMessage.error(res.msg);
          }
        })
        .catch(() => {
          state.loading = false;
          onRefreshCode();
        });
    }
  });
};

const onResetPassword = () => {
  if (!isEmail(resetPassword.email)) {
    ElMessage.error("邮箱格式错误");
    return;
  } else if(resetPassword.password == '') {
    ElMessage.error("密码不能为空");
    return;
  } else if(resetPassword.captcha == '') {
    ElMessage.error("验证码不能为空");
    return;
  }

  state.loading = true;
  baseService
    .post("/resetPassword", resetPassword)
    .then((res) => {
      state.loading = false;
      if (res.code === 0) {
        ElMessage.success("重置密码成功，正在前往登录");
        forgetPasswordFlag.value = false;
        //重新获取图形验证码
        getCaptchaUrl();
      } else {
        ElMessage.error(res.msg);
      }
    })
    .catch(() => {
      state.loading = false;
    });

};

// 请求后端 发送 验证码 至 用户邮箱
const postCaptchaToEmail = () => {

    if (isEmail(resetPassword.email)) {
      baseService
        .post("/postCaptchaToEmail", resetPassword)
        .then((res) => {
          if (res.code === 0) {
            ElMessage.success(res.msg);
            isDisposed.value = true; //发送成功，60秒后可再点击
            handleCountdown(); //开始计时
          } else {
            ElMessage.error(res.msg);
          }
        })
        .catch(() => {
          
        });
    } else {
      ElMessage.error("邮箱格式错误");
    }
};

const particles_options = {
  fpsLimit: 60,
  interactivity: {
    detectsOn: 'canvas',
    events: {
      onClick: { // 开启鼠标点击的效果
        enable: true,
        mode: 'push'
      },
      onHover: { // 开启鼠标悬浮的效果(线条跟着鼠标移动)
        enable: true,
        mode: 'grab'
      },
      resize: true
    },
    modes: { // 配置动画效果
      bubble: {
        distance: 400,
        duration: 2,
        opacity: 0.8,
        size: 40
      },
      push: {
        quantity: 4
      },
      grab: {
        distance: 200,
        duration: 0.4
      },
      attract: { // 鼠标悬浮时，集中于一点，鼠标移开时释放产生涟漪效果
        distance: 200,
        duration: 0.4,
        factor: 5
      }
    }
  },
  particles: {
    color: {
      value: '#339AFA' // 粒子点的颜色
    },
    links: {
      color: '#2BA0FF', // 线条颜色
      distance: 150,//线条距离
      enable: true,
      opacity: 0.4, // 不透明度
      width: 1.2 // 线条宽度
    },
    collisions: {
      enable: true
    },
    move: {
      attract: { enable: false, rotateX: 600, rotateY: 1200 },
      bounce: false,
      direction: 'none',
      enable: true,
      out_mode: 'out',
      random: false,
      speed: 0.5, // 移动速度
      straight: false
    },
    number: {
      density: {
        enable: true,
        value_area: 800
      },
      value: 80//粒子数
    },
    opacity: {//粒子透明度
      value: 0.7
    },
    shape: {//粒子样式
      type: 'star'
    },
    size: {//粒子大小
      random: true,
      value: 3
    }
  },
  detectRetina: true
}

</script>

<style lang="less" scoped>
@import url("@/assets/theme/base.less");
.rr-login {
  width: 100vw;
  height: 100vh;
  //background: #019ec4;  
  background: linear-gradient(135deg, #c7e9fb, #a6defa, #80d4f9, #5bc9f8, #35bef7);
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  @media only screen and (max-width: 992px) {
    .rr-login-wrap {
      width: 96% !important;
    }
    .rr-login-right {
      width: 80% !important;
    }
  }

  &-wrap {
    margin: 0 auto;
    width: 1000px;
    box-shadow: -4px 5px 10px rgba(0, 0, 0, 0.4);
    animation-duration: 1s;
    animation-fill-mode: both;
    border-radius: 5px;
    overflow: hidden;
  }

  &-left {
    justify-content: center;
    flex-direction: column;
    background-color: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
    color: #fff;
    float: left;
    width: 50%;

    &-title {
      text-align: center;
      color: #FFFFFF;
      font-weight: 520;
      letter-spacing: 2px;
      font-size: 32px;
    }

    &-content {
      text-align: center;
      color: #FFFFFF;
      font-weight: 420;
      letter-spacing: 2px;
      font-size: 16px;
      
    }
  }

  &-right {
    border-left: none;
    color: #fff;
    //background-color: #fff;linear-gradient(135deg, #001f3f, #0088a9, #00c9a7, #92d5c6, #ebf5ee);
    background: linear-gradient(120deg, #89f7fe 0%, #66a6ff 100%);
    width: 50%;
    float: left;
    position: relative;

    &-main {
      margin: 15%;
      width: 70%;
      position: absolute;
      &-title {
        color: #54575E; 
        margin-bottom: 40px;
        font-weight: 510;
        font-size: 24px;
        text-align: center;
        letter-spacing: 4px;
      }

      &-lang .iconfont {
        font-size: 20px;
        color: #606266;
        font-weight: 800;
        width: 20px;
        height: 20px;
      }

      .el-input__inner {
        border-width: 0;
        border-radius: 0;
        border-bottom: 1px solid #dcdfe6;
      }

      &-code {
        width: 100%;
        .el-space__item:first-child {
          flex: 1;
        }
      }
      &-btn {
        width: 100%;
        height: 45px;
        font-size: 18px !important;
        letter-spacing: 2px;
        font-weight: 300 !important;
        cursor: pointer;
        margin-top: 10px;
        font-family: neo, sans-serif;
        transition: 0.25s;
      }
    }
  }

  .login-footer {
    text-align: center;
    position: absolute;
    bottom: 0;
    padding: 20px;
    color: rgba(255, 255, 255, 0.6);
    p {
      margin: 10px 0;
    }
    a {
      padding: 0 5px;
      color: rgba(255, 255, 255, 0.6);
      &:focus,
      &:hover {
        color: #fff;
      }
    }
  }

  &-left,
  &-right {
    position: relative;
    min-height: 425px;
    align-items: center;
    display: flex;
  }

  @keyframes animate-down {
    0%,
    60%,
    75%,
    90%,
    to {
      animation-timing-function: cubic-bezier(0.215, 0.61, 0.355, 1);
    }
    0% {
      opacity: 0;
      transform: translate3d(0, -3000px, 0);
    }
    60% {
      opacity: 1;
      transform: translate3d(0, 25px, 0);
    }
    75% {
      transform: translate3d(0, -10px, 0);
    }
    90% {
      transform: translate3d(0, 5px, 0);
    }
    to {
      transform: none;
    }
  }

  .animate-down {
    animation-name: animate-down;
  }
}

.login__particles {
    height: 100%;
    width: 100%;
    background-size: cover;
    background-repeat: no-repeat;
    //background-image: url('@/assets/0001.jpg');
    opacity:0.9;
    position:fixed;
     pointer-events: none;
}


/*
  进入和离开动画可以使用不同
  持续时间和速度曲线。
*/
// .slide-fade-enter-active {
//   transition: all 0.3s ease-out;
// }

// .slide-fade-leave-active {
//   transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
// }

// .slide-fade-enter-from,
// .slide-fade-leave-to {
//   transform: translateX(20px);
//   opacity: 0;
// }
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.35s ease-out;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

.bounce-enter-active {
  animation: bounce-in 0.5s;
}
.bounce-leave-active {
  animation: bounce-in 0.5s reverse;
}
@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.25);
  }
  100% {
    transform: scale(1);
  }
}
</style>
