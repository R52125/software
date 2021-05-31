<template>
  <div>
    <br>
    <div class="card text-center shadow-lg ml-5 mr-5">
      <div class="card-header ml-5 mr-5">
        <h4>选择监测的房间号</h4>
      </div>
      <div class="card-body">
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon1">开始日期</span>
          </div>
          <b-form-datepicker id="basic-addon1"></b-form-datepicker>
        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon2">结束日期</span>
          </div>
          <b-form-datepicker id="basic-addon2"></b-form-datepicker>
        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon3">房间号</span>
          </div>
          <input type="number" class="form-control" placeholder="RoomNumber" aria-label="Username" aria-describedby="basic-addon1">
        </div>
      </div>
      <div class="card-footer text-muted text-center">
        <a class="btn btn-success ml-5 mr-5" href="#" role="button" >监测单个房间</a>
        <a class="btn btn-success ml-5 mr-5" @click="startmonitor" role="button">监测所有房间</a>
      </div>
    </div>
  </div>
</template>

<script>
import {mapActions, mapMutations} from "vuex";

export default {
  name: "CentralMonitoroption",
  computed:{

  },
  methods:{
    ...mapMutations(['test']),
    ...mapActions(['receivemsg']),
    startmonitor(){
      this.test()
      this.receivemsg()
    }
  }
}
</script>

<style scoped>

</style>