<template>
  <!-- 缓冲动画 -->
  <div v-if="isLoading" class="spinner"></div>
  <div v-if="isLoading" class="spinner_backgroud"></div>
  <div class="mod-exam__epexampaper">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">

      <el-form-item v-if="hasSchoolListPermission">
        <el-select v-model="state.dataForm.schoolId" placeholder="选择学校" filterable clearable @change="resetCourseList();state.getDataList()">
          <!--单选 去掉multiple-->
          <el-option v-for="school in schoolList" :key="school.schoolId" :label="school.schoolName" :value="school.schoolId"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-select v-model="state.dataForm.courseId" placeholder="选择课程" filterable clearable @change="state.getDataList()">
          <el-option v-for="course in courseList" :key="course.id" :label="course.courseName" :value="course.id"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-input v-model="state.dataForm.title" placeholder="考卷标题" @change="state.getDataList()" clearable>
          <template #prefix>
            <el-button link @click="state.getDataList()" style="width: 20px;margin-left: -11px;align-content: center;">
              <el-icon class="el-input__icon">
                <search/>
              </el-icon>
            </el-button>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item>
        <ren-select v-model="state.dataForm.type" dict-type="examPaperType" placeholder="组卷类型" @change="state.getDataList()"></ren-select>
      </el-form-item>

      <el-form-item>
        <ren-select v-model="state.dataForm.scope" dict-type="examPaperScope" placeholder="适用范围" @change="state.getDataList()"></ren-select>
      </el-form-item>

      <el-form-item>
        <el-button v-if="state.hasPermission('exam:epexampaper:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('exam:epexampaper:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>

      <el-form-item>
        <el-button v-if="state.hasPermission('exam:epexampaper:export')" type="info" @click="state.exportHandle()">导出考卷列表</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border
    @selection-change="state.dataListSelectionChangeHandle" style="width: 100%" tooltip-effect="dark">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="title" label="试卷标题" header-align="center" align="center" show-overflow-tooltip>
                <template v-slot="scope">
                    <div v-html="extractText(scope.row.title)"></div>
                </template>
              </el-table-column>
              <el-table-column prop="courseName" label="所属课程" show-overflow-tooltip header-align="center" align="center"></el-table-column>
              <el-table-column prop="type" label="试卷类型" header-align="center" align="center">
                <template v-slot="scope">
                  <el-tag :type="scope.row.type == '1'?'success':'warning'">
                    {{ state.getDictLabel('examPaperType', scope.row.type) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="score" label="试卷总分" sortable  header-align="center" align="center">
                <template v-slot="scope">
                  <el-text type="success" tag="b">
                        {{
                          scope.row.score + " 分"
                        }}
                  </el-text>
                </template>
              </el-table-column>

              <el-table-column prop="scope" width="90px;" label="适用范围" header-align="center" align="center">

                <template v-slot="scope">
                  <el-tag :type="scope.row.scope == '0'?'success':'error'">
                    {{ state.getDictLabel('examPaperScope', scope.row.scope) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="questionCount" label="题目数量" sortable  header-align="center" align="center" width="110">

              </el-table-column>
              <el-table-column prop="remark" label="试卷介绍" show-overflow-tooltip header-align="center" align="center">
                <template v-slot="scope">
                    <div v-html="extractText(scope.row.remark)"></div>
                </template>
              </el-table-column>
              <el-table-column prop="creatorName" label="创建者" show-overflow-tooltip header-align="center" align="center"></el-table-column>
              <el-table-column prop="createDate" label="创建时间"  show-overflow-tooltip header-align="center" align="center" sortable></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center"  align="center" width="250">
              <template v-slot="scope">
                <el-button v-if="state.hasPermission('exam:epexampaper:update')" type="success" size="small"  @click="addOrUpdateHandle(scope.row.id)">修改</el-button>

                <el-button v-if="state.hasPermission('exam:epexampaper:export')" type="primary" size="small"  @click="exportHandle(scope.row.id)">导出</el-button>


                <el-button v-if="state.hasPermission('exam:epexampaper:delete')" type="danger" size="small" @click="state.deleteHandle(scope.row.id)">删除</el-button>

              </template>
          </el-table-column>
    </el-table>
    <el-pagination :current-page="state.page" :page-sizes="[10, 20, 50, 100]" :page-size="state.limit" :total="state.total" layout="total, sizes, prev, pager, next, jumper" @size-change="state.pageSizeChangeHandle" @current-change="state.pageCurrentChangeHandle"> </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update ref="addOrUpdateRef" @refreshDataList="state.getDataList">确定</add-or-update>
    <!-- 弹窗, 试卷导出-->
    <export ref="exportRef" >确定</export>
  </div>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, toRefs, onMounted } from "vue";
import AddOrUpdate from "./exampaper-add-or-update.vue";
import Export from "./exampaper-export.vue";
import baseService from "@/service/baseService";
import "../../assets/styles/loadingAnimation.css"

const isLoading = ref(false); //缓冲动画
const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/exam/epexampaper/page",
  getDataListIsPage: true,
  exportURL: "/exam/epexampaper/export",
  deleteURL: "/exam/epexampaper",
  dataForm: {
    schoolId: "",
    courseId: "",
    title: "",
    scope: "",
    type: ""
  }
});
const schoolList = ref<any[]>([]);
const courseList = ref<any[]>([]);
const state = reactive({ ...useView(view), ...toRefs(view) });

const hasSchoolListPermission = state.hasPermission("sys:school:list"); // 检查用户是否拥有展示学校列表权限
onMounted(() => {
  //有权限，才执行
  if (hasSchoolListPermission) {
    // 如果是超级管理员，那么先不获取，在选择了学校后会自动获取
    getSchoolList();
  } else {
    //如果不是超级管理员，即如果没有学校列表权限，直接获取
    getCourseList();
  }
});

// 获取学校列表
const getSchoolList = () => {
  return baseService.get("/sys/school/list").then((res) => {
    schoolList.value = res.data;
    // 默认选择第一个学校
    if (schoolList.value && schoolList.value.length > 0) {
      state.dataForm.schoolId = schoolList.value[0].schoolId;
    }

    // 选择后直接获取列表
    getCourseList();
  });
};

// 获取年级列表
const getCourseList = () => {
  let schoolId = "";
  if (state.dataForm.schoolId != "") {
    schoolId = state.dataForm.schoolId;
  }
  return baseService.get("/course/course/list", { schoolId }).then((res) => {
    courseList.value = res.data;
  });
};

// 当选择的学校改变后，重置courseList, 并重置课程选项
const resetCourseList = () => {
  state.dataForm.courseId = "";
  getCourseList();
};

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  //缓冲动画开启
  isLoading.value = true;
  // 页面初始化结束后调用回调函数
  addOrUpdateRef.value.init(id, ()=>{
    isLoading.value = false;
  });
};
const exportRef = ref();
const exportHandle = (id?: string) => {
  exportRef.value.init(id);
};

// 简单的HTML到纯文本转换函数
const extractText = (html: string) => {
  const tempDivElement = document.createElement('div');
  tempDivElement.innerHTML = html;
  return tempDivElement.textContent || tempDivElement.innerText || "";
};
</script>
