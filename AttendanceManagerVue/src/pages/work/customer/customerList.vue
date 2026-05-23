<template>
<div class="pro-container">
  <div style="text-align: left;">
    <div>
	    <el-select v-model="form1.type" clearable placeholder="璇烽€夋嫨瀹㈡埛绫诲埆">
            <el-option v-for="item in customerTypeList" :key="item.id" :label="item.value" :value="item.value"></el-option>
        </el-select>
	    <el-input v-model="form1.name" placeholder="璇疯緭鍏ュ悕绉?" style="width: 200px;" class="filter-item" @keyup.enter="onSubmit" clearable/>
      <br>
      <br>
    </div>
    <el-row>
      <el-button class="filter-item" type="primary"  @click="onSubmit"> 鏌ヨ</el-button>  	 	
      <el-button class="filter-item" style="margin-left: 10px;" type="primary"  @click="handleCreate"> 鏂板</el-button>

    </el-row>
    <div class="right-items">
        <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary"  >淇?  鏀?/el-button>
	    <el-button class="filter-item" style="margin-left: 10px;" type="primary"  >鍒? 闄?/el-button> -->
    </div>
  </div>
  <div style="margin-top: 20px;">
    <el-table :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column prop="number" label="瀹㈡埛缂栧彿"></el-table-column>
      <el-table-column prop="name" label="濮撳悕"></el-table-column>
      <el-table-column prop="phone" label="鑱旂郴鏂瑰紡"></el-table-column>
      <el-table-column prop="address" label="鍦板潃"></el-table-column>
      <el-table-column prop="type" label="绫诲埆"></el-table-column>
      <el-table-column prop="remarks" label="澶囨敞"></el-table-column>
      <el-table-column label="鎿嶄綔" width="260" fixed="right">
	      <template #default="scope">
	        <el-button type="success" @click="handleUpdate(scope.row)"> 缂栬緫</el-button>
	        <el-button type="danger" @click="handleDelete(scope.row)"> 鍒犻櫎</el-button>
	      </template>
	  </el-table-column>
    </el-table>
  </div>
<el-dialog :title="dialogTitle" v-model="dialogFormVisible" >
	<el-form ref="form" :rules="rules" :model="form" label-position="left" label-width="120px" style="width: 420px; margin-left:50px;">
		<el-form-item label="缂栧彿" prop="number">
		  <el-input v-model="form.number" placeholder="璇疯緭鍏ュ鎴风紪鍙穨"/>
		</el-form-item>
		<el-form-item label="濮撳悕鍚嶇О" prop="name">
		  <el-input v-model="form.name" placeholder="璇疯緭鍏ュ鎴峰鍚嶆垨鍚嶇О~"/>
		</el-form-item>
        <el-form-item label="绫诲埆" prop="type">
		  <el-select v-model="form.type" clearable placeholder="璇烽€夋嫨" style="width: 100%;">
            <el-option v-for="item in customerTypeList" :key="item.id" :label="item.value" :value="item.value"></el-option>
          </el-select>
		</el-form-item>
        <el-form-item label="鑱旂郴鏂瑰紡">
		  <el-input v-model="form.phone" placeholder="璇疯緭鍏ヨ仈绯绘柟寮弤"/>
		</el-form-item>
        <el-form-item label="鍦板潃">
		  <el-input v-model="form.address" placeholder="璇疯緭鍏ュ湴鍧€~"/>
		</el-form-item>
        <el-form-item label="澶囨敞">
		  <el-input v-model="form.remarks" placeholder="璇疯緭鍏ュ娉▇"/>
		</el-form-item>
	</el-form>
	<div slot="footer" class="dialog-footer">
		<el-button @click="dialogFormVisible = false"> 鍙栨秷</el-button>
		<el-button v-if="isCreate" type="primary" @click="createData()"> 鏂板</el-button>
    <el-button v-if="isUpdate" type="primary" @click="updateData()"> 淇敼</el-button>
	</div>
</el-dialog>
</div>
</template>

<script>
import axios from '@/axios/axios'
import { getLoginUsername } from '@/utils/auth'
  export default {
    data() {
      return {
        tableData: [],
        customerType:'',
        customerTypeList:[
            {
                id: '1',
                value: '涓汉',
            },{
                id: '2',
                value: '鍏徃',
            },
        ],
        dialogFormVisible: false,
        dialogTitle:'',
        form: {
          id: '',
          number: '',
          name: '',
          type: '',
          phone: '',
          address: '',
          remarks: '',
          applyNumber: '',
        },
        form1: {
          id: '',
          number: '',
          name: '',
          type: '',
          phone: '',
          address: '',
          remarks: '',
          applyNumber: '',
        },
        id: '',
        isCreate: false,
        isUpdate: false,
        number: getLoginUsername(),
        employeeType: '',
        rules: {
          number: [
            { required: true, message: '瀹㈡埛缂栧彿涓嶈兘涓虹┖', trigger: 'blur' }
          ],
          name: [
            { required: true, message: '瀹㈡埛鍚嶇О涓嶈兘涓虹┖', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '璇烽€夋嫨瀹㈡埛绫诲瀷', trigger: 'change' }
          ],
        }
      }
    },
    created() {
      this.getData();
    },
    methods: {
      getData(){
        axios.post("/employee/findByNumber",{
          number: this.number,
        }).then(res => {
          if(res.data.type == "3"){
            axios.get("/customer/list").then(res => {
              this.tableData = res.data;
            })
          }else{
            axios.post("/customer/findByApplyNumber",{
              applyNumber: this.number,
            }).then(res => {
              this.tableData = res.data;
            })
          }
        })
      },
      resetTemp() {
	      this.form = {
	        id: '',
          number: '',
          name: '',
          type: '',
          phone: '',
          address: '',
          remarks: '',
	      }
      },
      resetForm() {
        this.form = {
          id: '',
          number: '',
          name: '',
          type: '',
          phone: '',
          address: '',
          remarks: '',
          applyNumber: '',
        }
      },
      handleCreate(){
        //this.resetTemp()
        this.dialogTitle = "娣诲姞瀹㈡埛"
        this.isCreate = true
        this.isUpdate = false
        this.dialogFormVisible = true
        this.resetForm()
      },
      handleUpdate(row){
        //this.tempForm = this.$wtables.getSelectedRowData(this, "employeeList");
        this.form = Object.assign({}, row) // copy obj
        this.dialogTitle = "缂栬緫瀹㈡埛淇℃伅"
        this.isUpdate = true
        this.isCreate = false
        this.dialogFormVisible = true
      },
      handleDelete(row){
        if(row){
          this.id = row.id
        }
        this.$confirm("确定删除？" + row.id, "提示", {
          confirmButtonText: "纭畾",
          cancelButtonText: "鍙栨秷",
          type: "warning",
        }).then(() => {
            axios.post("/customer/deleteById", {
                id: this.id,
            }).then((res) => {
                //if (res.data.code == 200) {
                  this.$message({ message: "鍒犻櫎鎴愬姛", type: "success" });
                  this.getData();
                //}
              });
          });
      },
      onSubmit(){
        axios.post('/customer/findByNameAndType',this.form1).then(res => {
          this.tableData = res.data;
        })
      },
      createData(){
        this.$refs['form'].validate((valid) => {
          if (valid) {
            this.form.applyNumber = this.number;
            axios.post('/customer/insert',this.form).then(res => {
              if(res.data.code == 200){
                this.dialogFormVisible = false;
                this.$message({ message: "娣诲姞鎴愬姛", type: "success" });
                this.getData();
              }else if(res.data.code == 20005){
                this.$message({ message: "璇ョ紪鍙峰凡瀛樺湪", type: "error" });
              }
            })
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      updateData(){
        axios.post('/customer/update',this.form).then(res => {
          this.dialogFormVisible = false;
          this.$message({ message: "淇敼鎴愬姛", type: "success" });
          this.getData();
        })
      }
    }
  }
</script>
<style scoped>
  .pro-container {
    padding: 15px 32px;
    margin: 4px 2px;
  }
  .fenye {
	  margin-top: 10px;
  }
</style>

