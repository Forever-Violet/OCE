<template>
  <el-drawer v-model="visible" :title="!dataForm.id ? '新增配置' : '修改配置'" :close-on-click-modal="true"
    :close-on-press-escape="false" size="35%">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" @keyup.enter="dataFormSubmitHandle()"
      label-width="150px">
      <el-form-item label="配置名称" prop="configName">
        <el-input style="width:310px;" v-model="dataForm.configName" placeholder="配置名称"></el-input>
      </el-form-item>
      <el-form-item label="种群大小" prop="populationSize">
        <el-input-number v-model="dataForm.populationSize" style="width:210px;margin-right: 15px;"
         :min="10" :max="500" :disabled="dataForm.id != ''"></el-input-number>
        <el-tooltip 
                content="种群大小指的是组卷过程中每一次种群迭代中包含的考卷数量。" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="最大迭代次数" prop="maxIteratorNum">
        <el-input-number v-model="dataForm.maxIteratorNum" style="width:210px;margin-right: 15px;"
         :min="10" :max="1000" :disabled="dataForm.id != ''"></el-input-number>
        <el-tooltip 
                content="当种群进化达到最大迭代次数时，将结束迭代，返回最符合预期的考卷。" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="交叉概率" prop="crossoverRate">
        <el-input-number v-model="dataForm.crossoverRate" style="width:210px;margin-right: 15px;"
         :step="0.01" :precision="2" :min="0" :max="1" :disabled="dataForm.id != ''"></el-input-number>
        <el-tooltip 
                content="交叉概率指的是在种群进化过程中，个体由上一代种群的两个个体交叉产生的概率。" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="变异概率" prop="mutationRate">
        <el-input-number v-model="dataForm.mutationRate" style="width:210px;margin-right: 15px;"
         :step="0.01" :precision="2" :min="0" :max="1" :disabled="dataForm.id != ''"></el-input-number>
        <el-tooltip 
                content="变异概率指的是在种群进化过程中，每个考卷个体的每一道题目被替换的概率。" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="期望适应度" prop="expectedFitness">
        <el-input-number v-model="dataForm.expectedFitness" style="width:210px;margin-right: 15px;" 
        :step="0.01" :precision="2" :min="0" :max="1" :disabled="dataForm.id != ''"></el-input-number>
        <el-tooltip 
                content="期望适应度越小，组卷的期望准确度越高。适应度最优值为0。" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>

      <el-form-item label="适用范围" prop="scope">
          <ren-radio-group :disabled="dataForm.id != ''" v-model="dataForm.scope" dict-type="examGenerateParamsScope"></ren-radio-group>
      </el-form-item>

    </el-form>
    <template v-slot:footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">确定</el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import baseService from "@/service/baseService";
import { ElMessage } from "element-plus";
const emit = defineEmits(["refreshDataList"]);

const visible = ref(false);
const dataFormRef = ref();

const dataForm = reactive({
  id: '', configName: '', populationSize: '', maxIteratorNum: '', mutationRate: '', crossoverRate: '', expectedFitness: '', accuracy: '', useTimes: '', scope: '', createDate: '', creator: ''
});

const rules = ref({
  populationSize: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  maxIteratorNum: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  mutationRate: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  crossoverRate: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  expectedFitness: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  configName: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  scope: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
});

const init = (id?: number) => {
  visible.value = true;
  dataForm.id = "";
  dataForm.configName = "";
  dataForm.populationSize = "100";
  dataForm.maxIteratorNum = "100";
  dataForm.mutationRate = "0";
  dataForm.crossoverRate = "0";
  dataForm.expectedFitness = "0";
  dataForm.accuracy = "";
  dataForm.useTimes = "";
  dataForm.scope = "0";
  if (id) {
    getInfo(id);
  }
};

// 获取信息
const getInfo = (id: number) => {
  baseService.get("/exam/generateparams/" + id).then((res) => {
    Object.assign(dataForm, res.data);
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/exam/generateparams", dataForm).then((res) => {
      ElMessage.success({
        message: '成功',
        duration: 500,
        onClose: () => {
          visible.value = false;
          emit("refreshDataList");
        }
      });
    });
  });
};

defineExpose({
  init
});
</script>
