<template>
  <el-drawer v-model="visible" title="考卷导出" 
             :close-on-press-escape="false" size="40%" destroy-on-close >
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef"
             label-width="150px">
      <el-form-item label="是否显示题目分数" prop="scoreFlag">
        <el-switch
          v-model="dataForm.scoreFlag"
          active-value="true"
          inactive-value="false"
          :active-action-icon="View"
          :inactive-action-icon="Hide"
          inline-prompt
          active-text="是"
          inactive-text="否"
        />
        
      </el-form-item>

      <el-form-item label="" prop="scoreFlag">
        <el-collapse style="width: 100%" v-model="scoreCollapse">
          <el-collapse-item name="1">
            <template #title>
              效果展示<el-icon class="header-icon">
                <info-filled />
                </el-icon>
            </template>
            <!-- <el-image
            src="src\views\exam\images\scoreFlag.png"
            fit="cover"
            /> -->
            
            <el-image
            :src="'data:image/png;base64,'+scoreFlagBase64"
            fit="cover"
            /> 
          </el-collapse-item>
        </el-collapse>
      </el-form-item>


      <el-form-item label="是否显示题目答案" prop="answerFlag">
        <el-switch
          v-model="dataForm.answerFlag"
          inline-prompt
          active-value="true"
          inactive-value="false"
          :active-action-icon="View"
          :inactive-action-icon="Hide"
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>

      <el-form-item label="" prop="answerFlag">
        <el-collapse style="width: 100%" v-model="answerCollapse">
          <el-collapse-item name="1">
            <template #title>
              效果展示<el-icon class="header-icon">
                <info-filled />
                </el-icon>
            </template>
            <!-- <el-image
            src="src\views\exam\images\answerFlag.png"
            fit="cover"
            /> -->
            <el-image
            :src="'data:image/png;base64,'+answerFlagBase64"
            fit="cover"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form-item>

      <el-form-item label="是否显示题目解析" prop="analysisFlag">
        <el-switch
          v-model="dataForm.analysisFlag"
          inline-prompt
          active-value="true"
          inactive-value="false"
          :active-action-icon="View"
          :inactive-action-icon="Hide"
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>

      <el-form-item label="" prop="analysisFlag">
        <el-collapse style="width: 100%" v-model="answerCollapse">
          <el-collapse-item name="1">
            <template #title>
              效果展示<el-icon class="header-icon">
                <info-filled />
                </el-icon>
            </template>
            <!-- <el-image
            src="src\views\exam\images\analysisFlag.png"
            fit="cover"
            /> -->
            <el-image
            :src="'data:image/png;base64,'+analysisFlagBase64"
            fit="cover"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form-item>


      <el-form-item label="是否显示试卷介绍" prop="remarkFlag">
        <el-switch
          v-model="dataForm.remarkFlag"
          inline-prompt
          active-value="true"
          inactive-value="false"
          :active-action-icon="View"
          :inactive-action-icon="Hide"
          active-text="是"
          inactive-text="否"
        />
      </el-form-item>

      <el-form-item label="" prop="remarkFlag">
        <el-collapse style="width: 100%" v-model="remarkCollapse">
          <el-collapse-item name="1">
            <template #title>
              效果展示<el-icon class="header-icon">
                <info-filled />
                </el-icon>
            </template>
            <!-- <el-image
            src="src\views\exam\images\remarkFlag.png"
            fit="cover"
            /> -->
            <el-image
            :src="'data:image/png;base64,'+remarkFlagBase64"
            fit="cover"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form-item>


    </el-form>
    <template v-slot:footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="exportExamPaperWord()">导出Word格式</el-button>
      <el-button type="success" @click="exportExamPaperPDF()">导出PDF格式</el-button>

      <el-button type="primary" plain @click="exportExamPaperAnswerWord()">导出纯答案版本</el-button>

    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import {reactive, ref, toRefs} from "vue";
import baseService from "@/service/baseService";
import {ElMessage} from "element-plus";
import axios, { AxiosResponse } from "axios";
import { getToken } from "@/utils/cache";
import app from "@/constants/app";
import qs from "qs";
import { Hide, View } from '@element-plus/icons-vue'
import useView from "@/hooks/useView";

//const emit = defineEmits(["refreshDataList"]);

const visible = ref(false);
const dataFormRef = ref();
const exportWordUrl = ref("");

const dataForm = reactive({
  id: "",
  scoreFlag: "",
  remarkFlag: "",
  answerFlag: "",
  analysisFlag: "",
});
const state = reactive({ ...useView(dataForm), ...toRefs(dataForm) });
const rules = ref({
  scoreFlag: [
    {required: true, message: '必选项不能为空', trigger: 'blur'}
  ],
  remarkFlag: [
    {required: true, message: '必选项不能为空', trigger: 'blur'}
  ],
  answerFlag: [
    {required: true, message: '必选项不能为空', trigger: 'blur'}
  ],
  analysisFlag: [
    {required: true, message: '必选项不能为空', trigger: 'blur'}
  ],

});

const init = (id: string) => {
  visible.value = true;
  dataForm.id = id;
  dataForm.scoreFlag = "true"; //默认显示
  dataForm.remarkFlag = "true"; //默认显示
  dataForm.answerFlag = "";
  dataForm.analysisFlag = "";
  // 重置表单数据
  if (dataFormRef.value) {
    dataFormRef.value.resetFields();
  }

};

// 导出word
const exportExamPaperWord = () => {
  exportWordUrl.value = `${app.api}/exam/epexampaper/export/word`;
  const downloadUrl = `${exportWordUrl.value}?${qs.stringify({
    ...dataForm,
    token: getToken()
  })}`;

  // 创建一个隐藏的可下载链接
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.style.display = 'none';
  //link.target = '_blank'; // 在新的页面中打开链接
  document.body.appendChild(link);

  // 触发下载并在成功后显示 ElMessage
  link.addEventListener('click', () => {
    ElMessage.success('导出成功');
  });
  link.click();
  // 删除掉
  document.body.removeChild(link);
  // 发送POST请求
  // axios.post('您的API端点', dataForm, {
  //   responseType: 'blob', // 重要：设置响应类型为blob处理二进制文件
  //   headers: {
  //     'Authorization': `Bearer ${getToken()}` // 如果需要的话
  //   }
  // })
  //   .then((response: AxiosResponse) => {
  //     // 创建一个URL对象表示来自服务器响应数据的Blob对象
  //     const url = window.URL.createObjectURL(new Blob([response.data]));
  //     // 创建一个链接元素
  //     const link = document.createElement('a');
  //     link.href = url;
  //     link.setAttribute('download', 'file.docx'); // 或者您想要的文件名
  //     // 将链接元素添加到DOM中
  //     document.body.appendChild(link);
  //     // 触发下载
  //     link.click();
  //     // 清理并移除链接元素
  //     link.parentNode.removeChild(link);
  //   })
  //   .catch(error => console.error('下载错误:', error));
};
// 导出PDF
const exportExamPaperAnswerWord = () => {
  exportWordUrl.value = `${app.api}/exam/epexampaper/export/answer`;
  const downloadUrl = `${exportWordUrl.value}?${qs.stringify({
    ...dataForm,
    token: getToken()
  })}`;

  // 创建一个隐藏的可下载链接
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.style.display = 'none';
  document.body.appendChild(link);

  // 触发下载并在成功后显示 ElMessage
  link.addEventListener('click', () => {
    ElMessage.success('导出成功');
  });
  link.click();
  // 删除掉
  document.body.removeChild(link);
}

// 导出纯答案
const exportExamPaperPDF = () => {
  exportWordUrl.value = `${app.api}/exam/epexampaper/export/pdf`;
  const downloadUrl = `${exportWordUrl.value}?${qs.stringify({
    ...dataForm,
    token: getToken()
  })}`;

  // 创建一个隐藏的可下载链接
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.style.display = 'none';
  document.body.appendChild(link);

  // 触发下载并在成功后显示 ElMessage
  link.addEventListener('click', () => {
    ElMessage.success('导出成功');
  });
  link.click();
  // 删除掉
  document.body.removeChild(link);
}


defineExpose({
  init
});

const scoreCollapse = [""];
const answerCollapse = [""];
const analysisCollapse = [""];
const remarkCollapse = [""];

const scoreFlagBase64 = "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";
const answerFlagBase64 = "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"; 
const analysisFlagBase64 = "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";
const remarkFlagBase64 = "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";
</script>

<style lang="less" scoped>

</style>
