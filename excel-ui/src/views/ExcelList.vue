<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExcelList, uploadExcel } from '../api/excel'

const router = useRouter()
const excelList = ref([])
const uploadLoading = ref(false)

const loadExcelList = async () => {
  try {
    const { data } = await getExcelList()
    excelList.value = data
  } catch (error) {
    ElMessage.error('加载Excel列表失败')
  }
}

const handleUpload = async (file) => {
  try {
    uploadLoading.value = true
    await uploadExcel(file)
    ElMessage.success('上传成功')
    await loadExcelList()
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploadLoading.value = false
  }
}

const viewTable = (id) => {
  router.push(`/table/${id}`)
}

onMounted(() => {
  loadExcelList()
})
</script>

<template>
  <div class="excel-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Excel文件列表</span>
          <el-upload
            :auto-upload="false"
            accept=".xlsx,.xls"
            :show-file-list="false"
            :on-change="file => handleUpload(file.raw)"
          >
            <el-button type="primary" :loading="uploadLoading">
              上传Excel
            </el-button>
          </el-upload>
        </div>
      </template>
      
      <el-table :data="excelList" style="width: 100%">
        <el-table-column prop="fileName" label="文件名" />
        <el-table-column prop="createTime" label="上传时间" />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewTable(row.id)">
              查看数据
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.excel-list {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 