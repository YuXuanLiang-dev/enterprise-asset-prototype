<template>
  <div class="login-container">
    <div class="left-section">
      <div class="logo-area">
        <div class="logo-placeholder">
          <span class="logo-icon">✨</span>
          <span class="logo-text">广软信息系统原型设计平台</span>
        </div>
      </div>
      <div class="illustration-area">
        <div class="illustration-placeholder">
          <div class="placeholder-3d">3D Illustration Area</div>
        </div>
      </div>
    </div>

    <div class="right-section">
      <div class="form-wrapper">
        <h2 class="welcome-title">欢迎使用<br />广软信息系统原型设计平台</h2>

        <el-tabs v-model="activeTab" class="custom-tabs" @tab-click="handleTabClick">
          <el-tab-pane label="密码登录" name="password"></el-tab-pane>
          <el-tab-pane label="验证码登录/注册" name="sms"></el-tab-pane>
        </el-tabs>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          class="login-form"
          size="large"
          hide-required-asterisk
        >
          <el-form-item prop="phone">
            <el-input 
              v-model="loginForm.phone" 
              placeholder="请输入手机号码"
              :prefix-icon="Iphone"
            />
          </el-form-item>

          <el-form-item prop="password" v-if="activeTab === 'password'">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item prop="code" v-if="activeTab === 'sms'">
            <div class="code-input-group">
              <el-input 
                v-model="loginForm.code" 
                placeholder="请输入验证码" 
                :prefix-icon="Key"
              />
              <el-button class="code-btn">获取验证码</el-button>
            </div>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <el-button link type="primary" class="forgot-pwd">忘记密码?</el-button>
          </div>

          <el-button 
            type="primary" 
            class="submit-btn" 
            :class="{ 'is-inactive': isSubmitDisabled }"
            :loading="loading" 
            @click="handleLogin"
          >
            登录
          </el-button>

          <div class="agreement-area">
             <el-checkbox v-model="loginForm.agree">
               我已阅读并同意 
               <span class="link">用户协议</span>、
               <span class="link">隐私保护政策</span>
             </el-checkbox>
          </div>
        </el-form>
      </div>

      <div class="copyright">广软信息系统原型设计平台荣誉出品</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { Iphone, Lock, Key } from '@element-plus/icons-vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { useRouter } from 'vue-router';
import { login } from '@/api/login';
import { useSessionStore } from '@/stores/session';

const router = useRouter();
const sessionStore = useSessionStore();
const loginFormRef = ref<FormInstance>();
const loading = ref(false);
const activeTab = ref('password');

const loginForm = reactive({
  phone: '17620927807',
  password: '123456',
  code: '',
  rememberMe: false,
  agree: true
});

const isSubmitDisabled = computed(() => {
  if (activeTab.value === 'password') {
    return !(loginForm.phone && loginForm.password);
  } else {
    return !(loginForm.phone && loginForm.code);
  }
});

const validatePhone = (_rule: any, value: string, callback: any) => {
  const phoneReg = /^1[3-9]\d{9}$/;
  if (!value) {
    callback(new Error('请输入手机号码'));
  } else if (!phoneReg.test(value)) {
    callback(new Error('请输入正确的手机号码'));
  } else {
    callback();
  }
};

const rules = reactive<FormRules>({
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
});

const handleTabClick = () => {
  loginFormRef.value?.clearValidate();
};

const handleLogin = async () => {
  if (isSubmitDisabled.value) return;
  if (!loginFormRef.value) return;

  if (!loginForm.agree) {
    ElMessage.warning('请先阅读并同意用户协议');
    return;
  }

  if (activeTab.value !== 'password') {
    ElMessage.warning('当前仅支持密码登录');
    return;
  }

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const resp = await login({
          phone: loginForm.phone,
          password: loginForm.password
        });
        sessionStore.setSession(resp);
        ElMessage.success('登录成功');
        router.push('/dashboard');
      } catch (error: any) {
        ElMessage.error(
          error?.response?.data?.message || '登录失败，请检查账号或密码'
        );
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped lang="scss">
// 变量定义
$primary-color: #FF6B3B; // 橙红色
$bg-left: #F2F4F9;       // 左侧背景色
$text-main: #333;
$text-light: #999;

.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;

  .left-section {
    width: 55%;
    background-color: $bg-left;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;

    .logo-area {
      position: absolute;
      top: 40px;
      left: 40px;
      
      .logo-placeholder {
        display: flex;
        align-items: center;
        gap: 8px;
        .logo-icon { font-size: 24px; }
        .logo-text {
          font-weight: bold;
          font-size: 20px;
          color: $text-main;
        }
      }
    }

    .illustration-placeholder {
      .placeholder-3d {
        width: 360px;
        height: 360px;
        background: linear-gradient(135deg, #FFE7D9, #FFF2E5);
        border-radius: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        color: #ff7a45;
      }
    }
  }

  .right-section {
    width: 45%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 40px;
    background: #fff;

    .form-wrapper {
      width: 400px;
      background: #fff;
      padding: 20px;
      border-radius: 12px;
      box-shadow: 0 6px 30px rgba(0,0,0,0.08);
    }

    .welcome-title {
      margin-bottom: 16px;
      color: $text-main;
    }

    .custom-tabs {
      margin-bottom: 20px;
    }

    .login-form {
      .form-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
      }

      .submit-btn {
        width: 100%;
        background: $primary-color;
        border-color: $primary-color;
        font-weight: 600;
      }

      .agreement-area {
        margin-top: 12px;
        color: $text-light;
      }
    }
  }
}
</style>
