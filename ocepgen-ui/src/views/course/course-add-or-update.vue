<template>
  <el-drawer v-model="visible" :title="!dataForm.id ? '新增课程' : '修改课程'"  :close-on-click-modal="false" 
    :close-on-press-escape="false" destroy-on-close size="50%">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" @keyup.enter="dataFormSubmitHandle()" v-loading="loading"
      label-width="120px">

      <el-form-item prop="gradeName" label="所属年级">
        <el-select v-model="dataForm.gradeId" filterable placeholder="选择年级" clearable>
          <el-option v-for="grade in gradeList" :key="grade.id" :label="grade.gradeName" :value="grade.id"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="课程名称" prop="courseName">
        <el-input 
        v-model="dataForm.courseName" 
        maxlength="200"
        show-word-limit
        type="textarea" 
        placeholder="课程名称，限制200字符"></el-input>
      </el-form-item>

      <el-form-item label="课程介绍" prop="remark">
        <editor v-model="dataForm.remark" placeholder="课程介绍"></editor>

      </el-form-item>

      <el-form-item label="总课时" prop="hours">
        <el-input-number v-model="dataForm.hours" min="0" controls-position="right" />

      </el-form-item>

      <el-form-item prop="teacherIdList" label="任课教师" >  <!--多选-->
        <el-select v-model="dataForm.teacherIdList" filterable multiple placeholder="分配教师至当前课程" style="width: 100%">
          <el-option v-for="teacher in teacherList" :key="teacher.id" :label="teacher.username + '（' + teacher.realName + ')'" :value="teacher.id"></el-option>
        </el-select>
      </el-form-item>


      <el-form-item label="状态" prop="status">
        <ren-radio-group v-model="dataForm.status" dict-type="courseStatus"></ren-radio-group>
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
import { IObject } from "@/types/interface";
import editor from '../../components/wang-editor/editor.vue';


const emit = defineEmits(["refreshDataList"]);

const visible = ref(false);
const loading = ref(true);
const dataFormRef = ref();
const gradeList = ref<any[]>([]);
const teacherList = ref<any[]>([]);
const dataForm = reactive({
  id: '',
  courseName: '', 
  gradeId: '', 
  remark: '', 
  hours: '', 
  status: '', 
  creator: '', 
  createDate: '',
  teacherIdList: [] as IObject[],

});

const rules = ref({
  courseName: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  gradeId: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  remark: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '必填项不能为空', trigger: 'blur' }
  ],
});

const init = (id?: number) => {
  visible.value = true;
  loading.value = true;

  dataForm.id = "";
  dataForm.courseName = "";
  dataForm.gradeId = "";
  dataForm.remark = "";
  dataForm.status = "0";
  dataForm.creator = "";
  dataForm.teacherIdList = [];

  // 重置表单数据
  // if (dataFormRef.value) {
  //   dataFormRef.value.resetFields();
  // }
  getGradeList();
  if (id) {
    getInfo(id);
  } else {
    loading.value = false;
    getTeacherList();
  }
};

// 获取信息
const getInfo = (id: number) => {
  baseService.get("/course/course/" + id).then((res) => {
    Object.assign(dataForm, res.data);
    loading.value = false;
    getTeacherList();

  });
};

// 获取年级列表
const getGradeList = () => {
  let schoolId = "";

  return baseService.get("/sys/grade/list", { schoolId }).then((res) => {
    gradeList.value = res.data;
  });
};

// 获取教师列表
const getTeacherList = () => {
  let schoolId = "";

  return baseService.get("/sys/user/teacher", { schoolId }).then((res) => {
    teacherList.value = res.data;
    if(dataForm.id == '') { //新增自加
      getCurrentUserInfoAndSelectedCurTeacher();
    }
  });
};

// 获取当前登录用户的用户id
const getCurrentUserInfoAndSelectedCurTeacher = () => {
  baseService.get(`/sys/user/info`).then((res) => {
    const currentUserId = res.data.id; //填充到表单

    // 检查当前用户ID是否在教师列表中
    const isTeacherPresent = teacherList.value.some(teacher => teacher.id === currentUserId);
    
    // 如果当前用户是教师之一，则将其ID设置为el-select的默认值
    if (isTeacherPresent) {
      // 由于是多选，确保为数组形式
      dataForm.teacherIdList.push(currentUserId);
    }
  });
};


// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/course/course", dataForm).then((res) => {
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

