<template>
  <div>
    <br>
    <div class="card text-center shadow-lg ml-5 mr-5">
      <div class="card-header">
        <h4>选择查看的房间号和时间段</h4>
      </div>
      <div class="card-body ml-5 mr-5">
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon1">开始日期</span>
          </div>
          <b-form-datepicker id="sd" v-model="startdata"></b-form-datepicker>
        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon2">结束日期</span>
          </div>
          <b-form-datepicker id="sd1" v-model="enddata"></b-form-datepicker>
        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon3">房间号</span>
          </div>
          <input v-model="Room_id" type="number" class="form-control" placeholder="RoomNumber" aria-label="Username" aria-describedby="basic-addon1">
        </div>
      </div>
      <div class="card-footer text-muted text-center">
        <div class="custom-control custom-radio custom-control-inline mr-5 ml-5">
          <input type="radio" id="customRadioInline1" name="customRadioInline" class="custom-control-input" value="0" v-model="formmodel">
          <label class="custom-control-label" for="customRadioInline1">日报表</label>
        </div>
        <div class="custom-control custom-radio custom-control-inline mr-5 ml-5">
          <input type="radio" id="customRadioInline2" name="customRadioInline" class="custom-control-input" value="1" v-model="formmodel">
          <label class="custom-control-label" for="customRadioInline2">周报表</label>
        </div>
        <div class="custom-control custom-radio custom-control-inline mr-5 ml-5">
          <input type="radio" id="customRadioInline3" name="customRadioInline" class="custom-control-input" value="2" v-model="formmodel">
          <label class="custom-control-label" for="customRadioInline3">月报表</label>
        </div>
        <div>
          <br>
          <a class="btn btn-success" @click="getform" role="button">查询报表</a>
        </div>
        </div>
    </div>
  </div>
</template>

<script>
import {mapActions, mapMutations, mapState} from "vuex";

export default {
  name: "FormOption",
  computed:{
    ...mapState(['Room_id','startdata', 'enddata', "formmodel"]),
    Room_id:{
      get(){
        return this.$store.state.Room_id;
      },
      set(value) {
        return this.$store.commit('handleRoomId', value);
      }
    },
    startdata:{
      get(){
        return this.$store.state.startdata;
      },
      set(value){
        return this.$store.commit('handlestartdata', value);
      },
    },
    enddata:{
      get(){
        return this.$store.state.enddata;
      },
      set(value){
        return this.$store.commit('handleenddata', value);
      },
    },
    formmodel: {
      get(){
        return this.$store.state.formmodel;
      },
      set(value){
        return this.$store.commit('handleformmodel', value);
      }
    },
  },
  methods:{
    ...mapMutations(['getForm']),
    getform(){
      this.getForm()
    }
  }
}
</script>

<style scoped>

</style>