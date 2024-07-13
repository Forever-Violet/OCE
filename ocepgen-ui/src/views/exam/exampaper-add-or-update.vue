<template>
  <el-drawer v-model="visible" :title="!dataForm.id ? '新增试卷' : '修改试卷'" :close-on-click-modal="false"
    :close-on-press-escape="false" size="100%" destroy-on-close >
    <!-- <template #header>
      <div class="custom-drawer-header"
      style="background: red; ">the new header</div>
    </template> -->

      <el-form :model="dataForm" :rules="rules" ref="dataFormRef"
        label-width="100px" style="position: relative;">
        <el-form-item label="试卷标题" prop="title">
          <div style="width: 100%;">
            <editor :style="'height: 100px;'" v-model="dataForm.title" placeholder="试卷标题" ></editor>
          </div>
        </el-form-item>

        <el-form-item label="试卷介绍" prop="remark">
          <div style="width: 100%;">
            <editor v-model="dataForm.remark" placeholder="试卷介绍" ></editor>
          </div>
        </el-form-item>

        <el-form-item prop="courseId" label="课程">
          <el-select v-model="dataForm.courseId" filterable placeholder="请先选择课程"
          clearable
          :disabled="dataForm.id != '' || dataForm.id == null"
          @change="resetSubtitles()"
          style="width: 20%">
            <el-option v-for="course in courseList" :key="course.id" :label="course.courseName"
              :value="course.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="适用范围" prop="scope" v-if="dataForm.courseId">
          <ren-radio-group v-model="dataForm.scope" dict-type="examPaperScope"></ren-radio-group>
        </el-form-item>

        <el-form-item label="组卷方式" prop="type" v-if="dataForm.courseId">
          <ren-radio-group v-model="dataForm.type" dict-type="examPaperType" :disabled="dataForm.id != ''" @change="resetSubtitles()"></ren-radio-group>
        </el-form-item>

        <!-- 导航 -->
        <el-affix :offset="110" style="position: absolute;right: 0%;margin-top: -10px">
          <el-popover placement="left" :width="300"  trigger="click">
            <template #reference>
              <el-button style="margin-right: 0px;z-index: 99999;">
                <el-icon><DArrowLeft /></el-icon>
              </el-button>
            </template>
            <el-row><el-text type="default">题目导航</el-text></el-row>
            <el-row v-for="(subtitle,subtitleIndex) in dataForm.subtitles">
              <el-row><el-text type="primary">{{subtitle.examSubtitleName}}</el-text></el-row>
              <el-col>
                <el-button v-for="(question, index) in subtitle.questions"
                @click="goAnchor('#question'+calCurrentQuestionIndex(subtitleIndex, index))"
                style="width:35px;margin-left: 2%" >
                  {{ calCurrentQuestionIndex(subtitleIndex, index) }}
                </el-button>
              </el-col>
            </el-row>

          </el-popover>
        </el-affix>

        <el-form-item label="" prop="subtitles" v-if="dataForm.courseId">

          <!-- 智能组卷 -->
          <div v-if="dataForm.type == '1' && dataForm.id == ''" style="width:100%" class="generateClass">

            <el-text>组卷配置：</el-text>
            <el-select v-model="generatePaperDataForm.configId" filterable placeholder="若不选择则使用默认配置"
            clearable
            :disabled="dataForm.id != '' || dataForm.id == null"
            @change="resetSubtitles()"
            style="width: 15%;margin-left: 3px;">
              <el-option v-for="config in configList" :key="config.id"
              :label="config.configName + '（准确率：' + (config.accuracy * 100).toFixed(2) + '%)'"
                :value="config.id"></el-option>
            </el-select>

            <div style="display: flex;margin-top: 2px;">
              <div style="flex: 1;">
                <el-text>期望题型占比：</el-text>
                <el-tooltip
                content="题型所设值越大，在总题数中的占比就越大。若该题型实际数量达不到期望占比，将平均分配剩余的题数。" placement="top">
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip>
                <div v-for="(type, index) in questionTypes" :key="type.dictValue" :value="type.dictValue">
                  <el-text>{{ type.dictLabel }}&nbsp;&nbsp;</el-text>
                  <el-input-number v-model="questionTypeWeight[type.dictValue]"
                   :min="0" :max="99999" style="margin-bottom: 12px;">

                  </el-input-number>

                </div>
                <el-row>
                    <el-text style="margin-top: 2px;">期望难度：</el-text>
                    <el-rate v-model="generatePaperDataForm.expectedDifficulty"
                    size="large"
                    allow-half
                    :colors = "['#CFD146', '#F7BA2A', '#FF9900']"
                    :texts="['容易ヽ(￣▽￣)ﾉ', '较易(～￣▽￣)～ ', '正常(｡･ω･｡)', '较难╮(╯﹏╰）╭', '困难o(╥﹏╥)o']"
                    show-text
                    clearable />
                </el-row>


                <el-row style="margin-top: 5px;">
                    <el-text>期望题数：</el-text>
                    <el-input-number v-model="generatePaperDataForm.expectedQuestionCount" style="width:110px;" :min="0" :max="999"></el-input-number>
                </el-row>
                <el-row style="margin-top: 8px;">
                  <el-button type="success" @click="genPaperDataFormSubmitHandle()">一键组卷</el-button>
                </el-row>

                <el-row style="margin-top: 8px;" v-if="dataForm.difficulty">
                  <el-text>实际难度：</el-text>
                  <el-rate v-model="dataForm.difficulty"
                    size="large"
                    allow-half
                    :colors = "['#CFD146', '#F7BA2A', '#FF9900']"
                    :texts="['容易ヽ(￣▽￣)ﾉ', '较易(～￣▽￣)～ ', '正常(｡･ω･｡)', '较难╮(╯﹏╰）╭', '困难o(╥﹏╥)o']"
                    show-text
                    disabled
                    clearable />
                </el-row>


                <br/><br/>
              </div>

              <div style="flex:1;margin-top: 5px;">
                <el-text type="success" tag="b" style="font-size: 20px;margin-top: 10px;">
                    试卷总分：
                    <a-statistic title="" :value="Number(dataForm.score)"
                    :precision="2"
                    :value-from="0"
                    :value-style="{'font-size': '20px','font-weight': 'bold','color': '#67C23A',}"
                      animation>
                      <template #prefix>
                          <icon-arrow-rise />
                      </template>

                  </a-statistic>
                  {{ dataForm.score != '' ? ' 分' : '' }}
                </el-text>

                <v-chart class="chart" :loading="loading" :option="getExpectOption" autoresize />
                <v-chart class="chart" :loading="loading" :option="getRealOption" autoresize />
              </div>

            </div>
          </div>

          <el-divider style="border-color:#5BC9F8; border-top-width: 3px; opacity: 0.7;" />

          <!-- 手动组卷 或 智能组卷成功后显示 -->
          <div v-if="dataForm.type == '0' || generateFlag" style="width:100%">
            <div v-for="(subtitle, subtitleIndex) in dataForm.subtitles" :key="subtitleIndex">

              <el-input v-model="subtitle.examSubtitleName" type="textarea" placeholder="自定义标题" style="margin-bottom:10px;">
              </el-input>
              <!-- <template #suffix>
                <el-button type="danger" style="margin-right: -10px;" @click="removeSubtitle(subtitleIndex)"><el-icon>
                    <Remove />
                  </el-icon></el-button>
              </template> -->
               <el-button type="danger" style="margin-right: -10px;margin-bottom:10px;"
               @click="removeSubtitle(subtitleIndex)"
               >
               <el-icon><Remove />
                  </el-icon>&nbsp删除当前标题</el-button>

              <el-select placeholder="添加题目（输入关键字以筛选题目）" filterable clearable
                style="width:100%;margin-bottom:10px;" multiple :popper-append-to-body="false">
                <template #prefix>
                  <el-icon :size="25" style="color: #64DB29">
                    <Plus />
                  </el-icon>
                </template>


                <!-- <el-input v-model="searchInput" placeholder="请输入关键字" @input="handleSearch" clearable>
                  <template #suffix>
                    <el-icon class="el-input__icon"><search /></el-icon>
                  </template>
                </el-input> -->
                <!-- <el-option v-if="questionList.length == 0" :disabled="true"
                  :style="{ textAlign: 'center' }" style="width:100%">未查询到数据</el-option> -->

                <el-option v-for="question in filtedQuestionList" :key="question.id" class="select_item"
                  :label="'[' + state.getDictLabel('questionType', Number(question.type)) + '(' + question.score + '分)' + '] ' + question.content"
                  :value="question.id" @click="addQuestion(subtitleIndex, question.id)" style="width:100%">
                    <!-- <span v-html="question.content"></span> -->
                </el-option>
              </el-select>

              <div v-for="(question, index) in subtitle.questions" :key="index">

                <!-- <el-input v-model="question.content" disabled>
                  <template v-slot:prepend>
                    <span style="width:40px;margin-left:-10px;">
                      {{
                        '(' + question.score + '分) ' + calCurrentQuestionIndex(subtitleIndex, index) + '、'
                      }}
                    </span>
                  </template>
                  <template #suffix>
                    <el-button type="danger" style="margin-right: -10px;"
                      @click="removeQuestion(subtitleIndex, index)"><el-icon>
                        <Remove />
                      </el-icon></el-button>
                  </template>

                </el-input> -->
                <div :id="'question'+ calCurrentQuestionIndex(subtitleIndex, index)">
                  <span
                  v-html="'(' + question.score + '分) ' + calCurrentQuestionIndex(subtitleIndex, index)
                  + '、' + replaceFirstPTagWithSpan(question.content)"></span>
                </div>


                <!-- 选择题才循环显示选项 内容 -->
                <div v-if="question.type == '0' || question.type == '1'" v-for="(option, index) in question.options" :key="index">
                  <sapn v-html="replaceFirstPTagWithSpan(option.content)"></sapn>
                </div>

                <el-row>
                  <el-text>难度：</el-text>

                  <el-rate v-model="question.difficulty" size="large" disabled />
                </el-row>

                <el-text v-if="question.type == '0' || question.type == '1'">
                  答案：{{ getCorrectAnswer(subtitleIndex, index) }}
                </el-text>
                <!-- 判断题和主观题 -->
                <el-text v-if="question.type == '2' || question.type == '4'" v-for="(option, index) in question.options" :key="index">
                  <sapn v-html="'答案：' + replaceFirstPTagWithSpan(option.answer)"></sapn>
                </el-text>
                <!-- 填空题 -->
                <el-text v-if="question.type == '3'" v-for="(option, index) in question.options" :key="index">
                  <sapn v-html="'答案' + (index+1) +'：' + replaceFirstPTagWithSpan(option.answer) + '&nbsp&nbsp&nbsp&nbsp'"></sapn>
                </el-text>
                <br>

                <el-button type="danger" style="margin-right: -10px;"
                      @click="removeQuestion(subtitleIndex, index)"><el-icon>
                        <Remove />
                      </el-icon>&nbsp删除当前题目</el-button>
                <el-divider style="border-color:#5BC9F8; border-top-width: 0.1px; opacity: 0.3;" />


              </div>

              <el-divider style="border-color:#5BC9F8; border-top-width: 3px; opacity: 0.7;" />
            </div>
            <el-button @click="addSubtitle(dataForm.subtitles.length)" type="success">添加标题</el-button>
          </div>

        </el-form-item>

        <!-- 智能组卷完成后 或 修改时出现 -->
        <el-form-item label="平均难度" prop="difficulty" v-if="generateFlag || dataForm.id != ''">
          <el-rate v-model="dataForm.difficulty"
                    style="margin-top: -5px;margin-left: 8px;"
                    size="large"
                    allow-half
                    :colors = "['#CFD146', '#F7BA2A', '#FF9900']"
                    :texts="['容易ヽ(￣▽￣)ﾉ', '较易(～￣▽￣)～ ', '正常(｡･ω･｡)', '较难╮(╯﹏╰）╭', '困难o(╥﹏╥)o']"
                    show-text
                    disabled
                    clearable />
        </el-form-item>

        <el-form-item label="试卷总分" prop="score" v-if="dataForm.score != ''">
          <el-text size="large" type="success" tag="b" style="margin-left: -2px;"
            >&nbsp&nbsp&nbsp{{ dataForm.score }} 分</el-text>
        </el-form-item>

        <el-form-item label="题目数量" prop="questionCount" v-if="dataForm.questionCount != ''">
          <el-text size="large" type="primary" tag="b" style="margin-top: -3px;" >（
            {{ dataForm.questionCount }} ）</el-text>
          <!-- <el-input v-model="dataForm.questionCount" style="width: 45px;text-align: center;" placeholder="题目数量"></el-input> -->
        </el-form-item>



      </el-form>

      <template v-slot:footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="dataFormSubmitHandle()">确定</el-button>
      </template>
      <el-backtop :right="100" :bottom="100" />
    </el-drawer>

</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, watch, toRefs, computed, onMounted } from "vue";
import baseService from "@/service/baseService";
import { ElMessage } from "element-plus";
import { getDictDataList } from "@/utils/utils";
import { useAppStore } from "@/store";
import editor from '../../components/wang-editor/editor.vue';
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart } from "echarts/charts";
import { IEditorConfig } from '@wangeditor/editor';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent,
  GridComponent,
  MarkPointComponent,
} from "echarts/components";
use([
    CanvasRenderer,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    ToolboxComponent,
    GridComponent,
    MarkPointComponent,
]);
const emit = defineEmits(["refreshDataList"]);

const visible = ref(false);
const loading = ref(false); //组卷请求发送缓冲
const generateFlag = ref(false); //组卷成功flag，成功后显示试卷题目
const dataFormRef = ref();
const courseList = ref<any[]>([]);
const searchInput = ref("");
const questionList = ref<any[]>([]);
const configList = ref<any[]>([]); //组卷配置自定义
const filtedQuestionList = ref<any[]>([]); //将题目的content从html转换成纯文本的list，用于在添加题目中展示
const originalQuestionList = ref<any[]>([]); //原始的问题列表，没有通过题干查询到匹配的问题，可以通过这个列表重置学生列表
const currentQuestionCount = ref(0); //当前题目数量
const currentExamPaperScore = ref(0); //当前试卷总分
const questionTypes = ref<any[]>([]);
const questionTypeWeight = reactive([
  0, //单选
  0, //多选
  0, //判断
  0, //填空
  0 //主观
]);

const dataForm = reactive({
  id: '',
  title: '',
  courseId: '',
  type: '',
  score: '',
  questionCount: '',
  remark: '',
  scope: '',
  creator: '',
  difficulty: '',
  createDate: '',
  subtitles: [] as {
    id: string;
    examSubtitleName: string;
    sort: string;
    questions: Array<{
      id: string;
      content: string;
      type: string;
      analysis: string;
      difficulty: string;
      //scope: string;
      score: string;
      //courseId: string;
      createDate: string;
      options: Array<{
        id: string;
        content: string;
        answer: string;
      }>
    }>
  }[],
});
const generatePaperDataForm = reactive({
  questionTypeWeightMap: new Map(),
  expectedDifficulty: '',
  courseId: '',
  expectedQuestionCount: '',
  configId: ''
});

const state = reactive({ ...useView(dataForm), ...toRefs(dataForm) });


// 监听currentQuestionCount的变化，同步给questionCount
watch(currentQuestionCount,
  (newValue) => {
    dataForm.questionCount = String(newValue);
  });
// 监听currentQuestionCount的变化，同步给questionScore
watch(currentExamPaperScore,
  (newValue) => {
    dataForm.score = String(newValue);
  });

const rules = ref({
  title: [
    { required: true, message: '请填写试卷标题', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择组卷类型', trigger: 'blur' }
  ],
  remark: [
    { required: true, message: '请填写试卷介绍', trigger: 'blur' }
  ],
  scope: [
    { required: true, message: '请选择适用范围', trigger: 'blur' }
  ],
});

// 无工具栏编辑器配置
const noToolBarConfig = {
  // 用于去掉不需要的工具栏配置
  toolbarKeys: [
    // // 菜单 key
    // // 'headerSelect',
    // 'bold', // 加粗
    // 'italic', // 斜体
    // 'through', // 删除线
    // 'underline', // 下划线
    // // 'bulletedList', // 无序列表
    // // 'numberedList', // 有序列表
    // 'color', // 文字颜色
    // // 'insertLink', // 插入链接
    // 'fontSize', // 字体大小
    // // 'lineHeight', // 行高
    // 'uploadImage', // 上传图片
    // 'delIndent', // 缩进
    // 'indent', // 增进
    // // 'deleteImage',//删除图片
    // // 'divider', // 分割线
    // 'insertTable', // 插入表格
    // 'justifyCenter', // 居中对齐
    // 'justifyJustify', // 两端对齐
    // 'justifyLeft', // 左对齐
    // 'justifyRight', // 右对齐
    // 'undo', // 撤销
    // 'redo', // 重做
    // 'clearStyle', // 清除格式
    // 'fullScreen' // 全屏
  ]
};

// 替换html文本的第一个p标签为span标签
const replaceFirstPTagWithSpan = (htmlString: string) =>  {
  // 使用正则表达式匹配第一个<p>标签和对应的</p>
  const openingPTag = /<p/i; // 匹配第一个<p>标签，不区分大小写
  const closingPTag = /<\/p>/i; // 匹配第一个</p>标签，不区分大小写

  // 检查是否存在<p>标签
  if (openingPTag.test(htmlString) && closingPTag.test(htmlString)) {
    // 替换第一个<p>标签为<span>
    htmlString = htmlString.replace(openingPTag, '<span');
    // 替换第一个</p>标签为</span>
    htmlString = htmlString.replace(closingPTag, '</span>');
  }

  return htmlString;
}

// 去掉html文本的第一个p标签
const deleteFirstPTagWithSpan = (htmlString: string) =>  {
  // 使用正则表达式匹配第一个<p>标签和对应的</p>
  const openingPTagWithAttrs = /<p\s*[^>]*>/i; // 匹配第一个<p>标签及其属性，不区分大小写
  const closingPTag = /<\/p>/i; // 匹配第一个</p>标签，不区分大小写

  // 检查是否存在<p>标签
  if (openingPTagWithAttrs.test(htmlString) && closingPTag.test(htmlString)) {
    htmlString = htmlString.replace(openingPTagWithAttrs, '');
    htmlString = htmlString.replace(closingPTag, '');
  }

  return htmlString;
}

// 获取期望题型占比
const getExpectOption = computed(()=>{
    const data = [
      { value: questionTypeWeight[0], name: '单选题' },
      { value: questionTypeWeight[1], name: '多选题' },
      { value: questionTypeWeight[2], name: '判断题' },
      { value: questionTypeWeight[3], name: '填空题' },
      { value: questionTypeWeight[4], name: '主观题' },
    ];
    return getEchartOption("预期题型占比",data)
})
// 获取实际题型占比
const getRealOption = computed(()=>{
  const questionCounts = dataForm.subtitles.map(subtitle => subtitle.questions.length);
  const totalQuestions = questionCounts.reduce((total, count) => total + count, 0);
  if (totalQuestions == 0) {
    const data = [
      { value: 0, name: '单选题' },
      { value: 0, name: '多选题' },
      { value: 0, name: '判断题' },
      { value: 0, name: '填空题' },
      { value: 0, name: '主观题' },
    ];
    return getEchartOption("组卷题型占比",data)
  }
  const questionTypePercentage = Array.from({ length: 5}, (_, i) => {
    // 查找组卷返回 是否存在 当前题型
    const foundSubtitle = dataForm.subtitles.find(subtitle => Number(subtitle.sort) === i);
    console.log("当前题型："+state.getDictLabel("questionType", i)+"当前索引："+i);
    return {
      // 如果存在，那么计算百分比，如果不存在，设置为0
      value: foundSubtitle ? ((foundSubtitle.questions.length / totalQuestions) * 100).toFixed(2) : 0,
      name: state.getDictLabel("questionType", i),
    };
  });
  // const questionTypePercentage = questionCounts.map((count, index) => ({
  //     value: ((count / totalQuestions) * 100).toFixed(2),
  //     name: state.getDictLabel("questionType", Number(dataForm.subtitles[index].sort)),
  // }));
  //

  return getEchartOption("组卷题型占比", questionTypePercentage);
});

const getEchartOption = (title: string, data: any)=>{
    return  {
        title: {
            text: title,
            left: "center",
        },
        tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c} ({d}%)",
        },
        legend: {
            orient: "vertical",
            left: "left",
            data: data.map((item: any) => item.name),
        },
        series: [
            {
                name: "题型百分比",
                type: "pie",
                radius: ["40%", "70%"],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: "#fff",
                    borderWidth: 2,
                },
                label: {
                    show: false,
                    position: "center",
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 50,
                        fontWeight: "bold",
                    },
                },
                labelLine: {
                    show: false,
                },
                data: data,
            },
        ],
    };
}



// 获取学生列表
const getQuestionList = () => {
  let courseId = "";
  if (dataForm.courseId != "") {
    courseId = dataForm.courseId;
  }
  return baseService.get("/question/question/list", { courseId }).then((res) => {
    questionList.value = res.data;
    originalQuestionList.value = res.data;

    // 转换html为纯文本
    processQuestions();
  });
};

// 获取组卷配置列表
const getConfigList = () => {

  return baseService.get("/exam/generateparams/list", {  }).then((res) => {
    configList.value = res.data;
  });
};

const handleSearch = () => {
  // 在这里过滤题目列表
  const keyword = searchInput.value.toLowerCase();
  // 更新过滤后的学生列表
  questionList.value = questionList.value.filter((question) => {
    return question.content.toLowerCase().includes(keyword);
  });
  if (keyword == "") {
    resetQuestionList();
  }
};

// 重置题目列表
const resetQuestionList = () => {
  searchInput.value = "";
  questionList.value = originalQuestionList.value;
};


// 添加小标题
const addSubtitle = (subtitlesLength: number) => {
  dataForm.subtitles.splice(subtitlesLength + 1, 0, { id: "", examSubtitleName: "", sort: String(subtitlesLength), questions: [] });
};

// 删除小标题
const removeSubtitle = (subtitleIndex: number) => {
  // 先删除小标题下的题目
  console.log("当前小标题题目数量：" +  dataForm.subtitles[subtitleIndex].questions.length);
  let questionLength = dataForm.subtitles[subtitleIndex].questions.length; //先把题目长度保存下来，因为会实时被改变，导致遍历不完全
  // 从后往前遍历，不然会出界
  for (let i = questionLength-1; i > -1; i--) {
    removeQuestion(subtitleIndex, i);
    console.log("当前小标题: " + subtitleIndex + "; 当前题目索引：" + i);
  }
  dataForm.subtitles.splice(subtitleIndex, 1);
};

// 更新试卷难度
const updateDifficulty = () => {
  // 总难度
  let totalDifficulty = 0;

  dataForm.subtitles.forEach((subtitle) => {
    subtitle.questions.forEach((question) => {
      totalDifficulty += Number(question.difficulty);
    });
  });

  if(currentQuestionCount.value > 0) {
    dataForm.difficulty = (totalDifficulty / currentQuestionCount.value).toFixed(2);
  } else {
    dataForm.difficulty = '0';
  }
}

// 添加题目
const addQuestion = (subtitleIndex: number, questionId?: string) => {
  if (subtitleIndex >= 0 && subtitleIndex < dataForm.subtitles.length) {
    const newQuestion = questionList.value.find((q) => q.id === questionId);

    // 将题目添加到指定索引的subtitle对象的questions列表中
    dataForm.subtitles[subtitleIndex].questions.push(newQuestion);

    // 题目数量+1
    currentQuestionCount.value++;
    // 试卷总分增加
    currentExamPaperScore.value += Number(newQuestion.score);
    // 更新试卷难度
    updateDifficulty();
    // 成功提示
    ElMessage.success('添加成功');
  }
};

// 删除题目
const removeQuestion = (subtitleIndex: number, questionIndex: number) => {
  // 题目数量-1
  currentQuestionCount.value--;
  // 试卷总分减少，先处理，不然question先被删除了就读不到分数了
  currentExamPaperScore.value -= Number(dataForm.subtitles[subtitleIndex].questions[questionIndex].score);
  dataForm.subtitles[subtitleIndex].questions.splice(questionIndex, 1);
    // 更新试卷难度
    updateDifficulty();
  // 成功提示
  ElMessage.success('删除成功');
};

// 重置智能组卷参数表单
const resetGeneratePaperDataForm = () => {
  generatePaperDataForm.questionTypeWeightMap = new Map();
  generatePaperDataForm.expectedDifficulty = '';
  generatePaperDataForm.courseId = '';
  generatePaperDataForm.expectedQuestionCount = '';
  generatePaperDataForm.configId = '';
};


const init = (id?: number, callback?: () => void) => {
  //visible.value = true;
  dataForm.id = "";
  dataForm.title = "",
  dataForm.courseId = "",
  dataForm.scope = "",
  dataForm.type = "",
  dataForm.score = "",
  dataForm.questionCount = "",
  dataForm.remark = "",
  dataForm.difficulty = "",
  courseList.value = [];
  generateFlag.value = false;
  questionTypeWeight.forEach((_weight, index) => {
    questionTypeWeight[index] = 0;
  });
  resetSubtitles();
  resetGeneratePaperDataForm();

  const store = useAppStore();
  questionTypes.value = getDictDataList(store.state.dicts, "questionType");

  //获取课程列表
  getCourseList();
  //获取问题列表 在resetSubtitles已调用
  //getQuestionList();

  if (id) {
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
    //初始化试卷小标题
    initSubtitle();
  }
};

// 获取信息
const getInfo = (id: number, callback?: () => void) => {
  baseService.get("/exam/epexampaper/" + id).then((res) => {
    Object.assign(dataForm, res.data);

    if (dataForm.type == '1') { //如果是智能组卷，将generateFlag置为ture
      generateFlag.value = true;
    }

    initSubtitle();


    // 响应结果返回，调用回调函数
    if (typeof callback === 'function') {
      callback();
    }
  });

};

// 获取年级列表
const getCourseList = () => {

  return baseService.get("/course/course/list").then((res) => {
    courseList.value = res.data;
  });
};

// 根据小标题索引和题目索引计算当前题目的序号
const calCurrentQuestionIndex = (subTitleIndex: number, questionIndex: number) => {
  let currentQuestionIndex = 0;
  // 计算前面所有小标题下的题目总数
  for (let i = 0; i < subTitleIndex; i++) {
    currentQuestionIndex += dataForm.subtitles[i].questions.length;
  }

  // 加上当前小标题下的题目索引，因为该索引从0开始所以加1
  currentQuestionIndex += questionIndex + 1;
  return currentQuestionIndex;
};

// 获取单选题、多选题的正确答案
const getCorrectAnswer = (subTitleIndex: number, questionIndex: number) => {
  return dataForm.subtitles[subTitleIndex].questions[questionIndex].options
    .filter((option) => option.answer !== '')
    .map((option) => deleteFirstPTagWithSpan(option.content).slice(0, 1)) //只取第一个字符，即A B C D...
    .join('、');
};

const initSubtitle = () => {
  // 使页面可见
  visible.value = true;
  console.log("init" + dataForm.subtitles.length);
  if (dataForm.subtitles.length == 0) {
    dataForm.subtitles.push({ id: "", examSubtitleName: "", sort: String(0), questions: [] });
  }
  console.log("Initialized" + dataForm.subtitles.length);
  // 初始化题目数量 和 试卷分数
  dataForm.subtitles.forEach(subtitle => {
    subtitle.questions.forEach(question => {
      currentQuestionCount.value++;
      currentExamPaperScore.value += Number(question.score);
    });
  })
}

// 当课程改变后，重置小标题列表，已经重新查询问题列表
const resetSubtitles = () => {
  dataForm.subtitles = [],
  // 重置题目分数和数量
  currentQuestionCount.value = 0;
  currentExamPaperScore.value = 0;

  // 重置问题列表
  questionList.value = [];

  // 初始化小标题
  initSubtitle();
  // 重新查询问题列表 ，携带courseId
  getQuestionList();
  // 查询配置列表
  getConfigList();


}

// 简单的HTML到纯文本转换函数
const extractText = (html: string) => {
  const tempDivElement = document.createElement('div');
  tempDivElement.innerHTML = html;
  return tempDivElement.textContent || tempDivElement.innerText || "";
};
// 处理转换
const processQuestions = () => {
  filtedQuestionList.value = questionList.value.map(question => ({
    ...question,
    content: extractText(question.content),
    options: question.options.map((option: { content: string; }) => ({
      ...option,
      content: extractText(option.content)
    }))
  }));
};

// 检查智能组卷表单
const checkGenPaperDataForm = () => {
  let allZero = true;
  for (let i = 0; i < questionTypeWeight.length; i++) {
    if (questionTypeWeight[i] != 0) {
      allZero = false;
      break;
    }
  }
  if (allZero) {
    ElMessage({
      message: "请至少设置一个题型的占比",
      type: "error",
      duration: 2000,
    })
    return false;
  }

  if (generatePaperDataForm.expectedDifficulty == '0') {
    ElMessage({
      message: "请设置期望难度",
      type: "error",
    })
    return false;
  }
  if (generatePaperDataForm.expectedQuestionCount == '0') {
    ElMessage({
      message: "请设置期望题数",
      type: "error",
    })
    return false;
  }

  return true;
};

// 智能组卷表单提交
const genPaperDataFormSubmitHandle = () => {
  if (!checkGenPaperDataForm()) {
    return;
  }

  // 如果 questionTypeWeightMap 不是 Map 类型，将其转换为 Map 对象 ，因为要用户可能需要重复进行组卷，所以这里要重复转换类型才能修改
  if (!(generatePaperDataForm.questionTypeWeightMap instanceof Map)) {
    generatePaperDataForm.questionTypeWeightMap = new Map(Object.entries(generatePaperDataForm.questionTypeWeightMap));
  }

  generatePaperDataForm.courseId = dataForm.courseId;
  // 填充表单中的题目类型权重
  for(let i = 0; i < questionTypeWeight.length; i++) {
    let weight = questionTypeWeight[i];
    if (Number(weight) > 0) { //大于等于0才添加
      generatePaperDataForm.questionTypeWeightMap.set(state.getDictLabel("questionTypeEnumName", i), weight.toString());
    } else { //否则删除该题型权重

      generatePaperDataForm.questionTypeWeightMap.delete(state.getDictLabel("questionTypeEnumName", i));

    }
  }

  // 使用 Object.fromEntries 函数将 questionTypeWeightMap 转换为普通对象 ，因为js无法直接将map转为json，所以发送请求的时候map会变空值
  generatePaperDataForm.questionTypeWeightMap = Object.fromEntries(generatePaperDataForm.questionTypeWeightMap);
  // 发送请求
  loading.value = true; //开启缓冲动画

  (baseService.post)("/exam/epexampaper/generate", generatePaperDataForm).then((res) => {
    // copy对象
    //Object.assign(dataForm, res.data);
    dataForm.subtitles = res.data.subtitles;
    dataForm.difficulty = res.data.difficulty;
    dataForm.score = res.data.score;
    dataForm.questionCount = res.data.questionCount;
    currentExamPaperScore.value = res.data.score;
    currentQuestionCount.value = res.data.questionCount;

    // 显示题目
    generateFlag.value = true;
    console.log(generateFlag.value);
    ElMessage({
      type: 'success',
      message: "组卷成功",
    });
    // 关闭缓冲动画
    loading.value = false;
  });


}

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/exam/epexampaper", dataForm).then((_res) => {
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

// 跳转锚点
const goAnchor = (selector: any) => {
  document.querySelector(selector).scrollIntoView({
    behavior:"smooth"
  })
};


defineExpose({
  init
});
</script>
<style scoped>
.custom-button {
  background-color: #67C23A;
  color: white;
  border: none;
  padding: 0 10px;
  font-size: 14px;
}

/* 修改总体选项的样式 最外层 */
.el-select-dropdown {
  background-color: #ffffff;
  margin: 0px !important;
  border: 0px !important;
  border-radius: 0px !important;
}

/* //修改单个的选项的样式 */
.el-select-dropdown__item {
  margin: 10px !important;
  background-color: transparent !important;
  color: #3d3d3d !important;
  /* font-weight: bold; */

}

/* //item选项的hover样式 */
.el-select-dropdown__item.hover,
.el-select-dropdown__item:hover {
  color: #53b6e0 !important;
  background-color: #daf0fa !important;
}

/* //修改的是下拉框选项内容上方的尖角 */
.el-popper .popper__arrow,
.el-popper .popper__arrow::after {
  display: none !important;
}


.chart{
    height: 200px;
    width: 400px;
}

.main {
  .box {
    width: 100vw;
    margin: 10px auto;
    background-color: rgb(173, 173, 173);
    font-size: 50px;
    text-align: center;
    line-height: 400px;
  }
  .totop {
    display: none;
    position: fixed;
    bottom: 100px;
    right: 50px;
    background-color: skyblue;
    padding: 10px;
    &:hover {
      cursor: pointer;
      color: #fff;
    }
  }
}

.select_item {
  height: 40px !important;
}

</style>
