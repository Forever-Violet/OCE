<template>
  <div class="mod-sys__role">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item v-if="hasSchoolListPermission">
        <el-select v-model="state.dataForm.schoolId" placeholder="选择学校" clearable @change="state.getDataList();state.getDataList()"> 
          <el-option v-for="school in schoolList" :key="school.schoolId" :label="school.schoolName" :value="school.schoolId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="state.dataForm.name" placeholder="名称" clearable>
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
        <el-button v-if="state.hasPermission('sys:role:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('sys:role:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('sys:role:export')" type="info" @click="state.exportHandle()">导出</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" @sort-change="state.dataListSortChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="name" label="名称" header-align="center" align="center"></el-table-column>
      <el-table-column prop="schoolName" label="所属学校" header-align="center" align="center"></el-table-column>
      <el-table-column prop="remark" label="备注" header-align="center" align="center"></el-table-column>
      <el-table-column prop="createDate" label="创建时间" sortable="custom" header-align="center" align="center" width="180"></el-table-column>
      <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
        <template v-slot="scope">
          
          <el-button v-if="state.hasPermission('sys:role:update')" type="success" size="small"  @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          
          
          <el-button v-if="state.hasPermission('sys:role:delete')" type="danger" size="small" @click="state.deleteHandle(scope.row.id)">删除</el-button>
        
        </template>
      </el-table-column>
    </el-table>
    <el-pagination :current-page="state.page" :page-sizes="[10, 20, 50, 100]" :page-size="state.limit" :total="state.total" layout="total, sizes, prev, pager, next, jumper" @size-change="state.pageSizeChangeHandle" @current-change="state.pageCurrentChangeHandle"> </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update ref="addOrUpdateRef" @refreshDataList="state.getDataList"></add-or-update>
  </div>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, toRefs, onMounted } from "vue";
import AddOrUpdate from "./role-add-or-update.vue";
import baseService from "@/service/baseService";

const view = reactive({
  getDataListURL: "/sys/role/page",
  getDataListIsPage: true,
  deleteURL: "/sys/role",
  deleteIsBatch: true,
  dataForm: {
    name: "",
    schoolId: "",
  }
});

const schoolList = ref<any[]>([]);
const state = reactive({ ...useView(view), ...toRefs(view) });

const hasSchoolListPermission = state.hasPermission("sys:school:list"); 
onMounted(() => {
  //超级管理员才拥有该权限
  if (hasSchoolListPermission) {
    getSchoolList();
  }
});
// 获取学校列表
const getSchoolList = async () => {
  const res = await baseService.get("/sys/school/list");
  schoolList.value = res.data;
};
const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
