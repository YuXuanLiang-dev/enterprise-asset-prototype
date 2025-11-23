<template>
  <div class="profile-page">
    <el-page-header content="个人中心" class="mb-20" />

    <div class="card-grid">
      <el-card shadow="hover">
        <div class="card-title">基本信息</div>
        <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="90px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="profileForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingProfile" @click="handleSaveProfile">保存信息</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="hover">
        <div class="card-title">修改密码</div>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="savingPwd" @click="handleChangePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { fetchProfile, updateProfile, changePassword } from '@/api/user'
import { useSessionStore } from '@/stores/session'

const sessionStore = useSessionStore()
const profileFormRef = ref<FormInstance>()
const pwdFormRef = ref<FormInstance>()
const savingProfile = ref(false)
const savingPwd = ref(false)

const profileForm = reactive({
  name: sessionStore.user?.name || '',
  phone: sessionStore.user?.phone || ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneValidator = (_rule: any, value: string, callback: any) => {
  const phoneReg = /^1[3-9]\d{9}$/
  if (!value) return callback(new Error('请输入手机号'))
  if (!phoneReg.test(value)) return callback(new Error('手机号格式不正确'))
  return callback()
}

const profileRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ validator: phoneValidator, trigger: 'blur' }]
}

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '不少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (_rule, value, callback) => {
        if (!value) return callback(new Error('请再次输入新密码'))
        if (value !== pwdForm.newPassword) return callback(new Error('两次输入不一致'))
        return callback()
      },
      trigger: 'blur'
    }
  ]
}

const loadProfile = async () => {
  if (!sessionStore.user?.id) return
  try {
    const data = await fetchProfile(sessionStore.user.id)
    profileForm.name = data.name
    profileForm.phone = data.phone
  } catch (e: any) {
    console.error(e)
  }
}

const handleSaveProfile = async () => {
  if (!sessionStore.user?.id) return
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return
    savingProfile.value = true
    try {
      const updated = await updateProfile({
        userId: sessionStore.user!.id,
        name: profileForm.name,
        phone: profileForm.phone
      })
      sessionStore.updateUser(updated as any)
      ElMessage.success('保存成功')
    } catch (e: any) {
      ElMessage.error(e?.response?.data?.message || '保存失败')
    } finally {
      savingProfile.value = false
    }
  })
}

const handleChangePwd = async () => {
  if (!sessionStore.user?.id) return
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    savingPwd.value = true
    try {
      await changePassword({
        userId: sessionStore.user!.id,
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功')
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
      pwdForm.confirmPassword = ''
      pwdFormRef.value?.clearValidate()
    } catch (e: any) {
      ElMessage.error(e?.response?.data?.message || '修改失败')
    } finally {
      savingPwd.value = false
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped lang="scss">
.profile-page {
  padding: 20px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 16px;
}

.card-title {
  font-weight: 600;
  margin-bottom: 12px;
}

.mb-20 {
  margin-bottom: 20px;
}
</style>
