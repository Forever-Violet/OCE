<template>
  <div class="mod-exam__generateparams">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">

      <el-form-item v-if="hasSchoolListPermission">
        <el-select v-model="state.dataForm.schoolId" placeholder="选择学校" filterable clearable @change="state.getDataList();getRangkingList();">
          <!--单选 去掉multiple-->
          <el-option v-for="school in schoolList" :key="school.schoolId" :label="school.schoolName" :value="school.schoolId"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-input v-model="state.dataForm.configName" placeholder="配置名称" @change="state.getDataList()" clearable>
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
        <ren-select v-model="state.dataForm.scope" dict-type="examGenerateParamsScope" placeholder="适用范围" @change="state.getDataList()"></ren-select>
      </el-form-item>

      <el-form-item>
        <el-button v-if="state.hasPermission('exam:generateparams:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('exam:generateparams:delete')" type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
      <el-form-item>
        <el-button v-if="state.hasPermission('exam:generateparams:export')" type="info" @click="state.exportHandle()">导出</el-button>
      </el-form-item>
    </el-form>
    <div style="display :flex">
      <el-table :data="rankingList" border style="width: 22%;"  :header-cell-style="{background:'#f3a694',color:'#f3a694'}">
        <el-table-column label="排行榜"  align="center">

          <el-table-column label="排名" header-align="center" align="center" >
            <template v-slot="scope">
              <span style="font-weight: bold;color:#ecb0c1">
                {{scope.$index+1}}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="configName" show-overflow-tooltip label="配置名称" width="121px" header-align="center" align="center">
            <template v-slot="scope">
              <el-text type="primary">
                    {{
                      scope.row.configName
                    }}
              </el-text>
            </template>

          </el-table-column>
          <el-table-column prop="accuracy" label="准确率" header-align="center" align="center">
            <template v-slot="scope">
              <el-text type="success" tag="b">
                    {{
                      (scope.row.accuracy * 100).toFixed(2) + "%"
                    }}
              </el-text>
            </template>
          </el-table-column>

        </el-table-column>
      </el-table>

      <el-divider direction="vertical" style="height:482px;">
        <el-icon><star-filled /></el-icon>
      </el-divider>

      <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle"
      style="width: 85%;margin-left: 0%;">

        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
                <el-table-column prop="configName" show-overflow-tooltip label="配置名称" header-align="center" align="center"></el-table-column>
                <el-table-column prop="populationSize" label="种群大小" sortable width="90px" header-align="center" align="center"></el-table-column>
                <el-table-column prop="maxIteratorNum" label="最大迭代次数" sortable header-align="center" align="center"></el-table-column>
                <el-table-column prop="mutationRate" label="变异概率" sortable width="90px" header-align="center" align="center"></el-table-column>
                <el-table-column prop="crossoverRate" label="交叉概率" sortable width="90px" header-align="center" align="center"></el-table-column>
                <el-table-column prop="expectedFitness" label="期望适应度" sortable header-align="center" align="center"></el-table-column>
                <el-table-column prop="accuracy" label="准确率" width="90px" sortable header-align="center" align="center">
                  <template v-slot="scope">
                    <el-text type="success" tag="b">
                      {{
                        (scope.row.accuracy * 100).toFixed(2) + "%"
                      }}
                    </el-text>
                  </template>
                </el-table-column>
                <el-table-column prop="useTimes" label="使用次数" sortable width="90px" header-align="center" align="center">
                  <template v-slot="scope">
                    <el-text type="warning" tag="b">
                      {{
                        (scope.row.useTimes)
                      }}
                    </el-text>
                  </template>
                </el-table-column>
                <el-table-column prop="scope" label="适用范围" width="90px" header-align="center" align="center">
                  <template v-slot="scope">
                    <el-tag :type="scope.row.scope == '0'?'success':'error'">
                      {{ state.getDictLabel('examGenerateParamsScope', scope.row.scope) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createDate" show-overflow-tooltip label="创建时间" sortable header-align="center" align="center"></el-table-column>
                <el-table-column prop="creatorName" show-overflow-tooltip label="创建者" width="90px" header-align="center" align="center"></el-table-column>
              <el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
                <template v-slot="scope">


                  <el-button v-if="state.hasPermission('exam:generateparams:update')" type="success" size="small"  @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
                  <el-button v-if="state.hasPermission('exam:generateparams:delete')" type="danger" size="small"  @click="state.deleteHandle(scope.row.id)">删除</el-button>

                </template>
              </el-table-column>
      </el-table>
    </div>
    <el-pagination :current-page="state.page" :page-sizes="[10, 20, 50, 100]" :page-size="state.limit" :total="state.total" layout="total, sizes, prev, pager, next, jumper" @size-change="state.pageSizeChangeHandle" @current-change="state.pageCurrentChangeHandle"> </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update ref="addOrUpdateRef" @refreshDataList="state.getDataList();getRangkingList()">确定</add-or-update>
  </div>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { reactive, ref, toRefs, onMounted } from "vue";
import AddOrUpdate from "./generate-params-add-or-update.vue";
import baseService from "@/service/baseService";

const view = reactive({
  deleteIsBatch: true,
  getDataListURL: "/exam/generateparams/page",
  getDataListIsPage: true,
  exportURL: "/exam/generateparams/export",
  deleteURL: "/exam/generateparams",
  dataForm: {
    schoolId: "",
    configName: "",
    scope: "",
  }
});


const schoolList = ref<any[]>([]);
const rankingList = ref<any[]>([]); //排行榜列表
const state = reactive({ ...useView(view), ...toRefs(view) });

const hasSchoolListPermission = state.hasPermission("sys:school:list"); // 检查用户是否拥有展示学校列表权限
onMounted(() => {
  //有权限，才执行
  if (hasSchoolListPermission) {
    // 如果是超级管理员
    getSchoolList();
  }
  getRangkingList();
});

// 获取学校列表
const getSchoolList = () => {
  return baseService.get("/sys/school/list").then((res) => {
    schoolList.value = res.data;
  });
};

// 获取排行榜列表
const getRangkingList = () => {
  let schoolId = state.dataForm.schoolId;
  return baseService.get("/exam/generateparams/rankingList", { schoolId }).then((res) => {
    rankingList.value = res.data;
  });
};


const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  addOrUpdateRef.value.init(id);
};
</script>
