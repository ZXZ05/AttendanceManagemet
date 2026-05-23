<template>
  <div class="login">
    <el-card class="login-form-layout">
      <el-row :gutter="16">
        <el-col :span="8" style="text-align: right;">
          <img
            src="@/assets/01.png"
            style="
              width: 60px;
              height: 60px;
              margin: 5px 5px 0 0;
              -webkit-user-drag: none;
              -khtml-user-drag: none;
              -moz-user-drag: none;
              user-drag: none;
            "
          />
        </el-col>
        <el-col :span="16" style="font-size: 32px; text-align: left; margin: 15px 0 0 0; color: black;">
          <b class="login-title">考勤管理系统</b>
        </el-col>
      </el-row>
      <br />

      <el-form ref="formRef" :model="form" :rules="rules">
        <el-row :gutter="16">
          <el-col :span="3" style="padding-top: 12px;">
            <i class="iconfont icon-r-user2" style="font-size: 28px; color: grey;"></i>
          </el-col>
          <el-col
            :span="21"
            style="font-size: 24px; font-weight: 600; text-align: left; margin: 7px 0 0 0; color: rgb(64, 64, 64);"
          >
            <el-form-item prop="number">
              <el-input v-model="form.number" clearable placeholder="请输入账号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="3" style="padding-top: 12px;">
            <i class="iconfont icon-r-lock" style="font-size: 28px; color: grey;"></i>
          </el-col>
          <el-col
            :span="21"
            style="font-size: 24px; font-weight: 600; text-align: left; margin: 7px 0 0 0; color: rgb(64, 64, 64);"
          >
            <el-form-item prop="password">
              <el-input v-model="form.password" show-password placeholder="请输入密码"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <br />
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登录</el-button>
        </el-form-item>
        <el-link type="primary" @click="goTo('/register')">还没有账号？去注册</el-link>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from '@/axios/axios'
import { setLoginSession } from '@/utils/auth'

export default {
  name: 'LoginPage',
  data() {
    return {
      form: {
        number: '',
        password: ''
      },
      loading: false,
      rules: {
        number: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (!valid) {
          return
        }
        this.loading = true
        axios
          .post('/login/login', this.form)
          .then((res) => {
            if (res.data.code === 200) {
              setLoginSession(this.form.number)
              this.$message({ message: '登录成功', type: 'success' })
              this.$router.push('/home')
              return
            }
            if (res.data.code === 20004) {
              this.$message({ message: '账号不存在', type: 'error' })
              return
            }
            if (res.data.code === 20002) {
              this.$message({ message: '密码错误', type: 'error' })
              return
            }
            if (res.data.code === 10002) {
              this.$message({ message: '账号和密码不能为空', type: 'error' })
              return
            }
            this.$message({ message: res.data.message || '登录失败', type: 'error' })
          })
          .catch((e) => {
            const message = e?.response?.data || e?.message || '登录请求失败'
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
    goTo(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.login-form-layout {
  position: absolute;
  left: 0;
  right: 0;
  width: 440px;
  margin: 140px auto;
  border-top: 10px solid #409eff;
}

.login-title {
  text-align: center;
}

.login {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  overflow-y: auto;
  height: 100%;
  background: url("../assets/04.jpg") center top / cover no-repeat;
}
</style>
