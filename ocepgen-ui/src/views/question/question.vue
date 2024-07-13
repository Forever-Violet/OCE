<template>
  <!-- 缓冲动画 -->
  <div v-if="isLoading" class="spinner"></div>
  <div v-if="isLoading" class="spinner_backgroud"></div>
  <div class="mod-question__question">
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
        <el-input v-model="state.dataForm.content" placeholder="题干" @change="state.getDataList()" clearable>
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
        <ren-select v-model="state.dataForm.type"  dict-type="questionType" placeholder="题目类型" @change="state.getDataList()"></ren-select>
      </el-form-item>

      <el-form-item>
        <ren-select v-model="state.dataForm.scope" dict-type="questionScope" placeholder="适用范围" @change="state.getDataList()"></ren-select>
      </el-form-item>


      <el-form-item class="floatWindows">
        <floatWindow :content-class="'i18n-list'" :animation="'main-fade'">

                <template #header>

                  <sapn v-if="state.hasPermission('question:question:save')">新增</sapn>
      
                </template>

                <template #content>
                  <div>
                    <el-row>
                      <el-col
                        v-for="item of questionTypes"
                        :key="item.dictValue"
                        :span="24"
                        style="text-align: center;"
                        >
                          <el-button
                            szie="small"
                            plain
                            type="primary"
                            style="width: 70px;"
                            @click="addOrUpdateHandle(1, item.dictValue)"
                          >
                            {{ item.dictLabel }}
                          </el-button>
                      </el-col>
                    </el-row>
                  </div>

                </template>
        </floatWindow>

      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('question:question:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>

      <el-form-item>
        <el-button v-if="state.hasPermission('question:question:export')" type="info" @click="state.exportHandle()">导出Word</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border 
    @selection-change="state.dataListSelectionChangeHandle" style="width: 100%"
    :row-style="{ height: 60+'px' }" 
    :cell-style="{padding:0+'px'}"
    >
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="content" label="题干" width="190px;" header-align="center"  show-overflow-tooltip align="center">
                <template v-slot="scope">
                    <div v-html="extractText(scope.row.content)"></div>
                </template>
              </el-table-column>

              <el-table-column prop="type" width="90px;" label="题型" header-align="center" align="center">
                <template v-slot="scope">
                  <el-tag type="primary" >{{ state.getDictLabel('questionType', scope.row.type) }}</el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="difficulty" label="难度" sortable width="134px;" header-align="center" align="center">
                <template v-slot="scope">
                  <el-rate v-model="scope.row.difficulty" 
                  :colors="['#CFD146', '#F7BA2A', '#FF9900']" 
                  count="5" 
                  disabled-void-color = "#DBE7FA"
                  disabled  />
                </template>
              </el-table-column>

              <el-table-column prop="scope" width="90px;" label="适用范围" header-align="center" align="center">
                
                <template v-slot="scope">
                  <el-tag :type="scope.row.scope == '0'?'success':'warning'">
                    {{ state.getDictLabel('questionScope', scope.row.scope) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="analysis" width="140px;"  show-overflow-tooltip label="题目解析" header-align="center" align="center">
                <template v-slot="scope">
                    <div v-html="extractText(scope.row.analysis)"></div>
                </template>
              </el-table-column>

              <el-table-column prop="score" label="分值" sortable width="80px;" header-align="center" align="center"></el-table-column>

              <el-table-column prop="courseName" label="所属课程"  show-overflow-tooltip header-align="center" align="center"></el-table-column>

              <el-table-column prop="creatorName" label="创建者" header-align="center" align="center"></el-table-column>

              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center" sortable></el-table-column>

            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">

          <el-button v-if="state.hasPermission('question:question:update')" type="success" size="small"  @click="addOrUpdateHandle(scope.row.id, scope.row.type)">修改</el-button>
          
          
          <el-button v-if="state.hasPermission('question:question:delete')" type="danger" size="small" @click="state.deleteHandle(scope.row.id)">删除</el-button>
        
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination :current-page="state.page" :page-sizes="[10, 20, 50, 100]" :page-size="state.limit" :total="state.total" layout="total, sizes, prev, pager, next, jumper" @size-change="state.pageSizeChangeHandle" @current-change="state.pageCurrentChangeHandle"> </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update ref="addOrUpdateRef" @refreshDataList="state.getDataList">确定</add-or-update>
  </div>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, toRefs, onMounted } from "vue";
import AddOrUpdate from "./question-add-or-update.vue";
import { getDictDataList } from "@/utils/utils";
import baseService from "@/service/baseService";
import { useAppStore } from "@/store";
import floatWindow from "@/components/float-window.vue";
import "../../assets/styles/loadingAnimation.css"



const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/question/question/page",
  getDataListIsPage: true,
  exportURL: "/question/question/export",
  deleteURL: "/question/question",
  dataForm: {
    questionType: "",
    schoolId: "",
    courseId: "",
    content: "",
    scope: "",
    type: ""
  }
});
const schoolList = ref<any[]>([]);
const courseList = ref<any[]>([]);
const isLoading = ref(false);
const state = reactive({ ...useView(view), ...toRefs(view) });
const hasSchoolListPermission = state.hasPermission("sys:school:list"); // 检查用户是否拥有展示学校列表权限
const questionTypes = ref<any[]>([]);
const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number, questionType?:string) => {
  //缓冲动画开启
  isLoading.value = true;
  // 页面初始化结束后调用回调函数
  addOrUpdateRef.value.init(id, questionType, ()=>{
    isLoading.value = false;
  });

};

onMounted(() => {
  const store = useAppStore();
  questionTypes.value = getDictDataList(store.state.dicts, "questionType");
    //有权限，才执行
  if (hasSchoolListPermission) {
    // 如果是超级管理员，那么先不获取年级列表和学期列表，在选择了学校后会自动获取
    getSchoolList();
  } else {
    //如果不是超级管理员，即如果没有学校列表权限，直接获取年级列表和学期列表
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

// 简单的HTML到纯文本转换函数
const extractText = (html: string) => {
  const tempDivElement = document.createElement('div');
  tempDivElement.innerHTML = html;
  return tempDivElement.textContent || tempDivElement.innerText || "";
};
</script>
<style>
.floatWindows {
  position: relative;
  z-index: 1000; /* 设置较高的 z-index 值 */
  /* 其他样式 */
}
.el-table {
  position: relative;
  z-index: 100; /* 设置较低的 z-index 值 */
  /* 其他样式 */
}
</style>