<template>
  <div class="register">
    <el-card class="login-form-layout">
      <h2 class="login-title">欢迎注册</h2>
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="90px">
        <el-form-item label="账号" prop="number">
          <el-input v-model="ruleForm.number" placeholder="请输入登录账号"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="ruleForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="ruleForm.password" show-password placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input v-model="ruleForm.checkPass" show-password placeholder="请再次输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">注册</el-button>
          <el-button @click="goToLogin">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from '@/axios/axios'

export default {
  name: 'RegisterPage',
  data() {
    const validateCheckPass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
        return
      }
      if (value !== this.ruleForm.password) {
        callback(new Error('两次输入密码不一致'))
        return
      }
      callback()
    }

    return {
      loading: false,
      ruleForm: {
        number: '',
        name: '',
        phone: '',
        password: '',
        checkPass: ''
      },
      rules: {
        number: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 4, max: 20, message: '账号长度需在 4 到 20 个字符', trigger: 'blur' }
        ],
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 32, message: '密码长度需在 6 到 32 个字符', trigger: 'blur' }
        ],
        checkPass: [{ validator: validateCheckPass, trigger: 'blur' }]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.ruleFormRef.validate((valid) => {
        if (!valid) {
          return
        }

        this.loading = true
        axios
          .post('/login/register', {
            number: this.ruleForm.number,
            name: this.ruleForm.name,
            phone: this.ruleForm.phone,
            password: this.ruleForm.password
          })
          .then((res) => {
            if (res.data.code === 200) {
              this.$message({ message: '注册成功，请登录', type: 'success' })
              this.$router.push('/')
              return
            }
            if (res.data.code === 20005) {
              this.$message({ message: '账号已存在，请更换', type: 'error' })
              return
            }
            if (res.data.code === 10001 || res.data.code === 10002) {
              this.$message({ message: '提交参数不合法，请检查输入', type: 'error' })
              return
            }
            this.$message({ message: res.data.message || '注册失败', type: 'error' })
          })
          .catch((e) => {
            const message = e?.response?.data || e?.message || '注册请求失败'
            this.$message({
              showClose: true,
              message,
              type: 'error',
              duration: 8000
            })
          })
          .finally(() => {
            this.loading = false
          })
      })
    },
    goToLogin() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.login-form-layout {
  position: absolute;
  left: 0;
  right: 0;
  width: 420px;
  margin: 120px auto;
  border-top: 10px solid #409eff;
}

.login-title {
  text-align: center;
}

.register {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow-y: auto;
  background: url("../assets/04.jpg") center top / cover no-repeat;
}
</style>
