<template>
  <div class="mod-course__course">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item v-if="hasSchoolListPermission">
        <el-select v-model="state.dataForm.schoolId" placeholder="选择学校" filterable clearable @change="resetGradeList();state.getDataList()"> <!--单选 去掉multiple-->
          <el-option v-for="school in schoolList" :key="school.schoolId" :label="school.schoolName" :value="school.schoolId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="state.dataForm.gradeId" placeholder="选择年级" filterable clearable @change="state.getDataList()">
          <el-option v-for="grade in gradeList" :key="grade.id" :label="grade.gradeName" :value="grade.id"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-input v-model="state.dataForm.courseName" placeholder="课程名称" @change="state.getDataList()" clearable>
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
        <el-button v-if="state.hasPermission('course:course:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('course:course:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>

      <el-form-item>
        <el-button v-if="state.hasPermission('course:course:export')" type="info" @click="state.exportHandle()">导出</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>

              <el-table-column prop="courseName" label="课程名称"  header-align="center" align="center"></el-table-column>

              <el-table-column prop="gradeName" label="年级" header-align="center" align="center"></el-table-column>

              <el-table-column prop="remark" label="课程介绍" header-align="center" align="center">
                <template v-slot="scope">
                  <span v-html="scope.row.remark"></span>
                </template>
              </el-table-column>

              <el-table-column prop="teacherNameList" label="任课教师" header-align="center" align="center">
                <template v-slot="scope">
                  <span>{{ scope.row.teacherNameList != null ? scope.row.teacherNameList.join(', ') : '无'}}</span>
                </template>
              </el-table-column>

              <el-table-column prop="hours" label="总课时" header-align="center" align="center">
                <template v-slot="scope">
                  <el-tag type="primary" effect="light">{{ scope.row.hours }}</el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="status" label="状态" header-align="center" align="center">
                <template v-slot="scope">
                  <el-tag :type="scope.row.scope == '0'?'warning':'success'" >{{ state.getDictLabel('courseStatus', scope.row.status) }}</el-tag>
                </template>
              </el-table-column>

              <el-table-column prop="creatorName" label="创建者" header-align="center" align="center"></el-table-column>

              <el-table-column prop="createDate" label="创建时间" header-align="center" align="center" sortable></el-table-column>
            <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">

          <el-button v-if="state.hasPermission('course:course:update')" type="success" size="small"  @click="addOrUpdateHandle(scope.row.id)">修改</el-button>


          <el-button v-if="state.hasPermission('course:course:delete')" type="danger" size="small" @click="state.deleteHandle(scope.row.id)">删除</el-button>

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
import AddOrUpdate from "./course-add-or-update.vue";
import baseService from "@/service/baseService";

const view = reactive({
  deleteIsBatch: true,
  //createdIsNeed: false, //创建页面时是否发送分页查询请求
  getDataListURL: "/course/course/page",
  getDataListIsPage: true,
  exportURL: "/course/course/export",
  deleteURL: "/course/course",
  dataForm: {
    schoolId: "",
    gradeId: "",
    courseName: "",
    status: "",

  }
});
const schoolList = ref<any[]>([]);
const gradeList = ref<any[]>([]);
const state = reactive({ ...useView(view), ...toRefs(view) });
const hasSchoolListPermission = state.hasPermission("sys:school:list"); // 检查用户是否拥有展示学校列表权限
onMounted(() => {
  //超级管理员才拥有该权限
  if (hasSchoolListPermission) {
    getSchoolList();
  } else {
    // 没有学校列表权限的话，直接获取数据
    //state.getDataList();
    getGradeList();
  }
});

// 获取学校列表
const getSchoolList = () => {
  return baseService.get("/sys/school/list").then((res) => {
    schoolList.value = res.data;
    // 检查返回的列表是否非空
    if (schoolList.value && schoolList.value.length > 0) {
      // 设置默认选中第一个学校
      state.dataForm.schoolId = schoolList.value[0].schoolId;
    }
    // 获取数据
    state.getDataList();
    // 获取年级列表
    getGradeList();
  });
};

// 获取年级列表
const getGradeList = () => {
  let schoolId = "";
  if (state.dataForm.schoolId != "") {
    schoolId = state.dataForm.schoolId;
  }
  return baseService.get("/sys/grade/list", { schoolId }).then((res) => {
    gradeList.value = res.data;
  });
};
// 当选择的学校改变后，重置gradeList, 并重置年级选项
const resetGradeList = () => {
  state.dataForm.gradeId = "";
  getGradeList();
};
const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
