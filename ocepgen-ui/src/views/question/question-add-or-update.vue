<template>

  <el-drawer v-model="visible"
  :title="dataForm.id == '' ? '新增'+state.getDictLabel('questionType', Number(dataForm.type)) : '修改'+state.getDictLabel('questionType', Number(dataForm.type))"
  :close-on-click-modal="false" direction="ttb"
    :close-on-press-escape="false" size="100%" destroy-on-close> <!--destroy-on-close这个要写上，关闭时销毁组件-->
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef"
      label-width="120px">

      <el-form-item prop="courseId" label="课程">
        <el-select v-model="dataForm.courseId" filterable placeholder="选择课程" clearable style="width: 20%;">
          <el-option v-for="course in courseList" :key="course.id" :label="course.courseName" :value="course.id"></el-option>
        </el-select>


        <el-radio-group v-model="dataForm.type" @change="init(1, dataForm.type, ()=>{}, dataForm.courseId)" style="position: absolute; right: 0%">
          
          <el-text type="success" size="large" style="margin-right: 10px">
            <el-icon><Pointer /></el-icon>
            切换题型
          </el-text>

          <el-radio border v-for="type in typeList" 
          :key="type.dictValue" 
          :label="type.dictValue"  
          style="margin-right: 5px;" 
          :disabled="dataForm.id != ''">
            {{ type.dictLabel }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="题干" prop="content">
        <div style="width:100%">
          <editor v-model="dataForm.content" maxLength="20000" placeholder="题目内容" ></editor>
        </div>

        <!-- <TextEditor @blur="$emit('editorBlur', 'question')" :mode="getEditMode()" v-model="dataForm.content"></TextEditor> -->
      </el-form-item>

      <!-- 题目类型   0：单选   1：多选   2：判断   3：填空   4：主观 -->
      <!-- <el-form-item :label="state.getDictLabel('questionType', Number(dataForm.type))"> -->
      <el-form-item prop="options" :label="(dataForm.type=='0'||dataForm.type=='1')?'选项':'答案'">
        <div v-if="dataForm.type == '0'" style="width:100%">
          <div v-for="(option, index) in dataForm.options" :key="index">
            <editor v-model="option.content" maxLength="10000" :style="'height: 100px'" placeholder="自定义选项" ></editor>
            <el-checkbox label="正确" style="left: 3px;margin-top:5px;margin-bottom:5px" v-model="option.answer" @click="unCheckOtherOptions(index)" :checked="option.answer != ''"></el-checkbox>
            <el-button v-if="dataForm.options.length > 1" @click="removeOption(index)" type="danger" class="custom-button-remove" :icon="Delete" circle />
          </div>
          <el-button @click="addOption(dataForm.options.length)" type="success" class="custom-button" :icon="Plus" circle style="margin-left: -12px"></el-button>
        </div>

        <div v-if="dataForm.type == '1'" style="width:100%">
          <div v-for="(option, index) in dataForm.options" :key="index">
            <editor v-model="option.content" placeholder="自定义选项" :style="'height: 100px'"></editor>
            <el-checkbox label="正确" style="left: 3px;margin-top:5px;margin-bottom:5px" v-model="option.answer" :checked="option.answer != ''"></el-checkbox>
            <el-button v-if="dataForm.options.length > 1" @click="removeOption(index)" type="danger" class="custom-button-remove" :icon="Delete" circle />
          </div>
          <el-button @click="addOption(dataForm.options.length)" type="success" class="custom-button" :icon="Plus" circle style="margin-left: -12px"></el-button>
        </div>

        <div v-if="dataForm.type == '2'" style="width:100%">
          <div v-for="(option, index) in dataForm.options" :key="index">
            <el-radio label="正确" style="left: 3px;" value="" v-model="option.answer"></el-radio>
            <el-radio label="错误" style="left: 3px;" value="" v-model="option.answer"></el-radio>
          </div>
        </div>

        <div v-if="dataForm.type == '3'" style="width:100%">
          <div v-for="(option, index) in dataForm.options" :key="index">
            <sapn>第 {{ index + 1 }} 空</sapn>
            <editor v-model="option.answer" placeholder="请填写答案" :style="'height: 100px'"></editor>
            <el-button v-if="dataForm.options.length > 1" @click="removeOption(index)" type="danger" class="custom-button-remove" :icon="Delete" circle></el-button>
          </div>
          <el-button @click="addOption(dataForm.options.length)" type="success" class="custom-button" :icon="Plus" circle ></el-button>

        </div>

        <div v-if="dataForm.type == '4'" style="width:100%">
          <div v-for="(option, index) in dataForm.options" :key="index">
            <editor v-model="option.answer" placeholder="请填写答案" ></editor>
          </div>
        </div>

      </el-form-item>


      <el-form-item label="难度" prop="difficulty">
        <!-- "题目难度   1 ~ 5  " -->
        <el-rate v-model="dataForm.difficulty" :colors="['#CFD146', '#F7BA2A', '#FF9900']" count="5" disable="false"  />
      </el-form-item>

      <el-form-item label="解析" prop="analysis">
        <!-- <editor v-model="dataForm.analysis"></editor> -->
          <div style="width:100%">
            <editor v-model="dataForm.analysis" placeholder="题目解析，限制8000字" max-length="8000"></editor>
          </div>
      </el-form-item>

      <el-form-item label="适用范围" prop="scope">
        <ren-radio-group v-model="dataForm.scope" dict-type="questionScope"></ren-radio-group>
      </el-form-item>

      <el-form-item label="分值" prop="score">
        <el-input-number v-model="dataForm.score" style="width: 130px;" :min="0" :max="100" size="default" :step="1"></el-input-number>
      </el-form-item>
    </el-form>
    <template v-slot:footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">确定</el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, toRefs, } from "vue";
import baseService from "@/service/baseService";
import { ElMessage } from "element-plus";
import editor from '../../components/wang-editor/editor.vue';
import {
  CirclePlus,
  Plus,
  Delete,
} from '@element-plus/icons-vue'
import { time } from "echarts";
import { getDictDataList } from "@/utils/utils";
const emit = defineEmits(["refreshDataList"]);
import { useAppStore } from "@/store";

const visible = ref(false);
const dataFormRef = ref();
const courseList = ref<any[]>([]);
const dataForm = reactive({
  id: '',
  content: '',
  type: '',
  analysis: '',
  difficulty: '',
  scope: '',
  score: '',
  courseId: '',
  createDate: '',
  options: [] as {
    id: string; content: string; answer: string;
  }[],

});
const state = reactive({ ...useView(dataForm), ...toRefs(dataForm) });
const store = useAppStore();
const typeList = getDictDataList(store.state.dicts, "questionType");

const rules = ref({
  content: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  // analysis: [
  //   { required: true, message: '必填项不能为空', trigger: 'blur' }
  // ],
  options: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  difficulty: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  scope: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  score: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  courseId: [
     { required: true, message: '必填项不能为空', trigger: 'blur' }
   ],
});

// 获取年级列表
const getCourseList = () => {

  return baseService.get("/course/course/list").then((res) => {
    courseList.value = res.data;
  });
};


const init = (id?: number, questionType?: string, callback?: () => void, courseId?: string) => {
  //等请求的数据响应成功再出现页面
  //visible.value = true;
  dataForm.id = "";
  dataForm.content = "";
  dataForm.type = "";
  dataForm.analysis = "";
  dataForm.difficulty = "";
  dataForm.scope = "0"; //默认公共
  dataForm.score = "5"; //默认5分
  dataForm.courseId = courseId == null? "" : courseId;
  dataForm.options = [];
  //dataForm.options.splice(0, dataForm.options.length);

  // 填充题目类型
  if (questionType != null && questionType != '') {
    dataForm.type = questionType;
  }

  // 重置表单数据，这东西没用，反而会使重置失效
  // if (dataFormRef.value) {
  //   dataFormRef.value.resetFields();
  // }
  getCourseList();

  if (id && id != 1) {
    getInfo(id, () => {
      // getInfo 执行完毕，调用回调函数
      if (typeof callback === 'function') {
        callback();
      }
    });
  } else {
    // 新增直接调用回调，取消缓冲动画
    if (typeof callback === 'function') {
      callback();
    }
    initOption();
  }


};

const addOption = (length: number) => {
  const letters = ['<p>A、</p>', '<p>B、</p>', '<p>C、</p>', '<p>D、</p>', '<p>E、</p>', '<p>F、</p>'];

  const letter = letters[length];
  // 选择题和填空题分开处理
  (dataForm.type == '0' || dataForm.type == '1')
  ? dataForm.options.splice(length + 1, 0, { id: "", content: letter, answer: "" })
  : dataForm.options.splice(length + 1, 0, { id: "", content: "", answer: "" });

};

const removeOption = (index: number) => {
  dataForm.options.splice(index, 1);
};

// 用于单选题，选中一个选项后，取消选中其他选项
const unCheckOtherOptions = (index: number) => {
  for (var i = 0; i < dataForm.options.length; i++) {
    if (i != index) {
      dataForm.options[i].answer = "";
    }
  }
}

const initOption = () => {
  // 使页面可见
  visible.value = true;
  console.log("initoption: " + dataForm.options.length);
  if (dataForm.options.length == 0) {
      switch (dataForm.type) {
        case '0' :
        case '1' :
          {
            dataForm.options = [
              { id: "", content: "<p>A、</p>", answer: "" },
              { id: "", content: "<p>B、</p>", answer: "" },
              { id: "", content: "<p>C、</p>", answer: "" },
              { id: "", content: "<p>D、</p>", answer: "" },
            ];
          }
          break;
        case '2' : // 判断题
          {
          dataForm.options = [
              { id: "", content: "", answer: "" },
            ];

          }
          break;
        case '3' : // 填空题
          {
            dataForm.options = [
              { id: "", content: "", answer: "" },
              { id: "", content: "", answer: "" },
            ];
          }
          break;
        case '4' : // 主观题
          {
            dataForm.options = [
              { id: "", content: "", answer: "" },
            ];
          }
          break;
      }
  }
}


// 获取信息
const getInfo = async (id: number, callback?: () => void) => {
  baseService.get("/question/question/" + id).then((res) => {
    Object.assign(dataForm, res.data);
    // 初始化自定义选项
    initOption();
    // 响应结果返回，调用回调函数
    if (typeof callback === 'function') {
      callback();
    }
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  if (!checkQuestionAnswer()) { //校验答案是否正确设置
    if(dataForm.type == '1') {
      ElMessage.error({
        message: '请设置至少两个正确答案',
        duration: 1000
      });
    } else {
      ElMessage.error({
        message: '请设置正确答案',
        duration: 1000
      });
    }
    return;
  }
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    // 将customOptions的值赋给dataForm.options
    //dataForm.options = customOptions.value;
    (!dataForm.id ? baseService.post : baseService.put)("/question/question", dataForm).then((res) => {
      visible.value = false;
      ElMessage.success({
        message: '成功',
        duration: 3000,
        onClose: () => {
        }
      });
      setTimeout(() => { //0.5秒后执行
        if (dataForm.id == '') {
          init(1, dataForm.type, ()=>{}, dataForm.courseId);
        }
        emit("refreshDataList");
      }, 500);
    });

  });
};

// 校验题目答案
const checkQuestionAnswer = () => {
  let isPassed = false;
  if (dataForm.type == '0') { //单选题
    dataForm.options.forEach((option) => {
      if(Boolean(option.answer) == true) {
        isPassed = true;
      }
    });
  } else if (dataForm.type == '1') { //多选题
    let checkCount = 0;
    dataForm.options.forEach((option) => {
      if(Boolean(option.answer) == true) {
        checkCount++;
      }
    });
    if (checkCount >= 2) {
      isPassed = true;
    }
  } else if (dataForm.type == '2') { // 判断题
    dataForm.options.forEach((option) => {
      if(option.answer != '') {
        isPassed = true;
      }
    });
  } else if (dataForm.type == '3') { // 填空题
    let CompletedCount = 0;
    dataForm.options.forEach((option) => {
      if(extractText(option.answer) != '') {
        CompletedCount++;
      }
    });
    if (CompletedCount == dataForm.options.length) {
      isPassed = true;
    }

  } else if (dataForm.type == '4'){
    dataForm.options.forEach((option) => { //主观题
      if(extractText(option.answer) != '') {
        isPassed = true;
      }
    });
  }

  return isPassed;
};

// HTML到纯文本
const extractText = (html: string) => {
  const tempDivElement = document.createElement('div');
  tempDivElement.innerHTML = html;
  return tempDivElement.textContent || tempDivElement.innerText || "";
};

defineExpose({
  init
});
</script>
<style scoped>
.custom-option-input {
  margin-bottom: 10px;
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.custom-button {
  width: 25px;
  height: 25px;
  top: -3.5px;
  position: relative;
  left: 10px;
  font-size: 18px; /* Adjust the font size as needed */
}

.custom-button-remove {
  background-color: #F56C6C !important;
  width: 25px;
  height: 25px;
  top: -3.5px;
  position: relative;
  left: 10px;
  font-size: 18px; /* Adjust the font size as needed */
}


:deep(.el-rate__icon){
font-size: 24px;
}


</style>
